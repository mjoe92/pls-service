package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum FahrzeugIcon implements Icon {

  FZG_KONFIG_16X16("fzg-konfig-16x16.png"),
  FZG_KONFIG_24X24("fzg-konfig-24x24.png"),
  FZG_KONFIG_32X32("fzg-konfig-32x32.png"),
  FZG_KONFIG_48X48("fzg-konfig-48x48.png"),

  FZG_24X24("fzg-24x24.png"),
  FZG_32X32("fzg-32x32.png"),
  FZG_48X48("fzg-48x48.png"),

  MOTOR_24X24("motor-24x24.png"),
  MOTOR_32X32("motor-32x32.png"),
  MOTOR_48X48("motor-48x48.png"),

  GETRIEBE_24X24("getriebe-24x24.png"),
  GETRIEBE_32X32("getriebe-32x32.png"),
  GETRIEBE_48X48("getriebe-48x48.png"),

  ENGINE_GEARBOX_24x24("engine-gearbox-24x24.png"),
  ENGINE_GEARBOX_32x32("engine-gearbox-32x32.png"),
  ENGINE_GEARBOX_48x48("engine-gearbox-48x48.png"),
  ;

  private static final String FOLDER_NAME = "icons/fahrzeug";

  private final String fileName;
  private final Image image;

  FahrzeugIcon(String fileName) {
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
