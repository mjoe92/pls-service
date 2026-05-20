package de.vw.paso.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.IEfsElement;
import de.vw.paso.partlist.domain.WeightControlFlag;

public class EfsEntityWeightCalculator {

    private final Map<Long, Double> weights;

    public EfsEntityWeightCalculator() {
        weights = new HashMap<>();
    }

    public double calculateWeight(IEfsElement element, Collection<Long> changedElements) {
        double weightOfElement = calculateWeightOfElement(element);
        updateWeight(element, weightOfElement, IEfsElement::getWeight, IEfsElement::setWeight, changedElements);

        double weightOfChildren = calculateWeightOfChildren(element, changedElements);

        double nodeWeight = weightOfElement + weightOfChildren;
        updateWeight(element, nodeWeight, IEfsElement::getNodeWeight, IEfsElement::setNodeWeight, changedElements);

        weights.put(element.getId(), nodeWeight);

        return nodeWeight;
    }

    public double calculateWeightOfElement(IEfsElement element) {
        if (element == null) {
            return 0;
        }

        WeightControlFlag weightControlFlag = element.getWeightControlFlag();
        if (WeightControlFlag.YES == weightControlFlag || WeightControlFlag.TEMP == weightControlFlag) {
            return EfsEntityWeightUtil.calculateWeight(element);
        } else if (WeightControlFlag.NO == weightControlFlag) {
            return 0;
        }

        return element.getChildren() != null && !element.getChildren().isEmpty() ? 0
                : EfsEntityWeightUtil.calculateWeight(element);
    }

    public Map<Long, Double> getWeights() {
        return weights;
    }

    private double calculateWeightOfChildren(IEfsElement element, Collection<Long> changedElements) {
        double weightOfChildren = 0;
        for (EfsElement child : element.getChildren()) {
            if (!child.isDeleted()) {
                weightOfChildren += calculateWeight(child, changedElements);
            }
        }

        return weightOfChildren;
    }

    private void updateWeight(IEfsElement element, double newWeight, Function<IEfsElement, Double> accessor,
            BiConsumer<IEfsElement, Double> setter, Collection<Long> changedElements) {
        Double currentWeight = accessor.apply(element);
        if (currentWeight != newWeight) {
            changedElements.add(element.getId());
        }

        setter.accept(element, newWeight);
    }
}
