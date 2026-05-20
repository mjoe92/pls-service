package de.vw.paso.pll.preprocessing;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import de.vw.paso.pll.preprocessing.formats.raw.RowWrapper;

public class FileFormatChecker {

  public boolean testFormat(Path path, TiWhFileType type) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.US_ASCII)) {
      String firstLine = reader.readLine();
      if (firstLine == null) {
        throw new IllegalArgumentException("File is empty for path: " + path);
      }

      String secondLine = reader.readLine();
      if (secondLine == null) {
        throw new IllegalArgumentException(
          "Second line should exist for path: " + path + ".\nFirst line is: " + firstLine);
      }

      RowWrapper wrapper = type.createWrapper(secondLine);
      return wrapper.testRowFormat();
    }
  }
}
