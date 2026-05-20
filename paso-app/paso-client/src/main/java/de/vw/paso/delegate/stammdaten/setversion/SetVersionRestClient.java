package de.vw.paso.delegate.stammdaten.setversion;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.masterdata.setversion.AddSetVersionRequestDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionListDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionRestService;
import de.vw.paso.service.masterdata.setversion.UpdateSetVersionRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

@Slf4j
public class SetVersionRestClient implements SetVersionRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public SetVersionRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public SetVersionListDTO loadSetVersions() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getSetVersionsRequest = new HttpGet(pasoServerHost + SetVersionRestService.URL);

        return httpClient.execute(getSetVersionsRequest, SetVersionListDTO.class);
    }

    @Override
    public SetVersionDTO addSetVersion(AddSetVersionRequestDTO requestDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + SetVersionRestService.URL);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, SetVersionDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SetVersionDTO updateSetVersion(Long setVersionId, UpdateSetVersionRequestDTO requestDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPut = new HttpPut(pasoServerHost + SetVersionRestService.URL + "/" + setVersionId);
            httpPut.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPut, SetVersionDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSetVersion(Long id) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(id);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpDelete httpDelete = new HttpDelete(pasoServerHost + SetVersionRestService.URL);
            httpDelete.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            httpClient.execute(httpDelete);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
