package de.vw.paso.logic.partlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.vw.paso.exception.DataNotFoundException;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.EfsElementMapper;
import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementMara;
import de.vw.paso.partlist.domain.IPartListChild;
import de.vw.paso.partlist.domain.SpecialPartNumberType;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.repository.partlist.EfsElementMaraRepository;
import de.vw.paso.repository.partlist.EfsElementRepository;
import de.vw.paso.repository.partlist.VehiclePartListRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.service.partlist.DeleteNonPersistedEfsElementException;
import de.vw.paso.service.partlist.MovingHierachyConflictException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.Pair;
import de.vw.paso.vehicle.domain.VehicleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO - Refactor needed!!!
@Service
public class EfsElementManager {

    private static final Logger LOG = LoggerFactory.getLogger(EfsElementManager.class);

    private final UserManager userManager;
    private final VehiclePartListRepository vehiclePartListRepository;
    private final VehicleConfigRepository vehicleConfigRepository;
    private final EfsElementRepository efsElementRepository;
    private final EfsElementMaraRepository efsElementMaraRepository;
    private final EfsElementHistoryManager efsElementHistoryManager;

    public EfsElementManager(UserManager userManager, VehiclePartListRepository vehiclePartListRepository,
            VehicleConfigRepository vehicleConfigRepository, EfsElementRepository efsElementRepository,
            EfsElementMaraRepository efsElementMaraRepository, EfsElementHistoryManager efsElementHistoryManager) {
        this.userManager = userManager;
        this.vehiclePartListRepository = vehiclePartListRepository;
        this.vehicleConfigRepository = vehicleConfigRepository;
        this.efsElementRepository = efsElementRepository;
        this.efsElementMaraRepository = efsElementMaraRepository;
        this.efsElementHistoryManager = efsElementHistoryManager;
    }

    public Collection<EfsElement> loadPartList(long vehicleConfigId) {
        Collection<EfsElement> nonDeletedEfsElements = efsElementRepository.loadNonDeletedEfsElementsByVehicleConfigId(
                vehicleConfigId);
        return sortLeafsFirst2(nonDeletedEfsElements);
    }

    @Transactional
    public EfsElement saveEfsElement(EfsElement newEfsElement) {
        VehiclePartList vehiclePartList = vehiclePartListRepository.findById(newEfsElement.getVehiclePartListId())
                .orElseThrow();
        updateChanged(vehiclePartList);

        EfsElementMara efsElementMara = newEfsElement.getEfsElementMara();
        return efsElementMara.getId() == null ? saveEfsElementMaraTeilenummerImpl(newEfsElement, vehiclePartList) :
                saveEfsElementImpl(newEfsElement, vehiclePartList, efsElementMara.isEntityChange());
    }

    @Transactional
    public Collection<EfsElement> saveEfsElements(List<EfsElement> changeList) {
        long start = System.currentTimeMillis();

        Long vehiclePartListId = changeList.getFirst().getVehiclePartListId();
        VehiclePartList vehiclePartList = vehiclePartListRepository.findById(vehiclePartListId).orElseThrow();
        updateChanged(vehiclePartList);

        Collection<EfsElement> oldEfsElements = new ArrayList<>(changeList.size());
        Collection<Pair<EfsElement, EfsElementMara>> efsElementMaras = new ArrayList<>(changeList.size());
        for (EfsElement newEfsElement : changeList) {
            EfsElement oldElement = efsElementRepository.findById(newEfsElement.getId()).orElseThrow();
            oldEfsElements.add(oldElement);

            efsElementMaras.add(new Pair<>(newEfsElement, newEfsElement.getEfsElementMara()));
        }

        Long revision = efsElementHistoryManager.historicizeEfsElementMaras(efsElementMaras);
        efsElementHistoryManager.historicizeEfsElements(oldEfsElements);
        saveEfsElementsImpl(changeList, revision);

        LOG.info("Save efsElements in {} ms", (System.currentTimeMillis() - start));
        return changeList;
    }

    @Transactional
    public Collection<EfsElement> copyEfsElements(IPartListChild parent, List<EfsElement> efsElements) {
        long newRevision = efsElementHistoryManager.historicizeEfsElementsCopy(parent.getVehiclePartListId());

        return copyEfsElementsImpl(parent, efsElements, newRevision);
    }

