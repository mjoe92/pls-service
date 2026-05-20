package de.vw.paso.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlsError {

  private long date;
  private String errorCode;
  private String errorMessage;

}
