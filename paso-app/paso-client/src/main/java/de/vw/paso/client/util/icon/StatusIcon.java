package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum StatusIcon implements Icon {

  // @formatter:off
		ACCEPT_16X16("accept-16x16.png"),
		ACCEPT_24X24("accept-24x24.png"),
		ACCEPT_32X32("accept-32x32.png"),
		ACCEPT_48X48("accept-48x48.png"),
		ACCEPT_64X64("accept-64x64.png"),
		ACCEPT_72X72("accept-72x72.png"),
		ACCEPT_96X96("accept-96x96.png"),
		ACCEPT_128X128("accept-128x128.png"),

		ERROR_16X16("error-16x16.png"),
		ERROR_24X24("error-24x24.png"),
		ERROR_32X32("error-32x32.png"),
		ERROR_48X48("error-48x48.png"),
		ERROR_64X64("error-64x64.png"),
		ERROR_72X72("error-72x72.png"),
		ERROR_96X96("error-96x96.png"),
		ERROR_128X128("error-128x128.png"),

		HELP_16X16("help-16x16.png"),
		HELP_24X24("help-24x24.png"),
		HELP_32X32("help-32x32.png"),
		HELP_48X48("help-48x48.png"),
		HELP_64X64("help-64x64.png"),
		HELP_72X72("help-72x72.png"),
		HELP_96X96("help-96x96.png"),
		HELP_128X128("help-128x128.png"),

		INFO_16X16("info-16x16.png"),
		INFO_24X24("info-24x24.png"),
		INFO_32X32("info-32x32.png"),
		INFO_48X48("info-48x48.png"),
		INFO_64X64("info-64x64.png"),
		INFO_72X72("info-72x72.png"),
		INFO_96X96("info-96x96.png"),
		INFO_128X128("info-128x128.png"),

		WARNING_16X16("warning-16x16.png"),
		WARNING_24X24("warning-24x24.png"),
		WARNING_32X32("warning-32x32.png"),
		WARNING_48X48("warning-48x48.png"),
		WARNING_64X64("warning-64x64.png"),
		WARNING_72X72("warning-72x72.png"),
		WARNING_96X96("warning-96x96.png"),
		WARNING_128X128("warning-128x128.png")
		// @formatter:on
  ;

  private static final String FOLDER_NAME = "icons/status";

  private final String fileName;
  private final Image image;

  StatusIcon(String fileName) {
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
