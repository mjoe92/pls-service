package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum AdminAreaIcon implements Icon {

  TI_WH_REQUEST_QUEUE_ICON_16x16("requestQueue-16x16.png"),
  TI_WH_REQUEST_QUEUE_ICON_32x32("requestQueue-32x32.png"),
  SET_EXPIRY_DAYS_32x32("systemPropertiesDialog-32x32.png");

  private static final String FOLDER_NAME = "icons/adminarea";

  private final String fileName;
  private final Image image;

  AdminAreaIcon(final String fileName) {
    this.fileName = fileName;
    this.image = IconUtil.loadImage(getPath());
  }

  public String getPath() {
    return FOLDER_NAME + "/" + getFileName();
  }

  public String getFileName() {
    return fileName;
  }

  public Image getImage() {
    return image;
  }

}
