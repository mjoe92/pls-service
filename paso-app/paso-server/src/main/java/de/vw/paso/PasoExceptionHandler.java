package de.vw.paso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PasoExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(PasoExceptionHandler.class);

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<String> handleError(Exception ex) {
    LOG.error(ex.getMessage(), ex);

    return ResponseEntity.internalServerError().build();
  }

}
