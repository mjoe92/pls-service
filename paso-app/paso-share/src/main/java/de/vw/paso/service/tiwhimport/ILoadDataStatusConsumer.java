package de.vw.paso.service.tiwhimport;

import de.vw.paso.exception.ServiceConsumer;

public interface ILoadDataStatusConsumer extends ServiceConsumer {
  void loadDataStatus(String productKey);
}
