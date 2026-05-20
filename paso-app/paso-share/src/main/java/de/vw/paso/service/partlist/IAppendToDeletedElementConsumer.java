package de.vw.paso.service.partlist;

import de.vw.paso.exception.ServiceConsumer;

public interface IAppendToDeletedElementConsumer extends ServiceConsumer {

  void handle(AppendToDeletedElementException exception);
}
