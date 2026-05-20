package de.vw.paso.exception;

public class MBTImportException extends ServerException {

  public MBTImportException(String message, Throwable cause) {
    super(message, cause);
  }

  public MBTImportException(String message) {
    super(message);
  }

  @Override
  public String getMessageKey() {
    return "mbtimport.MBTImportException";
  }
}
