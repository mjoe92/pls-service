package de.vw.paso.client.control.table;

import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;

import de.vw.paso.client.control.tablebase.header.CustomTableHeaderRow;

public class CustomTableViewSkin<T> extends TableViewSkin<T> {

  public CustomTableViewSkin(TableView<T> control) {
    super(control);
  }

  @Override
  protected TableHeaderRow createTableHeaderRow() {
    return new CustomTableHeaderRow(this);
  }
}
