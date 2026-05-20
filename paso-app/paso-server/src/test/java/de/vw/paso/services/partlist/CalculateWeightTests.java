package de.vw.paso.services.partlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.vw.paso.mapper.VehiclePartListMapper;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.utility.EfsElementResolver;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CalculateWeightTests extends AbstractEfsTests {

    /**
     * + A
     * + B
     * + C
     * + D
     * + E
     */
    @Test
    public void calculatePartListWeightJustRootNodes() {
        List<EfsElementDTO> elements = persistedEfsElements();
        for (int i = 0; i < elements.size(); i++) {
            EfsElementDTO element = elements.get(i);
            element.setWeightControlFlag(WeightControlFlag.YES);
            element.setEntityChange(true);
            element.setQuantity(i + 1);
            element.setQuantityUnit("G");
            element.getEfsElementMara().setWeightWeightedTe(i + 1.0);
            element.getEfsElementMara().setWeightCalculatedTe(i + 2.0);
            saveEfsElementConsumer.saveEfsElement(element);
        }

        Map<Long, Double> result = efsWeightManager.calculateWeight(
                VehiclePartListMapper.toEntity(vehiclePartList(), null));

        assertEquals(1, result.get(elements.get(0).getId()), 0.0);
        assertEquals(4, result.get(elements.get(1).getId()), 0.0);
        assertEquals(9, result.get(elements.get(2).getId()), 0.0);
        assertEquals(16, result.get(elements.get(3).getId()), 0.0);
        assertEquals(25, result.get(elements.get(4).getId()), 0.0);
        assertEquals(55, result.get(Long.MIN_VALUE), 0.0);
    }

    @Test
    public void calculatePartListWeightEmptyPartList() {
        Map<Long, Double> result = efsWeightManager.calculateWeight(
                VehiclePartListMapper.toEntity(emptyPartList(), null));

        assertEquals(0, result.get(Long.MIN_VALUE), 0.0);
    }

    /**
     * + A
     * + C
     * + E
     * + B
     * + D
     */
    @Disabled("Fails due to unexpected weight") //FIXME
    @Test
    public void calculatePartListWeightWithElementsInHierarchy() {
        List<EfsElementDTO> updatedElements = new ArrayList<>();
        int[] results = { 35, 20, 9, 16, 25 };
        for (int i = 0; i < 5; i++) {
            EfsElementDTO element = getOriginal(i);
            element.setWeightControlFlag(WeightControlFlag.YES);
            element.setEntityChange(true);
            element.setQuantity(i + 1);
            element.setQuantityUnit("G");
            element.getEfsElementMara().setWeightWeightedTe(i + 1.0);
            saveEfsElementConsumer.saveEfsElement(element);
            if (i > 1) {
                moveEfsElementConsumer.moveEfsElements(updatedElements.get(i % 2),
                        List.of(saveEfsElementConsumer.getResult()));
                updatedElements.addAll(moveEfsElementConsumer.getResult());
            } else {
                updatedElements.add(saveEfsElementConsumer.getResult());
            }
        }
        for (int i = 0; i < updatedElements.size(); i++) {
            assertEquals(results[i], updatedElements.get(i).getNodeWeight(), 0.0);
        }

        Map<Long, Double> result = efsWeightManager.calculateWeight(
                VehiclePartListMapper.toEntity(vehiclePartList(), null));

        for (int i = 0; i < updatedElements.size(); i++) {
            assertEquals(results[i], result.get(updatedElements.get(i).getId()), 0.0);
        }
        assertEquals(55, result.get(Long.MIN_VALUE), 0.0);
    }

    /**
     * + A
     * - B
     * + C
     * - D
     * + E
     */
    @Test
    public void calculatePartListWeightWithSomeDeletedElementsInRoot() {
        List<EfsElementDTO> elements = persistedEfsElements();
        for (int i = 0; i < elements.size(); i++) {
            EfsElementDTO element = elements.get(i);
            element.setWeightControlFlag(WeightControlFlag.YES);
            element.setEntityChange(true);
            element.setQuantity(i + 1);
            element.setQuantityUnit("G");
            element.getEfsElementMara().setWeightWeightedTe(i + 1.0);
            saveEfsElementConsumer.saveEfsElement(element);
            saveEfsElementConsumer.getResult();
            if (i % 2 == 1) {
                deleteEfsElementConsumer.deleteEfsElement(List.of(element));
                deleteEfsElementConsumer.getResult();
            }
        }

        Map<Long, Double> result = efsWeightManager.calculateWeight(
                VehiclePartListMapper.toEntity(vehiclePartList(), null));

        assertEquals(1, result.get(elements.get(0).getId()), 0.0);
        assertEquals(4, result.get(elements.get(1).getId()), 0.0);
        assertEquals(9, result.get(elements.get(2).getId()), 0.0);
        assertEquals(16, result.get(elements.get(3).getId()), 0.0);
        assertEquals(25, result.get(elements.get(4).getId()), 0.0);
        assertEquals(35, result.get(Long.MIN_VALUE), 0.0);
    }

    /**
     * + A
     * + B
     * + C
     * - D
     * - E
     */
    @Test
    public void calculatePartListWeightWithDeletedElementsInHierarchy() {
        List<EfsElementDTO> elements = persistedEfsElements();
        for (int i = 0; i < elements.size(); i++) {
            EfsElementDTO element = elements.get(i);
            element.setWeightControlFlag(WeightControlFlag.YES);
            element.setEntityChange(true);
            element.setQuantity(i + 1);
            element.setQuantityUnit("G");
            element.getEfsElementMara().setWeightWeightedTe(i + 1.0);
            if (i > 0) {
                element.setParentId(elements.get(i - 1).getId());
            }

            saveEfsElementConsumer.saveEfsElement(element);
            saveEfsElementConsumer.getResult();
            if (i >= 3) {
                deleteEfsElementConsumer.deleteEfsElement(List.of(element));
                deleteEfsElementConsumer.getResult();
            }
        }

        Map<Long, Double> result = efsWeightManager.calculateWeight(
                VehiclePartListMapper.toEntity(vehiclePartList(), null));

        assertEquals(14, result.get(elements.get(0).getId()), 0.0);
        assertEquals(13, result.get(elements.get(1).getId()), 0.0);
        assertEquals(9, result.get(elements.get(2).getId()), 0.0);
        assertEquals(14, result.get(Long.MIN_VALUE), 0.0);
    }

    @Test
    public void updateWeightForEditedElementRoot() {
        EfsElementDTO element = getOriginal(0);
        element.getEfsElementMara().setWeightWeightedTe(8.0);
        saveEfsElementConsumer.saveEfsElement(element);
        assertNodeWeight(888, saveEfsElementConsumer.getResult());

        element = getOriginal(0);
        element.getEfsElementMara().setWeightWeightedTe(6.0);
        saveEfsElementConsumer.saveEfsElement(element);

        assertNodeWeight(666, saveEfsElementConsumer.getResult());
    }

    @Test
    public void updateWeightForEditedElementLeafAmountIncrease() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);

        child.setQuantity(200);
        saveEfsElementConsumer.saveEfsElement(child);
        child = saveEfsElementConsumer.getResult();

        assertNodeWeight(801, child.getParent());
        assertNodeWeight(800, child);

    }

    @Test
    public void updateWeightForEditedElementLeafAmountDecrease() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);

        child.setQuantity(10);
        saveEfsElementConsumer.saveEfsElement(child);
        child = saveEfsElementConsumer.getResult();

        assertNodeWeight(41.0, child.getParent());
        assertNodeWeight(40.0, child);
    }

    private EfsElementDTO createSimpleHierarchy(double childWeight, WeightControlFlag weightControlFlag) {
        EfsElementDTO parent = getOriginal(0);
        parent.getEfsElementMara().setWeightWeightedTe(1.0);
        parent.setQuantity(1);
        saveEfsElementConsumer.saveEfsElement(parent);
        parent = saveEfsElementConsumer.getResult();
        EfsElementDTO child = getOriginal(1);
        child.getEfsElementMara().setWeightWeightedTe(childWeight);
        child.setWeightControlFlag(weightControlFlag);
        saveEfsElementConsumer.saveEfsElement(child);
        moveEfsElementConsumer.moveEfsElements(parent, List.of(saveEfsElementConsumer.getResult()));
        return moveEfsElementConsumer.getResult().getFirst();
    }

    @Test
    public void updateWeightForEditedElementLeafChangeCalculationStrategyToJ() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.NO);
        assertNodeWeight(1, child.getParent());
        assertNodeWeight(0, child);

        child.setWeightControlFlag(WeightControlFlag.YES);
        saveEfsElementConsumer.saveEfsElement(child);
        child = saveEfsElementConsumer.getResult();

        assertNodeWeight(445.0, child.getParent());
        assertNodeWeight(444.0, child);
    }

    @Test
    public void updateWeightForEditedElementLeafChangeCalculationStrategyToN() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445.0, child.getParent());
        assertNodeWeight(444.0, child);

        child.setWeightControlFlag(WeightControlFlag.NO);
        saveEfsElementConsumer.saveEfsElement(child);
        child = saveEfsElementConsumer.getResult();

        assertNodeWeight(1.0, child.getParent());
        assertNodeWeight(0.0, child);
    }

    @Test
    public void updateWeightForEditedElementLeafWeightIncrease() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);

        child.getEfsElementMara().setWeightWeightedTe(8.0);
        saveEfsElementConsumer.saveEfsElement(child);
        child = saveEfsElementConsumer.getResult();

        assertNodeWeight(889.0, child.getParent());
        assertNodeWeight(888.0, child);
    }

    @Test
    public void updateWeightForEditedElementLeafWeightDecrease() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);

        child.getEfsElementMara().setWeightWeightedTe(0.5);
        saveEfsElementConsumer.saveEfsElement(child);
        child = saveEfsElementConsumer.getResult();

        assertNodeWeight(56.5, child.getParent());
        assertNodeWeight(55.5, child);
    }

    @Test
    public void updateWeightForEditedElementLeafRemovingHigherPrioritizedWeightAttribute() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        child.getEfsElementMara().setWeightWeightedProd(2.0);
        saveEfsElementConsumer.saveEfsElement(child);
        child = synchronize(child);
        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);

        child.getEfsElementMara().setWeightWeightedTe(0.0);
        saveEfsElementConsumer.saveEfsElement(child);

        child = synchronize(child);
        assertNodeWeight(223, child.getParent());
        assertNodeWeight(222, child);
    }

    @Test
    public void updateWeightForEditedElementLeafAddingHigherPrioritizedWeightAttribute() {
        EfsElementDTO child = createSimpleHierarchy(0, WeightControlFlag.YES);
        child.getEfsElementMara().setWeightWeightedProd(4.0);
        saveEfsElementConsumer.saveEfsElement(child);
        child = synchronize(child);
        assertWeight(1, child.getParent());
        assertNodeWeight(445, child.getParent());

        assertWeight(444, child);
        assertNodeWeight(444, child);

        child.getEfsElementMara().setWeightWeightedTe(2.0);
        saveEfsElementConsumer.saveEfsElement(child);

        child = synchronize(child);
        assertNodeWeight(223, child.getParent());
        assertNodeWeight(222, child);

    }

    /**
     * +1
     * +2 (same mara)
     * +6 (same mara)
     */
    @Test
    public void updateWeightOfHierarchyChildChangeMultipleUsedMara() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        EfsElementDTO elementWithSameMara = createAndSaveEfsElement(11, vehiclePartList());
        assertNodeWeight(444, elementWithSameMara);
        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);

        child.getEfsElementMara().setWeightWeightedTe(5.0);
        saveEfsElementConsumer.saveEfsElement(child);
        child = saveEfsElementConsumer.getResult();

        assertNodeWeight(556, child.getParent());
        assertNodeWeight(555, child);
        assertNodeWeight(555, synchronize(elementWithSameMara));
    }

    /**
     * +1
     * +2 (same mara)
     * +6 (same mara)
     */
    @Test
    public void updateWeightOfHierarchyRootChangeMultipleUsedMara() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        EfsElementDTO elementWithSameMara = createAndSaveEfsElement(11, vehiclePartList());
        assertNodeWeight(444, elementWithSameMara);
        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);

        elementWithSameMara.getEfsElementMara().setWeightWeightedTe(5.0);
        saveEfsElementConsumer.saveEfsElement(elementWithSameMara);
        elementWithSameMara = saveEfsElementConsumer.getResult();
        child = synchronize(child);

        assertNodeWeight(555, child);
        assertNodeWeight(556, child.getParent());
        assertNodeWeight(555, elementWithSameMara);
    }

    /**
     * +1
     * -2 (same mara)
     * +6 (same mara)
     */
    @Disabled("fails as weight of deleted child is 0") //FIXME
    @Test
    public void updateWeightOfHierarchyRootChangeMultipleUsedMaraDeleted() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        deleteEfsElementConsumer.deleteEfsElement(List.of(child));
        child = deleteEfsElementConsumer.getResult().getFirst();
        EfsElementDTO elementWithSameMara = createAndSaveEfsElement(11, vehiclePartList());
        assertNodeWeight(444, elementWithSameMara);
        assertNodeWeight(1, child.getParent());
        assertNodeWeight(444, child);

        elementWithSameMara.getEfsElementMara().setWeightWeightedTe(5.0);
        saveEfsElementConsumer.saveEfsElement(elementWithSameMara);
        elementWithSameMara = saveEfsElementConsumer.getResult();
        child = synchronize(child);

        assertNodeWeight(555, child);
        assertNodeWeight(1, child.getParent());
        assertNodeWeight(555, elementWithSameMara);
    }

    /**
     * +1
     * +2 (same mara) making this smaller
     * +6 (same mara)
     */
    //REQ Should this be possible? Using nodes with the same mara in itself? Should all actions (move,copy, create, edit) forbid this?
    @Test
    @Disabled
    public void decreaseWeightOfHierarchyChildChangeMultipleUsedMara() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        EfsElementDTO elementWithSameMara = createAndSaveEfsElement(11, vehiclePartList());
        moveEfsElementConsumer.moveEfsElements(child, List.of(elementWithSameMara));
        elementWithSameMara = moveEfsElementConsumer.getResult().getFirst();
        child = synchronize(child);
        assertNodeWeight(444, elementWithSameMara);
        assertNodeWeight(889, child.getParent());
        assertNodeWeight(888, child);

        child.getEfsElementMara().setWeightWeightedTe(2.0);
        saveEfsElementConsumer.saveEfsElement(child);
        child = saveEfsElementConsumer.getResult();

        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);
        assertNodeWeight(222, synchronize(elementWithSameMara));
    }

    @Test
    public void updateWeightChangePartNumberToNewPartNumber() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        EfsElementDTO nodeSameMaraBeforeChanging = createAndSaveEfsElement(11, vehiclePartList());
        assertNodeWeight(444, nodeSameMaraBeforeChanging);
        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);

        EfsElementMaraDTO newMara = PartListFactory.createEfsElementMara("NewPartNumber", "N1212345678");
        newMara.setWeightWeightedTe(2.0);
        child.setEfsElementMara(newMara);
        saveEfsElementConsumer.saveEfsElement(child);
        child = synchronize(child);

        assertNodeWeight(223, child.getParent());
        assertNodeWeight(222, child);
        assertNodeWeight(444, synchronize(nodeSameMaraBeforeChanging));
    }

    @Test
    public void updateWeightChangePartNumberToExistingPartNumber() {
        EfsElementDTO parent = getOriginal(2);
        parent.getEfsElementMara().setWeightWeightedTe(1.0);
        saveEfsElementConsumer.saveEfsElement(parent);
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        EfsElementDTO nodeSameMaraBeforeChanging = createAndSaveEfsElement(11, vehiclePartList());
        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);
        assertNodeWeight(444, nodeSameMaraBeforeChanging);

        EfsElementMaraDTO newMara = PartListFactory.createEfsElementMara("NewPartNumber", "12111111111");
        child.setEfsElementMara(newMara);
        saveEfsElementConsumer.saveEfsElement(child);
        child = saveEfsElementConsumer.getResult();

        assertNodeWeight(112, child.getParent());
        assertNodeWeight(111, child);
        assertNodeWeight(444, synchronize(nodeSameMaraBeforeChanging));
    }

    @Disabled("fails as weight of deleted child is 0") //FIXME
    @Test
    public void updateWeightForEditedElementLeafDelete() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);

        child.getEfsElementMara().setWeightWeightedTe(2.0);
        deleteEfsElementConsumer.deleteEfsElement(List.of(child));

        child = synchronize(child);
        assertNodeWeight(1, child.getParent());
        assertNodeWeight(444, child);
    }

    /**
     * +0
     * +1
     * +2
     * +3
     * +4
     * <p>
     * moving [0] under [2]
     * <p>
     * -0
     * -1
     * +2
     * +0
     * +1
     * +3
     * +4
     */
    @Test
    public void updateWeightForMovingHierarchyInsidePartList_FromRoot_ToAnotherNode() {
        EfsElementDTO child = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445, child.getParent());
        assertNodeWeight(444, child);
        child = synchronize(child);

        EfsElementDTO newParent = getOriginal(2);
        moveEfsElementConsumer.moveEfsElements(newParent, List.of(child.getParent()));
        List<EfsElementDTO> movedElements = moveEfsElementConsumer.getResult();

        assertFalse(movedElements.getFirst().isDeleted());
        assertEquals(child.getParentId(), movedElements.getFirst().getId());
        assertEquals(synchronize(newParent), movedElements.getFirst().getParent());
        assertEquals(synchronize(child.getParent()).getId(), synchronize(child).getParentId());
        assertNodeWeight(445, synchronize(newParent));
        assertNodeWeight(445, synchronize(child.getParent()));
        assertNodeWeight(444, synchronize(child));
    }

    /**
     * +2
     * +0
     * +1
     * +3
     * +4
     * <p>
     * moving [0] under [3]
     * <p>
     * +2
     * +3
     * +0
     * +1
     * +4
     */
    @Test
    public void updateWeightForMovingHierarchyInsidePartList_Parent_ToAnotherNode() {
        EfsElementDTO leaf = createSimpleHierarchy(4, WeightControlFlag.YES);
        EfsElementDTO oldParent = getOriginal(2);
        moveEfsElementConsumer.moveEfsElements(oldParent, List.of(leaf.getParent()));
        leaf = synchronize(leaf);
        oldParent = synchronize(oldParent);
        assertNodeWeight(445, oldParent);
        assertNodeWeight(445, leaf.getParent());
        assertNodeWeight(444, leaf);

        EfsElementDTO newParent = getOriginal(3);
        moveEfsElementConsumer.moveEfsElements(newParent, List.of(leaf.getParent()));

        List<EfsElementDTO> movedElements = moveEfsElementConsumer.getResult();
        assertEquals(1, movedElements.size());
        assertFalse(movedElements.getFirst().isDeleted());
        assertEquals(leaf.getParentId(), movedElements.getFirst().getId());
        assertEquals(synchronize(newParent), movedElements.getFirst().getParent());
        assertEquals(synchronize(leaf.getParent()).getId(), synchronize(leaf).getParentId());
        assertNodeWeight(445, synchronize(newParent));
        assertNodeWeight(445, synchronize(leaf.getParent()));
        assertNodeWeight(444, synchronize(leaf));
        assertNodeWeight(0, synchronize(oldParent));
    }

    /**
     * +2
     * +0
     * +1
     * +3
     * +4
     * <p>
     * moving [0] under [3]
     * <p>
     * +2
     * +3
     * +4
     * +0
     * +1
     */
    @Test
    public void updateWeightForMovingHierarchyInsidePartList_Parent_ToRoot() {
        EfsElementDTO leaf = createSimpleHierarchy(4, WeightControlFlag.YES);
        EfsElementDTO oldParent = getOriginal(2);
        moveEfsElementConsumer.moveEfsElements(oldParent, List.of(leaf.getParent()));
        leaf = synchronize(leaf);
        oldParent = synchronize(oldParent);
        assertNodeWeight(445, oldParent);
        assertNodeWeight(445, leaf.getParent());
        assertNodeWeight(444, leaf);

        moveEfsElementConsumer.moveEfsElements(vehiclePartList(), List.of(leaf.getParent()));

        List<EfsElementDTO> movedElements = moveEfsElementConsumer.getResult();
        assertEquals(1, movedElements.size());
        assertEquals(1, movedElements.size());
        assertFalse(movedElements.getFirst().isDeleted());
        assertEquals(leaf.getParentId(), movedElements.getFirst().getId());
        assertNull(movedElements.getFirst().getParent());
        assertEquals(synchronize(leaf.getParent()).getId(), synchronize(leaf).getParentId());
        assertNodeWeight(445, synchronize(leaf.getParent()));
        assertNodeWeight(444, synchronize(leaf));
        assertNodeWeight(0, synchronize(oldParent));
    }

    /**
     * +2
     * +0
     * +1
     * +3
     * +4
     * <p>
     * moving [0] to the empty part list
     */
    @Test
    public void updateWeightForMovingHierarchyDifferentPartList_ToRoot() {
        EfsElementDTO leaf = createSimpleHierarchy(4, WeightControlFlag.YES);
        EfsElementDTO oldParent = getOriginal(2);
        VehiclePartListDTO emptyVehiclePartList = emptyPartList();
        moveEfsElementConsumer.moveEfsElements(oldParent, List.of(leaf.getParent()));
        leaf = synchronize(leaf);
        oldParent = synchronize(oldParent);
        assertNodeWeight(445, oldParent);
        assertNodeWeight(445, leaf.getParent());
        assertNodeWeight(444, leaf);
        assertEquals(Double.valueOf(0),
                efsWeightManager.calculateWeight(VehiclePartListMapper.toEntity(emptyVehiclePartList, null))
                        .get(Long.MIN_VALUE));

        moveEfsElementConsumer.moveEfsElements(emptyVehiclePartList, List.of(leaf.getParent()));
        assertEquals(Double.valueOf(445),
                efsWeightManager.calculateWeight(VehiclePartListMapper.toEntity(emptyVehiclePartList, null))
                        .get(Long.MIN_VALUE));

        List<EfsElementDTO> movedElements = moveEfsElementConsumer.getResult();
        assertEquals(4, movedElements.size());
        assertFalse(movedElements.get(0).isDeleted() || movedElements.get(1).isDeleted());
        assertTrue(movedElements.get(2).isDeleted() && movedElements.get(3).isDeleted());
        assertEquals(Long.valueOf(leaf.getId() + 4), movedElements.get(1).getId());
        assertEquals(Long.valueOf(leaf.getId() + 5), movedElements.get(0).getId());
        assertNull(movedElements.get(1).getParentId());
        assertEquals(movedElements.get(1), movedElements.get(0).getParent());
        assertNodeWeight(445, movedElements.get(1));
        assertNodeWeight(444, movedElements.get(0));
        assertNodeWeight(0, synchronize(oldParent));
    }

    /**
     * +2
     * +0
     * +1
     * +3
     * +4
     * <p>
     * moving [0] to the empty part list
     */
    @Test
    public void updateWeightForMovingHierarchyDifferentPartList_ToAnother() {
        EfsElementDTO leaf = createSimpleHierarchy(4, WeightControlFlag.YES);
        EfsElementDTO oldParent = getOriginal(2);
        VehiclePartListDTO emptyVehiclePartList = emptyPartList();
        moveEfsElementConsumer.moveEfsElements(oldParent, List.of(leaf.getParent()));
        leaf = synchronize(leaf);
        oldParent = synchronize(oldParent);

        EfsElementDTO newParent = createAndSaveEfsElement(76, emptyVehiclePartList);
        assertNodeWeight(445, oldParent);
        assertNodeWeight(445, leaf.getParent());
        assertNodeWeight(444, leaf);

        moveEfsElementConsumer.moveEfsElements(newParent, List.of(leaf.getParent()));
        assertEquals(Double.valueOf(445),
                efsWeightManager.calculateWeight(VehiclePartListMapper.toEntity(emptyVehiclePartList, null))
                        .get(Long.MIN_VALUE));

        newParent = synchronize(newParent);
        List<EfsElementDTO> movedAndDeletedElements = moveEfsElementConsumer.getResult();
        assertEquals(4, movedAndDeletedElements.size());
        assertFalse(movedAndDeletedElements.get(0).isDeleted() || movedAndDeletedElements.get(1).isDeleted());
        assertTrue(movedAndDeletedElements.get(2).isDeleted() || movedAndDeletedElements.get(3).isDeleted());
        assertEquals(Long.valueOf(leaf.getId() + 5), movedAndDeletedElements.get(1).getId());
        assertEquals(Long.valueOf(leaf.getId() + 6), movedAndDeletedElements.get(0).getId());
        assertEquals(leaf.getId(), movedAndDeletedElements.get(2).getId());
        assertEquals(Long.valueOf(leaf.getId() - 1), movedAndDeletedElements.get(3).getId());
        assertEquals(newParent, movedAndDeletedElements.get(1).getParent());
        assertEquals(movedAndDeletedElements.get(1), movedAndDeletedElements.get(0).getParent());
        assertNodeWeight(445, newParent);
        assertNodeWeight(445, movedAndDeletedElements.get(1));
        assertNodeWeight(444, movedAndDeletedElements.get(0));
        assertNodeWeight(0, synchronize(oldParent));
    }

    /**
     * +2
     * +0
     * +1
     * +3
     * <p>
     * copy [0] under [3]
     * <p>
     * +2
     * +0
     * +1
     * +3
     * +0'
     * +1'
     */
    @Test
    public void updateWeightCopyHierarchyInsidePartList_Parent_To_AnotherNode() {
        EfsElementDTO leaf = createSimpleHierarchy(4, WeightControlFlag.YES);
        EfsElementDTO parent = getOriginal(2);
        moveEfsElementConsumer.moveEfsElements(parent, List.of(leaf.getParent()));
        leaf = synchronize(leaf);
        parent = synchronize(parent);
        assertNodeWeight(445, parent);
        assertNodeWeight(445, leaf.getParent());
        assertNodeWeight(444, leaf);

        EfsElementDTO newParent = getOriginal(3);
        copyEfsElementConsumer.copyEfsElements(newParent, List.of(leaf.getParent()));

        List<EfsElementDTO> copiedList = copyEfsElementConsumer.getResult();
        EfsElementDTO copiedLeaf = synchronize(leaf);
        EfsElementDTO copiedParent = synchronize(leaf.getParent());
        assertEquals(2, copiedList.size());
        assertFalse(copiedLeaf.isDeleted() || copiedParent.isDeleted());
        assertFalse(copiedList.get(0).isDeleted() || copiedList.get(1).isDeleted());
        assertEquals(leaf.getParent().getId(), copiedParent.getId());
        assertEquals(leaf.getId(), copiedLeaf.getId());
        assertEquals(Long.valueOf(leaf.getId() + 4), copiedList.get(1).getId());
        assertEquals(Long.valueOf(leaf.getId() + 5), copiedList.get(0).getId());
        assertEquals(synchronize(newParent).getId(),
                EfsElementResolver.getElement(copiedList.get(1).getParentId()).getId());
        assertEquals(copiedList.get(1).getId(), EfsElementResolver.getElement(copiedList.get(0).getParentId()).getId());
        assertNodeWeight(445, synchronize(newParent));
        assertNodeWeight(445, copiedList.get(1));
        assertNodeWeight(444, copiedList.get(0));
        assertNodeWeight(445, copiedParent);
        assertNodeWeight(444, copiedLeaf);
    }

    /**
     * +2
     * +0
     * +1
     * <p>
     * copy [0] under [2] again
     * <p>
     * +2
     * +0
     * +1
     * +0'
     * +1'
     */
    @Test
    public void updateWeightCopyHierarchyInsidePartList_Parent_To_SameNode() {
        EfsElementDTO leaf = createSimpleHierarchy(4, WeightControlFlag.YES);
        EfsElementDTO parent = getOriginal(2);
        moveEfsElementConsumer.moveEfsElements(parent, List.of(leaf.getParent()));
        leaf = synchronize(leaf);
        parent = synchronize(parent);
        assertNodeWeight(445, parent);
        assertNodeWeight(445, leaf.getParent());
        assertNodeWeight(444, leaf);

        copyEfsElementConsumer.copyEfsElements(parent, List.of(leaf.getParent()));

        List<EfsElementDTO> copiedList = copyEfsElementConsumer.getResult();
        EfsElementDTO copiedLeaf = synchronize(leaf);
        EfsElementDTO copiedParent = synchronize(leaf.getParent());
        assertEquals(2, copiedList.size());
        assertFalse(copiedLeaf.isDeleted() || copiedParent.isDeleted());
        assertFalse(copiedList.get(0).isDeleted() || copiedList.get(1).isDeleted());
        assertEquals(leaf.getParent().getId(), copiedParent.getId());
        assertEquals(leaf.getId(), copiedLeaf.getId());
        assertEquals(Long.valueOf(leaf.getId() + 4), copiedList.get(1).getId());
        assertEquals(Long.valueOf(leaf.getId() + 5), copiedList.get(0).getId());
        assertEquals(synchronize(parent).getId(), copiedList.get(1).getParent().getId());
        assertEquals(copiedList.get(1).getId(), copiedList.get(0).getParent().getId());
        assertNodeWeight(890, synchronize(parent));
        assertNodeWeight(445, copiedList.get(1));
        assertNodeWeight(444, copiedList.get(0));
        assertNodeWeight(445, copiedParent);
        assertNodeWeight(444, copiedLeaf);
    }

    /**
     * +0
     * +1
     * +2
     * <p>
     * copy [0] under root
     * <p>
     * +0
     * +1
     * +2
     * +0'
     * +1'
     */
    @Test
    public void updateWeightCopyHierarchyInsidePartList_Parent_To_Root() {
        EfsElementDTO leaf = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445, leaf.getParent());
        assertNodeWeight(444, leaf);

        copyEfsElementConsumer.copyEfsElements(vehiclePartList(), List.of(leaf.getParent()));

        List<EfsElementDTO> copiedList = copyEfsElementConsumer.getResult();
        EfsElementDTO copiedLeaf = synchronize(leaf);
        EfsElementDTO copiedParent = synchronize(leaf.getParent());
        assertEquals(2, copiedList.size());
        assertFalse(copiedLeaf.isDeleted());
        assertFalse(copiedParent.isDeleted());
        assertFalse(copiedList.get(0).isDeleted());
        assertFalse(copiedList.get(1).isDeleted());
        assertEquals(leaf.getParent().getId(), copiedParent.getId());
        assertEquals(leaf.getId(), copiedLeaf.getId());
        assertEquals(Long.valueOf(leaf.getId() + 4), copiedList.get(1).getId());
        assertEquals(Long.valueOf(leaf.getId() + 5), copiedList.get(0).getId());
        assertNull(copiedList.get(1).getParent());
        assertEquals(copiedList.get(1), copiedList.get(0).getParent());
        assertNodeWeight(445, copiedList.get(1));
        assertNodeWeight(444, copiedList.get(0));
        assertNodeWeight(445, copiedParent);
        assertNodeWeight(444, copiedLeaf);
    }

    /**
     * +0
     * +1
     * +2
     * <p>
     * copy [0] under [2]
     * <p>
     * +0
     * +1
     * +2
     * +0'
     * +1'
     */
    @Disabled("Fails due to unexpected weight") //FIXME
    @Test
    public void updateWeightCopyHierarchyInsidePartList_Root_To_AnotherNode() {
        EfsElementDTO leaf = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445, leaf.getParent());
        assertNodeWeight(444, leaf);

        EfsElementDTO newParent = getOriginal(2);
        copyEfsElementConsumer.copyEfsElements(newParent, List.of(leaf.getParent()));

        List<EfsElementDTO> copiedList = copyEfsElementConsumer.getResult();
        EfsElementDTO copiedLeaf = synchronize(leaf);
        EfsElementDTO copiedParent = synchronize(leaf.getParent());
        assertEquals(2, copiedList.size());
        assertFalse(copiedLeaf.isDeleted() || copiedParent.isDeleted());
        assertFalse(copiedList.get(0).isDeleted() || copiedList.get(1).isDeleted());
        assertEquals(leaf.getParent().getId(), copiedParent.getId());
        assertEquals(leaf.getId(), copiedLeaf.getId());
        assertEquals(Long.valueOf(leaf.getId() + 4), copiedList.get(1).getId());
        assertEquals(Long.valueOf(leaf.getId() + 5), copiedList.get(0).getId());
        assertEquals(synchronize(newParent), copiedList.get(1).getParent());
        assertEquals(copiedList.get(1), copiedList.get(0).getParent());
        assertNodeWeight(445, synchronize(newParent));
        assertNodeWeight(445, copiedList.get(1));
        assertNodeWeight(444, copiedList.get(0));
        assertNodeWeight(445, copiedParent);
        assertNodeWeight(444, copiedLeaf);
    }

    /**
     * +0
     * +1
     * <p>
     * copy [0] under root
     * <p>
     * +0
     * +1
     * +0'
     * +1'
     */
    @Test
    public void updateWeightCopyHierarchyInsidePartList_Root_To_Root() {
        EfsElementDTO leaf = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445, leaf.getParent());
        assertNodeWeight(444, leaf);

        copyEfsElementConsumer.copyEfsElements(vehiclePartList(), List.of(leaf.getParent()));

        List<EfsElementDTO> copiedList = copyEfsElementConsumer.getResult();
        EfsElementDTO copiedLeaf = synchronize(leaf);
        EfsElementDTO copiedParent = synchronize(leaf.getParent());
        assertEquals(2, copiedList.size());
        assertFalse(copiedLeaf.isDeleted() || copiedParent.isDeleted());
        assertFalse(copiedList.get(0).isDeleted() || copiedList.get(1).isDeleted());
        assertEquals(leaf.getParent().getId(), copiedParent.getId());
        assertEquals(leaf.getId(), copiedLeaf.getId());
        assertEquals(Long.valueOf(leaf.getId() + 4), copiedList.get(1).getId());
        assertEquals(Long.valueOf(leaf.getId() + 5), copiedList.get(0).getId());
        assertNull(copiedList.get(1).getParent());
        assertEquals(copiedList.get(1), copiedList.get(0).getParent());
        assertNodeWeight(445, copiedList.get(1));
        assertNodeWeight(444, copiedList.get(0));
        assertNodeWeight(445, copiedParent);
        assertNodeWeight(444, copiedLeaf);
    }

    /**
     * +0
     * +1
     * <p>
     * to another part of the list
     */
    //REQ when a copy of mara in another part of the list will be created, should the weight properties be copied too or should they all be 0.0?
    @Test
    public void updateWeightCopyHierarchyDifferentPartList_To_Root() {
        EfsElementDTO leaf = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445, leaf.getParent());
        assertNodeWeight(444, leaf);

        copyEfsElementConsumer.copyEfsElements(emptyPartList(), List.of(leaf.getParent()));

        List<EfsElementDTO> copiedList = copyEfsElementConsumer.getResult();
        EfsElementDTO copiedLeaf = synchronize(leaf);
        EfsElementDTO copiedParent = synchronize(leaf.getParent());
        assertEquals(2, copiedList.size());
        assertFalse(copiedLeaf.isDeleted() || copiedParent.isDeleted());
        assertFalse(copiedList.get(0).isDeleted() || copiedList.get(1).isDeleted());
        assertEquals(leaf.getParent().getId(), copiedParent.getId());
        assertEquals(leaf.getId(), copiedLeaf.getId());
        assertEquals(Long.valueOf(leaf.getId() + 4), copiedList.get(1).getId());
        assertEquals(Long.valueOf(leaf.getId() + 5), copiedList.get(0).getId());
        assertNull(copiedList.get(1).getParent());
        assertEquals(copiedList.get(1), copiedList.get(0).getParent());
        assertNodeWeight(445, copiedList.get(1));
        assertNodeWeight(444, copiedList.get(0));
        assertNodeWeight(445, copiedParent);
        assertNodeWeight(444, copiedLeaf);
    }

    /**
     * +0
     * +1
     * <p>
     * to another part of the list
     */
    @Test
    public void updateWeightCopyHierarchyDifferentPartList_To_AnotherNode() {
        EfsElementDTO leaf = createSimpleHierarchy(4, WeightControlFlag.YES);
        assertNodeWeight(445, leaf.getParent());
        assertNodeWeight(444, leaf);
        EfsElementDTO newParent = createAndSaveEfsElement(33, emptyPartList());
        assertNodeWeight(0, newParent);

        copyEfsElementConsumer.copyEfsElements(newParent, List.of(leaf.getParent()));

        newParent = synchronize(newParent);
        List<EfsElementDTO> copiedList = copyEfsElementConsumer.getResult();
        for (EfsElementDTO efsElementDTO : copiedList) {
            setParentChildRelation(copiedList, efsElementDTO);
        }

        EfsElementDTO copiedLeaf = synchronize(leaf);
        EfsElementDTO copiedParent = synchronize(leaf.getParent());
        assertEquals(2, copiedList.size());
        assertFalse(copiedLeaf.isDeleted() || copiedParent.isDeleted());
        assertFalse(copiedList.get(0).isDeleted() || copiedList.get(1).isDeleted());
        assertEquals(leaf.getParent().getId(), copiedParent.getId());
        assertEquals(leaf.getId(), copiedLeaf.getId());
        assertEquals(Long.valueOf(leaf.getId() + 6), copiedList.get(0).getId());
        assertEquals(Long.valueOf(leaf.getId() + 5), copiedList.get(1).getId());
        assertEquals(newParent, copiedList.get(1).getParent());
        assertEquals(copiedList.get(1), copiedList.get(0).getParent());
        assertNodeWeight(445, newParent);
        assertNodeWeight(445, copiedList.get(1));
        assertNodeWeight(444, copiedList.get(0));
        assertNodeWeight(445, copiedParent);
        assertNodeWeight(444, copiedLeaf);
    }

    private void assertNodeWeight(double expected, EfsElementDTO element) {
        assertEquals(Double.valueOf(expected), element.getNodeWeight());
    }

    private void assertWeight(double expected, EfsElementDTO element) {
        assertEquals(Double.valueOf(expected), element.getWeight());
    }

    private void setParentChildRelation(List<EfsElementDTO> efsElementDTOS, EfsElementDTO efsElement) {
        List<EfsElementDTO> children = new ArrayList<>();
        for (EfsElementDTO efsElementDTO : efsElementDTOS) {
            if (efsElement.getId() != null && efsElementDTO.getParentId() != null && efsElementDTO.getParentId()
                    .equals(efsElement.getId())) {
                children.add(efsElementDTO);
            }
        }

        if (children.isEmpty()) {
            efsElement.setChildren(List.of());
            return;
        }

        efsElement.setChildren(children);
        for (EfsElementDTO child : children) {
            child.setParent(efsElement);
        }
    }
}
