package de.vw.paso.delegate.stueckliste.partlistviewgroup;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.partlist.domain.PartListViewMode;
import de.vw.paso.service.partlist.partlistviewgroup.PartListViewGroupListDTO;
import de.vw.paso.service.partlist.partlistviewgroup.PartListViewGroupRestService;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class PartListViewGroupRestClient implements PartListViewGroupRestService {

  private final PasoRestClient httpClient;

  public PartListViewGroupRestClient(PasoRestClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public PartListViewGroupListDTO loadPartListViewGroupsByPartListViewMode(PartListViewMode viewMode) {
    String pasoServerHost = PasoClientProperties.get().getServerUrl();
    HttpGet getPartListViewGroupRequest = new HttpGet(pasoServerHost + PartListViewGroupRestService.URL + "/" + viewMode);

    return httpClient.execute(getPartListViewGroupRequest, PartListViewGroupListDTO.class);
  }
}
