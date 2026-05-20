package de.vw.paso.pls.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlsError {

  private long date;
  private String errorCode;
  private String errorMessage;

  public PlsError(ErrorCode errorCode) {
    this.errorCode = errorCode.name();
    this.errorMessage = errorCode.getReason();
    this.date = System.currentTimeMillis();
  }
}
