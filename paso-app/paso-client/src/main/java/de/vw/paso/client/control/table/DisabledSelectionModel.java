package de.vw.paso.client.control.table;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

public class DisabledSelectionModel<S> extends TableView.TableViewSelectionModel<S> {
  public DisabledSelectionModel(TableView<S> tableView) {
    super(tableView);
  }

  @Override
  public ObservableList<TablePosition> getSelectedCells() {
    return FXCollections.emptyObservableList();
  }

  @Override
  public boolean isSelected(int i, TableColumn<S, ?> tableColumn) {
    return false;
  }

  @Override
  public void select(int i, TableColumn<S, ?> tableColumn) {
    // default implementation ignored
  }

  @Override
  public void clearAndSelect(int i, TableColumn<S, ?> tableColumn) {
    // default implementation ignored
  }

  @Override
  public void clearSelection(int i, TableColumn<S, ?> tableColumn) {
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
