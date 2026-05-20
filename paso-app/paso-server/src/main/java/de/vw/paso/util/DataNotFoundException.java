package de.vw.paso.util;

public class DataNotFoundException extends RuntimeException {

  public DataNotFoundException(String msg) {
    super(msg);
  }

  public DataNotFoundException() {
    super();
  }
}
