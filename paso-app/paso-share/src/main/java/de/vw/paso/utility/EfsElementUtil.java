package de.vw.paso.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.FilteredOutEfsElement;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class EfsElementUtil {

    private static final String REGEX_EMPTY_END = "\\. {1,2}$";

    private EfsElementUtil() {
    }

    public static Collection<EfsElementDTO> sortParentsFirst(Collection<EfsElementDTO> unsortedElements) {
        Map<Long, EfsElementDTO> idToNodeMap = new HashMap<>(unsortedElements.size());
        for (EfsElementDTO node : unsortedElements) {
            idToNodeMap.put(node.getId(), node);
        }

        return createTreeParentFirst(unsortedElements, idToNodeMap);
    }

    private static Collection<EfsElementDTO> createTreeParentFirst(Collection<EfsElementDTO> unsortedElements,
            Map<Long, EfsElementDTO> idToNodeMap) {
        Collection<EfsElementDTO> sortedList = new ArrayList<>();
        for (EfsElementDTO node : unsortedElements) {
            addElement(idToNodeMap, sortedList, node);
        }

        return sortedList;
    }

    private static void addElement(Map<Long, EfsElementDTO> idToNodeMap, Collection<EfsElementDTO> sortedList,
            EfsElementDTO node) {
        if (sortedList.contains(node)) {
            return;
        }

        EfsElementDTO parent = idToNodeMap.get(node.getParentId());
        if (parent != null) {
            addElement(idToNodeMap, sortedList, parent);
        }

        sortedList.add(node);
    }

    public static List<EfsElementDTO> sortByCheckingStructure(Collection<EfsElementDTO> unsortedElements) {
        return sortByCheckingStructure(unsortedElements, null);
    }

    public static List<EfsElementDTO> sortByCheckingStructure(Collection<EfsElementDTO> unsortedElements,
            EfsElementDTO root) {
        if (unsortedElements == null) {
            return null;
        }

        EfsElementDTO nullPlaceholder = root == null ? new EfsElementDTO() : root;
        Map<EfsElementDTO, List<EfsElementDTO>> byParent = unsortedElements.stream().collect(Collectors.groupingBy(
                obj -> (obj.getParent() == null || !unsortedElements.contains(obj.getParent()) ? nullPlaceholder
                        : obj.getParent()), Collectors.toList()));

        List<EfsElementDTO> ordered = new ArrayList<>();
        Queue<EfsElementDTO> processor = new LinkedList<>();

        if (byParent.get(nullPlaceholder) != null) {
            processor.addAll(byParent.get(nullPlaceholder));
        }

        while (!processor.isEmpty()) {
            EfsElementDTO tmp = processor.poll();
            byParent.getOrDefault(tmp, Collections.emptyList()).stream()
                    .sorted((o1, o2) -> compareDescendingNullsFirst(o1.getTisSort(), o2.getTisSort()))
                    .forEach(processor::add);
            ordered.add(tmp);
        }

        return ordered;
    }

    private static <T extends Comparable<T>> int compareDescendingNullsFirst(T object1, T object2) {
        if (object1 == null && object2 == null) {
            return 0;
        }
        if (object1 == null) {
            return -1;
        }
        if (object2 == null) {
            return 1;
        }

        return object2.compareTo(object1);
    }

    public static boolean checkParentsFirst(List<EfsElementDTO> elements) {
        for (int index = 0; index < elements.size(); index++) {
            EfsElementDTO efsElement = elements.get(index);
            if (efsElement.getParent() == null) {
                continue;
            }

            int indexOfParent = elements.indexOf(efsElement.getParent());
            if (indexOfParent >= index) {
                return false;
            }
        }

        return true;
    }

    public static String convertPartNumberString(String value) {
        if (value == null) {
            return StringConstant.EMPTY;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < value.length(); ) {
            char charAt = stringBuilder.length() % 4 == 3 ? StringConstant.DOT_CHAR : value.charAt(index++);
            stringBuilder.append(charAt);
        }

        return stringBuilder.toString().replaceFirst(REGEX_EMPTY_END, StringConstant.EMPTY);
    }

    public static EfsElementDTO copyEfsElement(EfsElementDTO efsElement) {
        EfsElementDTO newEfsElement = PartListFactory.createEfsElement();

        newEfsElement.setId(efsElement.getId());
        newEfsElement.setParentId(efsElement.getParentId());
        newEfsElement.setParent(efsElement.getParent());
        newEfsElement.setVehiclePartListId(efsElement.getVehiclePartListId());
        newEfsElement.setRevision(efsElement.getRevision());
        newEfsElement.setDeleted(efsElement.getDeleted());
        newEfsElement.setNodeId(efsElement.getNodeId());
        newEfsElement.setNodeLabel(efsElement.getNodeLabel());
        newEfsElement.setBomNumber(efsElement.getBomNumber());
        newEfsElement.setProduct(efsElement.getProduct());
        newEfsElement.setGap(efsElement.getGap());
        newEfsElement.setTisSort(efsElement.getTisSort());
        newEfsElement.setNodeType(efsElement.getNodeType());
        newEfsElement.setNodeValueParent(efsElement.getNodeValueParent());
        newEfsElement.setNodeValue(efsElement.getNodeValue());
        newEfsElement.setQuantity(efsElement.getQuantity());
        newEfsElement.setQuantityUnit(efsElement.getQuantityUnit());
        newEfsElement.setQuantityUnitExtended(efsElement.getQuantityUnitExtended());
        newEfsElement.setWeightControlFlag(efsElement.getWeightControlFlag());
        newEfsElement.setProductStructure(efsElement.getProductStructure());
        newEfsElement.setPositionVariant(efsElement.getPositionVariant());
        newEfsElement.setDeletionFlag(efsElement.getDeletionFlag());
        newEfsElement.setCostGroup(efsElement.getCostGroup());
        newEfsElement.setTiWhImportId(efsElement.getTiWhImportId());
        newEfsElement.setAp(efsElement.getAp());
        newEfsElement.setPrNumberRule(efsElement.getPrNumberRule());
        newEfsElement.setBeginDateKey(efsElement.getBeginDateKey());
        newEfsElement.setEndDateKey(efsElement.getEndDateKey());
        newEfsElement.setBeginDate(efsElement.getBeginDate());
        newEfsElement.setEndDate(efsElement.getEndDate());
        newEfsElement.setPartType(efsElement.getPartType());
        newEfsElement.setAggregate(efsElement.getAggregate());
        newEfsElement.setSetKey(efsElement.getSetKey());
        newEfsElement.setEfsElementMara(efsElement.getEfsElementMara());
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
        newEfsElement.setCogX(efsElement.getCogX());
        newEfsElement.setCogY(efsElement.getCogY());
        newEfsElement.setCogZ(efsElement.getCogZ());
        newEfsElement.setTimestampCreate(efsElement.getTimestampCreate());
        newEfsElement.setTimestampChange(efsElement.getTimestampChange());
        newEfsElement.setUserChange(efsElement.getUserChange());
        newEfsElement.setUserCreate(efsElement.getUserCreate());
        newEfsElement.setChildren(efsElement.getChildren());

        return newEfsElement;
    }

    public static EfsElementDTO copyEfsElementHierarchy(EfsElementDTO efsElement,
            Consumer<EfsElementDTO> parentCopyCallback, Consumer<EfsElementDTO> childCopyCallback) {
        EfsElementDTO copyElement = copyEfsElement(efsElement);

        Collection<EfsElementDTO> children = efsElement.getChildren();
        copyElement.setChildren(new ArrayList<>(children.size()));

        for (EfsElementDTO child : children) {
            EfsElementDTO copyChild = child.isLeaf() ? copyEfsElement(child)
                    : copyEfsElementHierarchy(child, childCopyCallback, childCopyCallback);
            copyElement.getChildren().add(copyChild);
            copyChild.setParent(copyElement);
            childCopyCallback.accept(copyChild);
        }
        parentCopyCallback.accept(copyElement);
        return copyElement;
    }

    public static EfsElement filteredOutPartToEfsElement(FilteredOutEfsElement filteredOutEfsElement) {
        EfsElement efsElement = new EfsElement();

        efsElement.setBomNumber(filteredOutEfsElement.getBomNumber());
        efsElement.setProduct(filteredOutEfsElement.getProduct());
        efsElement.setDeleted(filteredOutEfsElement.getDeleted());
        efsElement.setNodeId(filteredOutEfsElement.getNodeId());
        efsElement.setNodeLabel(filteredOutEfsElement.getNodeLabel());
        efsElement.setNodeLevel(filteredOutEfsElement.getNodeLevel());
        efsElement.setNodeType(filteredOutEfsElement.getNodeType());
        efsElement.setGap(filteredOutEfsElement.getGap());
        efsElement.setTisSort(filteredOutEfsElement.getTisSort());
        efsElement.setNodeValue(filteredOutEfsElement.getNodeValue());
        efsElement.setQuantity(filteredOutEfsElement.getQuantity());
        efsElement.setQuantityUnit(filteredOutEfsElement.getQuantityUnit());
        efsElement.setQuantityUnitExtended(filteredOutEfsElement.getQuantityUnitExtended());
        efsElement.setWeightControlFlag(filteredOutEfsElement.getWeightControlFlag());
        efsElement.setConstructionsGroup(filteredOutEfsElement.getConstructionsGroup());
        efsElement.setCostGroup(filteredOutEfsElement.getCostGroup());
        efsElement.setTiWhImportId(filteredOutEfsElement.getTiWhImportId());
        efsElement.setAp(filteredOutEfsElement.getAp());
        efsElement.setPrNumberRule(filteredOutEfsElement.getPrNumberRule());
        efsElement.setBeginDate(filteredOutEfsElement.getBeginDate());
        efsElement.setEndDate(filteredOutEfsElement.getEndDate());
        efsElement.setAggregate(filteredOutEfsElement.getAggregate());
        efsElement.setSetKey(filteredOutEfsElement.getSetKey());
        efsElement.setEfsElementMara(filteredOutEfsElement.getEfsElementMara());
        efsElement.setParentId(
                filteredOutEfsElement.getParent() != null ? filteredOutEfsElement.getParent().getId() : null);
        efsElement.setVehiclePartListId(filteredOutEfsElement.getVehiclePartListId());
        efsElement.setWahlweiseFall(filteredOutEfsElement.getWahlweiseFall());
        efsElement.setWahlweiseNr(filteredOutEfsElement.getWahlweiseNr());
        efsElement.setBaukasten(filteredOutEfsElement.getBaukasten());
        efsElement.setBaukastenNodeId(filteredOutEfsElement.getBaukastenNodeId());
        efsElement.setBaukastenStatus(filteredOutEfsElement.getBaukastenStatus());
        efsElement.setRevision(filteredOutEfsElement.getRevision());
        efsElement.setWorkPackageNumber(filteredOutEfsElement.getWorkPackageNumber());
        efsElement.setProcessStatus(filteredOutEfsElement.getProcessStatus());
        efsElement.setDmuRelevant(filteredOutEfsElement.getDmuRelevant());
        efsElement.setMaterialType(filteredOutEfsElement.getMaterialType());
        efsElement.setEarliestNs(filteredOutEfsElement.getEarliestNs());
        efsElement.setEarliestPvs(filteredOutEfsElement.getEarliestPvs());
        efsElement.setEarliestSop(filteredOutEfsElement.getEarliestSop());
        efsElement.setPActivationDate(filteredOutEfsElement.getPActivationDate());
        efsElement.setKonstructureDate(filteredOutEfsElement.getKonstructureDate());
        efsElement.setAvonStatus(filteredOutEfsElement.getAvonStatus());

        return efsElement;
    }
}