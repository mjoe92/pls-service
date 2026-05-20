package de.vw.paso.services.partlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.service.partlist.EditingDeletedEfsElementException;
import de.vw.paso.service.partlist.SameMaraInHierachyException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class EditEfsTests extends AbstractEfsTests {

    @Test
    public void editEfsElement() {
        assertEquals(Integer.valueOf(111), getOriginal(0).getQuantity());

        EfsElementDTO element = getOriginal(0);
        element.setQuantity(5);

        saveEfsElementConsumer.saveEfsElement(element);

        Long revision = getOriginal(0).getRevision();

        assertEquals((initialRevision + 1), revision.longValue());
        assertEquals(Integer.valueOf(5), getOriginal(0).getQuantity());
    }

    @Test
    public void editDeletedEfsElementWithoutRemovingDeletedFlag() {
        deleteEfsElementConsumer.deleteEfsElement(asList(getOriginal(0)));
        assertTrue(getOriginal(0).isDeleted());

        EfsElementDTO element = getOriginal(0);
        element.setQuantity(5);
        saveEfsElementConsumer.saveEfsElement(element);

        saveEfsElementConsumer.getResult(EditingDeletedEfsElementException.class);
    }

    private List<EfsElementDTO> asList(EfsElementDTO efsElementDTO) {
        List<EfsElementDTO> list = new ArrayList<>();
        list.add(efsElementDTO);
        return list;
    }

    @Test
    public void editDeletedEfsElementByRemovingDeletedFlag() {
        deleteEfsElementConsumer.deleteEfsElement(Collections.singletonList(getOriginal(0)));
        assertTrue(getOriginal(0).isDeleted());

        EfsElementDTO element = getOriginal(0);
        element.setDeleted(0);
        saveEfsElementConsumer.saveEfsElement(element);

        assertFalse(getOriginal(0).isDeleted());
    }

    @Test
    public void changeMaraToNewMara() {
        EfsElementDTO element = synchronize(getOriginal(0));

        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara(StringUtils.EMPTY, "N5555555551");
        element.setEfsElementMara(mara);
        saveEfsElementConsumer.saveEfsElement(element);
        element = saveEfsElementConsumer.getResult();
        element.getEfsElementMara().setWeightWeightedTe(23.0);
        saveEfsElementConsumer.saveEfsElement(element);

        assertEquals("N5555555551", saveEfsElementConsumer.getResult().getEfsElementMara().getPartNumber());
        assertEquals(Double.valueOf(23.0),
                saveEfsElementConsumer.getResult().getEfsElementMara().getWeightWeightedTe());
    }

    @Test
    public void changeMaraToAlreadyExistingMara() {
        EfsElementDTO nodeWithReferencedMara = getOriginal(1);
        final EfsElementMaraDTO referencedMara = nodeWithReferencedMara.getEfsElementMara();
        referencedMara.setWeightWeightedTe(85.3);
        saveEfsElementConsumer.saveEfsElement(nodeWithReferencedMara);
        EfsElementDTO element = getOriginal(0);
        assertNotEquals(element.getEfsElementMara().getPartNumber(),
                saveEfsElementConsumer.getResult().getEfsElementMara().getPartNumber());
        assertEquals(Double.valueOf(85.3),
                saveEfsElementConsumer.getResult().getEfsElementMara().getWeightWeightedTe());
        EfsElementMaraDTO newMara = PartListFactory.createEfsElementMara(StringUtils.EMPTY,
                referencedMara.getPartNumber());

        element.setEfsElementMara(newMara);
        saveEfsElementConsumer.saveEfsElement(element);

        assertEquals(getOriginal(1).getEfsElementMara().getPartNumber(),
                saveEfsElementConsumer.getResult().getEfsElementMara().getPartNumber());
        assertEquals(Double.valueOf(85.3),
                saveEfsElementConsumer.getResult().getEfsElementMara().getWeightWeightedTe());
    }

    @Test
    public void forbidToEditElementToSameMaraAsParent() {
        EfsElementDTO parent = getOriginal(0);
        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "11111111111");
        EfsElementDTO child = PartListFactory.createEfsElement(parent.getId(), mara, 0, "G",
                parent.getVehiclePartListId());
        saveEfsElementConsumer.saveEfsElement(child);
        child = saveEfsElementConsumer.getResult();

        EfsElementMaraDTO newMara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "10111111111");
        child.setEfsElementMara(newMara);
        saveEfsElementConsumer.saveEfsElement(child);

        saveEfsElementConsumer.getResult(SameMaraInHierachyException.class);
    }

    @Test
    public void forbidToEditElementToSameMaraAboveInHierarchy() {
        moveEfsElementConsumer.moveEfsElements(getOriginal(0), List.of(getOriginal(1)));
        EfsElementDTO parent = moveEfsElementConsumer.getResult().getFirst();
        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "12111111111");
        EfsElementDTO child = PartListFactory.createEfsElement(parent.getId(), mara, 0, "G",
                parent.getVehiclePartListId());
        saveEfsElementConsumer.saveEfsElement(child);
        child = saveEfsElementConsumer.getResult();

        EfsElementMaraDTO newMara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "10111111111");
        child.setEfsElementMara(newMara);
        saveEfsElementConsumer.saveEfsElement(child);

        saveEfsElementConsumer.getResult(SameMaraInHierachyException.class);
    }
}
