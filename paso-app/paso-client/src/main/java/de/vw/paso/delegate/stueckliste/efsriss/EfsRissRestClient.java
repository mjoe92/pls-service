package de.vw.paso.delegate.stueckliste.efsriss;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.partlist.efsriss.AlternativePartsForGapListDTO;
import de.vw.paso.service.partlist.efsriss.EfsRissRestService;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class EfsRissRestClient implements EfsRissRestService {

    private final PasoRestClient httpClient;

    public EfsRissRestClient(PasoRestClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public AlternativePartsForGapListDTO getAlternativePartsForGap(String nodeId, long vehicleConfigId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getSetKeyRequest = new HttpGet(
                pasoServerHost + URL + "?nodeId=" + nodeId + "&vehicleConfigId=" + vehicleConfigId);

        return httpClient.execute(getSetKeyRequest, AlternativePartsForGapListDTO.class);
    }
}

