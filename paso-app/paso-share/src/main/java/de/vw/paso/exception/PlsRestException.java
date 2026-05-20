package de.vw.paso.exception;

import lombok.Getter;

public class PlsRestException extends RuntimeException {

  @Getter
  private PlsError errorCode;

  public PlsRestException(PlsError error) {
    this.errorCode = error;
  }
}
