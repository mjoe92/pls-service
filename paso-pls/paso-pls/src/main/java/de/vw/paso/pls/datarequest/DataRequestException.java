package de.vw.paso.pls.datarequest;

public class DataRequestException extends Exception {

  public DataRequestException(String s) {
    super(s);
  }

  public DataRequestException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public DataRequestException(Throwable throwable) {
    super(throwable);
  }
}
