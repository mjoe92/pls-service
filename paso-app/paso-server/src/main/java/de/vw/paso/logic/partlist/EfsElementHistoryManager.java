package de.vw.paso.logic.partlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.EfsElementHistoryMapper;
import de.vw.paso.mapper.EfsElementMapper;
import de.vw.paso.mapper.EfsElementMaraHistoryMapper;
import de.vw.paso.mapper.EfsElementMaraMapper;
import de.vw.paso.partlist.domain.AbstractEfsElement;
import de.vw.paso.partlist.domain.AbstractEfsElementMara;
import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementHistory;
import de.vw.paso.partlist.domain.EfsElementMara;
import de.vw.paso.partlist.domain.EfsElementMaraHistory;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.repository.partlist.EfsElementHistoryRepository;
import de.vw.paso.repository.partlist.EfsElementHistoryToGoTo2DTO;
import de.vw.paso.repository.partlist.EfsElementMaraHistoryRepository;
import de.vw.paso.repository.partlist.EfsElementMaraRepository;
import de.vw.paso.repository.partlist.EfsElementRepository;
import de.vw.paso.repository.partlist.MaraHistoryElementToGoTo2DTO;
import de.vw.paso.repository.partlist.VehiclePartListRepository;
import de.vw.paso.service.partlist.DeleteNonPersistedEfsElementException;
import de.vw.paso.service.partlist.efselementhistory.EfsElementAndMaraAndHistoryDTO;
import de.vw.paso.service.partlist.efselementhistory.EfsElementAndMaraAndHistoryListDTO;
import de.vw.paso.util.DataNotFoundException;
import de.vw.paso.utility.Pair;
import org.springframework.stereotype.Service;

@Service
public class EfsElementHistoryManager {

    private final EfsElementRepository efsElementRepository;
    private final EfsElementHistoryRepository efsElementHistoryRepository;
    private final EfsElementMaraRepository efsElementMaraRepository;
    private final EfsElementMaraHistoryRepository efsElementMaraHistoryRepository;
    private final VehiclePartListRepository vehiclePartListRepository;
    private final UserManager userManager;

    public EfsElementHistoryManager(EfsElementRepository efsElementRepository,
            EfsElementHistoryRepository efsElementHistoryRepository, EfsElementMaraRepository efsElementMaraRepository,
            EfsElementMaraHistoryRepository efsElementMaraHistoryRepository,
            VehiclePartListRepository vehiclePartListRepository, UserManager userManager) {
        this.efsElementRepository = efsElementRepository;
        this.efsElementHistoryRepository = efsElementHistoryRepository;
        this.efsElementMaraRepository = efsElementMaraRepository;
        this.efsElementMaraHistoryRepository = efsElementMaraHistoryRepository;
        this.vehiclePartListRepository = vehiclePartListRepository;
        this.userManager = userManager;
    }

    public EfsElementAndMaraAndHistoryListDTO loadHistorie(long efsElementId) {
        Collection<AbstractEfsElement> efsElements = new ArrayList<>(
                efsElementHistoryRepository.findByEfsElementIdOrderByRevision(efsElementId));
        efsElements.add(efsElementRepository.findById(efsElementId).orElseThrow());

        List<EfsElementAndMaraAndHistoryDTO> dtos = connectEfsHistory(efsElements, false);
        return new EfsElementAndMaraAndHistoryListDTO(dtos);
    }

