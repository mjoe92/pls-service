package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum UserRightIcon implements Icon {

  REFRESH("refresh-32x32.png");

  private static final String FOLDER_NAME = "icons/userrightrefresh";

  private final String fileName;
  private final Image image;

  UserRightIcon(String fileName) {
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