    @Transactional
    public List<EfsElement> deleteEfsElements(List<EfsElement> efsElements)
            throws DeleteNonPersistedEfsElementException {
        long newRevision = efsElementHistoryManager.historicizeEfsElementsHierarchical(efsElements);
        return deleteEfsElementsImpl(efsElements, newRevision);
    }

    @Transactional
    public Collection<EfsElement> moveEfsElements(IPartListChild parent, List<EfsElement> efsElements)
            throws MovingHierachyConflictException {
        if (efsElements == null) {
            throw new RuntimeException("List of elements to move cannot be null");
        }

        if (parent != null && parent.asParent() != null) {
            for (EfsElement elementToMove : efsElements) {
                boolean samePartList = parent.getVehiclePartListId().equals(elementToMove.getVehiclePartListId());
                Collection<Long> ids = getAllIdsInHierarchy(elementToMove.getId(), samePartList);
                if (ids.contains(parent.asParent().getId())) {
                    throw new MovingHierachyConflictException(EfsElementMapper.toDto(parent.asParent()));
                }
            }
        }

        return efsElements.isEmpty() || parent == null || efsElements.getFirst().getVehiclePartListId()
                .equals(parent.getVehiclePartListId()) ? moveInsidePartList(parent, efsElements) :
                moveToDifferentPartList(parent, efsElements);
    }

    private Collection<EfsElement> moveToDifferentPartList(IPartListChild parent, List<EfsElement> efsElements) {
        VehiclePartList parentVehiclePartList = vehiclePartListRepository.findById(parent.getVehiclePartListId())
                .orElseThrow();
        updateChanged(parentVehiclePartList);
        Long newRevisionNewPartList = efsElementHistoryManager.historicizeEfsElementsCopy(
                parent.getVehiclePartListId());
        Collection<EfsElement> copiedElements = copyEfsElementsImpl(parent, efsElements, newRevisionNewPartList);

        Long newRevisionOldPartList = efsElementHistoryManager.historicizeEfsElements(
                efsElements.toArray(new EfsElement[0]));
        Collection<EfsElement> deletedElements = deleteEfsElementsImpl(efsElements, newRevisionOldPartList);

        Collection<EfsElement> changedElements = new ArrayList<>(copiedElements.size() + deletedElements.size());
        changedElements.addAll(copiedElements);
        changedElements.addAll(deletedElements);
        return changedElements;
    }

    private Collection<EfsElement> moveInsidePartList(IPartListChild parent, Collection<EfsElement> efsElements) {
        Long newRevision = efsElementHistoryManager.historicizeEfsElements(efsElements.toArray(new EfsElement[0]));
        VehiclePartList parentVehiclePartList = vehiclePartListRepository.findById(parent.getVehiclePartListId())
                .orElseThrow();
        updateChanged(parentVehiclePartList);

        Collection<EfsElement> changedNodes = new ArrayList<>();
        for (EfsElement node : efsElements) {
            EfsElement nodeFromRepository = efsElementRepository.findById(node.getId()).orElseThrow();
            Long parentId = null;
            if (parent.asParent() != null) {
                parentId = parent.asParent().getId();
            }

            nodeFromRepository.setParentId(parentId);
            nodeFromRepository.setRevision(newRevision);
            changedNodes.add(nodeFromRepository);
        }

        return efsElementRepository.saveAll(changedNodes);
    }

    private EfsElement saveEfsElementMaraTeilenummerImpl(EfsElement efsElement, VehiclePartList vehiclePartList) {
        String partNumber = efsElement.getEfsElementMara().getPartNumber();
        EfsElementMara coreMaterial = efsElementMaraRepository.findOneByPartNumberAndVehiclePartListId(partNumber,
                efsElement.getVehiclePartListId());

        boolean isRevision;
        if (coreMaterial == null) {
            isRevision = true;
        } else {
            efsElement.setEfsElementMara(coreMaterial);
            isRevision = false;
        }

        EfsElement savedEfsElement = saveEfsElementImpl(efsElement, vehiclePartList, isRevision);
        savedEfsElement.getEfsElementMara().setEntityChange(false);
        return savedEfsElement;
    }

