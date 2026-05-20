package de.vw.paso.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EfsWeightCalculatorTest {

    private EfsWeightCalculator weightCalculator;

    @BeforeEach
    void setUp() {
        weightCalculator = new EfsWeightCalculator();
    }

    @Test
    void testCalculateWeightOfAssemblyWithGwsYes() {
        EfsElementDTO assembly = createElement(WeightControlFlag.YES, 120d);
        EfsElementDTO singlePart = createElement(WeightControlFlag.NO, 10d);
        assembly.getChildren().add(singlePart);
        singlePart.setParent(assembly);

        double weight = weightCalculator.calculateWeight(assembly, new HashSet<>());

        assertEquals(120, weight);
    }

    @Test
    void testCalculateWeightOfAssemblyWithGwsVorlaeufig() {
        EfsElementDTO assembly = createElement(WeightControlFlag.TEMP, 120d);
        EfsElementDTO singlePart = createElement(WeightControlFlag.NO, 10d);
        assembly.getChildren().add(singlePart);
        singlePart.setParent(assembly);

        double weight = weightCalculator.calculateWeight(assembly, new HashSet<>());

        assertEquals(120, weight);
    }

    @Test
    void testCalculateWeightOfAssemblyWithGwsNo() {
        EfsElementDTO assembly = createElement(WeightControlFlag.NO, 120d);
        EfsElementDTO singlePart = createElement(WeightControlFlag.NO, 10d);
        assembly.getChildren().add(singlePart);
        singlePart.setParent(assembly);

        double weight = weightCalculator.calculateWeight(assembly, new HashSet<>());

        assertEquals(0, weight);
    }

    @Test
    void testCalculateWeightOfAssemblyWithGwsEmpty() {
        EfsElementDTO assembly = createElement(null, 120d);
        EfsElementDTO singlePart = createElement(WeightControlFlag.NO, 10d);
        assembly.getChildren().add(singlePart);
        singlePart.setParent(assembly);

        double weight = weightCalculator.calculateWeight(assembly, new HashSet<>());

        assertEquals(0, weight);
    }

    @Test
    void testCollectionOfWeightsByElementId() {
        EfsElementDTO assembly = createElement(null, 120d);
        assembly.setId(1L);
        EfsElementDTO singlePartA = createElement(null, 10d);
        singlePartA.setId(2L);
        EfsElementDTO singlePartB = createElement(null, 20d);
        singlePartB.setId(3L);
        assembly.getChildren().addAll(List.of(singlePartA, singlePartB));
        singlePartA.setParent(assembly);
        singlePartB.setParent(assembly);

        double weight = weightCalculator.calculateWeight(assembly, new HashSet<>());

        assertEquals(30, weight);
    }

    private EfsElementDTO createElement(WeightControlFlag gws, double weight) {
        EfsElementMaraDTO elementMara = new EfsElementMaraDTO();
        elementMara.setWeightWeightedTe(weight);

        EfsElementDTO element = new EfsElementDTO();
        element.setQuantity(1);
        element.setEfsElementMara(elementMara);
        element.setWeightControlFlag(gws);
        element.setChildren(new ArrayList<>());

        return element;
    }
}
