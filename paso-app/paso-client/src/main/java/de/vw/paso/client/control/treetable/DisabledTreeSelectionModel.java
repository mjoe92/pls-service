package de.vw.paso.client.control.treetable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;

public class DisabledTreeSelectionModel<S> extends TreeTableView.TreeTableViewSelectionModel<S> {
  public DisabledTreeSelectionModel(TreeTableView<S> treeTableView) {
    super(treeTableView);
  }

  @Override
  public ObservableList<TreeTablePosition<S, ?>> getSelectedCells() {
    return FXCollections.emptyObservableList();
  }

  @Override
  public boolean isSelected(int i, TableColumnBase<TreeItem<S>, ?> tableColumnBase) {
    return false;
  }

  @Override
  public void select(int i, TableColumnBase<TreeItem<S>, ?> tableColumnBase) {
    // default implementation ignored
  }

  @Override
  public void clearAndSelect(int i, TableColumnBase<TreeItem<S>, ?> tableColumnBase) {
    // default implementation ignored
  }

  @Override
  public void clearSelection(int i, TableColumnBase<TreeItem<S>, ?> tableColumnBase) {
    // default implementation ignored
  }

  @Override
  public void selectLeftCell() {
    // default implementation ignored
  }

  @Override
  public void selectRightCell() {
    // default implementation ignored
  }

  @Override
  public void selectAboveCell() {
    // default implementation ignored
  }

  @Override
  public void selectBelowCell() {
    // default implementation ignored
  }
}
