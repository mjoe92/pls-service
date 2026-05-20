package de.vw.paso.exception;

public class NullElementException extends AbstractServerValidationException {


  public NullElementException() {
    super("");
  }
  @Override
  public String getMessageKey()  {
    return "validation.NullElementException";
  }

  @Override
  public void accept(ServiceConsumer consumer) {
    ((IParamServiceConsumer)consumer).handle(this);
  }
}
