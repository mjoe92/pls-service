package de.vw.paso.delegate.buildinfo;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.buildinfo.BuildInfoRestService;
import de.vw.paso.service.buildinfo.ServerBuildInfoDTO;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class BuildInfoRestClient implements BuildInfoRestService {
  private final PasoRestClient httpClient;

  public BuildInfoRestClient(PasoRestClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public ServerBuildInfoDTO getBuildInfo() {
    String pasoServerHost = PasoClientProperties.get().getServerUrl();
    HttpGet getRequest = new HttpGet(pasoServerHost + URL);

    return httpClient.execute(getRequest, ServerBuildInfoDTO.class);
  }
}
