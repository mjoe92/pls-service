package de.vw.paso.services.partlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import de.vw.paso.exception.EmptyListException;
import de.vw.paso.mapper.EfsElementMapper;
import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.service.partlist.DeleteNonPersistedEfsElementException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DeleteEfsTests extends AbstractEfsTests {

    @Disabled("Fails due to revision not being updated correctly")
    @Test
    public void deleteEfsElement() {
        deleteEfsElementConsumer.deleteEfsElement(persistedEfsElements());

        final int deletedEfsCount = deleteEfsElementConsumer.getResult().size();
        evaluateRevisionVehiclePartList(initialRevision + 1);
        assertEquals(PERSISTED_EFS_ELEMENT_COUNT, deletedEfsCount);
        for (final EfsElementDTO efsElement : persistedEfsElements()) {
            final EfsElement deletedEfs = efsElementRepository.findById(efsElement.getId()).get();
            assertEquals(1, deletedEfs.getDeleted().intValue());
            evaluateEfsElementReversion(initialRevision + 1, EfsElementMapper.toDto(deletedEfs));
        }
    }

    @Test
    public void deleteEfsElementWithNullParameter() {
        assertThrows(RuntimeException.class, () -> deleteEfsElementConsumer.deleteEfsElement(null));
    }

    @Test
    public void deleteEfsElementWithEmptyList() {
        deleteEfsElementConsumer.deleteEfsElement(List.of());

        evaluateRevisionVehiclePartList(initialRevision);
        deleteEfsElementConsumer.getResult(EmptyListException.class);
    }

    @Test
    public void deleteEfsElementsWithOneElementHasNullId() {
        List<EfsElementDTO> elements = persistedEfsElements();
        elements.add(efsElement);
        final long efsElementCount = efsElementRepository.count();

        deleteEfsElementConsumer.deleteEfsElement(elements);

        evaluateRevisionVehiclePartList(initialRevision);
        for (EfsElementDTO element : persistedEfsElements()) {
            assertFalse(element.isDeleted());
        }
        deleteEfsElementConsumer.getResult(DeleteNonPersistedEfsElementException.class);
        assertEquals(efsElementCount, efsElementRepository.count());
    }

    @Disabled("Fails due to revision not being updated correctly")
    @Test
    public void deleteDeletedEfsElement() {
        deleteEfsElementConsumer.deleteEfsElement(List.of(getOriginal(1)));

        evaluateRevisionVehiclePartList(initialRevision + 1);
        deleteEfsElementConsumer.deleteEfsElement(List.of(getOriginal(1)));
        deleteEfsElementConsumer.getResult(EmptyListException.class);
    }

    @Disabled("Fails due to revision not being updated correctly")
    @Test
    public void deleteElementHierachy() {
        EfsElementDTO elementA = getOriginal(0);
        EfsElementDTO elementB = getOriginal(1);
        moveEfsElementConsumer.moveEfsElements(elementA, List.of(elementB));
        elementA = synchronize(elementA);
        elementB = moveEfsElementConsumer.getResult().getFirst();
        initialRevision = vehiclePartList().getRevision();

        deleteEfsElementConsumer.deleteEfsElement(List.of(elementA));

        elementA = synchronize(elementA);
        elementB = synchronize(elementB);
        evaluateRevisionVehiclePartList(initialRevision + 1);
        assertEquals(2, deleteEfsElementConsumer.getResult().size());
        assertTrue(elementB.isDeleted() && elementA.isDeleted());
        evaluateEfsElementReversion(initialRevision + 1, elementA, elementB);
    }
}