package de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws;

import java.util.Collection;
import java.util.HashSet;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.EfsElementUtil;
import de.vw.paso.utility.EfsWeightCalculator;
import de.vw.paso.utility.StringConstant;

public class GWSSolutionCalculator {

    public static double calculateGWSWeightDiff(Collection<TreeItem<InspectorTreeItemObject>> items,
        WeightControlFlag parentGWS) {
        EfsWeightCalculator calculator = new EfsWeightCalculator();

        Collection<EfsElementDTO> parents = getParents(items);

        double differences = 0;
        Collection<Long> changedElements = new HashSet<>();
        for (EfsElementDTO parent : parents) {
            // parent of parent is necessary, because weight calculation is looking at parentGWS of parent
            double weight = calculator.calculateWeight(parent, changedElements);

            EfsElementDTO newParent = EfsElementUtil.copyEfsElementHierarchy(parent,
                parentCopy -> parentCopy.setWeightControlFlag(parentGWS),
                childCopy -> childCopy.setWeightControlFlag(createValidChildGWS(parentGWS)));
            double weightChange = calculator.calculateWeight(newParent, changedElements);
            differences += weightChange - weight;
        }

        return differences;
    }

    private static WeightControlFlag createValidChildGWS(WeightControlFlag gws) {
        if (WeightControlFlag.YES == gws || WeightControlFlag.TEMP == gws || WeightControlFlag.NO == gws) {
            return WeightControlFlag.NO;
        } else if (gws == null) {
            return null;
        }

        throw new IllegalArgumentException(
            "Expected GWS to be one of " + WeightControlFlag.YES + StringConstant.COMMA_SPACE + WeightControlFlag.NO
                + " or " + WeightControlFlag.TEMP);
    }

    private static Collection<EfsElementDTO> getParents(Collection<TreeItem<InspectorTreeItemObject>> items) {
        Collection<EfsElementDTO> parents = new HashSet<>();
        for (TreeItem<InspectorTreeItemObject> item : items) {
            if (item.getValue().isTypeNode() || item.getValue().isGroupNode()) {
                Collection<EfsElementDTO> childParents = getParents(item.getChildren());
                parents.addAll(childParents);
                continue;
            }

            if (item.getValue().isEntryNode()) {
                EfsElementDTO parent = item.getValue().getEntry().getElement().getParent();
                if (parent != null) {
                    parents.add(parent);
                }
            }
        }

        return parents;
    }
}
