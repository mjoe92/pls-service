package de.vw.paso.exception;

public class EmptyListException extends AbstractServerValidationException {


  public EmptyListException() {
    super("");
  }
  @Override
  public String getMessageKey()  {
    return "validation.EmptyListException";
  }

  @Override
  public void accept(ServiceConsumer consumer) {
    ((IListParamServiceConsumer)consumer).handle(this);
  }
}
