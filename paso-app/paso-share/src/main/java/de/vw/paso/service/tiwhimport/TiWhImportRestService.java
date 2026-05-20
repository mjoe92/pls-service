package de.vw.paso.service.tiwhimport;

import de.vw.paso.status.ImportStatus;

public interface TiWhImportRestService {


  String URL = "/api/ti-wh-import";
  String PART_LIST = "/part-list";
  String IMPORT_STATUS = "/import-status";

  TiWhImportListDTO loadDatenstande(String productKey);

  TiWhImportDTO importPartList(String productKey);

  ImportStatus loadImportStatus(Long tiWhImportId);
}
