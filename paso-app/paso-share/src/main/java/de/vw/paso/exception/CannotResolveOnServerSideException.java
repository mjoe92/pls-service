package de.vw.paso.exception;

public class CannotResolveOnServerSideException extends ServerException {

  public CannotResolveOnServerSideException(String message) {
    super(message);
  }

  @Override
  public String getMessageKey() {
    return "server.CannotResolveOnServerSideException";
  }
}
