package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum HeaderIcon implements Icon {

  SELECTED_16x16("header_selected-16x16.png"),
  FILTER_16x16("header_filter-16x16.png"),
  ALL_16x16("header_all-16x16.png");

  private static final String FOLDER_NAME = "icons/header";
  private static final String ICON_NAME_SEPARATOR = "-";

  private final String fileName;
  private final Image image;

  HeaderIcon(final String fileName) {
    this.fileName = fileName;
    this.image = IconUtil.loadImage(getPath());
  }

  public String getPath() {
    return FOLDER_NAME + "/" + getFileName();
  }

  @Override
  public String getFileName() {
    return fileName;
  }

  @Override
  public Image getImage() {
    return image;
  }

}
