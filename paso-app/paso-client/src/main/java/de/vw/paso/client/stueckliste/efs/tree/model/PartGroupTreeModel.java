package de.vw.paso.client.stueckliste.efs.tree.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.util.PartGroupUtil;
import de.vw.paso.client.util.EfsElementUtil;
import de.vw.paso.client.util.TreeItemUtil;
import de.vw.paso.client.valueobject.PartGroupVMO;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.MathUtil;
import de.vw.paso.utility.SpecPartGroupCategory;
import de.vw.paso.utility.StringConstant;

public class PartGroupTreeModel extends AbstractTreeModel<PartGroupTreeItem, PartGroupTreeObject>
    implements IAggregatedEfsTreeModel {

    public PartGroupTreeModel() {
        super(new PartGroupTreeObject());
    }

    @Override
    public PartGroupTreeItem createTreeItem(PartGroupTreeObject userObject) {
        PartGroupTreeItem treeItem = new PartGroupTreeItem(userObject);

        cacheTreeItem(treeItem);

        return treeItem;
    }

    @Override
    public void updateNode(EfsElementDTO updatedEfsElement, boolean isNodeValid, boolean isHierarchical) {
        if (updatedEfsElement.getPartNumber() == null) {
            return;
        }

        if (!PartGroupUtil.isUgrNumeric(updatedEfsElement.getEfsElementMara().getPartNumberEndNumber())) {
            return;
        }

        PartGroupDTO knownPartGroup = checkIfItsKnownPartGroup(updatedEfsElement);
        boolean isKnown = knownPartGroup != null;

        PartGroupTreeItem treeItem;
        if (isKnown) {
            if (knownPartGroup.getCategory() >= 100) {
                String key = PartGroupUtil.getKeyForPartGroup(knownPartGroup);
                treeItem = getTreeItem(key);
            } else {
                treeItem = getTreeItem(
                    updatedEfsElement.getEfsElementMara().getPartNumberMittelgruppe().charAt(0) + StringConstant.DASH
                        + updatedEfsElement.getEfsElementMara().getPartNumberMittelgruppe() + StringConstant.DASH
                        + updatedEfsElement.getEfsElementMara().getPartNumberEndNumber());
            }

        } else {
            treeItem = getTreeItem(
                updatedEfsElement.getEfsElementMara().getPartNumberMittelgruppe() + StringConstant.DASH
                    + updatedEfsElement.getEfsElementMara().getPartNumberEndNumber());
        }

        if (treeItem == null && isNodeValid && !isKnown) {
            treeItem = addToUnknown(updatedEfsElement, isHierarchical);
        }

        if (treeItem == null && isNodeValid && isKnown) {
            PartGroupVMO partGroupVMO = new PartGroupVMO();
            partGroupVMO.setCategory(knownPartGroup.getCategory());
            partGroupVMO.setMgr(knownPartGroup.getMgr());
            partGroupVMO.setUgr(knownPartGroup.getUgr());
            partGroupVMO.setDescription(knownPartGroup.getDescription());

            treeItem = addElement(new PartGroupTreeObject(partGroupVMO), true);
        }

        if (treeItem == null) {
            return;
        }

        boolean efsElementPresent = treeItem.getUserObject().getEfsElements().contains(updatedEfsElement);

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

        if (treeItem.getUserObject().getEfsElements().isEmpty()) {
            removeElements(List.of(treeItem));
        }

        calculateNumberOfParts();
        updateSummaryValues();
    }

    public void updateSummaryValues() {
        PartGroupTreeItem summaryRowTreeItem = getRoot().getChildren().stream()
            .map(childTreeItem -> (PartGroupTreeItem) childTreeItem).filter(child -> child.propertySummaryRow().get())
            .findFirst().orElseThrow();

        calculateSummaryValue(summaryRowTreeItem, PartGroupTreeItem::getWeightPlatform,
            PartGroupTreeItem::setWeightPlatform, Double::sum);
        calculateSummaryValue(summaryRowTreeItem, PartGroupTreeItem::getWeightSystem,
            PartGroupTreeItem::setWeightSystem, Double::sum);
        calculateSummaryValue(summaryRowTreeItem, PartGroupTreeItem::getWeightHut, PartGroupTreeItem::setWeightHut,
            Double::sum);
        calculateSummaryValue(summaryRowTreeItem, PartGroupTreeItem::getWeightAll, PartGroupTreeItem::setWeightAll,
            Double::sum);

        calculateSummaryValue(summaryRowTreeItem, PartGroupTreeItem::getNumPlatform, PartGroupTreeItem::setNumPlatform,
            Integer::sum);
        calculateSummaryValue(summaryRowTreeItem, PartGroupTreeItem::getNumSystem, PartGroupTreeItem::setNumSystem,
            Integer::sum);
        calculateSummaryValue(summaryRowTreeItem, PartGroupTreeItem::getNumHut, PartGroupTreeItem::setNumHut,
            Integer::sum);
        calculateSummaryValue(summaryRowTreeItem, PartGroupTreeItem::getNumAll, PartGroupTreeItem::setNumAll,
            Integer::sum);
    }

    private <N> void calculateSummaryValue(PartGroupTreeItem summaryRowTreeItem, Function<PartGroupTreeItem, N> getter,
        BiConsumer<PartGroupTreeItem, N> setter, BinaryOperator<N> reduce) {
        N summaryValue = getRoot().getChildren().stream().map(localRoot -> (PartGroupTreeItem) localRoot)
            .filter(localRoot -> !localRoot.propertySummaryRow().get()).map(getter).filter(Objects::nonNull)
            .reduce(reduce).orElse(null);

        setter.accept(summaryRowTreeItem, summaryValue);
    }

    public void calculateNumberOfParts() {
        for (TreeItem<PartGroupTreeObject> localRoot : getRoot().getChildren()) {
            PartGroupTreeItem root = (PartGroupTreeItem) localRoot;
            if (!root.propertySummaryRow().get()) {
                calculateNumberOfParts(root);
            }
        }
    }

    private void calculateNumberOfParts(PartGroupTreeItem item) {
        List<EfsElementDTO> efsElements = new ArrayList<>(512);
        TreeItemUtil.collectAggregatedChildren(item, efsElements);

        item.propertyNumPlatform()
            .setValue(EfsElementUtil.countNumberOfPartsForItem(ApCompareGroup.PLATFORM, efsElements));
        item.propertyNumSystem().setValue(EfsElementUtil.countNumberOfPartsForItem(ApCompareGroup.SYSTEM, efsElements));
        item.propertyNumHut().setValue(EfsElementUtil.countNumberOfPartsForItem(ApCompareGroup.HUT, efsElements));
        item.propertyNumAll().setValue(EfsElementUtil.countNumberOfPartsSumForItem(efsElements));

        for (TreeItem<PartGroupTreeObject> child : item.getChildren()) {
            calculateNumberOfParts((PartGroupTreeItem) child);
        }
    }

    private PartGroupDTO checkIfItsKnownPartGroup(EfsElementDTO updatedEfsElement) {
        Collection<PartGroupDTO> partGroups = CacheManager.getPartGroups();
        String preNumber = updatedEfsElement.getEfsElementMara().getPartNumberVornummer();
        boolean isSpecPreNumber = preNumber.equals("WHT") || preNumber.startsWith("A") || preNumber.equals("N");

        for (PartGroupDTO partGroup : partGroups) {
            if (partGroup.isUgr()) {
                if (!isSpecPreNumber) {
                    if (partGroup.getUgr()
                        .equals(Integer.parseInt(updatedEfsElement.getEfsElementMara().getPartNumberEndNumber()))
                        && partGroup.getMgr()
                        .equals(Integer.parseInt(updatedEfsElement.getEfsElementMara().getPartNumberMittelgruppe()))
                        && partGroup.getCategory().equals(Integer.parseInt(
                        String.valueOf(updatedEfsElement.getEfsElementMara().getPartNumberMittelgruppe().charAt(0))))) {

                        return partGroup;
                    }
                } else if (partGroup.getCategory().equals(SpecPartGroupCategory.NORM_PART_GROUP.getCategory())) {
                    if (partGroup.getUgr()
                        .equals(Integer.parseInt(updatedEfsElement.getEfsElementMara().getPartNumberEndNumber()))
                        && partGroup.getMgr()
                        .equals(Integer.parseInt(updatedEfsElement.getEfsElementMara().getPartNumberMittelgruppe()))) {
                        return partGroup;
                    }
                }
            } else if (partGroup.isMgr() && isSpecPreNumber && partGroup.getCategory() >= 100) {
                if (checkSpecPartGroup(partGroup, updatedEfsElement)) {
                    return partGroup;
                }
            }
        }

        return null;
    }

    private boolean checkSpecPartGroup(PartGroupDTO partGroup, EfsElementDTO updatedEfsElement) {
        String preNumber = updatedEfsElement.getEfsElementMara().getPartNumberVornummer();
        String middleGroup = updatedEfsElement.getEfsElementMara().getPartNumberMittelgruppe();
        String category = SpecPartGroupCategory.getStringForCategory(partGroup.getCategory());

        if (partGroup.getCategory().equals(SpecPartGroupCategory.NORM_PART_GROUP.getCategory()) && preNumber.startsWith(
            category)) {
            return PartGroupUtil.groupToString(partGroup.getMgrEnd()).equals(middleGroup)
                || partGroup.getMgrEnd() != null && !middleGroup.equals("052");
        } else if (partGroup.getCategory().equals(SpecPartGroupCategory.WHT_PART_GROUP.getCategory())
            && preNumber.equals(category)) {
            return true;
        } else {
            return partGroup.getCategory().equals(SpecPartGroupCategory.A_PART_GROUP.getCategory())
                && preNumber.startsWith(category.substring(0, 1));
        }
    }

    @Override
    public void cacheTreeItem(PartGroupTreeItem treeItem) {
        super.cacheTreeItem(treeItem);
    }

    private void refreshElement(PartGroupTreeObject element) {
        String key = PartGroupUtil.getKeyForPartGroup(PartGroupVMO.toPartGroup(element.getAggregationObject()));
        super.refreshElement(key, element);
    }

    public void calculateWeights(PartGroupTreeItem itemToUpdate) {
        fromLeafToRootCalculation(itemToUpdate);
        fromRootToLeafsHierarchicalCorrection(findRootTreeItem(itemToUpdate));
    }

    private void fromLeafToRootCalculation(PartGroupTreeItem itemToUpdate) {
        PartGroupTreeObject userObject = itemToUpdate.getUserObject();
        Double oldPlatform = userObject.getPlatform();
        Double oldSystem = userObject.getSystem();
        Double oldHut = userObject.getHut();

        userObject.calculateWeights();

        Double diffPlatform = MathUtil.nullSafeSubtract(userObject.getPlatform(), oldPlatform);
        Double diffSystem = MathUtil.nullSafeSubtract(userObject.getSystem(), oldSystem);
        Double diffHut = MathUtil.nullSafeSubtract(userObject.getHut(), oldHut);

        PartGroupTreeItem parentItem = itemToUpdate;

        while (!Objects.equals(parentItem.getKey(), getRoot().getKey()) || parentItem.propertyUnknownRoot().get()) {
            parentItem.setWeightPlatform(MathUtil.nullSafeAddition(parentItem.getWeightPlatform(), diffPlatform));
            parentItem.setWeightSystem(MathUtil.nullSafeAddition(parentItem.getWeightSystem(), diffSystem));
            parentItem.setWeightHut(MathUtil.nullSafeAddition(parentItem.getWeightHut(), diffHut));
            parentItem.setWeightAll(
                MathUtil.nullSafeAddition(parentItem.getWeightPlatform(), parentItem.getWeightSystem(),
                    parentItem.getWeightHut()));

            if (parentItem.getParent() == null) {
                break;
            }

            parentItem = (PartGroupTreeItem) parentItem.getParent();
        }
    }

    private PartGroupTreeItem findRootTreeItem(PartGroupTreeItem partGroupTreeItem) {
        while (!Objects.equals(partGroupTreeItem.getParentKey(), getRoot().getKey())) {
            partGroupTreeItem = (PartGroupTreeItem) partGroupTreeItem.getParent();
        }

        return partGroupTreeItem;
    }

    private Map<ApCompareGroup, Boolean> fromRootToLeafsHierarchicalCorrection(PartGroupTreeItem partGroupTreeItem) {
        Map<ApCompareGroup, Boolean> isEmptyMap = new HashMap<>();

        isEmptyMap.put(ApCompareGroup.PLATFORM, true);
        isEmptyMap.put(ApCompareGroup.SYSTEM, true);
        isEmptyMap.put(ApCompareGroup.HUT, true);

        for (TreeItem<PartGroupTreeObject> treeItem : partGroupTreeItem.getChildren()) {
            Map<ApCompareGroup, Boolean> previousMap = fromRootToLeafsHierarchicalCorrection(
                (PartGroupTreeItem) treeItem);

            isEmptyMap.merge(ApCompareGroup.PLATFORM, previousMap.get(ApCompareGroup.PLATFORM),
                (oldValue, newValue) -> oldValue && newValue);
            isEmptyMap.merge(ApCompareGroup.SYSTEM, previousMap.get(ApCompareGroup.SYSTEM),
                (oldValue, newValue) -> oldValue && newValue);
            isEmptyMap.merge(ApCompareGroup.HUT, previousMap.get(ApCompareGroup.HUT),
                (oldValue, newValue) -> oldValue && newValue);
        }

        Collection<EfsElementDTO> efsElements = partGroupTreeItem.getUserObject().getEfsElements();
        Boolean isEmpty = isEmptyMap.merge(ApCompareGroup.PLATFORM,
            efsElements.stream().noneMatch(e -> ApCompareGroup.PLATFORM.containsAp(e.getAp())),
            (oldValue, newValue) -> oldValue && newValue);

        if (isEmpty) {
            partGroupTreeItem.setWeightPlatform(null);
        }

        isEmpty = isEmptyMap.merge(ApCompareGroup.SYSTEM,
            efsElements.stream().noneMatch(e -> ApCompareGroup.SYSTEM.containsAp(e.getAp())),
            (oldValue, newValue) -> oldValue && newValue);

        if (isEmpty) {
            partGroupTreeItem.setWeightSystem(null);
        }

        isEmpty = isEmptyMap.merge(ApCompareGroup.HUT,
            efsElements.stream().noneMatch(e -> ApCompareGroup.HUT.containsAp(e.getAp())),
            (oldValue, newValue) -> oldValue && newValue);

        if (isEmpty) {
            partGroupTreeItem.setWeightHut(null);
        }

        if (partGroupTreeItem.getWeightPlatform() == null && partGroupTreeItem.getWeightSystem() == null
            && partGroupTreeItem.getWeightHut() == null) {
            partGroupTreeItem.setWeightAll(null);

        }

        return isEmptyMap;
    }

    private PartGroupTreeItem addToUnknown(EfsElementDTO efsElement, boolean isHierarchical) {
        PartGroupVMO unknownPartGroup = new PartGroupVMO();

        unknownPartGroup.setMgr(Integer.parseInt(efsElement.getEfsElementMara().getPartNumberMittelgruppe()));
        unknownPartGroup.setUgr(Integer.parseInt(efsElement.getEfsElementMara().getPartNumberEndNumber()));

        return addElement(new PartGroupTreeObject(unknownPartGroup, new ArrayList<>(List.of(efsElement))),
            isHierarchical);
    }
}
