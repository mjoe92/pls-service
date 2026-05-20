package de.vw.paso.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SetVersionReferenceException extends RuntimeException {

  public SetVersionReferenceException(String message) {
    super(message);
  }

}
