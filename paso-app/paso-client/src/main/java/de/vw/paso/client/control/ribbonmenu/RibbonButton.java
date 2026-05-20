package de.vw.paso.client.control.ribbonmenu;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RibbonButton extends Button {

  private static final int RIBBON_BUTTON_SMALL_MAX_SIZE = 16;
  private static final String RIBBON_BUTTON_SMALL = "ribbon-button-small";
  private static final String RIBBON_BUTTON = "ribbon-button";

  public RibbonButton(final String text, final Image icon) {
    super(text, new ImageView(icon));

    getStyleClass().add((icon.getWidth() <= RIBBON_BUTTON_SMALL_MAX_SIZE) ? RIBBON_BUTTON_SMALL : RIBBON_BUTTON);
  }

}
