package de.vw.paso.client.util;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TreeItem;

import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.stueckliste.efs.tree.model.AggregatedEfsTreeObject;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;
import de.vw.paso.core.domain.AbstractDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TreeItemUtil {

  public static <U, T extends AggregatedEfsTreeObject<U>> List<EfsElementDTO> getAggregatedChildren(
    final TreeItem<T> treeItem) {
    final List<EfsElementDTO> efsElements = new ArrayList<>(treeItem.getValue().getEfsElements());

    for (final TreeItem<T> child : treeItem.getChildren()) {
      efsElements.addAll(getAggregatedChildren(child));
    }

    return efsElements;
  }

  public static <U, T extends AggregatedEfsTreeObject<U>> void collectAggregatedChildren(
    final TreeItem<T> treeItem, List<EfsElementDTO> aggregatedChildren) {
    aggregatedChildren.addAll(treeItem.getValue().getEfsElements());

    for (final TreeItem<T> child : treeItem.getChildren()) {
      collectAggregatedChildren(child, aggregatedChildren);
    }
  }

  public static <U, T extends AbstractDTO<U>> List<T> getChildTreeObjects(final TreeItem<T> treeItem) {
    final List<T> treeObjects = new ArrayList<>();

    treeObjects.add(treeItem.getValue());

    for (final TreeItem<T> child : treeItem.getChildren()) {
      treeObjects.addAll(getChildTreeObjects(child));
    }

    return treeObjects;
  }

  public static int getFilteredCount(CustomTreeTableView<?> treeTable) {
    int count = 0;
    if (treeTable.getRoot() instanceof FilterableTreeItem<?> root) {
      count += getFilteredCount(root);
    }
    return count;
  }

  public static int getFilteredCount(FilterableTreeItem<?> treeItem) {
    int count = 0;
    if (treeItem.matchesPredicate()) {
      count++;
    }
    FilteredList<? extends TreeItem<?>> filteredChildren = treeItem.getFilteredChildren();
    for (TreeItem<?> filteredChild : filteredChildren) {
      count += getFilteredCount((FilterableTreeItem<?>) filteredChild);
    }
    return count;
  }

}
