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
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.utility.MathUtil;

public class FgSetTreeModel extends AbstractTreeModel<FgSetTreeItem, FgSetTreeObject>
        implements IAggregatedEfsTreeModel {

    public FgSetTreeModel() {
        super(new FgSetTreeObject());
    }

    @Override
    protected FgSetTreeItem createTreeItem(final FgSetTreeObject element) {
        final FgSetTreeItem treeItem = new FgSetTreeItem(element);

        cacheTreeItem(treeItem);

        return treeItem;
    }

    public void refreshElement(final FgSetTreeObject element) {
        super.refreshElement(element.getAggregationObject().getSetKeyName(), element);
    }

    @Override
    public void updateNode(final EfsElementDTO updatedEfsElement, final boolean isNodeValid,
            final boolean isHierachical) {
        FgSetTreeItem treeItem = getTreeItem(updatedEfsElement.getSetKey());

        if (treeItem == null && isNodeValid) {
            treeItem = addToUnknown(updatedEfsElement.getSetKey(), isHierachical);
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
                treeItem.getUserObject().getAggregationObject().getSetVersionId()
                        == SetKeyDTO.UNKNOWN_SET_KEY_VERSION)) {
            removeElements(List.of(treeItem));
        }

        calculateNumberOfParts();
        updateSummaryValues();
    }

    public void updateSummaryValues() {
        FgSetTreeItem summaryRowTreeItem = getRoot().getChildren().stream()
                .map(childTreeItem -> (FgSetTreeItem) childTreeItem).filter(child -> child.propertySummaryRow().get())
                .findFirst().orElseThrow();

        calculateSummaryValue(summaryRowTreeItem, FgSetTreeItem::getWeightPlatform, FgSetTreeItem::setWeightPlatform,
                Double::sum);
        calculateSummaryValue(summaryRowTreeItem, FgSetTreeItem::getWeightSystem, FgSetTreeItem::setWeightSystem,
                Double::sum);
        calculateSummaryValue(summaryRowTreeItem, FgSetTreeItem::getWeightHut, FgSetTreeItem::setWeightHut,
                Double::sum);
        calculateSummaryValue(summaryRowTreeItem, FgSetTreeItem::getWeightAll, FgSetTreeItem::setWeightAll,
                Double::sum);

        calculateSummaryValue(summaryRowTreeItem, FgSetTreeItem::getNumPlatform, FgSetTreeItem::setNumPlatform,
                Integer::sum);
        calculateSummaryValue(summaryRowTreeItem, FgSetTreeItem::getNumSystem, FgSetTreeItem::setNumSystem,
                Integer::sum);
        calculateSummaryValue(summaryRowTreeItem, FgSetTreeItem::getNumHut, FgSetTreeItem::setNumHut, Integer::sum);
        calculateSummaryValue(summaryRowTreeItem, FgSetTreeItem::getNumAll, FgSetTreeItem::setNumAll, Integer::sum);
    }

    private <N> void calculateSummaryValue(FgSetTreeItem summaryRowTreeItem, Function<FgSetTreeItem, N> getter,
            BiConsumer<FgSetTreeItem, N> setter, BinaryOperator<N> reduce) {
        N summaryValue = getRoot().getChildren().stream().map(localRoot -> (FgSetTreeItem) localRoot)
                .filter(localRoot -> !localRoot.propertySummaryRow().get()).map(getter).filter(Objects::nonNull)
                .reduce(reduce).orElse(null);

        setter.accept(summaryRowTreeItem, summaryValue);
    }

    public void calculateNumberOfParts() {
        getRoot().getChildren().stream().map(localRoot -> (FgSetTreeItem) localRoot)
                .filter(localRoot -> !localRoot.propertySummaryRow().get()).forEach(this::calculateNumberOfParts);
    }

    private void calculateNumberOfParts(FgSetTreeItem item) {
        ArrayList<EfsElementDTO> efsElements = new ArrayList<>(512);
        TreeItemUtil.collectAggregatedChildren(item, efsElements);

        item.propertyNumPlatform()
                .setValue(EfsElementUtil.countNumberOfPartsForItem(ApCompareGroup.PLATFORM, efsElements));
        item.propertyNumSystem().setValue(EfsElementUtil.countNumberOfPartsForItem(ApCompareGroup.SYSTEM, efsElements));
        item.propertyNumHut().setValue(EfsElementUtil.countNumberOfPartsForItem(ApCompareGroup.HUT, efsElements));
        item.propertyNumAll().setValue(EfsElementUtil.countNumberOfPartsSumForItem(efsElements));

        for (TreeItem<FgSetTreeObject> child : item.getChildren()) {
            calculateNumberOfParts((FgSetTreeItem) child);
        }
    }

    public void calculateWeights(final FgSetTreeItem itemToUpdate) {
        fromLeafToRootCalculation(itemToUpdate);
        fromRootToLeafsHierarchicalCorrection(findRootTreeItem(itemToUpdate));
    }

    private void fromLeafToRootCalculation(final FgSetTreeItem itemToUpdate) {
        final Double oldPlatform = itemToUpdate.getUserObject().getPlatform();
        final Double oldSystem = itemToUpdate.getUserObject().getSystem();
        final Double oldHut = itemToUpdate.getUserObject().getHut();

        itemToUpdate.getUserObject().calculateWeights();

        final Double diffPlatform = MathUtil.nullSafeSubtract(itemToUpdate.getUserObject().getPlatform(), oldPlatform);
        final Double diffSystem = MathUtil.nullSafeSubtract(itemToUpdate.getUserObject().getSystem(), oldSystem);
        final Double diffHut = MathUtil.nullSafeSubtract(itemToUpdate.getUserObject().getHut(), oldHut);

        FgSetTreeItem parentItem = itemToUpdate;

        while (!Objects.equals(parentItem.getKey(), getRoot().getKey())) {
            parentItem.setWeightPlatform(MathUtil.nullSafeAddition(parentItem.getWeightPlatform(), diffPlatform));
            parentItem.setWeightSystem(MathUtil.nullSafeAddition(parentItem.getWeightSystem(), diffSystem));
            parentItem.setWeightHut(MathUtil.nullSafeAddition(parentItem.getWeightHut(), diffHut));
            parentItem.setWeightAll(
                    MathUtil.nullSafeAddition(parentItem.getWeightPlatform(), parentItem.getWeightSystem(),
                            parentItem.getWeightHut()));

            parentItem = (FgSetTreeItem) parentItem.getParent();
        }
    }

    private FgSetTreeItem findRootTreeItem(FgSetTreeItem fgSetTreeItem) {
        while (!Objects.equals(fgSetTreeItem.getParentKey(), getRoot().getKey())) {
            fgSetTreeItem = (FgSetTreeItem) fgSetTreeItem.getParent();
        }

        return fgSetTreeItem;
    }

    private Map<ApCompareGroup, Boolean> fromRootToLeafsHierarchicalCorrection(final FgSetTreeItem fgSetTreeItem) {
        final Map<ApCompareGroup, Boolean> isEmptyMap = new HashMap<>();

        isEmptyMap.put(ApCompareGroup.PLATFORM, true);
        isEmptyMap.put(ApCompareGroup.SYSTEM, true);
        isEmptyMap.put(ApCompareGroup.HUT, true);

        for (final TreeItem treeItem : fgSetTreeItem.getChildren()) {
            final Map<ApCompareGroup, Boolean> previousMap = fromRootToLeafsHierarchicalCorrection(
                    (FgSetTreeItem) treeItem);

            isEmptyMap.merge(ApCompareGroup.PLATFORM, previousMap.get(ApCompareGroup.PLATFORM),
                    (oldValue, newValue) -> oldValue && newValue);
            isEmptyMap.merge(ApCompareGroup.SYSTEM, previousMap.get(ApCompareGroup.SYSTEM),
                    (oldValue, newValue) -> oldValue && newValue);
            isEmptyMap.merge(ApCompareGroup.HUT, previousMap.get(ApCompareGroup.HUT),
                    (oldValue, newValue) -> oldValue && newValue);
        }

        final List<EfsElementDTO> efsElements = fgSetTreeItem.getUserObject().getEfsElements();

        boolean isEmpty = isEmptyMap.merge(ApCompareGroup.PLATFORM,
                efsElements.stream().noneMatch(e -> ApCompareGroup.PLATFORM.containsAp(e.getAp())),
                (oldValue, newValue) -> oldValue && newValue);

        if (isEmpty) {
            fgSetTreeItem.setWeightPlatform(null);
        }

        isEmpty = isEmptyMap.merge(ApCompareGroup.SYSTEM,
                efsElements.stream().noneMatch(e -> ApCompareGroup.SYSTEM.containsAp(e.getAp())),
                (oldValue, newValue) -> oldValue && newValue);

        if (isEmpty) {
            fgSetTreeItem.setWeightSystem(null);
        }

        isEmpty = isEmptyMap.merge(ApCompareGroup.HUT,
                efsElements.stream().noneMatch(e -> ApCompareGroup.HUT.containsAp(e.getAp())),
                (oldValue, newValue) -> oldValue && newValue);

        if (isEmpty) {
            fgSetTreeItem.setWeightHut(null);
        }

        if (fgSetTreeItem.getWeightPlatform() == null && fgSetTreeItem.getWeightSystem() == null
                && fgSetTreeItem.getWeightHut() == null) {
            fgSetTreeItem.setWeightAll(null);

        }

        return isEmptyMap;
    }

    private FgSetTreeItem addToUnknown(final String setKey, final boolean isHierachical) {
        if (SetKeyDTO.NOT_RELEVANT_SET_KEY.equals(setKey)) {
            return null;
        }
        final SetKeyDTO unknownSetKey = new SetKeyDTO();
        unknownSetKey.setSetKeyName(setKey);
        unknownSetKey.setSetVersionId(SetKeyDTO.UNKNOWN_SET_KEY_VERSION);
        final Optional<TreeItem<FgSetTreeObject>> unknownRoot = findUnknownRootTreeItem();

        if (unknownRoot.isEmpty()) {
            return null;
        }

        unknownSetKey.setParentSetKey(unknownRoot.get().getValue().getAggregationObject());

        return addElement(new FgSetTreeObject(unknownSetKey), isHierachical);
    }

    private FgSetTreeItem addToEmpty(EfsElementDTO element) {
        final Optional<TreeItem<FgSetTreeObject>> unknownRoot = findUnknownRootTreeItem();

        if (unknownRoot.isEmpty()) {
            return null;
        }

        FgSetTreeItem emptySetKeyTreeItem = unknownRoot.get().getChildren().stream()
                .map(treeItem -> (FgSetTreeItem) treeItem).filter(fgSetTreeItem -> Objects.equals(
                        fgSetTreeItem.getUserObject().getAggregationObject().getSetVersionId(),
                        SetKeyDTO.EMPTY_SET_KEY_VERSION)).findFirst().orElse(null);

        if (emptySetKeyTreeItem == null) {
            return null;
        }

        if (!emptySetKeyTreeItem.getUserObject().getEfsElements().contains(element)) {
            emptySetKeyTreeItem.getUserObject().getEfsElements().add(element);
        }

        return emptySetKeyTreeItem;
    }

    private Optional<TreeItem<FgSetTreeObject>> findUnknownRootTreeItem() {
        return getRoot().getChildren().stream().filter(s -> ((FgSetTreeItem) s).propertyUnknownRoot().get())
                .findFirst();
    }
}