    public EfsElementAndMaraAndHistoryListDTO loadRevisions(long vehiclePartListId) {
        Collection<EfsElementHistory> histories = efsElementHistoryRepository.findAllByVehiclePartListIdOrderByRevision(
                vehiclePartListId);
        Map<Long, List<EfsElementHistory>> historyList = histories.stream()
                .collect(Collectors.toMap(e -> e.getEfsElement().getId(), List::of, this::mergeLists));

        Collection<EfsElement> efsElementsByVehicle = efsElementHistoryRepository.findAllEfsElementByEfsElementVehiclePartListId(
                vehiclePartListId);
        Collection<EfsElement> efsElementsMyMaraByVehicle = efsElementMaraHistoryRepository.findAllEfsElementByEfsElementVehiclePartListId(
                vehiclePartListId);

        Collection<AbstractEfsElement> allEfs = new ArrayList<>(
                efsElementsByVehicle.size() + efsElementsMyMaraByVehicle.size());
        for (EfsElement efsElement : efsElementsByVehicle) {
            Collection<EfsElementHistory> efsElementHistories = historyList.get(efsElement.getId());
            if (efsElementHistories != null) {
                allEfs.addAll(efsElementHistories);
            }

            allEfs.add(efsElement);
        }

        for (EfsElement efsElement : efsElementsMyMaraByVehicle) {
            Collection<EfsElementHistory> efsElementHistories = historyList.get(efsElement.getId());
            if (efsElementHistories != null) {
                allEfs.addAll(efsElementHistories);
            }

            allEfs.add(efsElement);
        }

        List<EfsElementAndMaraAndHistoryDTO> dtos = connectEfsHistory(allEfs, true);
        return new EfsElementAndMaraAndHistoryListDTO(dtos);
    }

    private <T> List<T> mergeLists(List<T> list1, List<T> list2) {
        List<T> mergedList = new ArrayList<>(list1.size() + list2.size());
        mergedList.addAll(list1);
        mergedList.addAll(list2);
        return mergedList;
    }

    private EfsElementAndMaraAndHistoryDTO extractDataFromAbstractElements(AbstractEfsElement abstractEfsElement,
            AbstractEfsElementMara abstractEfsElementMara) {
        EfsElementAndMaraAndHistoryDTO efsElementAndMaraAndHistoryDTO = new EfsElementAndMaraAndHistoryDTO();
        if (abstractEfsElement instanceof EfsElement efsElement) {
            efsElementAndMaraAndHistoryDTO.setEfsElementDTO(EfsElementMapper.toDto(efsElement));
        } else if (abstractEfsElement instanceof EfsElementHistory history) {
            efsElementAndMaraAndHistoryDTO.setEfsElementHistoryDTO(EfsElementHistoryMapper.toDto(history));
        }

        if (abstractEfsElementMara instanceof EfsElementMara mara) {
            efsElementAndMaraAndHistoryDTO.setEfsElementMaraDTO(EfsElementMaraMapper.toDto(mara));
        } else if (abstractEfsElementMara instanceof EfsElementMaraHistory maraHistory) {
            efsElementAndMaraAndHistoryDTO.setEfsElementMaraHistoryDTO(EfsElementMaraHistoryMapper.toDto(maraHistory));
        }

        return efsElementAndMaraAndHistoryDTO;
    }

    public Long historicizeEfsElements(EfsElement... efsElements) {
        long partListRevision = 0;

        for (EfsElement efsElement : efsElements) {
            if (partListRevision == 0) {
                partListRevision = increaseRevision(efsElement.getVehiclePartListId());
            }

            Long elementId = efsElement.getId();
            if (elementId == null) {
                continue;
            }

            EfsElement foundElement = efsElementRepository.findById(elementId).orElseThrow();
            EfsElementHistory efsElementHistory = EfsElementHistoryMapper.toEntity(foundElement,
                    userManager.getCurrentUserId());

            efsElementHistoryRepository.save(efsElementHistory);
        }

        return partListRevision;
    }

    public void historicizeEfsElements(Collection<EfsElement> oldEfsElements) {
        Collection<EfsElementHistory> newHistoryElements = new ArrayList<>();
        Collection<Long> maraIds = new HashSet<>();
        for (EfsElement efsElement : oldEfsElements) {
            if (efsElement == null || efsElement.getId() == null) {
                continue;
            }

            Long efsElementMaraId = efsElement.getEfsElementMara().getId();
            if (efsElementMaraId != null) {
                maraIds.add(efsElementMaraId);
            }

            EfsElementHistory efsElementHistory = EfsElementHistoryMapper.toEntity(efsElement,
                    userManager.getCurrentUserId());
            newHistoryElements.add(efsElementHistory);
        }

        Map<Long, EfsElementMara> maraByIdMap = new HashMap<>();
        Collection<EfsElementMara> loadedMara = efsElementMaraRepository.findAllById(maraIds);
        for (EfsElementMara mara : loadedMara) {
            maraByIdMap.put(mara.getId(), mara);
        }

        for (EfsElementHistory history : newHistoryElements) {
            EfsElementMara efsElementMara = history.getEfsElement().getEfsElementMara();
            if (efsElementMara != null) {
                history.setEfsElementMara(maraByIdMap.get(efsElementMara.getId()));
            }
        }

        efsElementHistoryRepository.saveAll(newHistoryElements);
    }

