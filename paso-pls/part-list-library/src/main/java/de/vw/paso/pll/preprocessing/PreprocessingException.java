package de.vw.paso.pll.preprocessing;

import java.io.Serial;

public class PreprocessingException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 3945601920731318413L;

  public PreprocessingException(String message) {
    super(message);
  }

  public PreprocessingException(String format, Object... args) {
    this(String.format(format, args));
  }

  public PreprocessingException(Throwable cause, String format, Object... args) {
    this(String.format(format, args), cause);
  }

  public PreprocessingException(String message, Throwable cause) {
    super(message, cause);
  }

}
