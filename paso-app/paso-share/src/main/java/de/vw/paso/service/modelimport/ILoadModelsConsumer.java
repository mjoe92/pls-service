package de.vw.paso.service.modelimport;

import de.vw.paso.exception.ServiceConsumer;

public interface ILoadModelsConsumer extends ServiceConsumer {

  void loadModels(Long modelImportId);

}
