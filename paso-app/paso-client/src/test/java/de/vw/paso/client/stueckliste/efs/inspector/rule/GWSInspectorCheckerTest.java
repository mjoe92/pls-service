package de.vw.paso.client.stueckliste.efs.inspector.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;
import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.utility.StringConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GWSInspectorCheckerTest {

    private GWSInspectorChecker inspector;

    @BeforeEach
    void setUp() {
        inspector = new GWSInspectorChecker();
    }

    @Test
    void testMultipleRootsWillThrowException() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root1 = createElement(null, null);
        EfsElementDTO root2 = createElement(null, null);

        assertThrows(IllegalArgumentException.class,
                () -> inspector.checkElements(List.of(root1, root2), resultMap, null, null));
    }

    @Test
    void testRootElementWithEmptyWeightFlagHasNoGwsIncorrectError() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO rootElement = createElement(null, null);

        inspector.checkElements(List.of(rootElement), resultMap, null, null);

        assertTrue(resultMap.get(InspectorEntryType.GWS_INCORRECT).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { "J", "N", "V" })
    void testRootWithNotEmptyWeightFlagProducesGwsIncorrectError(String weightControlFlag) {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO rootElement = createElement(WeightControlFlag.getType(weightControlFlag), null);

        inspector.checkElements(List.of(rootElement), resultMap, null, null);

        assertHasGwsIncorrectError(resultMap, rootElement);
    }

    @Test
    void testBranchAllEmptyDoesNotProduceAnyGwsIncorrectError() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO parent = createElement(null, null);
        EfsElementDTO child = createElement(null, null);
        establishHierarchy(parent, child);

        inspector.checkElements(List.of(parent, child), resultMap, null, null);

        assertTrue(resultMap.get(InspectorEntryType.GWS_INCORRECT).isEmpty());
        assertHasWeightNotSetError(resultMap, child);
    }

    @Test
    void testBranchEmptyNoProducesGwsIncorrectError() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent = createElement(null, null);
        EfsElementDTO child = createElement(WeightControlFlag.NO, null);
        establishHierarchy(root, parent, child);

        inspector.checkElements(List.of(root, parent, child), resultMap, null, null);

        assertHasGwsIncorrectError(resultMap, child);
    }

    @ParameterizedTest
    @ValueSource(strings = { "J", "V" })
    void testBranchEmptyXDoesNotProduceGwsIncorrectError(String weightControlFlagX) {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO parent = createElement(null, null);
        EfsElementDTO child = createElement(WeightControlFlag.getType(weightControlFlagX), 1234d);
        establishHierarchy(parent, child);

        inspector.checkElements(List.of(parent, child), resultMap, null, null);

        assertTrue(resultMap.get(InspectorEntryType.GWS_INCORRECT).isEmpty());
    }

    @Test
    void testChildWithYesWeightFlagAndNoWeightHasWeightNotSetError() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent = createElement(null, null);
        EfsElementDTO child = createElement(WeightControlFlag.YES, null);
        establishHierarchy(root, parent, child);

        inspector.checkElements(List.of(root, parent, child), resultMap, null, null);

        assertTrue(resultMap.get(InspectorEntryType.GWS_INCORRECT).isEmpty());

        assertHasWeightNotSetError(resultMap, child);
    }

    @Test
    void testBranchYesYesProducesGwsIncorrectError() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent = createElement(WeightControlFlag.YES, null);
        EfsElementDTO child = createElement(WeightControlFlag.YES, null);
        establishHierarchy(root, parent, child);

        inspector.checkElements(List.of(root, parent, child), resultMap, null, null);

        assertHasGwsIncorrectError(resultMap, child);
    }

    @Test
    void testParentAndChildWithYesWeightFlagAndChildWithWeightParentHasWeightNotSetError() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent = createElement(WeightControlFlag.YES, null);
        EfsElementDTO child = createElement(WeightControlFlag.YES, 1234d);
        establishHierarchy(root, parent, child);

        inspector.checkElements(List.of(root, parent, child), resultMap, null, null);

        assertHasGwsIncorrectError(resultMap, child);

        assertHasWeightNotSetError(resultMap, parent);
    }

    @Test
    void testBranchYesEmptyVorlaufigProducesGWSIncorrectError() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null);
        EfsElementDTO grandParent = createElement(WeightControlFlag.YES, 1234d);
        EfsElementDTO parent = createElement(null, null);
        EfsElementDTO child = createElement(WeightControlFlag.TEMP, null);
        establishHierarchy(root, grandParent, parent, child);

        inspector.checkElements(List.of(root, grandParent, parent, child), resultMap, null, null);
        assertHasGwsIncorrectError(resultMap, parent);
    }

    @Test
    void testBranchYesNoEmptyProducesGWSIncorrectError() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null);
        EfsElementDTO grandParent = createElement(WeightControlFlag.YES, 1234d);
        EfsElementDTO parent = createElement(WeightControlFlag.NO, null);
        EfsElementDTO child = createElement(null, null);
        establishHierarchy(root, grandParent, parent, child);

        inspector.checkElements(List.of(root, grandParent, parent, child), resultMap, null, null);
        assertHasGwsIncorrectError(resultMap, child);
        assertHasGwsIncorrectError(resultMap, parent);
    }

    @Test
    void testElementWithNoWeightFlagAndWeightHasNoError() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO element = createElement(WeightControlFlag.NO, 1234d);

        inspector.checkElements(List.of(element), resultMap, null, null);

        assertTrue(resultMap.get(InspectorEntryType.WEIGHT_NOT_SET).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { "J", "V" })
    void testBranchEmptyXNoDoesNotProduceGwsIncorrectError(String weightControlFlagX) {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent = createElement(WeightControlFlag.getType(weightControlFlagX), null);
        EfsElementDTO child = createElement(WeightControlFlag.NO, null);
        establishHierarchy(root, parent, child);

        inspector.checkElements(List.of(root, parent, child), resultMap, null, null);

        assertTrue(resultMap.get(InspectorEntryType.GWS_INCORRECT).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { "J", "V", "N" })
    void testBranchEmptyXEmptyProducesGwsIncorrectError(String weightControlFlagX) {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent = createElement(WeightControlFlag.getType(weightControlFlagX), null);
        EfsElementDTO child = createElement(null, null);
        establishHierarchy(root, parent, child);

        inspector.checkElements(List.of(root, parent, child), resultMap, null, null);

        assertHasGwsIncorrectError(resultMap, child);
    }

    @Test
    void testOnlyOneWeightControlFlagErrorOnAssembly() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent = createElement(WeightControlFlag.YES, null);
        EfsElementDTO child1 = createElement(null, null);
        EfsElementDTO child2 = createElement(null, null);
        establishHierarchy(root, parent, child1);
        parent.getChildren().add(child2);
        child2.setParent(parent);

        inspector.checkElements(List.of(root, parent, child1, child2), resultMap, null, null);

        assertEquals(2, resultMap.get(InspectorEntryType.GWS_INCORRECT).size());
    }

    @Test
    void testNoWeightIgnoredError() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null, null);
        EfsElementDTO parent = createElement(null, null, null);
        EfsElementDTO child = createElement(null, null);
        establishHierarchy(root, parent, child);

        inspector.checkElements(List.of(root, parent, child), resultMap, null, null);

        assertEquals(0, resultMap.get(InspectorEntryType.GWS_INCORRECT_NO_WEIGHT).size());
    }

    @Test
    void testWeightIgnoredError() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null, null);
        EfsElementDTO parent = createElement(null, 1234d, 0d);
        EfsElementDTO child1 = createElement(null, null);
        EfsElementDTO child2 = createElement(null, 456d);
        establishHierarchy(root, parent, child1);
        parent.getChildren().add(child2);
        child2.setParent(parent);

        inspector.checkElements(List.of(root, parent, child1, child2), resultMap, null, null);

        assertEquals(2, resultMap.get(InspectorEntryType.GWS_INCORRECT_NO_WEIGHT).size());
    }

    @Test
    void testNoWeightIgnoredErrorIfAssemblyHasNodeWeight() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null, null);
        EfsElementDTO parent = createElement(null, 1234d, 333d);
        EfsElementDTO child1 = createElement(null, null);
        EfsElementDTO child2 = createElement(null, 456d);
        establishHierarchy(root, parent, child1);
        parent.getChildren().add(child2);
        child2.setParent(parent);

        inspector.checkElements(List.of(root, parent, child1, child2), resultMap, null, null);

        assertEquals(0, resultMap.get(InspectorEntryType.GWS_INCORRECT_NO_WEIGHT).size());
    }

    @Test
    void testNoWeightIgnoredErrorIfNodeWeightEqualsPartWeight() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null, null);
        EfsElementDTO parent = createElement(null, 1234d, 1234d);
        EfsElementDTO child1 = createElement(null, null);
        EfsElementDTO child2 = createElement(null, 456d);
        establishHierarchy(root, parent, child1);
        parent.getChildren().add(child2);
        child2.setParent(parent);

        inspector.checkElements(List.of(root, parent, child1, child2), resultMap, null, null);

        assertEquals(0, resultMap.get(InspectorEntryType.GWS_INCORRECT_NO_WEIGHT).size());
    }

    @Test
    void testWeightIgnoredErrorOnDeepHierarchy() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null, null);
        EfsElementDTO grandParent = createElement(null, 1234d, null);
        EfsElementDTO parent = createElement(null, null, null);
        EfsElementDTO child = createElement(null, null);
        establishHierarchy(root, grandParent, parent, child);

        inspector.checkElements(List.of(root, grandParent, parent, child), resultMap, null, null);

        // grandParent -> parent relation produces an error
        assertEquals(1, resultMap.get(InspectorEntryType.GWS_INCORRECT_NO_WEIGHT).size());
    }

    @Test
    void testRepeatedCheckWillProduceErrors() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap1 = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap2 = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent = createElement(null, null);
        EfsElementDTO child = createElement(WeightControlFlag.YES, null);
        establishHierarchy(root, parent, child);

        inspector.checkElements(List.of(root, parent, child), resultMap1, null, null);
        inspector.checkElements(List.of(root, parent, child), resultMap2, null, null);

        assertEquals(resultMap1.size(), resultMap2.size());
    }

    @Test
    void testNoWeightDifferenceWarning() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();
        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent = createElement(null, 100d, 100d);
        EfsElementDTO child = createElement(null, 20d);
        establishHierarchy(root, parent, child);

        inspector.checkElements(List.of(root, parent, child), resultMap, null, null);

        assertEquals(0, resultMap.get(InspectorEntryType.WEIGHT_DIFFERENCE).size());
    }

    @ParameterizedTest
    @ValueSource(strings = { "J", "N", "V" })
    void testNoWeightDifferenceWarningForNonEmptyWeightControlFlag(String weightControlFlag) {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();
        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent = createElement(WeightControlFlag.getType(weightControlFlag), 4d, 100d);
        EfsElementDTO child = createElement(WeightControlFlag.NO, 20d);
        establishHierarchy(root, parent, child);

        inspector.checkElements(List.of(root, parent, child), resultMap, null, null);

        assertEquals(0, resultMap.get(InspectorEntryType.WEIGHT_DIFFERENCE).size());
    }

    @Test
    void testWeightDifferenceWarning() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();
        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent = createElement(null, 50d, 100d);
        EfsElementDTO child = createElement(null, 20d);
        establishHierarchy(root, parent, child);

        inspector.checkElements(List.of(root, parent, child), resultMap, null, null);

        assertEquals(1, resultMap.get(InspectorEntryType.WEIGHT_DIFFERENCE).size());
    }

    @Test
    void testWeightDifferenceWarningDeepHierarchy() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();
        EfsElementDTO root = createElement(null, null);
        EfsElementDTO grandParent = createElement(null, 50d, 100d);
        EfsElementDTO parent = createElement(null, 70d);
        EfsElementDTO child = createElement(null, 20d);
        establishHierarchy(root, grandParent, parent, child);

        inspector.checkElements(List.of(root, grandParent, parent, child), resultMap, null, null);

        assertEquals(1, resultMap.get(InspectorEntryType.WEIGHT_DIFFERENCE).size());
        InspectorEntry inspectorEntry = resultMap.get(InspectorEntryType.WEIGHT_DIFFERENCE).getFirst();
        assertEquals(parent, inspectorEntry.getElement());
    }

    @Test
    void testMultipleWeightDifferenceWarnings() {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = ListMultimapBuilder.hashKeys().arrayListValues()
                .build();

        EfsElementDTO root = createElement(null, null);
        EfsElementDTO parent1 = createElement(null, 50d, 100d);
        EfsElementDTO child1 = createElement(null, 20d);
        EfsElementDTO parent2 = createElement(null, 23d, 4000d);
        EfsElementDTO child2 = createElement(null, 15d);
        establishHierarchy(parent1, child1);
        establishHierarchy(parent2, child2);
        root.getChildren().addAll(List.of(parent1, parent2));
        parent1.setParent(root);
        parent2.setParent(root);

        inspector.checkElements(List.of(root, parent1, child1, parent2, child2), resultMap, null, null);

        assertEquals(2, resultMap.get(InspectorEntryType.WEIGHT_DIFFERENCE).size());
    }

    private EfsElementDTO createElement(WeightControlFlag weightControlFlag, Double partWeight, Double nodeWeight) {
        EfsElementDTO element = createElement(weightControlFlag, partWeight);
        element.setNodeWeight(nodeWeight);

        return element;
    }

    private EfsElementDTO createElement(WeightControlFlag weightControlFlag, Double weight) {
        EfsElementMaraDTO efsElementMara = new EfsElementMaraDTO();
        efsElementMara.setPartNumber(StringConstant.EMPTY);
        efsElementMara.setWeightCalculatedTe(weight);
        efsElementMara.setPrioritizedWeight(weight);

        EfsElementDTO efsElement = new EfsElementDTO();
        efsElement.setWeightControlFlag(weightControlFlag);
        efsElement.setEfsElementMara(efsElementMara);
        efsElement.setQuantity(1);
        efsElement.setChildren(new ArrayList<>());

        return efsElement;
    }

    private void establishHierarchy(EfsElementDTO... elementsInOrder) {
        EfsElementDTO previousElement = null;
        for (EfsElementDTO element : elementsInOrder) {
            element.setParent(previousElement);

            Optional.ofNullable(previousElement).ifPresent(parent -> parent.getChildren().add(element));

            previousElement = element;
        }
    }

    private void assertHasGwsIncorrectError(ListMultimap<InspectorEntryType, InspectorEntry> resultMap,
            EfsElementDTO element) {
        Collection<InspectorEntry> gwsIncorrectErrors = resultMap.get(InspectorEntryType.GWS_INCORRECT);
        assertFalse(gwsIncorrectErrors.isEmpty());

        boolean errorIsAssociatedWithElement = gwsIncorrectErrors.stream()
                .anyMatch(error -> element.equals(error.getElement()));
        assertTrue(errorIsAssociatedWithElement);
    }

    private void assertHasWeightNotSetError(ListMultimap<InspectorEntryType, InspectorEntry> resultMap,
            EfsElementDTO... elements) {
        assertFalse(resultMap.get(InspectorEntryType.WEIGHT_NOT_SET).isEmpty());

        Collection<InspectorEntry> inspectorEntries = resultMap.get(InspectorEntryType.WEIGHT_NOT_SET);
        Collection<EfsElementDTO> elementsOfInspectorEntries = inspectorEntries.stream().map(InspectorEntry::getElement)
                .toList();

        for (EfsElementDTO element : elements) {
            assertTrue(elementsOfInspectorEntries.contains(element));
        }
    }
}