    public long historicizeEfsElementsHierarchical(List<EfsElement> efsElements)
            throws DeleteNonPersistedEfsElementException {
        for (EfsElement efsElement : efsElements) {
            if (efsElement.getId() == null) {
                throw new DeleteNonPersistedEfsElementException("Element is not saved in database", efsElement);
            }

            recursiveEfsElementPersist(efsElement.getId());
        }

        return increaseRevision(efsElements.getFirst().getVehiclePartListId());
    }

    public long historicizeEfsElementsCopy(Long vehiclePartListId) {
        return increaseRevision(vehiclePartListId);
    }

    public void historicizeEfsElementMara(EfsElementMara efsElementMara) {
        Long maraId = efsElementMara.getId();
        if (maraId == null) {
            return;
        }

        EfsElementMara dbEfsElementMara = efsElementMaraRepository.findById(maraId)
                .orElseThrow(DataNotFoundException::new);
        EfsElementMaraHistory efsElementMaraHistory = EfsElementMaraHistoryMapper.toEntity(dbEfsElementMara,
                userManager.getCurrentUserId());

        efsElementMaraHistoryRepository.save(efsElementMaraHistory);
    }

    public Long historicizeEfsElementMaras(Collection<Pair<EfsElement, EfsElementMara>> efsElementsMaras) {
        Collection<Long> maraToHistoryIds = efsElementsMaras.stream().filter(element -> element.second() != null)
                .map(element -> element.second().getId()).toList();

        Collection<EfsElementMara> loadedMaras = efsElementMaraRepository.findAllById(maraToHistoryIds);
        Map<Long, EfsElementMara> loadedMaraByIdMap = loadedMaras.stream()
                .collect(Collectors.toMap(EfsElementMara::getId, Function.identity(), (first, second) -> second));

        Collection<EfsElementMaraHistory> maraHistoryList = new ArrayList<>();
        Long increaseRevisionOfPartList = null;
        for (Pair<EfsElement, EfsElementMara> pair : efsElementsMaras) {
            EfsElementMara efsElementMara = pair.second();
            if (efsElementMara.getId() == null) {
                continue;
            }

            EfsElementMara dbEfsElementMara = loadedMaraByIdMap.get(efsElementMara.getId());
            if (dbEfsElementMara == null) {
                throw new DataNotFoundException("Mara not found");
            }

            increaseRevisionOfPartList = efsElementMara.getVehiclePartListId();

            EfsElementMaraHistory efsElementMaraHistory = EfsElementMaraHistoryMapper.toEntity(dbEfsElementMara,
                    userManager.getCurrentUserId());
            maraHistoryList.add(efsElementMaraHistory);
        }

        efsElementMaraHistoryRepository.saveAll(maraHistoryList);

        if (increaseRevisionOfPartList == null) {
            return 0L;
        }

        return increaseRevision(increaseRevisionOfPartList);
    }

