package de.vw.paso.client.auth;

import de.vw.paso.client.base.exception.AbstractClientException;
import de.vw.paso.delegate.base.ErrorCode;

public class CouldNotAuthenticateException extends AbstractClientException {

  private static final ErrorCode noLogin = new ErrorCode("error.login.unknown", "Could not login");

  public CouldNotAuthenticateException() {
    super(noLogin);
  }
}
