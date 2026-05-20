package de.vw.paso.client.stueckliste.efs.display.strategy;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.AggregatedEfsTreeObject;
import de.vw.paso.client.stueckliste.efs.tree.model.CostGroupTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.CostGroupTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.CostGroupTreeObject;
import de.vw.paso.client.stueckliste.efs.tree.model.IAggregatedEfsTreeModel;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.StringConstant;

public class CostGroupDisplayStrategy extends AbstractDisplayStrategyForTrees<CostGroupTreeObject> {

    private static final String UNKNOWN_COST_GROUP_KEY = StringConstant.EMPTY;

    private CostGroupDisplayStrategy(boolean showDeletedElements) {
        super(showDeletedElements);
    }

    public static AbstractDisplayStrategyForTrees<CostGroupTreeObject> getStrategyWithoutDeletion() {
        return new CostGroupDisplayStrategy(false);
    }

    @Override
    public CostGroupTreeModel createDisplayModel(Collection<CostGroupTreeObject> nodes) {
        CostGroupTreeModel model = new CostGroupTreeModel();

        Collection<CostGroupTreeObject> sorted = nodes.stream()
                .sorted(Comparator.nullsFirst(Comparator.comparing(AggregatedEfsTreeObject::getAggregationObject)))
                .toList();
        for (CostGroupTreeObject node : sorted) {
            groupTreeNode(model, node);
        }

        createAndInitializeSummaryFakeCostGroup(model);

        return model;
    }

    @Override
    public boolean allowsCopy() {
        return false;
    }

    @Override
    public boolean allowsMove() {
        return false;
    }

    @Override
    public void updateNode(IAggregatedEfsTreeModel model, EfsElementDTO nodeToUpdate) {
        model.updateNode(nodeToUpdate, isNodeValid(nodeToUpdate), true);
    }

    @Override
    public CostGroupTreeObject getParentForCreatingNewElement(List<CostGroupTreeObject> efsElements) {
        if (efsElements.isEmpty()) {
            return null;
        }

        if (efsElements.size() == 1) {
            return efsElements.getFirst();
        }

        throw new IllegalArgumentException();
    }

    @Override
    protected <TI extends AbstractTreeItem<CostGroupTreeObject>> TI addNode(
            AbstractTreeModel<TI, CostGroupTreeObject> model, CostGroupTreeObject node) {
        TI treeItem = model.addElement(node, true);

        ((CostGroupTreeModel) model).calculateWeights((CostGroupTreeItem) treeItem);

        return treeItem;
    }

    private void groupTreeNode(CostGroupTreeModel model, CostGroupTreeObject node) {
        if (node == null) {
            return;
        }

        if (!node.isKnown()) {
            CostGroupTreeItem unknownGroup = model.getTreeItem(UNKNOWN_COST_GROUP_KEY);
            if (unknownGroup == null) {
                CostGroupDTO unknownCostGroup = new CostGroupDTO(UNKNOWN_COST_GROUP_KEY,
                        CostGroupDTO.UNKNOWN_COST_GROUP_VERSION);
                unknownCostGroup.setDescription(UNKNOWN_MESSAGE);

                unknownGroup = addNode(model, new CostGroupTreeObject(unknownCostGroup));
                unknownGroup.propertyUnknownRoot().setValue(true);
            }

            CostGroupDTO unknownSetKey = unknownGroup.getValue().getAggregationObject();
            CostGroupDTO aggregationObject = node.getAggregationObject();

            aggregationObject.setParent(unknownSetKey);
            aggregationObject.setParentCostGroupName(unknownSetKey.getCostGroupName());
        }

        addNode(model, node);
    }

    private void createAndInitializeSummaryFakeCostGroup(CostGroupTreeModel model) {
        CostGroupDTO costGroupForSummary = new CostGroupDTO("summary", -1);
        costGroupForSummary.setDescription(SUMMARY_MESSAGE);

        CostGroupTreeObject costGroupTreeObject = new CostGroupTreeObject(costGroupForSummary);
        CostGroupTreeItem summaryRowTreeItem = model.addElement(costGroupTreeObject, true);
        summaryRowTreeItem.propertySummaryRow().setValue(true);

        model.calculateNumberOfParts();
        model.updateSummaryValues();
    }
}