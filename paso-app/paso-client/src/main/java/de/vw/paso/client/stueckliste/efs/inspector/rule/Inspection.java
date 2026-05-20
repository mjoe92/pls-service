package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;
import java.util.function.Predicate;

import de.vw.paso.partlist.domain.SpecialPartNumberType;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

/**
 * The types for Baukasten inspection with defined predicates.
 */
public enum Inspection implements Predicate<EfsElementDTO> {

    BAUKASTEN(Inspection::baukasten), GAP(Inspection::gap), GWS(Inspection::gws), GWS_WEIGHT(Inspection::gwsWeight);

    private final Predicate<EfsElementDTO> inspection;

    Inspection(Predicate<EfsElementDTO> inspection) {
        this.inspection = inspection;
    }

    @Override
    public boolean test(EfsElementDTO value) {
        return inspection.test(value);
    }

    /**
     * @param node
     *     the {@link EfsElementDTO} node to inspect
     * @return <code>true</code>, if the node is a gap
     */
    private static boolean baukasten(EfsElementDTO node) {
        if (node == null) {
            return false;
        }

        Collection<EfsElementDTO> children = node.getChildren();
        if (children == null || children.isEmpty()) {
            return false;
        }

        if (node.getBaukasten() == 0) {
            for (EfsElementDTO child : children) {
                if (child.getBaukasten() != 0) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param node
     *     the {@link EfsElementDTO} node to inspect
     * @return <code>true</code>, if the node is a gap
     */
    private static boolean gap(EfsElementDTO node) {
        return node != null && node.getEfsElementMara().getPartNumber().equals(SpecialPartNumberType.GAP.getLabel());
    }

    /**
     * @param root
     *     the {@link EfsElementDTO} tree assembly to inspect
     * @return <code>true</code>, if the root inspection results valid in full depth (see {@link Inspection#deepGwsWeight})
     */
    private static boolean gwsWeight(EfsElementDTO root) {
        Boolean inspected = deepGwsWeight(root);
        return inspected != null && inspected;
    }

    /**
     * @param assembly
     *     see {@link Inspection#gwsWeight(EfsElementDTO)}
     * @return <code>true</code>, when the assembly:
     * <ul>
     *     <li>in cases the assembly {@link WeightControlFlag} is empty:
     *         <ol>
     *              <li>all the sub elements have {@link WeightControlFlag#NO}</li>
     *              <li>at least one sub element have no weight with other, than {@link WeightControlFlag#NO}</li>
     *         </ol>
     *     </li>
     *     <li>
     *         in cases the assembly {@link WeightControlFlag} is filled at least one sub element has no weight with other,
     *         than {@link WeightControlFlag#NO}
     *     </li>
     * </ul>
     * <code>null</code> immediately, when a child of assembly has weight, otherwise <code>false</code>
     */
    private static Boolean deepGwsWeight(EfsElementDTO assembly) {
        if (assembly == null || assembly.isLeaf()) {
            return false;
        }

        Boolean valid = false;
        for (EfsElementDTO child : assembly.getChildren()) {
            if (WeightControlFlag.NO == child.getWeightControlFlag()) {
                if (!child.isLeaf()) {
                    valid = deepGwsWeight(child);
                    if (valid == null) {
                        return null;
                    }
                }

                continue;
            }

            if (child.hasWeight() || child.hasNodeWeight()) {
                return null;
            }

            valid = true;
        }

        return assembly.getWeightControlFlag() == null || valid;
    }

    /**
     * @param root
     *     the {@link EfsElementDTO} to inspect
     * @return <code>true</code>, if the {@link EfsElementDTO} root or one of its nodes hasn't {@link WeightControlFlag#NO}
     */
    private static boolean gws(EfsElementDTO root) {
        if (root.getWeightControlFlag() == null) {
            return true;
        }

        for (EfsElementDTO child : root.getChildren()) {
            if (WeightControlFlag.NO != child.getWeightControlFlag() && gws(child)) {
                return true;
            }
        }

        return false;
    }
}
