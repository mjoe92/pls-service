package de.vw.paso.client.util.customfilter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;

import de.vw.paso.client.model.tree.AbstractFlatTreeItem;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;
import org.apache.commons.lang3.StringUtils;

public class CustomTreeTableUtil {

  //collecting of Items

  public static <S> void collectTreeItemsFromRoot(FilterableTreeItem treeItem, List<TreeItem<S>> abstractTreeItemList,
    boolean noFilters, Map<TableColumnBase<TreeItem<S>, ?>, PredicateData> columnPredicateDataMap,
    TableColumnBase<TreeItem<S>, ?> column) {
    if (noFilters || (columnPredicateDataMap.size() == 1 && columnPredicateDataMap.containsKey(column))) {

      // *treeItem instanceof PrNumberTreeItem* is a work workaround for a display issue
      // of the filter in the wizard configuration step
      if (treeItem.getParent() != null || treeItem instanceof AbstractFlatTreeItem || treeItem.getChildren() != null) {
        abstractTreeItemList.add(treeItem);

        if (treeItem instanceof AbstractFlatTreeItem && column.getCellObservableValue(treeItem) != null
          && column.getCellObservableValue(treeItem).getValue() == null && (treeItem.getParent() == null
          || treeItem.getParent().getParent() == null)) {
          abstractTreeItemList.remove(treeItem);
        }
      }
    } else if (columnPredicateDataMap.containsKey(column)) {
      final boolean[] relevantItem = { true };

      determineRelevance(treeItem, columnPredicateDataMap, column, relevantItem);

      if (relevantItem[0]) {
        abstractTreeItemList.add(treeItem);
      }
    } else {
      collectFilteredTreeItemsFromRoot(treeItem, abstractTreeItemList, columnPredicateDataMap, column);
      return;
    }
    final ObservableList<FilterableTreeItem> children = treeItem.getSourceChildren();

    if (children != null && !children.isEmpty()) {
      children.forEach(
        child -> collectTreeItemsFromRoot(child, abstractTreeItemList, noFilters, columnPredicateDataMap, column));
    }
  }

  private static <S> void collectFilteredTreeItemsFromRoot(FilterableTreeItem treeItem,
    List<TreeItem<S>> abstractTreeItemList, Map<TableColumnBase<TreeItem<S>, ?>, PredicateData> columnPredicateDataMap,
    TableColumnBase<TreeItem<S>, ?> column) {
    if (treeItem != null && treeItem.getValue() != null && !abstractTreeItemList.contains(treeItem)) {
      final ObservableList<FilterableTreeItem> children = treeItem.getFilteredChildren();

      if (children != null && !children.isEmpty()) {
        final boolean[] relevantItem = { true };

        determineRelevance(treeItem, columnPredicateDataMap, column, relevantItem);

        if (relevantItem[0]) {
          abstractTreeItemList.add(treeItem);
        }

        children.forEach(
          child -> collectFilteredTreeItemsFromRoot(child, abstractTreeItemList, columnPredicateDataMap, column));
      } else {
        final boolean[] isItemRelevant = { true };

        determineRelevance(treeItem, columnPredicateDataMap, column, isItemRelevant);

        if (isItemRelevant[0]) {
          abstractTreeItemList.add(treeItem);
        }

        if (children != null) {
          children.forEach(
            child -> collectFilteredTreeItemsFromRoot(child, abstractTreeItemList, columnPredicateDataMap, column));
        }
      }
    } else if ((treeItem != null ? treeItem.getFilteredChildren() : null) != null && !treeItem.getFilteredChildren()
      .isEmpty()) {
      abstractTreeItemList.add(treeItem);

      final ObservableList<FilterableTreeItem> children = treeItem.getFilteredChildren();

      if (children != null && !children.isEmpty()) {
        children.forEach(
          child -> collectFilteredTreeItemsFromRoot(child, abstractTreeItemList, columnPredicateDataMap, column));
      }
    }
  }

  private static <S> void determineRelevance(FilterableTreeItem treeItem,
    Map<TableColumnBase<TreeItem<S>, ?>, PredicateData> columnPredicateDataMap, TableColumnBase column,
    boolean[] isItemRelevant) {

    columnPredicateDataMap.keySet().forEach(keyColumn -> {
      if (keyColumn != column && keyColumn.getCellObservableValue(treeItem) != null) {
        if (!(keyColumn.getCellObservableValue(treeItem).getValue() instanceof Date)) {
          if (keyColumn.getCellObservableValue(treeItem).getValue() == null && (
            !columnPredicateDataMap.get(keyColumn).getValueItems().contains(CustomFilterUtil.getLocalizedEmptyString())
              || (treeItem.getParent() != null && treeItem.getParent().getValue() == null))) {
            isItemRelevant[0] = false;
          } else if (keyColumn.getCellObservableValue(treeItem).getValue() != null) {
            if (keyColumn.getCellObservableValue(treeItem).getValue().toString().compareToIgnoreCase(StringUtils.EMPTY)
              == 0 && (!columnPredicateDataMap.get(keyColumn).getValueItems()
              .contains(CustomFilterUtil.getLocalizedEmptyString()))) {
              isItemRelevant[0] = false;
            } else if ((keyColumn.getCellObservableValue(treeItem).getValue() instanceof Double
              || keyColumn.getCellObservableValue(treeItem).getValue() instanceof Float) && !columnPredicateDataMap.get(
              keyColumn).getValueItems().contains(keyColumn.getCellObservableValue(treeItem).getValue().toString())) {
              isItemRelevant[0] = false;
            } else if (!(keyColumn.getCellObservableValue(treeItem).getValue() instanceof Double
              || keyColumn.getCellObservableValue(treeItem).getValue() instanceof Float)
              && keyColumn.getCellObservableValue(treeItem).getValue().toString().compareToIgnoreCase(StringUtils.EMPTY)
              != 0 && (!columnPredicateDataMap.get(keyColumn).getValueItems()
              .contains(keyColumn.getCellObservableValue(treeItem).getValue().toString()))) {
              isItemRelevant[0] = false;
            }
          }
        } else {
          Date value = (Date) keyColumn.getCellObservableValue(treeItem).getValue();
          LocalDate localdate = DateToLocalDateConverter.convertToLocalDate(value);

          if (!columnPredicateDataMap.get(keyColumn).getValueItems()
            .contains(localdate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")))) {
            isItemRelevant[0] = false;
          }
        }
      }
    });
  }

}
