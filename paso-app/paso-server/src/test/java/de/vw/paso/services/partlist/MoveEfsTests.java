package de.vw.paso.services.partlist;

import static java.util.stream.Collectors.toCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import de.vw.paso.exception.EmptyListException;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.service.partlist.AppendToDeletedElementException;
import de.vw.paso.service.partlist.MovingHierachyConflictException;
import de.vw.paso.service.partlist.SameMaraInHierachyException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MoveEfsTests extends AbstractEfsTests {

    @Disabled("Fails due changes of PASO-1222") //FIXME
    @Test
    public void forbidToMoveEfsElementToRootWithSamePartListWithoutMovingThem() {
        moveEfsElementConsumer.moveEfsElements(vehiclePartList(), persistedEfsElements());

        moveEfsElementConsumer.getResult(EmptyListException.class);
        evaluateRevisionVehiclePartList(initialRevision);
        for (EfsElementDTO element : persistedEfsElements()) {
            assertFalse(element.isDeleted());

            evaluateParentAndPartList(vehiclePartList(), element);

            assertTrue(initialRevision >= element.getRevision());
        }
    }

    @Disabled("Fails due to revision not being updated correctly") //FIXME
    @Test
    public void forbidToMoveDeletedEfsElement() {
        deleteEfsElementConsumer.deleteEfsElement(List.of(getOriginal(1)));
        initialRevision = vehiclePartList().getRevision();

        moveEfsElementConsumer.moveEfsElements(getOriginal(0), List.of(getOriginal(1)));

        evaluateRevisionVehiclePartList(initialRevision);
        assertTrue(initialRevision >= getOriginal(0).getRevision());
        assertTrue(initialRevision >= getOriginal(1).getRevision());

        moveEfsElementConsumer.getResult(EmptyListException.class);
        assertNull(getOriginal(1).getParent());
    }

    /**
     * +4
     * +3
     * +2
     * -1
     * +0
     * <p>
     * to
     * <p>
     * +4
     * +3
     * +2
     * -1
     * +0
     */
    @Test
    public void moveHierachyWithDeletedEfsElementInSamePartList_movingDeletedElement() {
        EfsElementDTO element4 = getOriginal(4);
        EfsElementDTO element3 = getOriginal(3);
        EfsElementDTO element2 = getOriginal(2);
        EfsElementDTO element1 = getOriginal(1);
        moveEfsElementConsumer.moveEfsElements(element4, List.of(element3));
        element3 = moveEfsElementConsumer.getResult().getFirst();
        moveEfsElementConsumer.moveEfsElements(element3, List.of(element2));
        element2 = moveEfsElementConsumer.getResult().getFirst();
        moveEfsElementConsumer.moveEfsElements(element2, List.of(element1));
        element1 = moveEfsElementConsumer.getResult().getFirst();
        deleteEfsElementConsumer.deleteEfsElement(List.of(element1));

        evaluateAllElementsInPartListSize(5, 1, vehiclePartList());

        moveEfsElementConsumer.moveEfsElements(element4, List.of(element2));

        List<EfsElementDTO> result = moveEfsElementConsumer.getResult();
        element2 = result.getFirst();
        assertEquals(1, result.size());
        assertFalse(element2.isDeleted());
        evaluateAllElementsInPartListSize(5, 1, vehiclePartList());

    }

    /**
     * +4
     * -3
     * <p>
     * to different part list
     * <p>
     * +4
     */
    @Test
    public void moveHierachyWithDeletedEfsElementInDifferentPartList_notMovingDeletedElement() {
        EfsElementDTO element4 = getOriginal(4);
        EfsElementDTO element3 = getOriginal(3);
        moveEfsElementConsumer.moveEfsElements(element4, List.of(element3));
        element3 = moveEfsElementConsumer.getResult().getFirst();
        deleteEfsElementConsumer.deleteEfsElement(List.of(element3));
        initialRevision = vehiclePartList().getRevision();

        moveEfsElementConsumer.moveEfsElements(emptyPartList(), List.of(element4));

        evaluateRevisionEmptyPartList(1);
        List<EfsElementDTO> result = moveEfsElementConsumer.getResult().stream().filter((node) -> !node.isDeleted())
                .toList();
        assertEquals(1, result.size());
        evaluateEfsElementReversion(1, result.getFirst());
        evaluateAllElementsInPartListSize(1, 0, emptyPartList());
        evaluateAllElementsInPartListSize(5, 2, vehiclePartList());

        assertTrue(getOriginal(3).isDeleted());
        assertTrue(getOriginal(4).isDeleted());
    }

    @Test
    public void moveEfsElementToRootWithSamePartList() {
        moveEfsElementConsumer.moveEfsElements(getOriginal(4),
                List.of(getOriginal(3), getOriginal(2), getOriginal(1), getOriginal(0)));

        moveEfsElementConsumer.moveEfsElements(vehiclePartList(), moveEfsElementConsumer.getResult());

        Collection<EfsElementDTO> movedEfsElements = moveEfsElementConsumer.getResult();
        assertEquals(4, movedEfsElements.size());
        for (EfsElementDTO movedElement : movedEfsElements) {
            assertNull(movedElement.getParentId());
        }
    }

    // REQ ZN When copying to another part list, what should we do with efs elements' mara?
    @Test
    public void moveEfsElementToRootWithDifferentPartList() {
        long revisionNumber = emptyPartList().getRevision();

        moveEfsElementConsumer.moveEfsElements(emptyPartList(), persistedEfsElements());

        List<EfsElementDTO> copiedEfsElements = moveEfsElementConsumer.getResult().stream()
                .filter((node) -> !node.isDeleted()).collect(toCollection(ArrayList::new));
        evaluatePartListReversion(copiedEfsElements.getFirst(), revisionNumber + 1);

        assertEquals(PERSISTED_EFS_ELEMENT_COUNT, copiedEfsElements.size());

        copiedEfsElements.sort(Comparator.comparing(EfsElementDTO::getPartNumber));
        for (int index = 0; index < PERSISTED_EFS_ELEMENT_COUNT; index++) {
            EfsElementDTO copiedEfsElement = copiedEfsElements.get(index);
            evaluateEfsElementReversion(revisionNumber + 1, copiedEfsElement);
            evaluateMaraRevision(revisionNumber + 1, copiedEfsElement);
            evaluateParentAndPartList(emptyPartList(), copiedEfsElement);
        }
    }

    /**
     * + A
     * + B
     * + C
     * + C
     * + E
     * + F
     * <p>
     * to
     * <p>
     * + A
     * + B
     * + C
     * + C
     * + E
     * + F
     */
    @Disabled("Fails due to revision not being updated correctly") //FIXME
    @Test
    public void moveEfsElementToAnother() {
        EfsElementDTO targetEfsElement = createAndSaveEfsElement(99);
        long revisionNumber = getPartListRevision(targetEfsElement);

        moveEfsElementConsumer.moveEfsElements(targetEfsElement, persistedEfsElements());

        List<EfsElementDTO> copiedEfsElements = moveEfsElementConsumer.getResult();
        targetEfsElement = synchronize(targetEfsElement);
        evaluatePartListReversion(targetEfsElement, revisionNumber + 1);

        assertEquals(PERSISTED_EFS_ELEMENT_COUNT, copiedEfsElements.size());

        copiedEfsElements.sort(Comparator.comparing(EfsElementDTO::getPartNumber));
        for (int index = 0; index < PERSISTED_EFS_ELEMENT_COUNT; index++) {
            EfsElementDTO copiedEfsElement = copiedEfsElements.get(index);
            evaluateEfsElementReversion(revisionNumber + 1, copiedEfsElement);
            evaluateParentAndPartList(targetEfsElement, copiedEfsElement);
        }
    }

    @Disabled("Fails due to revision not being updated correctly") //FIXME
    @Test
    public void forbidfToMoveEfsElementToSameNode() {
        EfsElementDTO elementA = getOriginal(0);
        EfsElementDTO elementB = getOriginal(1);
        moveEfsElementConsumer.moveEfsElements(elementB, List.of(elementA));
        elementA = moveEfsElementConsumer.getResult().getFirst();
        elementB = synchronize(elementB);
        Long revisionNumber = getPartListRevision(elementB);

        moveEfsElementConsumer.moveEfsElements(elementB, List.of(elementA));

        evaluatePartListReversion(elementB, revisionNumber);
        assertEquals(revisionNumber, elementA.getRevision());
        moveEfsElementConsumer.getResult(EmptyListException.class);
    }

    @Test
    public void forbidToMoveEfsElementsUnderItself() {

        moveEfsElementConsumer.moveEfsElements(getOriginal(0), persistedEfsElements());

        moveEfsElementConsumer.getResult(MovingHierachyConflictException.class);
    }

    /**
     *
     */
    @Test
    public void forbidToMoveEfsElementsUnderItsChild() {
        EfsElementDTO elementA = getOriginal(0);
        EfsElementDTO elementB = getOriginal(1);
        moveEfsElementConsumer.moveEfsElements(elementA, List.of(elementB));
        elementB = moveEfsElementConsumer.getResult().getFirst();
        elementA = synchronize(elementA);

        moveEfsElementConsumer.moveEfsElements(elementB, List.of(elementA));

        moveEfsElementConsumer.getResult(MovingHierachyConflictException.class);
    }

    @Test
    public void forbidToMoveEfsElementsUnderItsChildChild() {
        EfsElementDTO elementA = getOriginal(0);
        EfsElementDTO elementB = getOriginal(1);
        EfsElementDTO elementC = getOriginal(2);
        moveEfsElementConsumer.moveEfsElements(elementA, List.of(elementB));
        elementB = moveEfsElementConsumer.getResult().getFirst();
        moveEfsElementConsumer.moveEfsElements(elementB, List.of(elementC));
        elementC = moveEfsElementConsumer.getResult().getFirst();

        moveEfsElementConsumer.moveEfsElements(elementC, List.of(synchronize(elementA)));

        moveEfsElementConsumer.getResult(MovingHierachyConflictException.class);
    }

    /**
     * +4
     * +3
     * +2
     * +1
     * <p>
     * and
     * <p>
     * +99
     * <p>
     * to
     * <p>
     * [empty part list]
     * <p>
     * and
     * <p>
     * +99
     * +4
     * +3
     * +2
     * +1
     * +0
     */
    @Disabled("Fails due to revision not being updated correctly") //FIXME
    @Test
    public void moveEfsElementToAnotherWithDifferentPartList() {
        EfsElementDTO targetEfsElement = createAndSaveEfsElement(99, emptyPartList());
        long revisionNumber = getPartListRevision(targetEfsElement);
        long originalReversion = getPartListRevision(getOriginal(0));

        moveEfsElementConsumer.moveEfsElements(targetEfsElement, persistedEfsElements());

        List<EfsElementDTO> copiedEfsElements = moveEfsElementConsumer.getResult();
        targetEfsElement = synchronize(targetEfsElement);
        evaluatePartListReversion(getOriginal(0), originalReversion + 1);
        evaluatePartListReversion(targetEfsElement, revisionNumber + 1);

        assertEquals(PERSISTED_EFS_ELEMENT_COUNT, copiedEfsElements.size());

        copiedEfsElements.sort(Comparator.comparing(EfsElementDTO::getPartNumber));
        for (int index = 0; index < PERSISTED_EFS_ELEMENT_COUNT; index++) {
            EfsElementDTO copiedEfsElement = copiedEfsElements.get(index);
            evaluateEfsElementReversion(revisionNumber + 1, copiedEfsElement);
            evaluateParentAndPartList(targetEfsElement, copiedEfsElement);
        }
    }

    @Test
    public void forbidToMoveEfsElementWithNullPartList() {
        assertThrows(IllegalArgumentException.class,
                () -> moveEfsElementConsumer.moveEfsElements(null, persistedEfsElements()));
    }

    @Test
    public void forbidToMoveEfsElementWithEmptyElements() {
        moveEfsElementConsumer.moveEfsElements(vehiclePartList(), List.of());

        moveEfsElementConsumer.getResult(EmptyListException.class);
    }

    @Test
    public void forbidToMoveEfsElementToDeletedEfsElement() {
        deleteEfsElementConsumer.deleteEfsElement(List.of(getOriginal(0)));

        moveEfsElementConsumer.moveEfsElements(getOriginal(0), List.of(getOriginal(1)));

        moveEfsElementConsumer.getResult(AppendToDeletedElementException.class);
    }

    @Test
    public void forbidToMoveEfsElementToEfsElementWithDeletedParent() {
        moveOtherBReturningNewA(getOriginal(1), getOriginal(0));
        deleteEfsElementConsumer.deleteEfsElement(List.of(getOriginal(0)));

        moveEfsElementConsumer.moveEfsElements(getOriginal(0), List.of(getOriginal(2)));

        moveEfsElementConsumer.getResult(AppendToDeletedElementException.class);
    }

    /**
     * +A(10111111111)
     * +B(10111111111)
     * <p>
     * forbid move B under A
     */
    @Test
    public void forbidToMoveElementToElementWithSameMaraAsParent() {
        EfsElementDTO parent = getOriginal(0);
        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "10111111111");
        EfsElementDTO original = PartListFactory.createEfsElement(null, mara, 0, "G", parent.getVehiclePartListId());
        saveEfsElementConsumer.saveEfsElement(original);
        original = saveEfsElementConsumer.getResult();

        moveEfsElementConsumer.moveEfsElements(parent, List.of(original));

        moveEfsElementConsumer.getResult(SameMaraInHierachyException.class);
    }

    /**
     * +A(10111111111)
     * +C(12111111111)
     * +B(10111111111)
     * <p>
     * forbid to move B under C
     */
    @Test
    public void forbidToMoveElementToElementWithSameMaraAboveInHierachy() {
        moveEfsElementConsumer.moveEfsElements(getOriginal(0), List.of(getOriginal(2)));
        EfsElementDTO parent = moveEfsElementConsumer.getResult().getFirst();
        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "10111111111");
        EfsElementDTO original = PartListFactory.createEfsElement(null, mara, 0, "G", parent.getVehiclePartListId());
        saveEfsElementConsumer.saveEfsElement(original);
        original = saveEfsElementConsumer.getResult();

        moveEfsElementConsumer.moveEfsElements(parent, List.of(original));

        moveEfsElementConsumer.getResult(SameMaraInHierachyException.class);
    }

    /**
     * +A(10111111111)
     * +B(11111111111)
     * +C(10111111111)
     * <p>
     * forbid to move B to A
     */
    @Test
    public void forbidToCopyElementWithChildToElementWithSameMaraAsChildAboveInHierachy() {
        moveEfsElementConsumer.moveEfsElements(getOriginal(1), List.of(getOriginal(0)));
        EfsElementDTO original = moveEfsElementConsumer.getResult().getFirst().getParent();
        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "10111111111");
        EfsElementDTO newParent = PartListFactory.createEfsElement(null, mara, 0, "G", original.getVehiclePartListId());
        saveEfsElementConsumer.saveEfsElement(newParent);
        newParent = saveEfsElementConsumer.getResult();

        moveEfsElementConsumer.moveEfsElements(newParent, List.of(original));

        moveEfsElementConsumer.getResult(SameMaraInHierachyException.class);
    }
}