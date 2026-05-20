package de.vw.paso.service.modelimport;

import de.vw.paso.service.masterdata.salesregion.SalesRegionNotExistingException;
import de.vw.paso.service.masterdata.salesregion.SalesRegionNotRelevantException;

public interface ModelRestService {

  String URL = "/api/model";

  ModelImportListDTO loadModelImports(String salesKey, String modelYear, String salesRegionId);

  ModelImportDTO updateModel(ModelUpdateDTO modelUpdateDTO)
    throws SalesRegionNotExistingException, SalesRegionNotRelevantException;

  ModelSetDTO loadModels(Long modelImportId);

}
