package de.vw.paso.services.partlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;

import de.vw.paso.exception.EmptyListException;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.service.partlist.AppendToDeletedElementException;
import de.vw.paso.service.partlist.SameMaraInHierachyException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CopyEfsTests extends AbstractEfsTests {

  @Test
  public void copyEfsElementToRootWithSamePartList() {
    copyEfsElementConsumer.copyEfsElements(vehiclePartList(), persistedEfsElements());

    final List<EfsElementDTO> copiedEfsElements = copyEfsElementConsumer.getResult();
    assertEquals(PERSISTED_EFS_ELEMENT_COUNT, copiedEfsElements.size());
    copiedEfsElements.sort(Comparator.comparing(EfsElementDTO::getPartNumber));
    for (int index = 0; index < PERSISTED_EFS_ELEMENT_COUNT; index++) {
      final EfsElementDTO originalEfsElement = persistedEfsElements().get(index);
      final EfsElementDTO copiedEfsElement = copiedEfsElements.get(index);
      evaluateParentAndPartList(vehiclePartList(), copiedEfsElement);
      assertTrue(copiedEfsElement.getRevision() > originalEfsElement.getRevision());
      validateCopiedElement(originalEfsElement, copiedEfsElement);
    }
  }

  @Test
  public void copyEfsElementToRootWithDifferentPartList() {
    VehiclePartListDTO emptyVehiclePartList = emptyPartList();

    copyEfsElementConsumer.copyEfsElements(emptyVehiclePartList, persistedEfsElements());

    final List<EfsElementDTO> copiedEfsElements = copyEfsElementConsumer.getResult();
    assertEquals(PERSISTED_EFS_ELEMENT_COUNT, copiedEfsElements.size());
    copiedEfsElements.sort(Comparator.comparing(EfsElementDTO::getPartNumber));
    for (int index = 0; index < PERSISTED_EFS_ELEMENT_COUNT; index++) {
      final EfsElementDTO originalEfsElement = getOriginal(index);
      final EfsElementDTO copiedEfsElement = copiedEfsElements.get(index);
      evaluateParentAndPartList(emptyVehiclePartList, copiedEfsElement);
      validateCopiedElement(originalEfsElement, copiedEfsElement);
    }
  }

  @Test
  public void copyEfsElementToAnother() {
    EfsElementDTO targetEfsElement = createAndSaveEfsElement(99);

    copyEfsElementConsumer.copyEfsElements(targetEfsElement, persistedEfsElements());

    final List<EfsElementDTO> copiedEfsElements = copyEfsElementConsumer.getResult();
    assertEquals(PERSISTED_EFS_ELEMENT_COUNT, copiedEfsElements.size());
    targetEfsElement = synchronize(targetEfsElement);
    copiedEfsElements.sort(Comparator.comparing(EfsElementDTO::getPartNumber));
    for (int index = 0; index < PERSISTED_EFS_ELEMENT_COUNT; index++) {
      final EfsElementDTO originalEfsElement = persistedEfsElements().get(index);
      final EfsElementDTO copiedEfsElement = copiedEfsElements.get(index);
      evaluateParentAndPartList(targetEfsElement, copiedEfsElement);
      assertTrue(copiedEfsElement.getRevision() > originalEfsElement.getRevision());
      validateCopiedElement(originalEfsElement, copiedEfsElement);
    }
  }

  @Test
  public void copyEfsElementToAnotherWithDifferentPartList() {
    EfsElementDTO targetEfsElement = createAndSaveEfsElement(99, emptyPartList());

    copyEfsElementConsumer.copyEfsElements(targetEfsElement, persistedEfsElements());

    final List<EfsElementDTO> copiedEfsElements = copyEfsElementConsumer.getResult();
    targetEfsElement = synchronize(targetEfsElement);
    assertEquals(PERSISTED_EFS_ELEMENT_COUNT, copiedEfsElements.size());
    copiedEfsElements.sort(Comparator.comparing(EfsElementDTO::getPartNumber));
    for (int index = 0; index < PERSISTED_EFS_ELEMENT_COUNT; index++) {
      final EfsElementDTO originalEfsElement = getOriginal(index);
      final EfsElementDTO copiedEfsElement = copiedEfsElements.get(index);
      evaluateParentAndPartList(targetEfsElement, copiedEfsElement);
      validateCopiedElement(originalEfsElement, copiedEfsElement);
    }
  }

  @Test
  public void forbidToCopyEfsElementWithNullPartList() {
    assertThrows(IllegalArgumentException.class,
      () -> copyEfsElementConsumer.copyEfsElements(null, persistedEfsElements()));
  }

  @Test
  public void forbidToCopyEfsElementWithNullElements() {

    copyEfsElementConsumer.copyEfsElements(vehiclePartList(), null);

    copyEfsElementConsumer.getResult(NullElementException.class);
  }

  @Test
  public void forbidToCopyEfsElementWithEmptyElements() {

    copyEfsElementConsumer.copyEfsElements(vehiclePartList(), List.of());

    copyEfsElementConsumer.getResult(EmptyListException.class);
  }

  /**
   * -A
   * <p>
   * Forbid to copy [A] to same part list
   */
  @Test
  public void forbidToCopyDeletedEfsElementToSamePartList() {
    deleteEfsElementConsumer.deleteEfsElement(List.of(getOriginal(1)));

    copyEfsElementConsumer.copyEfsElements(getOriginal(0), List.of(getOriginal(1)));
    copyEfsElementConsumer.getResult(EmptyListException.class);

    assertEquals(null, getOriginal(1).getParent());
  }

  /**
   * -A
   * <p>
   * Forbid to copy [A] to different part list
   */
  @Test
  public void forbidToCopyDeletedEfsElementToDifferentPartList() {
    deleteEfsElementConsumer.deleteEfsElement(List.of(getOriginal(1)));

    copyEfsElementConsumer.copyEfsElements(emptyPartList(), List.of(getOriginal(1)));
    copyEfsElementConsumer.getResult(EmptyListException.class);

    assertEquals(null, getOriginal(1).getParent());
  }

  /**
   * +A
   * +B
   * +C
   * -D
   * <p>
   * copy [C] to same part list
   * <p>
   * +A
   * +B
   * +C
   * -D
   * +C'
   */
  @Disabled("Fails due to revision not being updated correctly") //FIXME
  @Test
  public void copyHierachyWithDeletedEfsElementSamePartList_dontCopyDeletedElements() {
    final EfsElementDTO elementA = getOriginal(4);
    moveEfsElementConsumer.moveEfsElements(elementA, List.of(getOriginal(3)));
    final EfsElementDTO elementB = moveEfsElementConsumer.getResult().get(0);
    moveEfsElementConsumer.moveEfsElements(elementB, List.of(getOriginal(2)));
    final EfsElementDTO elementC = moveEfsElementConsumer.getResult().get(0);
    moveEfsElementConsumer.moveEfsElements(elementC, List.of(getOriginal(1)));
    final EfsElementDTO elementD = moveEfsElementConsumer.getResult().get(0);
    deleteEfsElementConsumer.deleteEfsElement(List.of(elementD));
    initialRevision = vehiclePartList().getRevision();

    copyEfsElementConsumer.copyEfsElements(elementA, List.of(elementC));

    final List<EfsElementDTO> result = copyEfsElementConsumer.getResult();
    evaluateRevisionVehiclePartList(initialRevision + 1);
    assertEquals(1, result.size());
    evaluateEfsElementReversion(initialRevision + 1, result.get(0));

  }

  /**
   * +A
   * -B
   * <p>
   * copy [A] to different part list
   * <p>
   * +A
   */
  @Test
  public void copyHierachyWithDeletedEfsElementDifferentPartList_dontCopyDeletedElements() {
    final EfsElementDTO elementA = getOriginal(4);
    moveEfsElementConsumer.moveEfsElements(elementA, List.of(getOriginal(3)));
    final EfsElementDTO elementB = moveEfsElementConsumer.getResult().get(0);
    deleteEfsElementConsumer.deleteEfsElement(List.of(elementB));
    initialRevision = emptyPartList().getRevision();

    copyEfsElementConsumer.copyEfsElements(emptyPartList(), List.of(elementA));

    final List<EfsElementDTO> result = copyEfsElementConsumer.getResult();
    evaluateRevisionEmptyPartList(initialRevision + 1);
    assertEquals(1, result.size());
    evaluateEfsElementReversion(initialRevision + 1, result.get(0));

  }

  /**
   * -A
   * +B
   * <p>
   * forbid to copy [B] under [A]
   */
  @Test
  public void forbidToCopyEfsElementToDeletedEfsElement() {
    deleteEfsElementConsumer.deleteEfsElement(List.of(getOriginal(0)));

    copyEfsElementConsumer.copyEfsElements(getOriginal(0), List.of(getOriginal(1)));

    copyEfsElementConsumer.getResult(AppendToDeletedElementException.class);
  }

  /**
   * -A
   * ?B
   * +C
   * <p>
   * forbid to move [C] under [B]
   */
  @Test
  public void forbidToCopyEfsElementToEfsElementWithDeletedParent() {
    final EfsElementDTO elementA = getOriginal(4);
    moveEfsElementConsumer.moveEfsElements(elementA, List.of(getOriginal(3)));
    final EfsElementDTO elementB = moveEfsElementConsumer.getResult().get(0);
    deleteEfsElementConsumer.deleteEfsElement(List.of(elementA));

    copyEfsElementConsumer.copyEfsElements(synchronize(elementB), List.of(getOriginal(2)));

    copyEfsElementConsumer.getResult(AppendToDeletedElementException.class);
  }

  /**
   * +A(10111111111)
   * +B(10111111111)
   * <p>
   * forbid copy B under A
   */
  @Test
  public void forbidToCopyElementToElementWithSameMaraAsParent() {
    EfsElementDTO parent = getOriginal(0);
    EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "10111111111");
    EfsElementDTO original = PartListFactory.createEfsElement(null, mara, 0, "G", parent.getVehiclePartListId());
    saveEfsElementConsumer.saveEfsElement(original);
    original = saveEfsElementConsumer.getResult();

    copyEfsElementConsumer.copyEfsElements(parent, List.of(original));

    copyEfsElementConsumer.getResult(SameMaraInHierachyException.class);
  }

  /**
   * +A(10111111111)
   * +C(12111111111)
   * +B(10111111111)
   * <p>
   * forbid to copy B under C
   */
  @Test
  public void forbidToCopyElementToElementWithSameMaraAboveInHierachy() {
    moveEfsElementConsumer.moveEfsElements(getOriginal(0), List.of(getOriginal(2)));
    EfsElementDTO parent = moveEfsElementConsumer.getResult().get(0);
    EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "10111111111");
    EfsElementDTO original = PartListFactory.createEfsElement(null, mara, 0, "G", parent.getVehiclePartListId());
    saveEfsElementConsumer.saveEfsElement(original);
    original = saveEfsElementConsumer.getResult();

    copyEfsElementConsumer.copyEfsElements(parent, List.of(original));

    copyEfsElementConsumer.getResult(SameMaraInHierachyException.class);
  }

  /**
   * +A(10111111111)
   * +B(11111111111)
   * +C(10111111111)
   * <p>
   * forbid to copy B under A
   */
  @Test
  public void forbidToCopyElementWithChildToElementWithSameMaraAsChildAboveInHierachy() {
    moveEfsElementConsumer.moveEfsElements(getOriginal(1), List.of(getOriginal(0)));
    EfsElementDTO original = moveEfsElementConsumer.getResult().get(0).getParent();
    EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1", "10111111111");
    EfsElementDTO newParent = PartListFactory.createEfsElement(null, mara, 0, "G", original.getVehiclePartListId());
    saveEfsElementConsumer.saveEfsElement(newParent);
    newParent = saveEfsElementConsumer.getResult();

    copyEfsElementConsumer.copyEfsElements(newParent, List.of(original));

    copyEfsElementConsumer.getResult(SameMaraInHierachyException.class);
  }
}
