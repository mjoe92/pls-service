package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

public enum StammdatenIcon implements Icon {

  STAMMDATEN_16X16("stammdaten-16x16.png"),
  STAMMDATEN_24X24("stammdaten-24x24.png"),
  STAMMDATEN_32X32("stammdaten-32x32.png"),
  STAMMDATEN_48X48("stammdaten-48x48.png"),

  ADD_16X16("stammdaten-add-16x16.png"),
  ADD_24X24("stammdaten-add-24x24.png"),
  ADD_32X32("stammdaten-add-32x32.png"),
  ADD_48X48("stammdaten-add-48x48.png"),

  REMOVE_16X16("stammdaten-remove-16x16.png"),
  REMOVE_24X24("stammdaten-remove-24x24.png"),
  REMOVE_32X32("stammdaten-remove-32x32.png"),
  REMOVE_48X48("stammdaten-remove-48x48.png"),

  EDIT_16X16("stammdaten-edit-16x16.png"),
  EDIT_24X24("stammdaten-edit-24x24.png"),
  EDIT_32X32("stammdaten-edit-32x32.png"),
  EDIT_48X48("stammdaten-edit-48x48.png"),

  REFRESH_16X16("stammdaten-refresh-16x16.png"),
  REFRESH_24X24("stammdaten-refresh-24x24.png"),
  REFRESH_32X32("stammdaten-refresh-32x32.png"),
  REFRESH_48X48("stammdaten-refresh-48x48.png"),

  UNKNOWN_ICON_16X16("stammdaten-unknownicon-16x16.png"),

  MBT_IMPORT("mbt-import-32x32.png"),
  PART_GROUP_IMPORT("mbt-import-32x32.png"),
  PARTLIST_FIX_32X32("partlist-fix-32x32.png"),
  PARTLIST_FIX_48X48("partlist-fix-48x48.png"),
  ;

  private static final String FOLDER_NAME = "icons/stammdaten";

  private final String fileName;
  private final Image image;

  StammdatenIcon(String fileName) {
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
