package de.vw.paso.client.control.ribbonmenu;

import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RibbonMenuToggleButton extends ToggleButton {

  private static final int RIBBON_BUTTON_SMALL_MAX_SIZE = 16;
  private static final String RIBBON_BUTTON_SMALL = "ribbon-button-small";
  private static final String RIBBON_BUTTON = "ribbon-button";

  public RibbonMenuToggleButton(final String text, final Image icon) {
    super(text, new ImageView(icon));

    getStyleClass().add((icon.getWidth() <= RIBBON_BUTTON_SMALL_MAX_SIZE) ? RIBBON_BUTTON_SMALL : RIBBON_BUTTON);
  }

}
