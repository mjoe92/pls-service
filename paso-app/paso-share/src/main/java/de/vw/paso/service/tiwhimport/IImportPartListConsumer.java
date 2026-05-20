package de.vw.paso.service.tiwhimport;

import de.vw.paso.exception.ServiceConsumer;

public interface IImportPartListConsumer extends ServiceConsumer {
  void importPartList(String productKey);
}
