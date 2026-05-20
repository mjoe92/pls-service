package de.vw.paso.client.util.desktop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DesktopLink {

  private static final Logger LOGGER = LoggerFactory.getLogger(DesktopLink.class);

  private DesktopLink() {
    throw new IllegalArgumentException("Util class");
  }

  public static void createLink(String url, String iconUrl, String linkName) throws IOException {
    Path iconDirectory = Path.of(System.getenv("APPDATA") + "/PASO");
    LOGGER.debug("Check if icon directory {} exists", iconDirectory);
    if (!iconDirectory.toFile().exists()) {
      LOGGER.debug("Directory does not exists. Will be created");
      Files.createDirectories(iconDirectory);
    } else {
      LOGGER.debug("Directory exists.");
    }

    Path iconPath = iconDirectory.resolve(linkName + ".ico");
    LOGGER.debug("Check if link icon exists:{}", iconPath);
    if (!iconPath.toFile().exists()) {
      LOGGER.debug("File does not exist. Create new");
      try (InputStream resourceAsStream = DesktopLink.class.getResourceAsStream(iconUrl)) {
        Files.copy(resourceAsStream, iconPath);
      }
    } else {
      LOGGER.debug("Link icon already exists. Will reuse.");
    }

    DesktopDirectory desktopDirectory = DesktopDirectory.find();
    String linkFilePath = String.format("%s/%s.url", desktopDirectory.path(), linkName);
    File file = new File(linkFilePath);

    if (!file.exists()) {
      LOGGER.debug("Create desktop link at: {}", linkFilePath);
      StringBuilder stringBuilder = new StringBuilder("[InternetShortcut]\n");
      stringBuilder.append("URL=").append(url);
      stringBuilder.append("\nIDList=\nHotKey=0");
      stringBuilder.append("\nIconFile=").append(iconPath.toAbsolutePath());
      stringBuilder.append("\nIconIndex=0");
      LOGGER.debug("Content of link:{}", stringBuilder);
      List<String> list = new ArrayList<>();
      list.add(stringBuilder.toString());
      Files.write(Path.of(linkFilePath), list, StandardOpenOption.CREATE);
    } else {
      LOGGER.debug("Desktop link already exists. Will not create new link.");
      throw new FileExistsException();
    }
  }
}