    private List<EfsElementAndMaraAndHistoryDTO> connectEfsHistory(Collection<AbstractEfsElement> efsElements,
            boolean isRevision) {
        Collection<Long> efsElementMaraIds = efsElements.stream().map(e -> e.getEfsElementMara().getId())
                .collect(Collectors.toSet());

        Collection<EfsElementMara> allElementMaras = efsElementMaraRepository.findAllById(efsElementMaraIds);
        Map<Long, EfsElementMara> efsElementMaraMap = allElementMaras.stream()
                .collect(Collectors.toMap(EfsElementMara::getId, Function.identity()));

        Collection<EfsElementMaraHistory> revisionOrderedHistories = efsElementMaraHistoryRepository.findAllByEfsElementMaraIdInOrderByRevision(
                efsElementMaraIds);
        Map<Long, List<EfsElementMaraHistory>> efsElementMaraHistoryMap = revisionOrderedHistories.stream()
                .collect(Collectors.toMap(e -> e.getEfsElementMara().getId(), List::of, this::mergeLists));

        Long efsElementMaraIdBefore = null;
        TreeMap<Long, EfsElementAndMaraAndHistoryDTO> mapRevEfs = new TreeMap<>();
        Map<Long, EfsElementAndMaraAndHistoryDTO> allRevisions = new HashMap<>();
        for (AbstractEfsElement abstractEfsElement : efsElements) {
            Long efsElementMaraId = abstractEfsElement.getEfsElementMara().getId();
            EfsElementMara efsElementMara = efsElementMaraMap.get(efsElementMaraId);
            Collection<EfsElementMaraHistory> efsElementMaraHistories = efsElementMaraHistoryMap.get(efsElementMaraId);

            Collection<AbstractEfsElementMara> efsMaraHistories = new ArrayList<>();
            if (efsElementMaraHistories != null) {
                efsMaraHistories.addAll(efsElementMaraHistories);
            }
            efsMaraHistories.add(efsElementMara);
            efsMaraHistories.add(abstractEfsElement.getEfsElementMara());

            if (efsElementMaraIdBefore != null && !efsElementMaraIdBefore.equals(efsElementMaraId)) {
                // when MARA part numbers are changed, the history elements that no longer affect the part are deleted
                mapRevEfs.remove(abstractEfsElement.getRevision());
            }

            for (AbstractEfsElementMara abstractEfsElementMara : efsMaraHistories) {
                // link EfsElement to Mara
                EfsElementAndMaraAndHistoryDTO efsElementAndMaraAndHistoryDTO = extractDataFromAbstractElements(
                        abstractEfsElement, abstractEfsElementMara);
                if (isRevision) {
                    allRevisions.put(abstractEfsElement.getId(), efsElementAndMaraAndHistoryDTO);
                    continue;
                }

                Long revision = Math.max(abstractEfsElement.getRevision(), abstractEfsElementMara.getRevision());
                mapRevEfs.put(revision, efsElementAndMaraAndHistoryDTO);
            }

            efsElementMaraIdBefore = efsElementMaraId;
        }

        if (isRevision) {
            return new ArrayList<>(allRevisions.values());
        }

        return new ArrayList<>(mapRevEfs.values());
    }

    private Long increaseRevision(long vehiclePartListId) {
        VehiclePartList dbPartList = vehiclePartListRepository.findById(vehiclePartListId)
                .orElseThrow(() -> new DataNotFoundException("VehiclePArtList not found"));

        long incrementedRevision = dbPartList.getRevision() + 1;
        dbPartList.setRevision(incrementedRevision);
        return dbPartList.getRevision();
    }

    private void recursiveEfsElementPersist(long parentEfsElementId) {
        Collection<EfsElement> childEfsElements = efsElementRepository.findAllByParentId(parentEfsElementId);
        for (EfsElement childEfsElement : childEfsElements) {
            recursiveEfsElementPersist(childEfsElement.getId());
        }

        EfsElement parentEfsElement = efsElementRepository.findById(parentEfsElementId)
                .orElseThrow(() -> new DataNotFoundException("Parent EfsElement not found"));
        EfsElementHistory convertedHistory = EfsElementHistoryMapper.toEntity(parentEfsElement,
                userManager.getCurrentUserId());

        efsElementHistoryRepository.save(convertedHistory);
    }

