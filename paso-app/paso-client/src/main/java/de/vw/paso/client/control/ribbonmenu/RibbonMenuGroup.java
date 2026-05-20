package de.vw.paso.client.control.ribbonmenu;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RibbonMenuGroup extends VBox {

  private Label labelTitle;

  public RibbonMenuGroup(String title) {
    super();
    init(title);
  }

  private void init(String title) {
    labelTitle = new Label(title);
    styleGroup();
  }


  private void styleGroup() {
    this.getStyleClass().add("ribbon-menu-group-box");
    labelTitle.getStyleClass().add("ribbon-menu-group-title");
  }


  public void addItemBox(Node node) {
    if (getChildren().contains(labelTitle)) {
      getChildren().remove(labelTitle);
    }

    getChildren().add(node);

    onlyGrowLastItemChild();

    getChildren().add(labelTitle);
  }


  private void onlyGrowLastItemChild() {
    int size = getChildren().size();
    if (size == 0)
      return;

    Node lastChild = getChildren().get(size - 1);

    for (Node child : getChildren()) {
      if (child.equals(lastChild)) {
        VBox.setVgrow(child, Priority.ALWAYS);
      } else {
        VBox.setVgrow(child, Priority.NEVER);
      }
    }
  }
}