    private EfsElement saveEfsElementImpl(EfsElement newEfsElement, VehiclePartList vehiclePartList,
            boolean updateMaraRevision) {
        Long newRevision = efsElementHistoryManager.historicizeEfsElements(newEfsElement);
        if (updateMaraRevision) {
            EfsElementMara efsElementMara = newEfsElement.getEfsElementMara();
            efsElementMara.setRevision(newRevision);
            efsElementMara.setEntityChange(false);

            efsElementHistoryManager.historicizeEfsElementMara(efsElementMara);
        }

        newEfsElement.setRevision(newRevision);
        newEfsElement.setEntityChange(false);
        newEfsElement.setChange(userManager.getCurrentUserId());

        vehiclePartList.setRevision(newRevision);
        vehiclePartListRepository.save(vehiclePartList);

        return efsElementRepository.save(newEfsElement);
    }

    private void saveEfsElementsImpl(List<EfsElement> changeList, Long newRevision) {
        Collection<EfsElement> saveEfsElementImplList = new ArrayList<>();
        Collection<EfsElement> saveEfsElementMaraTeilenummerImplList = new ArrayList<>();
        for (EfsElement newElement : changeList) {
            if (newElement.getEfsElementMara().getId() == null) {
                saveEfsElementMaraTeilenummerImplList.add(newElement);
            } else {
                saveEfsElementImplList.add(newElement);
            }
        }

        Collection<EfsElement> changedEfsElements = new ArrayList<>();
        for (EfsElement element : saveEfsElementImplList) {
            element.setRevision(newRevision);
            element.setEntityChange(false);

            if (element.getEfsElementMara().isEntityChange()) {
                element.getEfsElementMara().setRevision(newRevision);
                element.getEfsElementMara().setEntityChange(false);

                efsElementHistoryManager.historicizeEfsElementMara(element.getEfsElementMara());
            }

            changedEfsElements.add(element);
        }

        for (EfsElement element : saveEfsElementMaraTeilenummerImplList) {
            String partNumber = element.getEfsElementMara().getPartNumber();
            EfsElementMara coreMaterial = efsElementMaraRepository.findOneByPartNumberAndVehiclePartListId(partNumber,
                    element.getVehiclePartListId());

            if (coreMaterial == null) {
                element.getEfsElementMara().setRevision(newRevision);
                element.getEfsElementMara().setEntityChange(false);

                efsElementHistoryManager.historicizeEfsElementMara(element.getEfsElementMara());
            } else {
                element.setEfsElementMara(coreMaterial);
            }

            element.setRevision(newRevision);
            element.setEntityChange(false);

            changedEfsElements.add(element);
        }

        VehiclePartList vehiclePartList = vehiclePartListRepository.findById(
                changeList.getFirst().getVehiclePartListId()).orElseThrow();
        vehiclePartList.setRevision(newRevision);
        vehiclePartListRepository.save(vehiclePartList);

        efsElementRepository.saveAll(changedEfsElements);
    }

    private Collection<EfsElement> copyEfsElementsImpl(IPartListChild parent, Collection<EfsElement> efsElements,
            Long newRevision) {
        VehiclePartList parentVehiclePartList = vehiclePartListRepository.findById(parent.getVehiclePartListId())
                .orElseThrow();
        parentVehiclePartList.setRevision(newRevision);

        Map<Long, EfsElement> copiedEfsElements = new HashMap<>();
        for (EfsElement efsElement : efsElements) {
            VehiclePartList vehiclePartList = vehiclePartListRepository.findById(efsElement.getVehiclePartListId())
                    .orElseThrow();
            updateChanged(vehiclePartList);
            Collection<Long> efsElementIds = getAllIdsInHierarchy(efsElement.getId(), false);
            for (EfsElement dbEfsElement : efsElementRepository.findAllById(efsElementIds)) {
                IPartListChild newParent = copiedEfsElements.get(dbEfsElement.getParentId());
                if (newParent == null) {
                    newParent = parent;
                }

                copiedEfsElements.put(dbEfsElement.getId(), copyEfsElement(newParent, dbEfsElement, newRevision));
            }
        }

        return sortLeafsFirst2(copiedEfsElements.values());
    }

