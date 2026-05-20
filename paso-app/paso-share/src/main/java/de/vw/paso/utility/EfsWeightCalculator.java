package de.vw.paso.utility;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;

public class EfsWeightCalculator {

    public double calculateWeight(IEfsElementForDTO element, Collection<Long> changedElements) {
        double weightOfElement = calculateWeightOfElement(element);
        updateWeight(element, weightOfElement, IEfsElementForDTO::getWeight, IEfsElementForDTO::setWeight,
                changedElements);

        double weightOfChildren = calculateWeightOfChildren(element, changedElements);

        double nodeWeight = weightOfElement + weightOfChildren;
        updateWeight(element, nodeWeight, IEfsElementForDTO::getNodeWeight, IEfsElementForDTO::setNodeWeight,
                changedElements);

        return nodeWeight;
    }

    public double calculateWeightOfElement(IEfsElementForDTO element) {
        if (element == null) {
            return 0;
        }

        WeightControlFlag weightControlFlag = element.getWeightControlFlag();
        if (WeightControlFlag.YES == weightControlFlag || WeightControlFlag.TEMP == weightControlFlag) {
            return EfsWeightUtil.calculateWeight(element);
        } else if (WeightControlFlag.NO == weightControlFlag) {
            return 0;
        } else if (element.getChildren() == null || element.getChildren().isEmpty()) {
            return EfsWeightUtil.calculateWeight(element);
        }

        return 0;
    }

    private double calculateWeightOfChildren(IEfsElementForDTO element, Collection<Long> changedElements) {
        double weightOfChildren = 0;
        for (EfsElementDTO child : element.getChildren()) {
            if (!child.isDeleted()) {
                weightOfChildren += calculateWeight(child, changedElements);
            }
        }

        return weightOfChildren;
    }

    private void updateWeight(IEfsElementForDTO element, double newWeight, Function<IEfsElementForDTO, Double> accessor,
            BiConsumer<IEfsElementForDTO, Double> setter, Collection<Long> changedElements) {
        Double currentWeight = accessor.apply(element);
        if (currentWeight != newWeight) {
            changedElements.add(element.getId());
        }

        setter.accept(element, newWeight);
    }
}
