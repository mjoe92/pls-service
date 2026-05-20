package de.vw.paso.service.mbt;

import java.util.Date;

public interface MbtRestService {

  String URL = "/api/mbt-import";
  String DATE = "/get-import-date";

  void importData();

  Date getImportDateForFile();
}
