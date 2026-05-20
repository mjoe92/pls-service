package de.vw.paso.client.control.listview;

import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class ListWithHeader<T> extends VBox {

  private final ListView<T> listView;

  private Consumer<MouseEvent> doubleCLickAction;

  public ListWithHeader(String headerText) {
    Label l = new Label(headerText);
    getChildren().add(l);

    listView = new ListView<>(FXCollections.observableArrayList());
    listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    getChildren().add(listView);

    listView.setOnMouseClicked(mouseEvent -> {
      if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
        doubleClick(mouseEvent);
      }
    });

    VBox.setVgrow(listView, Priority.ALWAYS);
  }

  public ListView<T> getListView() {
    return listView;
  }

  private void doubleClick(MouseEvent mouseEvent) {
    if (doubleCLickAction != null) {
      doubleCLickAction.accept(mouseEvent);
    }
  }

  public void setOnDoubleClick(Consumer<MouseEvent> doubleCLickAction) {
    this.doubleCLickAction = doubleCLickAction;
  }
}