    private List<EfsElement> deleteEfsElementsImpl(Collection<EfsElement> efsElemente, long newRevision) {
        Map<Long, EfsElement> deletedElements = new HashMap<>();
        Collection<VehiclePartList> vehiclePartLists = new HashSet<>();
        Collection<Long> efsElementeIds = efsElemente.stream().map(element -> {
            VehiclePartList vehiclePartList = vehiclePartListRepository.findById(element.getVehiclePartListId())
                    .orElseThrow();
            vehiclePartLists.add(vehiclePartList);

            return element.getId();
        }).toList();

        for (long efsElementeId : efsElementeIds) {
            Collection<Long> ids = getAllIdsInHierarchy(efsElementeId, false);
            for (EfsElement nodeToDelete : efsElementRepository.findAllById(ids)) {
                nodeToDelete.setDeleted(1);
                nodeToDelete.setRevision(newRevision);
                nodeToDelete.setChange(userManager.getCurrentUserId());
                deletedElements.put(nodeToDelete.getId(), nodeToDelete);
            }

            efsElementRepository.saveAll(deletedElements.values());
        }

        updateChanged(vehiclePartLists);
        return sortLeafsFirst2(deletedElements.values());
    }

    public EfsElementDTO getOne(long efsElementeId) {
        return EfsElementMapper.toDto(efsElementRepository.findById(efsElementeId).orElseThrow());
    }

    public List<Long> getAllIdsInHierarchy(long efsElementeId, boolean addDeleted) {
        List<Long> allElementIds = new ArrayList<>(List.of(efsElementeId));
        Collection<Long> newParentIds = new ArrayList<>(List.of(efsElementeId));
        do {
            Collection<EfsElement> children;
            if (addDeleted) {
                children = efsElementRepository.findChildrenWithDeleted(newParentIds);
            } else {
                children = efsElementRepository.findChildren(newParentIds);
            }
            newParentIds.clear();

            if (children != null && !children.isEmpty()) {
                for (EfsElement child : children) {
                    Long childId = child.getId();
                    allElementIds.add(childId);
                    newParentIds.add(childId);
                }
            }
        } while (!newParentIds.isEmpty());

        return allElementIds;
    }

    private EfsElement copyEfsElement(IPartListChild parent, EfsElement efsElement, long newRevision) {
        EfsElementMara efsElementMara = efsElement.getVehiclePartListId().equals(parent.getVehiclePartListId()) ?
                efsElementMaraRepository.getReferenceById(efsElement.getEfsElementMara().getId()) :
                getMaraId(efsElement.getEfsElementMara().getId(), parent.getVehiclePartListId(), newRevision);

        Long parentId = parent.asParent() == null ? null : parent.asParent().getId();
        EfsElement newEfsElement = createEfsElement(parentId, efsElementMara, efsElement.getQuantity(),
                efsElement.getQuantityUnit(), efsElement.getAp(), parent.getVehiclePartListId());

        newEfsElement.setRevision(newRevision);
        newEfsElement.setDeleted(efsElement.getDeleted());
        newEfsElement.setGap(efsElement.getGap());
        newEfsElement.setProduct(efsElement.getProduct());
        newEfsElement.setTisSort(efsElement.getTisSort());
        // Todo - "TI_WH_EBOM_ID" column is missing from EfsElement entity
        //newEfsElement.setTiWhEbomId(efsElement.getTiWhEbomId());
        newEfsElement.setTiWhImportId(efsElement.getTiWhImportId());
        newEfsElement.setNodeId(efsElement.getNodeId());
        newEfsElement.setNodeType(efsElement.getNodeType());
        newEfsElement.setNodeValue(efsElement.getNodeValue());
        newEfsElement.setNodeLabel(efsElement.getNodeLabel());
        newEfsElement.setNodeLevel(efsElement.getNodeLevel());
        newEfsElement.setBomNumber(efsElement.getBomNumber());
        newEfsElement.setPartType(efsElement.getPartType());
        newEfsElement.setWeightControlFlag(efsElement.getWeightControlFlag());
        newEfsElement.setPrNumberRule(efsElement.getPrNumberRule());
        newEfsElement.setBeginDateKey(efsElement.getBeginDateKey());
        newEfsElement.setEndDateKey(efsElement.getEndDateKey());
        newEfsElement.setBeginDate(efsElement.getBeginDate());
        newEfsElement.setEndDate(efsElement.getEndDate());
        newEfsElement.setSetKey(efsElement.getSetKey());
        newEfsElement.setConstructionsGroup(efsElement.getConstructionsGroup());
        newEfsElement.setProductStructure(efsElement.getProductStructure());
        newEfsElement.setCostGroup(efsElement.getCostGroup());
        newEfsElement.setAggregate(efsElement.getAggregate());
        newEfsElement.setWahlweiseFall(efsElement.getWahlweiseFall());
        newEfsElement.setWahlweiseNr(efsElement.getWahlweiseNr());
        newEfsElement.setBaukasten(efsElement.getBaukasten());
        newEfsElement.setBaukastenStatus(efsElement.getBaukastenStatus());
        newEfsElement.setBaukastenNodeId(efsElement.getBaukastenNodeId());
        newEfsElement.setWorkPackageNumber(efsElement.getWorkPackageNumber());
        newEfsElement.setProcessStatus(efsElement.getProcessStatus());
        newEfsElement.setDmuRelevant(efsElement.getDmuRelevant());
        newEfsElement.setMaterialType(efsElement.getMaterialType());
        newEfsElement.setEarliestPvs(efsElement.getEarliestPvs());
        newEfsElement.setEarliestNs(efsElement.getEarliestNs());
        newEfsElement.setEarliestSop(efsElement.getEarliestSop());
        newEfsElement.setPActivationDate(efsElement.getPActivationDate());
        newEfsElement.setKonstructureDate(efsElement.getKonstructureDate());
        newEfsElement.setAvonStatus(efsElement.getAvonStatus());
        newEfsElement.setChange(userManager.getCurrentUserId());

        return efsElementRepository.save(newEfsElement);
    }

