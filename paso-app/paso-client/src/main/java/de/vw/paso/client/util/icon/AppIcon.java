package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum AppIcon implements Icon {

  APP_16_ICO("Paso_Icon_16x16px.ico"),
  APP_32_ICO("Paso_Icon_32x32px.ico"),
  APP_64_ICO("Paso_Icon_64x64px.ico"),

  APP_16_PNG("Paso_Icon_16x16px.png"),
  APP_32_PNG("Paso_Icon_32x32px.png"),
  APP_64_PNG("Paso_Icon_64x64px.png"),

  APP_LOGO("Paso_Logo.png"),
  APP_LOGO_SVG("Paso_Logo.svg"),

  APP_LINK("app_link.png"),
  ;

  private static final String FOLDER_NAME = "icons/application";
  private final String fileName;
  private final Image image;

  AppIcon(String fileName) {
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
