package de.vw.paso.client.exception;

public class ControllerException extends RuntimeException {

  public ControllerException(String message) {
    super(message);
  }

  public ControllerException(String message, Exception e) {
    super(message, e);
  }
}
