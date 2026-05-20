package de.vw.paso.utility;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementMaraDTO;

public final class EfsWeightUtil {

    public static PrioritizedWeight getMostPriorizedWeight(AbstractEfsElementMaraDTO mara) {
        Double weight = mara.getWeightWeightedTe();
        WeightOrigin origin = WeightOrigin.WeightedTe;
        if (weight == null || weight.equals(0.0)) {
            weight = mara.getWeightCalculatedTe();
            origin = WeightOrigin.CalculatedTe;
        }

        if (weight == null || weight.equals(0.0)) {
            weight = mara.getWeightEstimatedTe();
            origin = WeightOrigin.EstimatedTe;
        }

        if (weight == null || weight.equals(0.0)) {
            weight = mara.getWeightWeightedProd();
            origin = WeightOrigin.WeightedProd;
        }

        if (weight == null) {
            origin = null;
        }

        return new PrioritizedWeight(origin, weight);
    }

    public static Map<ApCompareGroup, Double> calculate(Collection<EfsElementDTO> elements) {
        Double platform = 0d;
        Double system = 0d;
        Double hut = 0d;

        Set<ApCompareGroup> existingGroups = new HashSet<>();
        for (EfsElementDTO efsElement : elements) {
            if (efsElement.isDeleted()) {
                continue;
            }

            if (ApCompareGroup.PLATFORM.containsAp(efsElement.getAp())) {
                platform = MathUtil.nullSafeAddition(platform, efsElement.getWeight());
                existingGroups.add(ApCompareGroup.PLATFORM);
            } else if (ApCompareGroup.SYSTEM.containsAp(efsElement.getAp())) {
                system = MathUtil.nullSafeAddition(system, efsElement.getWeight());
                existingGroups.add(ApCompareGroup.SYSTEM);
            } else if (ApCompareGroup.HUT.containsAp(efsElement.getAp())) {
                hut = MathUtil.nullSafeAddition(hut, efsElement.getWeight());
                existingGroups.add(ApCompareGroup.HUT);
            }
        }

        Map<ApCompareGroup, Double> calcResult = new HashMap<>(4);
        calcResult.put(ApCompareGroup.PLATFORM, existingGroups.contains(ApCompareGroup.PLATFORM) ? platform : null);
        calcResult.put(ApCompareGroup.SYSTEM, existingGroups.contains(ApCompareGroup.SYSTEM) ? system : null);
        calcResult.put(ApCompareGroup.HUT, existingGroups.contains(ApCompareGroup.HUT) ? hut : null);
        if (!existingGroups.isEmpty()) {
            calcResult.put(ApCompareGroup.SUM, MathUtil.nullSafeAddition(platform, system, hut));
        }

        return calcResult;
    }

    public static double calculateWeight(IEfsElementForDTO node) {
        return getMenge(node) * setMostPriorizedWeight(node.getEfsElementMara());
    }

    private static Integer getMenge(IEfsElementForDTO node) {
        return node.getQuantity() == null ? 0 : node.getQuantity();
    }

    private static double setMostPriorizedWeight(AbstractEfsElementMaraDTO mara) {
        PrioritizedWeight mostPiorizedWeight = getMostPriorizedWeight(mara);
        mara.setPrioritizedWeight(mostPiorizedWeight.getWeight());
        return mostPiorizedWeight.getWeight();
    }
}
