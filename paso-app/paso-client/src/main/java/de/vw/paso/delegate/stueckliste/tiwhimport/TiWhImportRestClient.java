package de.vw.paso.delegate.stueckliste.tiwhimport;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.tiwhimport.TiWhImportDTO;
import de.vw.paso.service.tiwhimport.TiWhImportListDTO;
import de.vw.paso.service.tiwhimport.TiWhImportRestService;
import de.vw.paso.status.ImportStatus;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class TiWhImportRestClient implements TiWhImportRestService {

  private final PasoRestClient httpClient;

  public TiWhImportRestClient(PasoRestClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public TiWhImportListDTO loadDatenstande(String productKey) {
    String pasoServerHost = PasoClientProperties.get().getServerUrl();
    HttpGet getTiWhImportRequest = new HttpGet(pasoServerHost + TiWhImportRestService.URL + "/" + productKey);

    return httpClient.execute(getTiWhImportRequest, TiWhImportListDTO.class);
  }

  @Override
  public TiWhImportDTO importPartList(String productKey) {
    String pasoServerHost = PasoClientProperties.get().getServerUrl();
    HttpGet getTiWhImportRequest = new HttpGet(
      pasoServerHost + TiWhImportRestService.URL + TiWhImportRestService.PART_LIST + "/" + productKey);

    return httpClient.execute(getTiWhImportRequest, TiWhImportDTO.class);
  }

  @Override
  public ImportStatus loadImportStatus(Long tiWhImportId) {
    String pasoServerHost = PasoClientProperties.get().getServerUrl();
    HttpGet getTiWhImportStatusRequest = new HttpGet(
      pasoServerHost + TiWhImportRestService.URL + TiWhImportRestService.IMPORT_STATUS + "/" + tiWhImportId);

    return httpClient.execute(getTiWhImportStatusRequest, ImportStatus.class);
  }
}
