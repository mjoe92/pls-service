package de.vw.paso.client.util.desktop;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DesktopDirectory {

  private static final List<String> POTENTIAL_LOCATIONS = List.of("OneDrive - Volkswagen AG/Desktop", "Desktop");

  private final Path path;

  private DesktopDirectory(Path path) {
    this.path = path;
  }

  public String path() {
    return path.toString();
  }

  public static DesktopDirectory find() {
    String userHome = System.getProperty("user.home");

    for (String potentialLocation : POTENTIAL_LOCATIONS) {
      Path potentialDesktopPath = Path.of(userHome, potentialLocation);
      if(Files.isDirectory(potentialDesktopPath)) {
        return new DesktopDirectory(potentialDesktopPath);
      }
    }

    return new DesktopDirectory(Path.of(userHome));
  }

}
