package de.vw.paso.pls.exception;

public class PlsException extends RuntimeException {

  private final ErrorCode errorCode;

  public PlsException(final ErrorCode errorCode) {
    super(errorCode.getReason());

    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
