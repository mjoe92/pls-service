package de.vw.paso.client.control.tablebase.filter.panel;

import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CustomFilterPanel extends VBox {

  private final ListView<CustomTableFilterValue> checkListView;
  private final HBox buttonBox;
  private final VBox searchArea;

  CustomFilterPanel(VBox searchAreaBox, ListView<CustomTableFilterValue> listView, HBox buttonContainingBox) {
    searchArea = searchAreaBox;
    checkListView = listView;
    buttonBox = buttonContainingBox;

    getChildren().addAll(searchArea, checkListView, buttonBox);
  }

  public ListView<CustomTableFilterValue> getCheckListView() {
    return checkListView;
  }

  public HBox getButtonBox() {
    return buttonBox;
  }

  public VBox getSearchArea() {
    return searchArea;
  }
}
