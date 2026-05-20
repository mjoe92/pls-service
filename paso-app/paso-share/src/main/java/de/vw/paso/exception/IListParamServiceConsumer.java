package de.vw.paso.exception;

public interface IListParamServiceConsumer extends IParamServiceConsumer{
  void handle(EmptyListException exception);
}