    public Collection<EfsElement> revertToRevision(Long partListId, Long revision) {
        Map<Long, EfsElementMara> savedMaraMap = revertToRevisionMara(partListId, revision);

        Collection<EfsElementHistoryToGoTo2DTO> revertInfo = efsElementHistoryRepository.findHistoryElementsToGoTo2(
                partListId, revision);

        Collection<Long> efsElementsToLoad = new HashSet<>();
        Collection<Long> efsHistoryElementsToLoad = new HashSet<>();
        Map<Long, Long> efsToHistoryMap = new HashMap<>();

        for (EfsElementHistoryToGoTo2DTO element : revertInfo) {
            Long efsHistoryId = element.efsElementHistoryId();
            Long efsId = element.efsElementId();

            efsElementsToLoad.add(efsId);
            efsHistoryElementsToLoad.add(efsHistoryId);
            efsToHistoryMap.put(efsId, efsHistoryId);
        }

        Collection<EfsElementHistory> allHistories = efsElementHistoryRepository.findAllById(efsHistoryElementsToLoad);
        Map<Long, EfsElementHistory> efsHistoryMap = allHistories.stream()
                .collect(Collectors.toMap(EfsElementHistory::getId, Function.identity(), (first, second) -> second));

        List<EfsElement> efsElements = efsElementRepository.findAllById(efsElementsToLoad);
        for (EfsElement element : efsElements) {
            EfsElementHistory targetHistory = efsHistoryMap.get(efsToHistoryMap.get(element.getId()));
            EfsElementHistoryMapper.map(element, targetHistory);

            // if there are reverted Maras, we have to set them to the efselement, else we keep the old one
            EfsElementMara efsElementMara = savedMaraMap.get(element.getEfsElementMara().getId());
            if (efsElementMara != null) {
                element.setEfsElementMara(efsElementMara);
            }
        }

        Long vehiclePartListId = efsElements.getFirst().getVehiclePartListId();
        VehiclePartList vehiclePartList = vehiclePartListRepository.findById(vehiclePartListId).orElseThrow();
        vehiclePartList.setRevision(revision);

        vehiclePartListRepository.save(vehiclePartList);

        Collection<EfsElement> savedElements = efsElementRepository.saveAll(efsElements);

        //todo: delete by partListId and revision
        Collection<EfsElementHistory> historyElementsToDelete = efsElementHistoryRepository.findAllByVehiclePartListIdAndRevisionGreaterThanEqual(
                partListId, revision);
        efsElementHistoryRepository.deleteAll(historyElementsToDelete);

        //todo: delete by partListId and revision
        Collection<EfsElementMaraHistory> historyMaraElementsToDelete = efsElementMaraHistoryRepository.findAllByVehiclePartListIdAndRevisionGreaterThanEqual(
                partListId, revision);
        efsElementMaraHistoryRepository.deleteAll(historyMaraElementsToDelete);

        return savedElements;
    }

    private Map<Long, EfsElementMara> revertToRevisionMara(Long partListId, Long revision) {
        Collection<MaraHistoryElementToGoTo2DTO> maraRevertInfo = efsElementMaraHistoryRepository.findHistoryElementsToGoTo2(
                partListId, revision);
        Collection<Long> maraToLoad = new HashSet<>();
        Collection<Long> maraHistoryToLoad = new HashSet<>();
        Map<Long, Long> maraToHistoryMap = new HashMap<>();

        for (MaraHistoryElementToGoTo2DTO data : maraRevertInfo) {
            Long maraHistoryId = data.efsElementMaraHistoryId();
            Long maraId = data.efsElementMaraId();

            maraToLoad.add(maraId);
            maraHistoryToLoad.add(maraHistoryId);
            maraToHistoryMap.put(maraId, maraHistoryId);
        }

        Collection<EfsElementMaraHistory> maraHistoryElements = efsElementMaraHistoryRepository.findAllById(
                maraHistoryToLoad);
        Map<Long, EfsElementMaraHistory> efsMaraHistoryMap = maraHistoryElements.stream().collect(
                Collectors.toMap(EfsElementMaraHistory::getId, Function.identity(), (first, second) -> second));

        Map<Long, EfsElementMara> savedMaraMap = new HashMap<>();
        Collection<EfsElementMara> efsMaraElements = efsElementMaraRepository.findAllById(maraToLoad);
        for (EfsElementMara element : efsMaraElements) {
            EfsElementMaraHistory targetHistory = efsMaraHistoryMap.get(maraToHistoryMap.get(element.getId()));
            EfsElementMaraHistoryMapper.map(element, targetHistory);
            savedMaraMap.put(element.getId(), element);
        }

        efsElementMaraRepository.saveAll(efsMaraElements);
        return savedMaraMap;
    }
}