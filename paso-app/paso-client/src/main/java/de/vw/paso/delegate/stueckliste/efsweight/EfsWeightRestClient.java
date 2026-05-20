package de.vw.paso.delegate.stueckliste.efsweight;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.partlist.efsweight.EfsWeightRestService;
import org.apache.hc.client5.http.classic.methods.HttpPut;

public class EfsWeightRestClient implements EfsWeightRestService {
  private final PasoRestClient httpClient;

  public EfsWeightRestClient(PasoRestClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public Double updateVehiclePartListWeight(Long vehiclePartListId) {
    String pasoServerHost = PasoClientProperties.get().getServerUrl();
    HttpPut httpPut = new HttpPut(pasoServerHost + URL + "/" + vehiclePartListId);

    return httpClient.execute(httpPut, Double.class);
  }
}
