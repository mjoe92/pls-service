package de.vw.paso.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EfsElementResolver {

    private static final Logger LOG = LoggerFactory.getLogger(EfsElementResolver.class);

    private static final Map<Long, EfsElementDTO> EFS_ELEMENT_BY_ID = new HashMap<>();
    private static final Map<Long, Collection<Long>> EFS_ELEMENT_IDS_BY_MARA_ID = new HashMap<>();
    private static final Map<Long, Long> MARA_ID_BY_EFS_ELEMENT_ID = new HashMap<>();
    private static final Map<Long, Set<EfsElementDTO>> VEHICLE_PART_LIST_ID_TO_ITS_ELEMENTS_MAP = new HashMap<>();

    private static final EfsWeightCalculator CALCULATOR = new EfsWeightCalculator();

    private static final Collection<IEfsElementResolverListener> LISTENERS = new ArrayList<>(2);

    private EfsElementResolver() {
        throw new IllegalArgumentException("Util class");
    }

    public static void addListener(IEfsElementResolverListener listener) {
        LISTENERS.add(listener);
    }

    public static void cleanPartList(VehiclePartListDTO partList) {
        Collection<EfsElementDTO> allElementsInPartList = VEHICLE_PART_LIST_ID_TO_ITS_ELEMENTS_MAP.remove(
                partList.getId());
        if (allElementsInPartList == null) {
            return;
        }

        for (EfsElementDTO node : allElementsInPartList) {
            EFS_ELEMENT_BY_ID.remove(node.getId());
            EFS_ELEMENT_IDS_BY_MARA_ID.remove(node.getEfsElementMara().getId());
            MARA_ID_BY_EFS_ELEMENT_ID.remove(node.getId());
        }
    }

    public static List<Long> getAllElementsInHierarchy(EfsElementDTO node) {
        List<Long> ids = new ArrayList<>();
        addChildrenIds(ids, node);
        return ids;
    }

    public static EfsElementDTO getElement(Long id) {
        EfsElementDTO element = EFS_ELEMENT_BY_ID.get(id);
        if (element == null) {
            return null;
        }

        element.setParent(EFS_ELEMENT_BY_ID.get(element.getParentId()));
        return element;
    }

    public static Collection<EfsElementDTO> getElementsInPartList(Long partListId) {
        return VEHICLE_PART_LIST_ID_TO_ITS_ELEMENTS_MAP.get(partListId);
    }

    public static Set<EfsElementDTO> getElementsInPartList(VehiclePartListDTO partList) {
        return VEHICLE_PART_LIST_ID_TO_ITS_ELEMENTS_MAP.get(partList.getId());
    }

    public static Map<Long, Double> registerElements(Collection<EfsElementDTO> newEfsElements) {
        Collection<Long> elementIdsToRefresh = new TreeSet<>();

        Collection<Long> partListsToRecalculate = new HashSet<>();

        // First pass: Clean up old elements and their relation.
        // If the new element has fewer children for whatever reason, we move the missing old children over.
        // Note that this was there before; I just fixed it. So I do not know if this is correct behavior.
        for (EfsElementDTO newElement : newEfsElements) {
            EfsElementDTO oldElement = EFS_ELEMENT_BY_ID.get(newElement.getId());

            if (newElement.getChildren() == null) {
                newElement.setChildren(new ArrayList<>());
            }

            if (oldElement == null) {
                continue;
            }

            if (oldElement.getVehiclePartListId() != null) {
                partListsToRecalculate.add(oldElement.getVehiclePartListId());
            }

            Long parentId = oldElement.getParentId();
            if (parentId != null) {
                EfsElementDTO oldParent = EFS_ELEMENT_BY_ID.get(parentId);
                if (oldParent != null && oldParent.getChildren() != null) {
                    oldParent.getChildren().remove(oldElement);
                }
            }

            for (EfsElementDTO oldElementChild : oldElement.getChildren()) {
                if (newElement.getChildren().stream().noneMatch(e -> e.getId().equals(oldElementChild.getId()))) {
                    newElement.getChildren().add(oldElementChild);

                    oldElementChild.setParent(newElement);
                    oldElementChild.setParentId(newElement.getId());
                }
            }

            oldElement.setChildren(new ArrayList<>());
            oldElement.setParent(null);
            oldElement.setParentId(null);
        }

        // Second pass: We register (put) the new elements and override all old element references.
        for (EfsElementDTO efsElement : newEfsElements) {
            registerNode(efsElement, elementIdsToRefresh);
        }

        // Third pass: Fix new element to parent relation if needed.
        for (EfsElementDTO newElement : newEfsElements) {
            partListsToRecalculate.add(newElement.getVehiclePartListId());

            initializeParentRelationship(newElement);

            elementIdsToRefresh.add(newElement.getId());
            if (newElement.getParentId() != null) {
                elementIdsToRefresh.add(newElement.getParentId());
            }
        }

        // There really should be just one part list here, but we keep the old logic for now.
        // VW said that there is no possibility to move EfsElements around part lists or other shenanigans.
        Map<Long, Double> newWeights = new HashMap<>();
        for (Long vehiclePartListId : partListsToRecalculate) {
            LOG.info("Calculate weight for part list id: {}", vehiclePartListId);
            long startCalc = System.currentTimeMillis();

            double newWeight = calculateWeight(vehiclePartListId, elementIdsToRefresh);
            newWeights.put(vehiclePartListId, newWeight);

            if (LOG.isInfoEnabled()) {
                LOG.info("Completed weight calculation in: {} ms. Weight is: {} kg",
                        System.currentTimeMillis() - startCalc, newWeight / 1000);
            }
        }

        LinkedHashSet<EfsElementDTO> elements = elementIdsToRefresh.stream().map(EFS_ELEMENT_BY_ID::get)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        fireEfsElementUpdate(elements);

        return newWeights;
    }

    public static void removeListener(IEfsElementResolverListener listener) {
        LISTENERS.remove(listener);
    }

    private static void addChildrenIds(List<Long> ids, EfsElementDTO node) {
        if (node != null) {
            ids.add(node.getId());
            for (EfsElementDTO child : node.getChildren()) {
                addChildrenIds(ids, child);
            }
        }
    }

    private static double calculateWeight(Long vehiclePartListId, Collection<Long> elementIdsToRefresh) {
        double partListWeight = 0;
        Collection<EfsElementDTO> elements = VEHICLE_PART_LIST_ID_TO_ITS_ELEMENTS_MAP.get(vehiclePartListId);
        for (EfsElementDTO element : elements) {
            if (element.getParent() == null) {
                partListWeight += CALCULATOR.calculateWeight(element, elementIdsToRefresh);
            }
        }

        return partListWeight;
    }

    private static void fireEfsElementUpdate(LinkedHashSet<EfsElementDTO> elements) {
        for (IEfsElementResolverListener listener : LISTENERS) {
            listener.onEfsElementUpdate(elements);
        }
    }

    private static void initializeParentRelationship(EfsElementDTO element) {
        if (element.getParentId() == null) {
            return;
        }

        EfsElementDTO parent = EFS_ELEMENT_BY_ID.get(element.getParentId());
        if (parent == null) {
            return;
        }

        element.setParent(parent);
        element.setParentId(parent.getId());

        if (parent.getChildren() == null) {
            parent.setChildren(new ArrayList<>());
        }

        if (!parent.getChildren().contains(element)) {
            parent.getChildren().add(element);
        }
    }

    private static void registerMara(EfsElementDTO oldElement, EfsElementDTO newElement) {
        EfsElementMaraDTO mara = newElement.getEfsElementMara();
        Collection<Long> maraUses = EFS_ELEMENT_IDS_BY_MARA_ID.get(mara.getId());
        if (maraUses == null) {
            maraUses = new ArrayList<>();
        }

        Long oldMaraId = MARA_ID_BY_EFS_ELEMENT_ID.get(newElement.getId());
        if (oldMaraId != null) {
            Collection<Long> oldMaraUses = EFS_ELEMENT_IDS_BY_MARA_ID.get(oldMaraId);
            oldMaraUses.remove(oldElement.getId());
        }

        maraUses.add(newElement.getId());
        EFS_ELEMENT_IDS_BY_MARA_ID.put(mara.getId(), maraUses);
    }

    private static void registerNode(EfsElementDTO element, Collection<Long> elementIdsToRefresh) {
        EfsElementDTO oldElement = EFS_ELEMENT_BY_ID.put(element.getId(), element);

        Collection<EfsElementDTO> allElementsInPartList = VEHICLE_PART_LIST_ID_TO_ITS_ELEMENTS_MAP.computeIfAbsent(
                element.getVehiclePartListId(), k -> new LinkedHashSet<>());

        if (element.getId() != null) {
            allElementsInPartList.removeIf(e -> element.getId().equals(e.getId()));
        }
        allElementsInPartList.add(element);

        synchronizeMara(oldElement, element, elementIdsToRefresh);
    }

    private static void synchronizeMara(EfsElementDTO oldElement, EfsElementDTO newElement,
            Collection<Long> elementsToRefresh) {
        registerMara(oldElement, newElement);
        if (oldElement != null) {
            updateElementsWithSameMara(newElement, elementsToRefresh);
        }

        MARA_ID_BY_EFS_ELEMENT_ID.put(newElement.getId(), newElement.getEfsElementMara().getId());
    }

    private static void updateElementsWithSameMara(EfsElementDTO element, Collection<Long> elementsToRefresh) {
        EfsElementMaraDTO newMara = element.getEfsElementMara();
        Collection<Long> efsElementIds = EFS_ELEMENT_IDS_BY_MARA_ID.get(newMara.getId());
        for (Long efsElementId : efsElementIds) {
            if (!element.getId().equals(efsElementId)) {
                EfsElementDTO efsElement = EFS_ELEMENT_BY_ID.get(efsElementId);
                efsElement.setEfsElementMara(newMara);
                elementsToRefresh.add(efsElement.getId());
            }
        }
    }
}
