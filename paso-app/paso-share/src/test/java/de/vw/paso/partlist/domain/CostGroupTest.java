package de.vw.paso.partlist.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CostGroupTest {

    @Test
    void compareAlphabetically() {
        CostGroup costGroup1 = new CostGroup("A", 1L);
        CostGroup costGroup2 = new CostGroup("B", 1L);
        assertTrue(costGroup2.compareTo(costGroup1) > 0);
        assertTrue(costGroup1.compareTo(costGroup2) < 0);
    }

    @Test
    void compareWithItself() {
        CostGroup costGroup = new CostGroup("A", 1L);
        assertEquals(0, costGroup.compareTo(costGroup));
    }

    @Test
    void compareActualCostGroupValues() {
        CostGroup costGroup1 = new CostGroup("A123", 1L);
        CostGroup costGroup2 = new CostGroup("A234", 1L);
        assertTrue(costGroup1.compareTo(costGroup2) < 0);
        assertTrue(costGroup2.compareTo(costGroup1) > 0);
    }

    @Test
    void compareEmptyWithCostGroupStartingWithNumber() {
        CostGroup costGroup1 = new CostGroup("3A42", 1L);
        CostGroup costGroup2 = new CostGroup("<EMPTY>", 1L);
        assertTrue(costGroup1.compareTo(costGroup2) < 0);
        assertTrue(costGroup2.compareTo(costGroup1) > 0);
    }

    @Test
    void compareEmptyWithCostGroupStartingWithLetter() {
        CostGroup costGroup1 = new CostGroup("A123", 1L);
        CostGroup costGroup2 = new CostGroup("<EMPTY>", 1L);
        assertTrue(costGroup1.compareTo(costGroup2) < 0);
        assertTrue(costGroup2.compareTo(costGroup1) > 0);
    }

    @Test
    void compareEmptyWithCostGroupStartingWithPointyBraces() {
        CostGroup costGroup1 = new CostGroup("<A123>", 1L);
        CostGroup costGroup2 = new CostGroup("<EMPTY>", 1L);
        assertTrue(costGroup1.compareTo(costGroup2) < 0);
        assertTrue(costGroup2.compareTo(costGroup1) > 0);
    }
}
