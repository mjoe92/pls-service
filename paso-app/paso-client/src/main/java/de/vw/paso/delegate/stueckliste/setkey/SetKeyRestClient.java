package de.vw.paso.delegate.stueckliste.setkey;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.partlist.setkey.SetKeyListDTO;
import de.vw.paso.service.partlist.setkey.SetKeyRestService;
import de.vw.paso.service.partlist.setkey.SetKeysDTO;
import de.vw.paso.service.partlist.setkey.UpdateSetKeyDTO;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class SetKeyRestClient implements SetKeyRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public SetKeyRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public SetKeyListDTO loadSetKeys() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getSetKeyRequest = new HttpGet(pasoServerHost + SetKeyRestService.URL);

        return httpClient.execute(getSetKeyRequest, SetKeyListDTO.class);
    }

    @Override
    public SetKeyListDTO loadSetKeys(Long setVersionId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getSetKeyRequest = new HttpGet(pasoServerHost + SetKeyRestService.URL + "/" + setVersionId);

        return httpClient.execute(getSetKeyRequest, SetKeyListDTO.class);
    }

    @Override
    public SetKeysDTO saveSetKeys(SetKeysDTO requestDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPostRequest = new HttpPost(pasoServerHost + SetKeyRestService.URL);
            httpPostRequest.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPostRequest, SetKeysDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SetKeyDTO updateSetKey(UpdateSetKeyDTO updateSetKeyDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(updateSetKeyDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPutRequest = new HttpPut(pasoServerHost + SetKeyRestService.URL);
            httpPutRequest.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPutRequest, SetKeyDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeSetKey(Long setVersionId, String setKeyName) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpDelete httpDelete = new HttpDelete(
                pasoServerHost + SetKeyRestService.URL + "?setVersionId=" + setVersionId + "&setKeyName=" + setKeyName);

        httpClient.execute(httpDelete);
    }
}
