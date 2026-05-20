package de.vw.paso.client.util.highlight;

import static de.vw.paso.client.stueckliste.compare.partlist.PartListCompareTabController.COMPARE_STATUS_ADDED;
import static de.vw.paso.client.stueckliste.compare.partlist.PartListCompareTabController.COMPARE_STATUS_CHANGED;
import static de.vw.paso.client.stueckliste.compare.partlist.PartListCompareTabController.COMPARE_STATUS_DELETED;

import java.util.Objects;
import java.util.function.BiConsumer;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.stueckliste.compare.partlist.PartListCompareRow;
import de.vw.paso.client.stueckliste.efs.tree.model.CostGroupTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.FgSetTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.PartGroupTreeItem;
import de.vw.paso.client.stueckliste.fzgkonfig.content.konfiguration.PrNumberTreeItemObject;
import de.vw.paso.compare.config.ConfigCompareRow;
import de.vw.paso.compare.costgroup.CostGroupCompareRow;
import de.vw.paso.compare.fgset.FGSetCompareRow;
import de.vw.paso.compare.partgroup.PartGroupCompareRow;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class SelectionHighlightManager<S> {

  private ChangeListener<Boolean> doHighlightChangeListener;
  private TreeTableView<S> table;

  private int lastIndex = -1;

  public void initTable(final TreeTableView<S> table, String rowStyle, String columnStyle,
    BiConsumer<TreeTableRow<S>, Boolean> updateItemCallback) {
    this.table = table;

    addCtrlDownHandler(table);
    setRowFactory(rowStyle, updateItemCallback);

    table.getSelectionModel().selectedIndexProperty()
      .addListener((obs, oldV, newV) -> lastIndex = table.getSelectionModel().getSelectedIndex());
    table.getSelectionModel().getSelectedCells()
      .addListener((InvalidationListener) c -> setColumnHighlighting(columnStyle));
    // runLater() so the sort will come first, and then we actually scroll.
    table.setOnSort(event -> Platform.runLater(() -> scrollToItem(table)));

    doHighlightChangeListener = (observable, oldValue, newValue) -> refreshView(columnStyle);
    SelectionHighlightManagerUtil.getDohighLightProperty().addListener(doHighlightChangeListener);
  }

  public void initTable(final TreeTableView<S> table, String rowStyle, String columnStyle) {
    initTable(table, rowStyle, columnStyle, null);
  }

  public void removeFromTable() {
    if (doHighlightChangeListener != null) {
      SelectionHighlightManagerUtil.getDohighLightProperty().removeListener(doHighlightChangeListener);
    }
  }

  public void setStyleToTreeTableColumnGroups(TreeTableView<?> treeTableView, boolean lastIsDelta) {
    int groupIndex = 0;
    ObservableList<? extends TreeTableColumn<?, ?>> columns = treeTableView.getColumns();
    for (int i = 0; i < columns.size(); i++) {
      if (!lastIsDelta || i + 1 < columns.size()) {
        TreeTableColumn<?, ?> column = columns.get(i);
        if (!column.getColumns().isEmpty()) {
          String styleClass = getGroupStyle(groupIndex);
          column.getColumns().forEach(childColumn -> childColumn.getStyleClass().add(styleClass));
          groupIndex++;
        }
      }
    }
  }

  private void addCtrlDownHandler(TreeTableView<?> view) {
    view.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
      if (event.getCode() == KeyCode.CONTROL) {
        SelectionHighlightManagerUtil.getControlPressed().set(true);
      }
    });
    view.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
      if (event.getCode() == KeyCode.CONTROL) {
        SelectionHighlightManagerUtil.getControlPressed().set(false);
      }
    });
  }

  private String getGroupStyle(int groupIndex) {
    int columnStyleNum = (groupIndex % 4) + 1;
    return "group-column" + columnStyleNum;
  }

  private void refreshView(String columnStyle) {
    removeColumnHighlighting(columnStyle);
    table.refresh();
    setColumnHighlighting(columnStyle);
  }

  private void removeColumnHighlighting(String columnStyle) {
    for (TreeTableColumn<?, ?> column : table.getColumns()) {
      column.getStyleClass().remove(columnStyle);
    }
  }

  private void scrollToItem(TreeTableView<S> table) {
    if (lastIndex == -1) {
      return;
    }

    if (table instanceof CustomTreeTableView<S> customTreeTableView) {
      customTreeTableView.scrollToCenter(lastIndex);
    }
  }

  private void setColumnHighlighting(String columnStyle) {
    if (!SelectionHighlightManagerUtil.getDohighLightProperty().get()) {
      return;
    }

    removeColumnHighlighting(columnStyle);

    ObservableList<? extends TreeTablePosition<?, ?>> selectedCells = table.getSelectionModel().getSelectedCells();

    for (TreeTablePosition<?, ?> cell : selectedCells) {
      TreeTableColumn<?, ?> tableColumn = cell.getTableColumn();
      if (tableColumn != null) {
        if (!tableColumn.getStyleClass().contains(columnStyle)) {
          tableColumn.getStyleClass().add(columnStyle);
        }
      }
    }
  }

  private void setRowFactory(String rowStyle, BiConsumer<TreeTableRow<S>, Boolean> updateItemCallback) {
    if (table.getRowFactory() != null) {
      throw new IllegalArgumentException("""
        Table already has a row factory.
        It will be overridden by the SelectionHighlightManager.
        Consider using the corresponding initTable(..) function instead and set a updateItemCallback.
        """);
    }

    table.setRowFactory(e -> new HighlightingRow(table, rowStyle, updateItemCallback));
  }

  private class HighlightingRow extends TreeTableRow<S> {

    private final String rowStyle;
    private final BiConsumer<TreeTableRow<S>, Boolean> updateItemCallback;

    public HighlightingRow(TreeTableView<S> table, String rowStyle, BiConsumer<TreeTableRow<S>, Boolean> updateItemCallback) {
      this.rowStyle = rowStyle;
      this.updateItemCallback = updateItemCallback;

      table.getSelectionModel().getSelectedItems()
        .addListener((InvalidationListener) c -> setRowHighlighting(rowStyle));
    }

    @Override
    protected void updateItem(S item, boolean empty) {
      super.updateItem(item, empty);

      if (updateItemCallback != null) {
        updateItemCallback.accept(this, empty);
      }

      getStyleClass().remove("tree-highlight-group-row");
      getStyleClass().remove("font-bold");
      getStyleClass().remove("duplicate");
      getStyleClass().remove("last-row");
      getStyleClass().remove("cost-group-last-row");
      getStyleClass().remove("part-group-last-row");
      getStyleClass().remove("compare_changed");
      getStyleClass().remove("summary-row");
      getStyleClass().removeAll(COMPARE_STATUS_ADDED, COMPARE_STATUS_DELETED, COMPARE_STATUS_CHANGED);

      setRowHighlighting(rowStyle);

      if (empty) {
        return;
      }

      TreeItem<?> treeItem = getTreeTableView().getTreeItem(getIndex());

      if (treeItem != null && treeItem.getValue() != null) {
        if (treeItem instanceof EfsElementTreeItem
          && ((EfsElementTreeItem) treeItem).propertyGroupString().get() != null
          && !((EfsElementTreeItem) treeItem).propertyGroupString().get().isEmpty()) {
          getStyleClass().add("tree-highlight-group-row");
        }
        if (treeItem instanceof FgSetTreeItem fgSetTreeItem) {
          if (fgSetTreeItem.propertySummaryRow().get()) {
            getStyleClass().add("summary-row");
          }
        }
        if (treeItem instanceof CostGroupTreeItem costGroupTreeItem) {
          if (costGroupTreeItem.propertySummaryRow().get()) {
            getStyleClass().add("summary-row");
          }
        }
        if (treeItem instanceof PartGroupTreeItem partGroupTreeItem) {
          if (partGroupTreeItem.propertySummaryRow().get()) {
            getStyleClass().add("summary-row");
          }
        }

        Object value = treeItem.getValue();
        if (value instanceof EfsElementDTO && ((EfsElementDTO) value).getDuplicateId() != null) {
          getStyleClass().add("duplicate");
        } else if (value instanceof FGSetCompareRow) {
          if (((FGSetCompareRow) value).isSum()) {
            getStyleClass().add("last-row");
          }
        } else if (value instanceof CostGroupCompareRow) {
          if (((CostGroupCompareRow) value).isSum()) {
            getStyleClass().add("cost-group-last-row");
          }
        } else if (value instanceof PartGroupCompareRow) {
          if (((PartGroupCompareRow) value).isSum()) {
            getStyleClass().add("part-group-last-row");
          }
        } else if (value instanceof ConfigCompareRow) {
          if (((ConfigCompareRow) value).getPrNumberFamily() != null) {
            getStyleClass().add("font-bold");
          }
        } else if (value instanceof PartListCompareRow partListCompareRow) {
          getStyleClass().removeAll(COMPARE_STATUS_ADDED, COMPARE_STATUS_DELETED, COMPARE_STATUS_CHANGED);
          switch (partListCompareRow.getRowStatus()) {
            case ADDED:
              getStyleClass().add(COMPARE_STATUS_ADDED);
              break;
            case DELETED:
              getStyleClass().add(COMPARE_STATUS_DELETED);
              break;
            case CHANGED:
              getStyleClass().add(COMPARE_STATUS_CHANGED);
          }
        }
        if (value instanceof PrNumberTreeItemObject) {
          if (((PrNumberTreeItemObject) value).isFamily()) {
            getStyleClass().add("font-bold");
          }
        }
      }
    }

    private void setRowHighlighting(String rowStyle) {
      if (!SelectionHighlightManagerUtil.getDohighLightProperty().get()) {
        getStyleClass().remove(rowStyle);
        return;
      }

      if (isEmpty()) {
        getStyleClass().remove(rowStyle);
        return;
      }

      TreeItem<?> treeItem = getTreeTableView().getTreeItem(getIndex());

      ObservableList<TreeItem<S>> selectedItems = getTreeTableView().getSelectionModel().getSelectedItems();
      if (selectedItems.stream().anyMatch(selectedItem -> Objects.equals(selectedItem, treeItem))) {
        if (!getStyleClass().contains(rowStyle)) {
          getStyleClass().add(rowStyle);
        }
      } else {
        getStyleClass().remove(rowStyle);
      }
    }
  }

}
