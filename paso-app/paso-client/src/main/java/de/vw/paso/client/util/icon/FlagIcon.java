package de.vw.paso.client.util.icon;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Locale;

import javafx.scene.image.Image;

import de.vw.paso.utility.StringCommonTermsUtil;

public enum FlagIcon implements Icon {

  DE_22X16("de-22x16.png"), GB_22X16("gb-22x16.png"), LANGUAGE_32X32("language-32x32.png");

  private static final String FOLDER_NAME = "icons/i18n";
  private final String fileName;
  private final Image image;

  FlagIcon(String fileName) {
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

  public static Image getImage(Locale locale) {
    return getImage(getFileNameFlag(locale));
  }

  private static Image getImage(String fileName) {
    try (var is = ClassLoader.getSystemResourceAsStream(fileName)) {

      if (is == null) {
        try (var is2 = ClassLoader.getSystemResourceAsStream(getFileNameUnknownFlag())) {
          if (is2 == null) {
            throw new IOException("Could not find icon: " + fileName);
          }

          return new Image(is2);
        }
      }

      return new Image(is);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static String getFileNameFlag(Locale locale) {
    return FOLDER_NAME + "/" + locale.getCountry().toLowerCase() + "." + StringCommonTermsUtil.PNG_LOW_CASE;
  }

  private static String getFileNameUnknownFlag() {
    return FOLDER_NAME + "/unknown.png";
  }

}
