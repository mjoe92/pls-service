package de.vw.paso.service.tableconfig;

public interface TableConfigRestService {

  String URL = "/api/table-config";

  TableConfigListDTO getConfigurationsForUser();

  TableConfigDTO saveConfiguration(TableConfigDTO config);

  void deleteConfiguration(Long id);
}
