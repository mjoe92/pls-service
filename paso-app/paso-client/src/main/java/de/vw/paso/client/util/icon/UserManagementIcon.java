package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum UserManagementIcon implements Icon {

  USER_MANAGEMENT_16x16("userManagement-16x16.png"),
  USER_MANAGEMENT_20x20("userManagement-20x20.png"),
  USER_MANAGEMENT_24x24("userManagement-24x24.png"),
  USER_MANAGEMENT_32x32("userManagement-32x32.png"),

  DELETE_32x32("delete-32x32.png"),
  NEW_32x32("new-32x32.png"),
  EDIT_32x32("edit-32x32.png"),

  USER_DATA_32x32("userdata-32x32.png");

  private static final String FOLDER_NAME = "icons/usermanagement";

  private final String fileName;
  private final Image image;

  UserManagementIcon(final String fileName) {
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
