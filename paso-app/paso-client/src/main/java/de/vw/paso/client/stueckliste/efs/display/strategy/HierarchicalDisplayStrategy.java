package de.vw.paso.client.stueckliste.efs.display.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.IAggregatedEfsTreeModel;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.utility.EfsElementUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HierarchicalDisplayStrategy extends AbstractDisplayStrategyForTrees<EfsElementDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(HierarchicalDisplayStrategy.class);

    private final boolean isSubPartsNeeded;

    private HierarchicalDisplayStrategy(boolean showDeletedElements, boolean isSubPartsNeeded) {
        super(showDeletedElements);

        this.isSubPartsNeeded = isSubPartsNeeded;
    }

    public static AbstractDisplayStrategyForTrees<EfsElementDTO> getStrategyWithoutDeletion(boolean isSubPartsNeeded) {
        return new HierarchicalDisplayStrategy(false, isSubPartsNeeded);
    }

    public static AbstractDisplayStrategyForTrees<EfsElementDTO> getStrategyWithDeletion(boolean isSubPartsNeeded) {
        return new HierarchicalDisplayStrategy(true, isSubPartsNeeded);
    }

    public static AbstractDisplayStrategyForTrees<EfsElementDTO> getStrategyWithoutDeletion() {
        return new HierarchicalDisplayStrategy(false, true);
    }

    public EfsElementTreeModel createDisplayModel(Collection<EfsElementDTO> nodes) {
        List<EfsElementDTO> sortedNodes = EfsElementUtil.sortByCheckingStructure(nodes);
        if (!EfsElementUtil.checkParentsFirst(sortedNodes)) {
            LOG.warn("Could not create hierachical structure. Nodes are not correctly sorted. Error in TisSort?");
        }

        if (!isSubPartsNeeded) {
            removeSubPartListParts(sortedNodes);
        }

        EfsElementDTO efs = PartListFactory.createEfsElement();
        EfsElementMaraDTO efsElementMara = PartListFactory.createEfsElementMara();

        efs.setEfsElementMara(efsElementMara);

        EfsElementTreeModel model = new EfsElementTreeModel(efs);
        for (EfsElementDTO node : sortedNodes) {
            if (isNodeValid(node)) {
                addNode(model, node);
            }
        }

        return model;
    }

    private void removeSubPartListParts(Collection<EfsElementDTO> sortedNodes) {
        Collection<EfsElementDTO> aggregates = new ArrayList<>(sortedNodes.size());
        for (EfsElementDTO efsElement : sortedNodes) {
            if (efsElement.getAggregate() != null && !efsElement.getAggregate().isEmpty()) {
                aggregates.add(efsElement);
            }
        }

        for (EfsElementDTO efsElement : aggregates) {
            sortedNodes.removeAll(getAllChildren(efsElement));
        }
    }

    @Override
    protected <TI extends AbstractTreeItem<EfsElementDTO>> TI addNode(AbstractTreeModel<TI, EfsElementDTO> model,
            EfsElementDTO node) {
        return model.addElement(node, true);
    }

    @Override
    public boolean allowsCopy() {
        return true;
    }

    @Override
    public boolean allowsMove() {
        return true;
    }

    @Override
    public void updateNode(IAggregatedEfsTreeModel model, EfsElementDTO nodeToUpdate) {
        model.updateNode(nodeToUpdate, isNodeValid(nodeToUpdate), true);
    }

    @Override
    public EfsElementDTO getParentForCreatingNewElement(List<EfsElementDTO> efsElements) {
        if (efsElements.isEmpty()) {
            return null;
        }

        if (efsElements.size() == 1 && !efsElements.getFirst().isDeleted()) {
            return efsElements.getFirst();
        }

        throw new IllegalArgumentException();
    }
}