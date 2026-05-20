package de.vw.paso.delegate.stammdaten.vehicleprojct;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.masterdata.vehicleproject.UpdateVehicleProjectArchiveStateDTO;
import de.vw.paso.service.masterdata.vehicleproject.UpdatedVehicleProjectSetVersionDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectListDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectRestService;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class VehicleProjectRestClient implements VehicleProjectRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public VehicleProjectRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public VehicleProjectListDTO loadVehicleProjects() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getVehicleProjectsRequest = new HttpGet(pasoServerHost + VehicleProjectRestService.URL);

        return httpClient.execute(getVehicleProjectsRequest, VehicleProjectListDTO.class);
    }

    @Override
    public void updateVehicleProjectArchiveState(UpdateVehicleProjectArchiveStateDTO updateDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(updateDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPut = new HttpPut(pasoServerHost + VehicleProjectRestService.URL);
            httpPut.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            httpClient.execute(httpPut);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VehicleProjectDTO updateVehicleProjectSetVersion(
            UpdatedVehicleProjectSetVersionDTO vehicleProjectSetVersionDTO) {
        try {
            String responseBodyJson = objectMapper.writeValueAsString(vehicleProjectSetVersionDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(
                    pasoServerHost + VehicleProjectRestService.URL + VehicleProjectRestService.UPDATE_SET_VERSION);
            httpPost.setEntity(new StringEntity(responseBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, VehicleProjectDTO.class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
