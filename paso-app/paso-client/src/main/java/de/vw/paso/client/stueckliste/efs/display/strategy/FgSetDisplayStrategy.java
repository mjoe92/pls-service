package de.vw.paso.client.stueckliste.efs.display.strategy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.FgSetTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.FgSetTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.FgSetTreeObject;
import de.vw.paso.client.stueckliste.efs.tree.model.IAggregatedEfsTreeModel;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.utility.StringConstant;

public class FgSetDisplayStrategy extends AbstractDisplayStrategyForTrees<FgSetTreeObject> {

    private static final String UNKNOWN_SET_KEY_SET_KEY = StringConstant.EMPTY;

    private FgSetDisplayStrategy(boolean showDeletedElements) {
        super(showDeletedElements);
    }

    public static AbstractDisplayStrategyForTrees<FgSetTreeObject> getStrategyWithoutDeletion() {
        return new FgSetDisplayStrategy(false);
    }

    @Override
    public FgSetTreeModel createDisplayModel(Collection<FgSetTreeObject> nodes) {
        FgSetTreeModel model = new FgSetTreeModel();

        Collection<SetKeyDTO> setKeys = nodes.stream().map(FgSetTreeObject::getAggregationObject).toList();
        Collection<FgSetTreeObject> sortedByTreeDepth = nodes.stream()
            .sorted((first, next) -> compareFgSetParents(first, next, setKeys)).toList();
        for (FgSetTreeObject node : sortedByTreeDepth) {
            groupTreeNode(model, node);
        }

        createAndInitializeSummaryFakeFgSet(model);

        return model;
    }

    private int compareFgSetParents(FgSetTreeObject first, FgSetTreeObject next, Collection<SetKeyDTO> nodes) {
        SetKeyDTO firstAggregation = first.getAggregationObject();
        int firstDepth = getDepth(firstAggregation, nodes, 0);

        SetKeyDTO nextAggregation = next.getAggregationObject();
        int nextDepth = getDepth(nextAggregation, nodes, 0);

        return Integer.compare(firstDepth, nextDepth);
    }

    private int getDepth(SetKeyDTO node, Collection<SetKeyDTO> nodes, int depth) {
        if (node == null) {
            return depth;
        }

        //todo: consuming operation but we do it once we open part list or change the config set key -> should be done once at the beginning into map
        Optional<SetKeyDTO> found = nodes.stream()
            .filter(element -> element.getSetKeyName().equals(node.getParentName())).findFirst();
        if (found.isPresent()) {
            depth = getDepth(found.get(), nodes, ++depth);
        }

        return depth;
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
    public FgSetTreeObject getParentForCreatingNewElement(List<FgSetTreeObject> efsElements) {
        if (efsElements.isEmpty()) {
            return null;
        }

        if (efsElements.size() == 1) {
            return efsElements.getFirst();
        }

        throw new IllegalArgumentException();
    }

    @Override
    protected <TI extends AbstractTreeItem<FgSetTreeObject>> TI addNode(AbstractTreeModel<TI, FgSetTreeObject> model,
        FgSetTreeObject node) {
        TI treeItem = model.addElement(node, true);

        ((FgSetTreeModel) model).calculateWeights((FgSetTreeItem) treeItem);

        return treeItem;
    }

    private void groupTreeNode(FgSetTreeModel model, FgSetTreeObject node) {
        if (node == null) {
            return;
        }

        if (!node.isKnown()) {
            FgSetTreeItem unknownGroup = model.getTreeItem(UNKNOWN_SET_KEY_SET_KEY);
            if (unknownGroup == null) {
                SetKeyDTO unknownSetKey = new SetKeyDTO(UNKNOWN_SET_KEY_SET_KEY, UNKNOWN_MESSAGE, null,
                    SetKeyDTO.UNKNOWN_SET_KEY_VERSION);

                unknownGroup = addNode(model, new FgSetTreeObject(unknownSetKey));
                unknownGroup.propertyUnknownRoot().setValue(true);
            }

            SetKeyDTO unknownSetKey = unknownGroup.getValue().getAggregationObject();
            SetKeyDTO aggregationObject = node.getAggregationObject();

            aggregationObject.setParentSetKey(unknownSetKey);
            aggregationObject.setParentName(unknownSetKey.getSetKeyName());
        }

        addNode(model, node);
    }

    private void createAndInitializeSummaryFakeFgSet(FgSetTreeModel model) {
        SetKeyDTO sumRowSetKey = new SetKeyDTO("summary", SUMMARY_MESSAGE, null, -1L);

        FgSetTreeObject fgSetTreeObject = new FgSetTreeObject(sumRowSetKey);
        FgSetTreeItem summaryRowTreeItem = model.addElement(fgSetTreeObject, true);
        summaryRowTreeItem.propertySummaryRow().setValue(true);

        model.calculateNumberOfParts();
        model.updateSummaryValues();
    }
}
