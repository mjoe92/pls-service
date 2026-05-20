package de.vw.paso.service.modelimport;

import de.vw.paso.exception.ServiceConsumer;
import de.vw.paso.service.masterdata.salesregion.SalesRegionNotExistingException;
import de.vw.paso.service.masterdata.salesregion.SalesRegionNotRelevantException;

public interface IImportModelConsumer extends ServiceConsumer {

  void importModels(String salesKey, Integer modelYear, String salesTag);

  void handle(SalesRegionNotExistingException salesRegionNotExisting);

  void handle(SalesRegionNotRelevantException salesRegionNotRelevant);
}
