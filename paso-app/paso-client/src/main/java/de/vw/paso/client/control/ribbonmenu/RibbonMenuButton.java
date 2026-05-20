package de.vw.paso.client.control.ribbonmenu;

import javafx.scene.control.MenuButton;

public class RibbonMenuButton extends MenuButton {

  protected static final String STYLE_RIBBON_BUTTON = "ribbon-button";
  protected static final String STYLE_RIBBON_MENU_BUTTON = "ribbon-menu-button";

  public RibbonMenuButton(String text) {
    super(text);
    initialize();
  }

  private void initialize() {
    getStyleClass().add(STYLE_RIBBON_BUTTON);
  }
}
