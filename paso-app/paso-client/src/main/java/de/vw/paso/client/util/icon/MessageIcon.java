package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum MessageIcon implements Icon {

  READ_16X16("read-16x16.png"),
  READ_24X24("read-24x24.png"),
  READ_32X32("read-32x32.png"),

  UNREAD_16X16("unread-16x16.png"),
  UNREAD_24X24("unread-24x24.png"),
  UNREAD_32X32("unread-32x32.png"),

  INFO_16X16("info-16x16.png"),
  INFO_24X24("info-24x24.png"),
  INFO_32X32("info-32x32.png"),
  ;

  private static final String FOLDER_NAME = "icons/message";

  private final String fileName;
  private final Image image;

  MessageIcon(String fileName) {
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
