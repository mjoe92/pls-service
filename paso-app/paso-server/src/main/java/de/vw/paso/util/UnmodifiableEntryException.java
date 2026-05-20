package de.vw.paso.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnmodifiableEntryException extends RuntimeException {

  public UnmodifiableEntryException(String message) {
    super(message);
  }
}
