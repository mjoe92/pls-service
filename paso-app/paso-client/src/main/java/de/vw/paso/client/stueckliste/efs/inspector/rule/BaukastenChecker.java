package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;
import java.util.HashSet;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

/**
 * The checker to test Baukasten under the given {@link Inspection}s.
 */
public final class BaukastenChecker {

    /**
     * @param node
     *         the {@link EfsElementDTO} as node to inspect
     * @param inspections
     *         the {@link Inspection}s to control
     * @return <code>true</code>, when the
     */
    public static boolean isEmpty(EfsElementDTO node, Inspection... inspections) {
        if (node == null) {
            return false;
        }

        for (Inspection predicate : inspections) {
            if (predicate.test(node)) {
                return true;
            }
        }

        for (EfsElementDTO child : node.getChildren()) {
            return isEmpty(child, inspections);
        }

        return false;
    }

    /**
     * @param node
     *         the {@link EfsElementDTO} as node to inspect
     * @param baukastenInspection
     *         the {@link Inspection} to control with the {@link Inspection}
     * @return the top inspected {@link EfsElementDTO}s in the tree
     */
    public static Collection<EfsElementDTO> findAll(EfsElementDTO node, Inspection baukastenInspection) {
        return findAll(node, baukastenInspection, new HashSet<>());
    }

    /**
     * @param node
     *         the {@link EfsElementDTO} as node root for the tree to inspect
     * @param inspection
     *         the {@link Inspection} to test the node
     * @param accumulator
     *         the {@link EfsElementDTO}s to collect
     * @return the found {@link EfsElementDTO}s
     */
    private static Collection<EfsElementDTO> findAll(EfsElementDTO node, Inspection inspection,
            Collection<EfsElementDTO> accumulator) {
        if (node == null) {
            return accumulator;
        }

        if (inspection.test(node)) {
            accumulator.add(node);
            return accumulator;
        }

        for (EfsElementDTO child : node.getChildren()) {
            findAll(child, inspection, accumulator);
        }

        return accumulator;
    }
}