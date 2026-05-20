package de.vw.paso.pls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StaticProductIdReader {

  private static final Logger LOG = LoggerFactory.getLogger(StaticProductIdReader.class);

  private StaticProductIdReader() {
    throw new IllegalArgumentException("Util class");
  }

  public static List<String> read(final String path) {
    try (InputStream inputStream = StaticProductIdReader.class.getClassLoader().getResourceAsStream(path);
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
      return bufferedReader.lines().toList();
    } catch (IOException exception) {
      LOG.error("Exception while reading static product ids", exception);
    }

    return new ArrayList<>();
  }

}
