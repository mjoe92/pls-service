package de.vw.paso.client.util;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class ExpandCollapseUtil {

  public static void setExpanded(TreeTableView treeTableView, TreeItem treeItem, boolean isExpand, boolean isSelected) {
    if (isExpand) {
      expandOneLevel(treeTableView, treeItem, isSelected);
    } else {
      List<List<TreeItem>> list = new ArrayList<>();
      findAllExpandedLevels(treeItem, 0, list);
      collabsOneLevel(treeTableView, list, isSelected);
    }
  }

  public static <T> List<TreeItem<T>> findAllExpandedItems(final TreeItem<T> parentTreeItem) {
    final List<TreeItem<T>> expandedTreeItems = new ArrayList<>();

    for (final TreeItem<T> treeItem : parentTreeItem.getChildren()) {
      if (treeItem.isExpanded()) {
        expandedTreeItems.add(treeItem);
        expandedTreeItems.addAll(findAllExpandedItems(treeItem));
      }
    }

    return expandedTreeItems;
  }

  private static void findAllExpandedLevels(TreeItem treeItem, int levelNumber, List<List<TreeItem>> list) {
    //initializing List when a new Level is first being evaluated
    if (list.size() == levelNumber) {
      List<TreeItem> level = new ArrayList<>();
      list.add(levelNumber, level);
    }
    if (treeItem != null && treeItem.getChildren() != null) {
      ObservableList<TreeItem<EfsElementDTO>> children = treeItem.getChildren();
      for (TreeItem child : children) {
        if (child.isExpanded()) {
          list.get(levelNumber).add(child);
          findAllExpandedLevels(child, levelNumber + 1, list);
        }
      }
    }
  }

  private static void collabsOneLevel(TreeTableView treeTableView, List<List<TreeItem>> list, Boolean isSelected) {
    treeTableView.requestFocus();
    int collapsLevel = list.size() - 1;
    //cutting potentially empty list at end + Adjusting collapseLevel
    if (list.get(collapsLevel).size() == 0) {
      list.remove(collapsLevel);
      collapsLevel -= 1;
    }
    if (collapsLevel == -1 && treeTableView.getSelectionModel().getSelectedItem() != null
      && treeTableView.getSelectionModel().getSelectedItem() instanceof TreeItem) {
      TreeItem treeItem = (TreeItem) treeTableView.getSelectionModel().getSelectedItem();
      if (!treeItem.isExpanded() && treeItem.getParent() != null) {
        treeTableView.getSelectionModel().clearSelection();
        treeTableView.getSelectionModel().select(treeItem.getParent());
        if (treeTableView.getRoot() != treeItem.getParent()) {
          ObservableList<TreeItem> children = treeItem.getParent().getChildren();
          for (TreeItem treeChildItem : children) {
            collapseAll(treeChildItem);
            treeChildItem.setExpanded(false);
          }
          treeItem.getParent().setExpanded(false);
        }
      } else {
        if (treeItem != null && isSelected) {
          treeItem.setExpanded(false);
        }
      }
    } else if (collapsLevel == -1) {
      //do nothing
    } else {
      List<TreeItem> expLList = list.get(collapsLevel);
      for (TreeItem item : expLList) {
        item.setExpanded(false);
      }
    }
  }

  private static void expandOneLevel(TreeTableView treeTableView, TreeItem treeItem, Boolean isSelected) {
    treeTableView.requestFocus();
    if (treeItem != null && isSelected) {
      if (!treeItem.isExpanded() && !treeItem.isLeaf()) {
        treeItem.setExpanded(true);
      } else {
        expandToDeeperLevel(treeItem);
      }
    } else {
      expandToDeeperLevel(treeItem);
    }
  }

  private static void expandToDeeperLevel(TreeItem treeItem) {
    Boolean allChildsExpanded = true;
    if (treeItem != null && treeItem.getChildren() != null) {
      ObservableList<TreeItem> children = treeItem.getChildren();
      for (TreeItem child : children) {
        if (!child.isExpanded()) {
          child.setExpanded(true);
          allChildsExpanded = false;
        }
      }
      if (allChildsExpanded) {
        for (TreeItem child : children) {
          if (!child.isLeaf()) {
            expandToDeeperLevel(child);
          }
        }
      }
    }
  }

  public static void collapseAll(TreeItem root) {
    List<List<TreeItem>> list = new ArrayList<>();
    findAllExpandedLevels(root, 0, list);
    for (int i = 0; i < list.size(); i++) {
      List<TreeItem> subList = list.get(i);
      for (TreeItem treeItem : subList) {
        treeItem.setExpanded(false);
      }
    }
  }

  public static void expandAll(final TreeItem root) {
    root.setExpanded(true);
    root.getChildren().forEach(treeItem -> expandAll((TreeItem) treeItem));
  }
}
