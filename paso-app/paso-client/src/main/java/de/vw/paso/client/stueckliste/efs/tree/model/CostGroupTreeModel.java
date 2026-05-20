package de.vw.paso.client.stueckliste.efs.tree.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.util.EfsElementUtil;
import de.vw.paso.client.util.TreeItemUtil;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.MathUtil;

public class CostGroupTreeModel extends AbstractTreeModel<CostGroupTreeItem, CostGroupTreeObject>
        implements IAggregatedEfsTreeModel {

    public CostGroupTreeModel() {
        super(new CostGroupTreeObject());
    }

    @Override
    protected CostGroupTreeItem createTreeItem(final CostGroupTreeObject element) {
        final CostGroupTreeItem treeItem = new CostGroupTreeItem(element);

        cacheTreeItem(treeItem);

        return treeItem;
    }

    public void refreshElement(final CostGroupTreeObject element) {
        super.refreshElement(element.getId(), element);
    }

    @Override
    public void updateNode(final EfsElementDTO updatedEfsElement, final boolean isNodeValid,
            final boolean isHierachical) {
        CostGroupTreeItem treeItem = getTreeItem(updatedEfsElement.getCostGroup());

        if (treeItem == null && isNodeValid) {
            treeItem = addToUnknown(updatedEfsElement.getCostGroup(), isHierachical);
        }

        if (treeItem == null) {
            return;
        }

        final boolean efsElementPresent = treeItem.getUserObject().getEfsElements().contains(updatedEfsElement);

        if (efsElementPresent && !isNodeValid) {
            treeItem.getUserObject().getEfsElements().remove(updatedEfsElement);
        }

        if (isNodeValid && !efsElementPresent) {
            treeItem.getUserObject().getEfsElements().add(updatedEfsElement);
        }

        if (isNodeValid && efsElementPresent) {
            treeItem.getUserObject().getEfsElements().remove(updatedEfsElement);
            treeItem.getUserObject().getEfsElements().add(updatedEfsElement);

            refreshElement(treeItem.getUserObject());
        }

        calculateWeights(treeItem);

        if (treeItem.getUserObject().getEfsElements().isEmpty() && (
                treeItem.getUserObject().getAggregationObject().getVersion()
                        == CostGroupDTO.UNKNOWN_COST_GROUP_VERSION)) {
            removeElements(List.of(treeItem));
        }

        calculateNumberOfParts();
        updateSummaryValues();
    }

    public void updateSummaryValues() {
        CostGroupTreeItem summaryRowTreeItem = getRoot().getChildren().stream()
                .map(childTreeItem -> (CostGroupTreeItem) childTreeItem)
                .filter(child -> child.propertySummaryRow().get()).findFirst().orElseThrow();

        calculateSummaryValue(summaryRowTreeItem, CostGroupTreeItem::getWeightPlatform,
                CostGroupTreeItem::setWeightPlatform, Double::sum);
        calculateSummaryValue(summaryRowTreeItem, CostGroupTreeItem::getWeightSystem,
                CostGroupTreeItem::setWeightSystem, Double::sum);
        calculateSummaryValue(summaryRowTreeItem, CostGroupTreeItem::getWeightHut, CostGroupTreeItem::setWeightHut,
                Double::sum);
        calculateSummaryValue(summaryRowTreeItem, CostGroupTreeItem::getWeightAll, CostGroupTreeItem::setWeightAll,
                Double::sum);

        calculateSummaryValue(summaryRowTreeItem, CostGroupTreeItem::getNumPlatform, CostGroupTreeItem::setNumPlatform,
                Integer::sum);
        calculateSummaryValue(summaryRowTreeItem, CostGroupTreeItem::getNumSystem, CostGroupTreeItem::setNumSystem,
                Integer::sum);
        calculateSummaryValue(summaryRowTreeItem, CostGroupTreeItem::getNumHut, CostGroupTreeItem::setNumHut,
                Integer::sum);
        calculateSummaryValue(summaryRowTreeItem, CostGroupTreeItem::getNumAll, CostGroupTreeItem::setNumAll,
                Integer::sum);
    }

    private <N> void calculateSummaryValue(CostGroupTreeItem summaryRowTreeItem, Function<CostGroupTreeItem, N> getter,
            BiConsumer<CostGroupTreeItem, N> setter, BinaryOperator<N> reduce) {
        N summaryValue = getRoot().getChildren().stream().map(localRoot -> (CostGroupTreeItem) localRoot)
                .filter(localRoot -> !localRoot.propertySummaryRow().get()).map(getter).filter(Objects::nonNull)
                .reduce(reduce).orElse(null);

        setter.accept(summaryRowTreeItem, summaryValue);
    }

    public void calculateNumberOfParts() {
        for (TreeItem<CostGroupTreeObject> localRoot : getRoot().getChildren()) {
            CostGroupTreeItem root = (CostGroupTreeItem) localRoot;
            if (!root.propertySummaryRow().get()) {
                calculateNumberOfParts(root);
            }
        }
    }

    private void calculateNumberOfParts(CostGroupTreeItem item) {
        ArrayList<EfsElementDTO> efsElements = new ArrayList<>(512);
        TreeItemUtil.collectAggregatedChildren(item, efsElements);

        item.propertyNumPlatform()
                .setValue(EfsElementUtil.countNumberOfPartsForItem(ApCompareGroup.PLATFORM, efsElements));
        item.propertyNumSystem().setValue(EfsElementUtil.countNumberOfPartsForItem(ApCompareGroup.SYSTEM, efsElements));
        item.propertyNumHut().setValue(EfsElementUtil.countNumberOfPartsForItem(ApCompareGroup.HUT, efsElements));
        item.propertyNumAll().setValue(EfsElementUtil.countNumberOfPartsSumForItem(efsElements));

        for (TreeItem<CostGroupTreeObject> child : item.getChildren()) {
            calculateNumberOfParts((CostGroupTreeItem) child);
        }
    }

    public void calculateWeights(final CostGroupTreeItem itemToUpdate) {
        fromLeafToRootCalculation(itemToUpdate);
        fromRootToLeafsHierarchicalCorrection(findRootTreeItem(itemToUpdate));
    }

    protected CostGroupTreeItem findRootTreeItem(CostGroupTreeItem treeItem) {
        while (!Objects.equals(treeItem.getParentKey(), getRoot().getKey())) {
            treeItem = (CostGroupTreeItem) treeItem.getParent();
        }

        return treeItem;
    }

    private void fromLeafToRootCalculation(final CostGroupTreeItem itemToUpdate) {
        final Double oldPlatform = itemToUpdate.getUserObject().getPlatform();
        final Double oldSystem = itemToUpdate.getUserObject().getSystem();
        final Double oldHut = itemToUpdate.getUserObject().getHut();

        itemToUpdate.getUserObject().calculateWeights();

        final Double diffPlatform = MathUtil.nullSafeSubtract(itemToUpdate.getUserObject().getPlatform(), oldPlatform);
        final Double diffSystem = MathUtil.nullSafeSubtract(itemToUpdate.getUserObject().getSystem(), oldSystem);
        final Double diffHut = MathUtil.nullSafeSubtract(itemToUpdate.getUserObject().getHut(), oldHut);

        CostGroupTreeItem parentItem = itemToUpdate;

        while (!Objects.equals(parentItem.getKey(), getRoot().getKey())) {
            parentItem.setWeightPlatform(MathUtil.nullSafeAddition(parentItem.getWeightPlatform(), diffPlatform));
            parentItem.setWeightSystem(MathUtil.nullSafeAddition(parentItem.getWeightSystem(), diffSystem));
            parentItem.setWeightHut(MathUtil.nullSafeAddition(parentItem.getWeightHut(), diffHut));
            parentItem.setWeightAll(
                    MathUtil.nullSafeAddition(parentItem.getWeightPlatform(), parentItem.getWeightSystem(),
                            parentItem.getWeightHut()));

            parentItem = (CostGroupTreeItem) parentItem.getParent();
        }
    }

    private Map<ApCompareGroup, Boolean> fromRootToLeafsHierarchicalCorrection(final CostGroupTreeItem treeItem) {
        final Map<ApCompareGroup, Boolean> isEmptyMap = new HashMap<>();

        isEmptyMap.put(ApCompareGroup.PLATFORM, true);
        isEmptyMap.put(ApCompareGroup.SYSTEM, true);
        isEmptyMap.put(ApCompareGroup.HUT, true);

        for (final TreeItem childTreeItem : treeItem.getChildren()) {
            final Map<ApCompareGroup, Boolean> previousMap = fromRootToLeafsHierarchicalCorrection(
                    (CostGroupTreeItem) childTreeItem);

            isEmptyMap.merge(ApCompareGroup.PLATFORM, previousMap.get(ApCompareGroup.PLATFORM),
                    (oldValue, newValue) -> oldValue && newValue);
            isEmptyMap.merge(ApCompareGroup.SYSTEM, previousMap.get(ApCompareGroup.SYSTEM),
                    (oldValue, newValue) -> oldValue && newValue);
            isEmptyMap.merge(ApCompareGroup.HUT, previousMap.get(ApCompareGroup.HUT),
                    (oldValue, newValue) -> oldValue && newValue);
        }

        final List<EfsElementDTO> efsElements = treeItem.getUserObject().getEfsElements();

        boolean isEmpty = isEmptyMap.merge(ApCompareGroup.PLATFORM,
                efsElements.stream().noneMatch(e -> ApCompareGroup.PLATFORM.containsAp(e.getAp())),
                (oldValue, newValue) -> oldValue && newValue);

        if (isEmpty) {
            treeItem.setWeightPlatform(null);
        }

        isEmpty = isEmptyMap.merge(ApCompareGroup.SYSTEM,
                efsElements.stream().noneMatch(e -> ApCompareGroup.SYSTEM.containsAp(e.getAp())),
                (oldValue, newValue) -> oldValue && newValue);

        if (isEmpty) {
            treeItem.setWeightSystem(null);
        }

        isEmpty = isEmptyMap.merge(ApCompareGroup.HUT,
                efsElements.stream().noneMatch(e -> ApCompareGroup.HUT.containsAp(e.getAp())),
                (oldValue, newValue) -> oldValue && newValue);

        if (isEmpty) {
            treeItem.setWeightHut(null);
        }

        if (treeItem.getWeightPlatform() == null && treeItem.getWeightSystem() == null
                && treeItem.getWeightHut() == null) {
            treeItem.setWeightAll(null);

        }

        return isEmptyMap;
    }

    private CostGroupTreeItem addToUnknown(final String costGroup, final boolean isHierachical) {
        final CostGroupDTO unknownCostGroup = new CostGroupDTO(costGroup, CostGroupDTO.UNKNOWN_COST_GROUP_VERSION);
        final Optional<TreeItem<CostGroupTreeObject>> parentTreeItem = findUnknownRootTreeItem();

        if (parentTreeItem.isEmpty()) {
            return null;
        }

        unknownCostGroup.setParent(parentTreeItem.get().getValue().getAggregationObject());

        return addElement(new CostGroupTreeObject(unknownCostGroup), isHierachical);
    }

    private CostGroupTreeItem addToEmpty(EfsElementDTO element) {
        final Optional<TreeItem<CostGroupTreeObject>> unknownRoot = findUnknownRootTreeItem();

        if (unknownRoot.isEmpty()) {
            return null;
        }

        CostGroupTreeItem emptyCostGroupKeyTreeItem = unknownRoot.get().getChildren().stream()
                .map(treeItem -> (CostGroupTreeItem) treeItem).filter(costGroupTreeItem -> Objects.equals(
                        costGroupTreeItem.getUserObject().getAggregationObject().getVersion(),
                        CostGroupDTO.EMPTY_COST_GROUP_KEY_VERSION)).findFirst().orElse(null);

        if (emptyCostGroupKeyTreeItem == null) {
            return null;
        }

        if (!emptyCostGroupKeyTreeItem.getUserObject().getEfsElements().contains(element)) {
            emptyCostGroupKeyTreeItem.getUserObject().getEfsElements().add(element);
        }

        return emptyCostGroupKeyTreeItem;
    }

    private Optional<TreeItem<CostGroupTreeObject>> findUnknownRootTreeItem() {
        return getRoot().getChildren().stream().filter(s -> ((CostGroupTreeItem) s).propertyUnknownRoot().get())
                .findFirst();
    }

}
