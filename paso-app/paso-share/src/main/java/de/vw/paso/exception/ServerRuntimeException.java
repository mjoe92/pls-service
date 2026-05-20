package de.vw.paso.exception;

public class ServerRuntimeException extends RuntimeException implements IServerException {

  public ServerRuntimeException(String s) {
    super(s);
  }

  public ServerRuntimeException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public ServerRuntimeException(Throwable throwable) {
    super(throwable);
  }

  @Override
  public String getMessageKey() {
    return "server.exception";
  }
}