    // Todo - Does efsElementMaraCopy never NULL?
    private EfsElementMara getMaraId(long efsElementMaraId, Long partListId, long newRevision) {
        EfsElementMara efsElementMaraCopy = efsElementMaraRepository.findById(efsElementMaraId).orElseThrow(
                () -> new DataNotFoundException("Could not load efsElementMAra with id:" + efsElementMaraId));

        EfsElementMara newEfsElementMara = efsElementMaraRepository.findOneByPartNumberAndVehiclePartListId(
                efsElementMaraCopy.getPartNumber(), partListId);

        if (newEfsElementMara != null) {
            return newEfsElementMara;
        }

        newEfsElementMara = createEfsElementMara(efsElementMaraCopy.getDescription1De(),
                efsElementMaraCopy.getPartNumber(), partListId);

        newEfsElementMara.setDescription1En(efsElementMaraCopy.getDescription1En());
        newEfsElementMara.setDescription2De(efsElementMaraCopy.getDescription2De());
        newEfsElementMara.setDescription1En(efsElementMaraCopy.getDescription1En());
        newEfsElementMara.setRevision(newRevision);

        newEfsElementMara.setDrawingDate(efsElementMaraCopy.getDrawingDate());
        newEfsElementMara.setDrawingStatus(efsElementMaraCopy.getDrawingStatus());
        newEfsElementMara.setWeightWeightedTe(efsElementMaraCopy.getWeightWeightedTe());
        newEfsElementMara.setWeightWeightedProd(efsElementMaraCopy.getWeightWeightedProd());
        newEfsElementMara.setWeightCalculatedTe(efsElementMaraCopy.getWeightCalculatedTe());
        newEfsElementMara.setWeightEstimatedTe(efsElementMaraCopy.getWeightEstimatedTe());

        newEfsElementMara = efsElementMaraRepository.save(newEfsElementMara);

        return newEfsElementMara;
    }

    private void updateChanged(Collection<VehiclePartList> vehiclePartListsToUpdate) {
        Collection<VehicleConfig> updatedConfigs = new ArrayList<>();
        Collection<VehiclePartList> updatedVehiclePartLists = new ArrayList<>();
        String currentUserId = userManager.getCurrentUserId();

        for (VehiclePartList vehiclePartList : vehiclePartListsToUpdate) {
            vehiclePartList.setChange(currentUserId);
            updatedVehiclePartLists.add(vehiclePartList);

            VehicleConfig vehicleConfig = vehiclePartList.getVehicleConfig();
            if (vehicleConfig != null) {
                vehicleConfig.setChange(currentUserId);
                vehicleConfig.setVehiclePartList(vehiclePartList);

                updatedConfigs.add(vehicleConfig);
            }
        }

        vehicleConfigRepository.saveAll(updatedConfigs);
        vehiclePartListRepository.saveAll(updatedVehiclePartLists);
    }

