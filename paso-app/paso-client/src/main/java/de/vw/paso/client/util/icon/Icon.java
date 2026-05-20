package de.vw.paso.client.util.icon;

import javafx.scene.image.Image;

import org.apache.commons.lang3.StringUtils;

public interface Icon {

  String getPath();

  String getFileName();

  Image getImage();

  default String getIconName() {
    return StringUtils.substringBeforeLast(getFileName(), "-").replace("-", "");
  }

}
