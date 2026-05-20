package de.vw.paso.client.util.icon;

import java.io.IOException;
import java.io.UncheckedIOException;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class IconUtil {

  private IconUtil() {
    //hidden
  }

  public static void setIcon(Stage stage) {
    stage.getIcons()
      .addAll(AppIcon.APP_16_PNG.getImage(), AppIcon.APP_32_PNG.getImage(), AppIcon.APP_64_PNG.getImage());
  }

  public static Image loadImage(String path) {
    try (var is = ClassLoader.getSystemResourceAsStream(path)) {
      if (is == null) {
        throw new IOException("Could not find icon: " + path);
      }

      return new Image(is);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
