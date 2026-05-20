package de.vw.paso.delegate.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperHolder {

  private static ObjectMapper instance;

  private ObjectMapperHolder() {
  }

  public static ObjectMapper getInstance() {
    if (instance == null) {
      instance = new ObjectMapper();
    }
    return instance;
  }
}
