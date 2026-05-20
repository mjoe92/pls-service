package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum FilterIcon implements Icon {

  CLEARFILTERS_32X32("deleteFilter-32x32.png"),
  CLEARFILTERS_16X16("deleteFilter-16x16.png"),
  FILTER_16x16("filter-16x16.png"),
  FILTER_24x24("filter-24x24.png");

  private static final String FOLDER_NAME = "icons/filter";

  private final String fileName;
  private final Image image;

  FilterIcon(final String fileName) {
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
