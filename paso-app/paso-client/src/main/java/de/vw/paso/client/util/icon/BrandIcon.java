package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

import de.vw.paso.masterdata.Brand;

public enum BrandIcon implements Icon {
  AU("au.gif"),
  BG("bg.gif"),
  BY("by.gif"),
  CU("cu.gif"),
  DU("du.gif"),
  FO("fo.gif"),
  LB("lb.gif"),
  MN("mn.gif"),
  PO("po.gif"),
  SE("se.gif"),
  SK("sk.gif"),
  VN("vn.gif"),
  VW("vw.gif"),
  UNKNOWN("unknown.gif");

  private static final String FOLDER_NAME = "icons/brand";

  private final String fileName;
  private final Image image;

  BrandIcon(String fileName) {
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

  public static Image getImageForBrand(Brand brand) {
    Image brandImage;
    try {
      brandImage = valueOf(brand.name()).getImage();
    } catch (Exception e) {
      brandImage = UNKNOWN.getImage();
    }
    return brandImage;
  }
}
