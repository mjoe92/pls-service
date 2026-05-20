package de.vw.paso.delegate.model;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.modelimport.ModelImportDTO;
import de.vw.paso.service.modelimport.ModelImportListDTO;
import de.vw.paso.service.modelimport.ModelRestService;
import de.vw.paso.service.modelimport.ModelSetDTO;
import de.vw.paso.service.modelimport.ModelUpdateDTO;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class ModelRestClient implements ModelRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public ModelRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public ModelImportListDTO loadModelImports(String salesKey, String modelYear, String salesRegionId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(pasoServerHost).append(ModelRestService.URL)
                .append("?salesKey=" + salesKey.replace(" ", "") + "&modelYear=");
        if (Objects.nonNull(modelYear)) {
            urlBuilder.append(modelYear);
        }
        urlBuilder.append("&salesRegionId=");
        if (Objects.nonNull(salesRegionId)) {
            urlBuilder.append(salesRegionId);
        }
        HttpGet getModelImportsRequest = new HttpGet(urlBuilder.toString());

        return httpClient.execute(getModelImportsRequest, ModelImportListDTO.class);
    }

    @Override
    public ModelImportDTO updateModel(ModelUpdateDTO modelUpdateDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(modelUpdateDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPutRequest = new HttpPut(pasoServerHost + ModelRestService.URL);
            httpPutRequest.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPutRequest, ModelImportDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ModelSetDTO loadModels(Long modelImportId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getModelRequest = new HttpGet(pasoServerHost + ModelRestService.URL + "/" + modelImportId);

        return httpClient.execute(getModelRequest, ModelSetDTO.class);
    }
}
