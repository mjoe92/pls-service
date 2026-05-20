package de.vw.paso.exception;

public class ServerException extends Exception implements IServerException {

  public ServerException(String message) {
    super(message);
  }

  public ServerException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public ServerException(Throwable throwable) {
    super(throwable);
  }

  @Override
  public String getMessageKey() {
    return "server.exception";
  }
}
