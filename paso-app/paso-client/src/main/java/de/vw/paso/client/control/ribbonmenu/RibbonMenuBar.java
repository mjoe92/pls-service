package de.vw.paso.client.control.ribbonmenu;

import javafx.scene.control.TabPane;

public class RibbonMenuBar extends TabPane {

  public RibbonMenuBar() {
    super();
    getStyleClass().add("ribbon-menu-tab-pane");
  }

  public void addMenu(RibbonMenu menu) {
    getTabs().add(menu);
    getSelectionModel().select(menu);
  }
}
