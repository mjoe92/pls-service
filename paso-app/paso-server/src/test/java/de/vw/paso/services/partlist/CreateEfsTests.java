package de.vw.paso.services.partlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import de.vw.paso.exception.NullElementException;
import de.vw.paso.mapper.EfsElementMapper;
import de.vw.paso.mapper.VehiclePartListMapper;
import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.service.partlist.CreateDeletedEfsElementException;
import de.vw.paso.service.partlist.PartNumberInappropriateException;
import de.vw.paso.service.partlist.SameMaraInHierachyException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.utility.StringConstant;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CreateEfsTests extends AbstractEfsTests {

    @Test
    public void savingEfsElement() {
        efsElement.setChange("TEST_USER");

        saveEfsElementConsumer.saveEfsElement(efsElement);

        final EfsElement foundElement = efsElementRepository.findById(saveEfsElementConsumer.getResult().getId())
                .orElseThrow();
        evaluateEfsElementReversion(initialRevision + 1, EfsElementMapper.toDto(foundElement));
        evaluateRevisionVehiclePartList(initialRevision + 1);
        assertNotNull(foundElement.getId());
        assertEquals(efsElement.getUserChange(), foundElement.getUserChange());
    }

    @Test
    public void createEfsElementWithPartNumberTooLongWithoutLeadingLetter() {
        testPartNumberInappropriate();
    }

    @Test
    public void createEfsElementWithNoMara() {
        EfsElementDTO newElement = PartListFactory.createEfsElement(null, null, 0, "G", emptyPartList().getId());
        saveEfsElementConsumer.saveEfsElement(newElement);

        saveEfsElementConsumer.getResult(NullElementException.class);
        evaluateRevisionEmptyPartList(0);
    }

    private void testPartNumberInappropriate() {
        String partNumberSample = "12345678901234567";
        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara(StringConstant.EMPTY, partNumberSample);
        EfsElementDTO newElement = PartListFactory.createEfsElement(null, mara, 0, "G", emptyPartList().getId());
        saveEfsElementConsumer.saveEfsElement(newElement);

        saveEfsElementConsumer.getResult(PartNumberInappropriateException.class);
        evaluateRevisionEmptyPartList(0);
        assertNull(efsElementMaraRepository.findOneByPartNumberAndVehiclePartListId(partNumberSample,
                VehiclePartListMapper.toEntity(emptyPartList(), null).getId()));
    }

    @Test
    public void createEfsElementAsChild() {
        EfsElementDTO parent = getOriginal(0);
        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "11111111111");
        EfsElementDTO child = PartListFactory.createEfsElement(parent.getId(), mara, 0, "G",
                parent.getVehiclePartListId());

        saveEfsElementConsumer.saveEfsElement(child);

        EfsElementDTO persistedElement = saveEfsElementConsumer.getResult();
        evaluateRevisionVehiclePartList(initialRevision + 1);
        assertEquals(getOriginal(0).getId(), persistedElement.getParent().getId());
        evaluateEfsElementReversion(initialRevision + 1, persistedElement);
        //FIXME ZN see EfsTreeController.createNewEfsElement
    }

    @Test
    public void createDeletedEfsElement() {
        efsElement.setDeleted(1);

        saveEfsElementConsumer.saveEfsElement(efsElement);

        saveEfsElementConsumer.getResult(CreateDeletedEfsElementException.class);
    }

    @Test
    public void forbidToCreateElementWithSameMaraAsParent() {
        EfsElementDTO parent = getOriginal(0);
        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "10111111111");
        EfsElementDTO child = PartListFactory.createEfsElement(parent.getId(), mara, 0, "G",
                parent.getVehiclePartListId());

        saveEfsElementConsumer.saveEfsElement(child);

        saveEfsElementConsumer.getResult(SameMaraInHierachyException.class);
    }

    @Test
    public void forbidToCreateElementWithSameMaraAboveInHierarchy() {
        moveEfsElementConsumer.moveEfsElements(getOriginal(0), List.of(getOriginal(1)));
        EfsElementDTO parent = moveEfsElementConsumer.getResult().getFirst();
        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "10111111111");
        EfsElementDTO child = PartListFactory.createEfsElement(parent.getId(), mara, 0, "G",
                parent.getVehiclePartListId());

        saveEfsElementConsumer.saveEfsElement(child);

        saveEfsElementConsumer.getResult(SameMaraInHierachyException.class);
    }
}