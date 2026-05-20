package de.vw.paso.client.stueckliste.efs.control;

import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AggregateImageTextFieldControl extends BaseAggregateTextFieldControl {

  private static final String FXML_FILE = "aggregate-text-field-image.fxml";

  @FXML
  private ImageView imageView;

  @Override
  protected String getFxmlFile() {
    return FXML_FILE;
  }

  public void setImageView(final Image image) {
    imageView.setImage(image);
  }

  public ImageView getImageView() {
    return imageView;
  }

  public void setTooltip(String text) {
    Tooltip tt = new Tooltip(text);
    Tooltip.install(getTextField(), tt);
    Tooltip.install(imageView, tt);
  }
}