    private void updateChanged(VehiclePartList vehiclePartList) {
        updateChanged(Collections.singletonList(vehiclePartList));
    }

    private List<EfsElement> sortLeafsFirst2(Collection<EfsElement> unsortedElements) {
        List<EfsElement> sortedList = sortParentsFirst(unsortedElements);
        Collections.reverse(sortedList);
        return sortedList;
    }

    private List<EfsElement> sortParentsFirst(Collection<EfsElement> unsortedElements) {
        Map<Long, EfsElement> idToNodeMap = new HashMap<>();
        for (EfsElement node : unsortedElements) {
            idToNodeMap.put(node.getId(), node);
        }

        return createTreeParentFirst(unsortedElements, idToNodeMap);
    }

    private List<EfsElement> createTreeParentFirst(Collection<EfsElement> unsortedElements,
            Map<Long, EfsElement> idToNodeMap) {
        List<EfsElement> sortedList = new ArrayList<>();
        for (EfsElement node : unsortedElements) {
            addElement(idToNodeMap, sortedList, node);
        }

        return sortedList;
    }

    private void addElement(Map<Long, EfsElement> idToNodeMap, Collection<EfsElement> sortedList, EfsElement node) {
        if (sortedList.contains(node)) {
            return;
        }

        EfsElement parent = idToNodeMap.get(node.getParentId());
        if (parent != null) {
            addElement(idToNodeMap, sortedList, parent);
        }

        sortedList.add(node);
    }

    private EfsElement createEfsElement(Long parentId, EfsElementMara efsElementMara, int quantity, String quantityUnit,
            String ap, Long vehiclePartListId) {
        EfsElement efsElement = new EfsElement();

        efsElement.setParentId(parentId);
        efsElement.setEfsElementMara(efsElementMara);
        efsElement.setVehiclePartListId(vehiclePartListId);
        efsElement.setEntityChange(true);
        efsElement.setQuantity(quantity);
        efsElement.setQuantityUnit(quantityUnit);
        efsElement.setAp(ap);

        return efsElement;
    }

    private EfsElementMara createEfsElementMara(String description1De, String partNumber, Long vehiclePartListId) {
        EfsElementMara mara = new EfsElementMara();

        mara.setDescription1De(description1De);
        mara.setPartNumber(partNumber);
        mara.setVehiclePartListId(vehiclePartListId);

        StringBuilder stringBuilder = new StringBuilder();

        if (partNumber.equals(SpecialPartNumberType.GAP.getLabel()) || partNumber.equals(
                SpecialPartNumberType.NO_MARA.getLabel())) {
            return mara;
        }

        int partNumberCharIndex = 1;
        for (int index = 0; index <= partNumber.length(); ) {
            if (stringBuilder.length() % 4 == 3) {
                index = setPartNumber(partNumber, mara, stringBuilder, partNumberCharIndex, index);

                stringBuilder.delete(0, index);
                partNumberCharIndex++;
                continue;
            }

            stringBuilder.append(partNumber.charAt(index++));

            if (index == partNumber.length() && partNumberCharIndex == 4) {
                index++;

                mara.setPartNumberIndex(stringBuilder.toString());
            }
        }

        return mara;
    }

    private int setPartNumber(String partNumber, EfsElementMara mara, StringBuilder stringBuilder,
            int partNumberCharIndex, int index) {
        if (partNumberCharIndex == 1) {
            mara.setPartNumberVornummer(stringBuilder.toString());
        } else if (partNumberCharIndex == 2) {
            mara.setPartNumberMittelgruppe(stringBuilder.toString());
        } else if (partNumberCharIndex == 3) {
            mara.setPartNumberEndNumber(stringBuilder.toString());
            if (index == partNumber.length()) {
                index++;
            }
        }

        return index;
    }
}