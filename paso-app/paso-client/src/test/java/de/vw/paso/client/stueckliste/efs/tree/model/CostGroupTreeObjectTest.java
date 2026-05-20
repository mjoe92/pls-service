package de.vw.paso.client.stueckliste.efs.tree.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import de.vw.paso.partlist.domain.AP;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import org.junit.jupiter.api.Test;

class CostGroupTreeObjectTest {

    @Test
    void testCostGroupWithoutElementsHasNoWeight() {
        CostGroupTreeObject treeObject = new CostGroupTreeObject(new CostGroupDTO(), List.of());

        treeObject.calculateWeights();

        assertNull(treeObject.getPlatform());
        assertNull(treeObject.getSystem());
        assertNull(treeObject.getHut());
        assertNull(treeObject.getWeightAll());
    }

    @Test
    void testCostGroupCalculateWeightsSumByApCompareGroup() {
        List<EfsElementDTO> efsElements = List.of(createElement(AP.PLATFORM, 1d), createElement(AP.PLATFORM, 5d),
                createElement(AP.SYSTEM, 3d), createElement(AP.HUT, 20d));
        CostGroupTreeObject treeObject = new CostGroupTreeObject(new CostGroupDTO(), efsElements);

        treeObject.calculateWeights();

        assertEquals(6d, treeObject.getPlatform());
        assertEquals(3d, treeObject.getSystem());
        assertEquals(20d, treeObject.getHut());
        assertEquals(29d, treeObject.getWeightAll());
    }

    @Test
    void testCostGroupIsKnown() {
        CostGroupDTO costGroup = new CostGroupDTO();
        costGroup.setDescription("--some-description--");
        CostGroupTreeObject treeObject = new CostGroupTreeObject(costGroup, List.of());

        assertTrue(treeObject.isKnown());
    }

    @Test
    void testCostGroupIsKnownWithEmptyDescription() {
        CostGroupDTO costGroup = new CostGroupDTO();
        costGroup.setDescription("");
        CostGroupTreeObject treeObject = new CostGroupTreeObject(costGroup, List.of());

        assertTrue(treeObject.isKnown());
    }

    @Test
    void testCostGroupIsKnownWithBlankDescription() {
        CostGroupDTO costGroup = new CostGroupDTO();
        costGroup.setDescription("    ");
        CostGroupTreeObject treeObject = new CostGroupTreeObject(costGroup, List.of());

        assertTrue(treeObject.isKnown());
    }

    @Test
    void testCostGroupIsNotKnown() {
        CostGroupDTO costGroup = new CostGroupDTO();
        costGroup.setDescription(null);
        CostGroupTreeObject treeObject = new CostGroupTreeObject(costGroup, List.of());

        assertFalse(treeObject.isKnown());
    }

    @Test
    void testCostGroupIsNotKnownIfNull() {
        CostGroupTreeObject treeObject = new CostGroupTreeObject(null, List.of());

        assertFalse(treeObject.isKnown());
    }

    private EfsElementDTO createElement(AP ap, Double weight) {
        EfsElementDTO efsElement = new EfsElementDTO();
        efsElement.setAp(ap.getApAbbreviation());
        efsElement.setWeight(weight);
        efsElement.setChildren(new ArrayList<>());

        return efsElement;
    }
}
