package de.vw.paso.delegate.fzgkonfig;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.vehicle.ConfigCountForVehicleProjectDTO;
import de.vw.paso.service.vehicle.VehicleConfigCategoryStatusDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.service.vehicle.VehicleConfigListDTO;
import de.vw.paso.service.vehicle.VehicleConfigRestService;
import de.vw.paso.vehicle.VehicleConfigCategory;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class VehicleConfigRestClient implements VehicleConfigRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public VehicleConfigRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public VehicleConfigListDTO loadNonDeletedVehicleConfigs() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + GET_NON_DELETED_CONFIGS);

        return httpClient.execute(httpGetRequest, VehicleConfigListDTO.class);
    }

    @Override
    public VehicleConfigListDTO loadDeletedVehicleConfigs() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + GET_DELETED_CONFIGS);

        return httpClient.execute(httpGetRequest, VehicleConfigListDTO.class);
    }

    @Override
    public VehicleConfigDTO loadFzgKonfig(Long vehicleConfigId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + GET_BY_CONFIG_ID + vehicleConfigId);

        return httpClient.execute(httpGetRequest, VehicleConfigDTO.class);
    }

    @Override
    public VehicleConfigDTO resetDeletion(Long id) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPost httpPost = new HttpPost(pasoServerHost + URL + RESET_DELETION);
        httpPost.setEntity(new StringEntity(id.toString(), StandardCharsets.UTF_8));

        return httpClient.execute(httpPost, VehicleConfigDTO.class);
    }

    @Override
    public VehicleConfigListDTO loadVehicleConfigs(List<String> vehicleConfigIds) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(
                pasoServerHost + URL + GET_BY_CONFIG_IDS + "?vehicleConfigIds=" + String.join(",", vehicleConfigIds));

        return httpClient.execute(httpGetRequest, VehicleConfigListDTO.class);
    }

    @Override
    public VehicleConfigListDTO loadVehicleConfigByProjectId(long vehicleProjectId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + GET_BY_PROJECT_ID + vehicleProjectId);

        return httpClient.execute(httpGetRequest, VehicleConfigListDTO.class);
    }

    @Override
    public VehicleConfigListDTO loadVehicleConfigByProjectIds(List<String> vehicleProjectIds) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(
                pasoServerHost + URL + GET_BY_PROJECT_IDS + "?vehicleProjectIds=" + String.join(",",
                        vehicleProjectIds));

        return httpClient.execute(httpGetRequest, VehicleConfigListDTO.class);
    }

    @Override
    public VehicleConfigListDTO loadVehicleConfigByProductKey(String productKey) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + GET_BY_PRODUCT_KEY + productKey);

        return httpClient.execute(httpGetRequest, VehicleConfigListDTO.class);
    }

    @Override
    public VehicleConfigListDTO loadVehicleConfigByBrand(String brand) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + GET_BY_BRAND + brand);

        return httpClient.execute(httpGetRequest, VehicleConfigListDTO.class);
    }

    @Override
    public VehicleConfigListDTO loadVehicleConfigByRecentlyUsed() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + GET_RECENTLY_USED);

        return httpClient.execute(httpGetRequest, VehicleConfigListDTO.class);
    }

    @Override
    public VehicleConfigDTO saveFzgKonfig(VehicleConfigDTO vehicleConfig) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(vehicleConfig);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + URL);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, VehicleConfigDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteVehicleConfig(Long vehicleConfigId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpDelete httpDelete = new HttpDelete(pasoServerHost + URL + DELETE_VEHICLE_CONFIG + vehicleConfigId);

        httpClient.execute(httpDelete);
    }

    @Override
    public VehicleConfigCategoryStatusDTO loadVehicleConfigCategoryStatus(Long vehicleConfigId,
            VehicleConfigCategory vehicleConfigCategory) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(
                pasoServerHost + URL + GET_VEHICLE_CONFIG_STATUS + "?vehicleConfigId=" + vehicleConfigId
                        + "&vehicleConfigCategory=" + vehicleConfigCategory);

        return httpClient.execute(httpGetRequest, VehicleConfigCategoryStatusDTO.class);
    }

    @Override
    public VehicleConfigDTO createVehiclePartList(long vehicleConfigId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPost httpPost = new HttpPost(pasoServerHost + URL + CREATE_PART_LIST + vehicleConfigId);

        return httpClient.execute(httpPost, VehicleConfigDTO.class);
    }

    @Override
    public ConfigCountForVehicleProjectDTO loadConfigurationCountForVehicleProject() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + GET_CONFIG_COUNT_FOR_VEHICLE_PROJECT);

        return httpClient.execute(httpGetRequest, ConfigCountForVehicleProjectDTO.class);
    }
}
