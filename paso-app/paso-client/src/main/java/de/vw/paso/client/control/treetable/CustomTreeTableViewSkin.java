package de.vw.paso.client.control.treetable;

import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TreeTableViewSkin;

import de.vw.paso.client.control.tablebase.header.CustomTableHeaderRow;

public class CustomTreeTableViewSkin<T> extends TreeTableViewSkin<T> {

  public CustomTreeTableViewSkin(TreeTableView<T> control) {
    super(control);
  }

  @Override
  protected TableHeaderRow createTableHeaderRow() {
    return new CustomTableHeaderRow(this);
  }
}
