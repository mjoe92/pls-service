package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ListMultimap;

import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class GWSInspectorChecker extends AbstractInspectorChecker {

    private final Map<EfsElementDTO, Set<InspectorEntryType>> inspectorEntryTypeOfElement = new HashMap<>();

    @Override
    public void checkElements(Collection<EfsElementDTO> efsElements,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap, EfsElementDTO rootEfsElement,
        VehicleConfigDTO vehicleConfig) {
        Collection<EfsElementDTO> rootElements = new HashSet<>();
        if (rootEfsElement != null) {
            rootElements.add(rootEfsElement);
        }

        Collection<EfsElementDTO> leafElements = new HashSet<>();
        Collection<EfsElementDTO> notDeletedElements = new HashSet<>();
        Collection<EfsElementDTO> assemblies = new HashSet<>();

        inspectorEntryTypeOfElement.clear();

        categorizeElements(efsElements, rootElements, leafElements, notDeletedElements, assemblies);
        int roots = rootElements.size();
        if (roots != 1) {
            throw new IllegalArgumentException("Invalid number of root nodes: " + roots);
        }

        EfsElementDTO root = rootElements.stream().findFirst().orElseThrow();
        if (!isWeightControlFlagOfRootEmpty(root, resultMap)) {
            return;
        }

        checkForWeightControlFlagIncorrect(leafElements, resultMap);
        checkForWeightNotSet(notDeletedElements, resultMap);
        checkForBaukasten(root, resultMap);
        for (EfsElementDTO assembly : assemblies) {
            checkForIgnoredWeights(assembly, resultMap);
            checkWeightDifference(assembly, resultMap);
        }
    }

    private boolean isWeightControlFlagOfRootEmpty(EfsElementDTO rootElement,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap) {
        if (rootElement.getWeightControlFlag() == null) {
            return true;
        }

        addInspectorEntry(rootElement, rootElement, InspectorEntryType.GWS_INCORRECT, resultMap);

        return false;
    }

    private void categorizeElements(Collection<EfsElementDTO> efsElements, Collection<EfsElementDTO> rootElements,
        Collection<EfsElementDTO> leafElements, Collection<EfsElementDTO> notDeletedElements,
        Collection<EfsElementDTO> assemblyElements) {
        for (EfsElementDTO element : efsElements) {
            if (element.isDeleted()) {
                continue;
            }

            notDeletedElements.add(element);
            if (element.isRoot()) {
                rootElements.add(element);
            }

            if (element.isLeaf()) {
                leafElements.add(element);
                continue;
            }

            assemblyElements.add(element);
        }
    }

    private void checkForWeightControlFlagIncorrect(Collection<EfsElementDTO> leafElements,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap) {
        for (EfsElementDTO element : leafElements) {
            if (multiElementsContainsGws(element, WeightControlFlag.YES, WeightControlFlag.TEMP, null)) {
                checkIfAllAncestorsEmpty(element, resultMap);
                continue;
            }

            EfsElementDTO parent = element.getParent();
            while (multiElementsContainsGws(parent, WeightControlFlag.NO)) {
                parent = parent.getParent();
            }

            if (multiElementsContainsGws(parent, WeightControlFlag.YES, WeightControlFlag.TEMP)) {
                checkIfAllAncestorsEmpty(parent, resultMap);
                continue;
            }

            addInspectorEntriesForChildren(parent, InspectorEntryType.GWS_INCORRECT, resultMap);
        }
    }

    private void checkIfAllAncestorsEmpty(EfsElementDTO element,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap) {
        if (element.isRoot()) {
            return;
        }

        EfsElementDTO ancestor = element.getParent();
        while (!ancestor.isRoot()) {
            if (!multiElementsContainsGws(ancestor)) {
                addInspectorEntriesForChildren(ancestor, InspectorEntryType.GWS_INCORRECT, resultMap);
            }

            ancestor = ancestor.getParent();
        }
    }

    private void checkForWeightNotSet(Collection<EfsElementDTO> notDeletedElements,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap) {
        for (EfsElementDTO element : notDeletedElements) {
            if (element.isWeightRelevant() && !element.hasWeight()) {
                addInspectorEntry(element, element.getParent(), InspectorEntryType.WEIGHT_NOT_SET, resultMap);
            }
        }
    }

    private void checkForIgnoredWeights(EfsElementDTO element,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap) {
        if (!multiElementsContainsGws(element) || element.hasNodeWeight() || !element.hasWeight()) {
            return;
        }

        addInspectorEntriesForChildren(element, InspectorEntryType.GWS_INCORRECT_NO_WEIGHT, resultMap);
    }

    private void checkWeightDifference(EfsElementDTO element,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap) {
        if (multiElementsContainsGws(element) && element.hasNodeWeight() && element.hasWeight()
            && !isNodeWeightEqualToTotalWeight(element)) {
            addInspectorEntriesForChildren(element, InspectorEntryType.WEIGHT_DIFFERENCE, resultMap);
        }
    }

    private boolean isNodeWeightEqualToTotalWeight(EfsElementDTO efsElement) {
        return efsElement.getNodeWeight().equals(efsElement.getTotalWeight());
    }

    private void addInspectorEntriesForChildren(EfsElementDTO element, InspectorEntryType type,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap) {
        if (element == null) {
            return;
        }

        for (EfsElementDTO child : element.getChildren()) {
            addInspectorEntry(child, element, type, resultMap);
        }
    }

    private void addInspectorEntry(EfsElementDTO element, EfsElementDTO problemGroupElement, InspectorEntryType type,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap) {
        if (!isNewInspectorEntryTypeForElement(type, element) || !isIncluded(element, type)) {
            return;
        }

        InspectorEntry inspectorEntry = new InspectorEntry(element, type, problemGroupElement.getNodeValue(),
            problemGroupElement.getNodeId() + problemGroupElement.getPartNumber());
        resultMap.put(type, inspectorEntry);
    }

    private boolean isNewInspectorEntryTypeForElement(InspectorEntryType type, EfsElementDTO element) {
        Collection<InspectorEntryType> inspectorEntryTypes = inspectorEntryTypeOfElement.computeIfAbsent(element,
            efsElement -> new HashSet<>());

        if (inspectorEntryTypes.contains(type)) {
            return false;
        }

        inspectorEntryTypes.add(type);

        return true;
    }

    private boolean multiElementsContainsGws(EfsElementDTO element, WeightControlFlag... gws) {
        if (element == null) {
            return false;
        }

        if (gws == null || gws.length == 0) {
            return singleElementsContainsGws(element, null);
        }

        for (WeightControlFlag flag : gws) {
            if (singleElementsContainsGws(element, flag)) {
                return true;
            }
        }

        return false;
    }

    private boolean singleElementsContainsGws(EfsElementDTO element, WeightControlFlag flag) {
        return flag == null && element.getWeightControlFlag() == null
            || element.getWeightControlFlag() != null && element.getWeightControlFlag().equals(flag);
    }

    private void checkForBaukasten(EfsElementDTO root, ListMultimap<InspectorEntryType, InspectorEntry> resultMap) {
        Collection<EfsElementDTO> baukasten = BaukastenChecker.findAll(root, Inspection.BAUKASTEN);
        for (EfsElementDTO toInspect : baukasten) {
            InspectorEntryType inspectorType =
                Inspection.GWS.test(toInspect) ? InspectorEntryType.GWS_BAUKASTEN : InspectorEntryType.BAUKASTEN;
            addInspectorEntriesForChildren(toInspect, inspectorType, resultMap);
        }
    }
}
