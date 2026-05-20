package de.vw.paso.exception;

public class CategoryCanNotBeDeletedException extends ServerException {

  public CategoryCanNotBeDeletedException(String message) {
    super(message);
  }

  @Override
  public String getMessageKey() {
    return "server.CategoryCanNotBeDeletedException";
  }
}
