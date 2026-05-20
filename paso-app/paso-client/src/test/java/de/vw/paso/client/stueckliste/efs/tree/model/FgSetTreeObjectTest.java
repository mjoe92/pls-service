package de.vw.paso.client.stueckliste.efs.tree.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import org.junit.jupiter.api.Test;

class FgSetTreeObjectTest {

    @Test
    void testFgSetIsKnown() {
        SetKeyDTO setKeyDTO = new SetKeyDTO("A1", "--some-description--", "A", 1L);
        FgSetTreeObject treeObject = new FgSetTreeObject(setKeyDTO, List.of());

        assertTrue(treeObject.isKnown());
    }

    @Test
    void testFgSetIsKnownWithEmptyDescription() {
        SetKeyDTO setKeyDTO = new SetKeyDTO("A1", "", "A", 1L);
        FgSetTreeObject treeObject = new FgSetTreeObject(setKeyDTO, List.of());

        assertTrue(treeObject.isKnown());
    }

    @Test
    void testFgSetIsKnownWithBlankDescription() {
        SetKeyDTO setKeyDTO = new SetKeyDTO("A1", "        ", "A", 1L);
        FgSetTreeObject treeObject = new FgSetTreeObject(setKeyDTO, List.of());

        assertTrue(treeObject.isKnown());
    }

    @Test
    void testFgSetIsNotKnown() {
        SetKeyDTO setKeyDTO = new SetKeyDTO("A1", null, "A", 1L);
        FgSetTreeObject treeObject = new FgSetTreeObject(setKeyDTO, List.of());

        assertFalse(treeObject.isKnown());
    }

    @Test
    void testFgSetIsNotKnownIfNull() {
        FgSetTreeObject treeObject = new FgSetTreeObject(null, List.of());

        assertFalse(treeObject.isKnown());
    }
}
