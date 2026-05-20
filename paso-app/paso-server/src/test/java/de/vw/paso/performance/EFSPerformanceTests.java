package de.vw.paso.performance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import de.vw.paso.mapper.EfsElementMaraMapper;
import de.vw.paso.mapper.VehiclePartListMapper;
import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementMara;
import de.vw.paso.partlist.domain.IPartListChild;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.services.partlist.AbstractEfsTests;
import de.vw.paso.utility.StringConstant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Component
@Transactional
public class EFSPerformanceTests extends AbstractEfsTests {

    private static final int NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL = 70;

    private Collection<EfsElement> savedEfsElements;

    @AfterEach
    public void cleanUp() {
        efsElementRepository.deleteAll(savedEfsElements);
    }

    @Test
    @Timeout(value = 5)
    public void calculatePartListWeight4900With70Mara() {
        VehiclePartListDTO partListDTO = createVehiclePartList();
        VehiclePartList partList = VehiclePartListMapper.toEntity(partListDTO, null);

        int size = 30 + NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL;
        Collection<EfsElementMara> maras = new ArrayList<>(size);
        for (int parentIndex = 30; parentIndex < size; parentIndex++) {
            EfsElementMara mara = EfsElementMaraMapper.toEntity(PartListFactory.createEfsElementMara("BEZEICHNUNG1",
                    StringConstant.EMPTY + parentIndex + "111111111"));
            assertNotNull(mara);

            mara.setWeightWeightedTe(1.0);
            maras.add(mara);
        }

        Collection<EfsElement> allElementsInPartList = new ArrayList<>(maras.size() * maras.size());
        for (EfsElementMara parentMara : maras) {
            EfsElement parent = createEfsElementWithWeights(allElementsInPartList, partList, parentMara);
            for (EfsElementMara childMara : maras) {
                createEfsElementWithWeights(allElementsInPartList, parent, childMara);
            }
        }
        savedEfsElements = efsElementRepository.saveAll(allElementsInPartList);
        Map<Long, Double> result = efsWeightManager.calculateWeight(partList);
        assertEquals((NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL + 1) * NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL,
                result.get(Long.MIN_VALUE), 0.0);
        assertEquals((NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL + 1) * NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL + 1,
                result.size());
    }

    @Test
    @Timeout(value = 5)
    public void calculatePartListWeight4900With4900Mara() {
        VehiclePartListDTO partListDTO = createVehiclePartList();
        VehiclePartList partList = VehiclePartListMapper.toEntity(partListDTO, null);
        Collection<EfsElement> allElementsInPartList = new ArrayList<>(
                NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL * NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL);
        for (int parentIndex = 0; parentIndex < NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL; parentIndex++) {
            createEfsElementWithWeights(allElementsInPartList, partList, getNextMara(parentIndex, 0));
            for (int childIndex = 0; childIndex < NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL; childIndex++) {
                createEfsElementWithWeights(allElementsInPartList, partList, getNextMara(parentIndex, childIndex + 1));
            }
        }

        savedEfsElements = efsElementRepository.saveAll(allElementsInPartList);
        Map<Long, Double> result = efsWeightManager.calculateWeight(partList);
        assertEquals((NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL + 1) * NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL,
                result.get(Long.MIN_VALUE), 0.0);
        assertEquals((NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL + 1) * NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL + 1,
                result.size());
    }

    @Test
    @Timeout(value = 5)
    public void calculatePartListWeight1000WithDeepHierarchy() {
        int count = 1000;
        VehiclePartListDTO partListDTO = createVehiclePartList();
        VehiclePartList partList = VehiclePartListMapper.toEntity(partListDTO, null);
        EfsElementMara mara = EfsElementMaraMapper.toEntity(
                PartListFactory.createEfsElementMara("BEZEICHNUNG1", "99111111199"));
        mara.setWeightWeightedTe(1.0);

        Collection<EfsElement> allElementsInPartList = new ArrayList<>();
        createHierarchy(allElementsInPartList, partList, mara, count);

        savedEfsElements = efsElementRepository.saveAll(allElementsInPartList);
        Map<Long, Double> result = efsWeightManager.calculateWeight(partList);
        assertEquals(count, result.get(Long.MIN_VALUE), 0.0);
        assertEquals(count + 1, result.size());
    }

    private EfsElementMara getNextMara(int parentIndex, int childIndex) {
        EfsElementMara mara = EfsElementMaraMapper.toEntity(PartListFactory.createEfsElementMara("BEZEICHNUNG1",
                StringConstant.EMPTY + (1000 + (parentIndex * NUMBER_OF_ELEMENTS_ON_SAME_HIERARCHY_LEVEL) + childIndex)
                        + "1111111"));
        mara.setWeightWeightedTe(1.0);
        return mara;
    }

    private EfsElement createEfsElementWithWeights(Collection<EfsElement> allElementsInPartList, IPartListChild parent,
            EfsElementMara mara) {
        assertNotNull(parent);

        mara.setId(null);
        mara.setVehiclePartListId(parent.getVehiclePartListId());
        EfsElement node = createEfsElement(mara, parent);
        node.setWeightControlFlag(WeightControlFlag.YES);
        node.setQuantity(1);
        node.setQuantityUnit("G");

        // efsElementManager.saveEfsElement(node);
        allElementsInPartList.add(node);
        return node;
    }

    private void createHierarchy(Collection<EfsElement> allElementsInPartList, IPartListChild parent,
            EfsElementMara mara, int count) {
        if (count > 0) {
            EfsElement newElement = createEfsElementWithWeights(allElementsInPartList, parent, mara);
            newElement.setWeightControlFlag(WeightControlFlag.YES);
            createHierarchy(allElementsInPartList, newElement, mara, count - 1);
        }
    }
}
