package de.vw.paso.delegate.stueckliste.efselementhistory;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.partlist.efselementhistory.EfsElementAndMaraAndHistoryListDTO;
import de.vw.paso.service.partlist.efselementhistory.EfsElementCollection;
import de.vw.paso.service.partlist.efselementhistory.EfsElementHistoryRestService;
import de.vw.paso.service.partlist.efselementhistory.RevertToRevisionDTO;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class EfsElementHistoryRestClient implements EfsElementHistoryRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public EfsElementHistoryRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public EfsElementAndMaraAndHistoryListDTO loadHistoryList(Long efsElementId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + "/" + efsElementId);

        return httpClient.execute(httpGetRequest, EfsElementAndMaraAndHistoryListDTO.class);
    }

    @Override
    public EfsElementAndMaraAndHistoryListDTO loadRevisions(Long vehiclePartListId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + LOAD_REVISIONS + vehiclePartListId);

        return httpClient.execute(httpGetRequest, EfsElementAndMaraAndHistoryListDTO.class);
    }

    @Override
    public EfsElementCollection revertToRevision(RevertToRevisionDTO revertToRevisionDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(revertToRevisionDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPut = new HttpPut(pasoServerHost + URL + REVERT_TO_REVISION);
            httpPut.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPut, EfsElementCollection.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
