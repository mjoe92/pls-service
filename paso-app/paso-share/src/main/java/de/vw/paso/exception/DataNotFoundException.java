package de.vw.paso.exception;

public class DataNotFoundException extends ServerRuntimeException {

  public DataNotFoundException(String message) {
    super(message);
  }

  @Override
  public String getMessageKey() {
    return "server.dataNotFoundException";
  }
}
