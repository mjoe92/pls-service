package de.vw.paso.pls.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

  INVALID_IMPORT_ID(HttpStatus.BAD_REQUEST, "Unknown import id"),
  INVALID_PRODUCT(HttpStatus.BAD_REQUEST, "Invalid product id"),
  INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter"),
  MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "Missing parameter"),
  PART_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "Part list not found"),
  PART_LIST_NOT_READY(HttpStatus.BAD_REQUEST, "Part list not ready for creation"),
  NO_PENDING_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "No requested request"),
  UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error");

  private final HttpStatus httpStatus;
  private final String reason;

  ErrorCode(HttpStatus httpStatus, String reason) {
    this.httpStatus = httpStatus;
    this.reason = reason;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public String getReason() {
    return reason;
  }
}
