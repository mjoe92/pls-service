package de.vw.paso.pls;

import de.vw.paso.pls.exception.ErrorCode;
import de.vw.paso.pls.exception.PlsError;
import de.vw.paso.pls.exception.PlsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PlsExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(PlsExceptionHandler.class);

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<String> handleError(Exception ex) {
    LOG.error(ex.getMessage(), ex);

    return ResponseEntity.internalServerError().build();
  }

  @ExceptionHandler(value = PlsException.class)
  protected ResponseEntity<Object> handlePlsException(PlsException exception) {
    LOG.error("PLS error code: {}", exception.getErrorCode(), exception);

    ErrorCode errorCode = exception.getErrorCode();
    return new ResponseEntity<>(new PlsError(errorCode), new HttpHeaders(), errorCode.getHttpStatus());
  }

}
