package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum FzgKonfigIcon implements Icon {

  WAIT_16X16("wait-16x16.png"),
  WAIT_24X24("wait-24x24.png"),
  WAIT_32X32("wait-32x32.png"),

  IMPORTED_16X16("imported-16x16.png"),
  IMPORTED_24X24("imported-24x24.png"),
  IMPORTED_32X32("imported-32x32.png"),

  NOT_IMPORTED_16X16("not-imported-16x16.png"),
  NOT_IMPORTED_24X24("not-imported-24x24.png"),
  NOT_IMPORTED_32X32("not-imported-32x32.png"),

  OK_16X16("ok-16x16.png"),
  OK_24X24("ok-24x24.png"),
  OK_32X32("ok-32x32.png"),

  WARN_16X16("warn-16x16.png"),
  WARN_24X24("warn-24x24.png"),
  WARN_32X32("warn-32x32.png"),

  ERROR_16X16("error-16x16.png"),
  ERROR_24X24("error-24x24.png"),
  ERROR_32X32("error-32x32.png"),

  NOTIFY_16X16("notify-16x16.png"),
  NOTIFY_24X24("notify-24x24.png"),
  NOTIFY_32X32("notify-32x32.png"),
  ;

  private static final String FOLDER_NAME = "icons/fzgkonfig";

  private final String fileName;
  private final Image image;

  FzgKonfigIcon(String fileName) {
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
