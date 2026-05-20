package de.vw.paso.delegate.stueckliste.userproperty;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.userproperty.FavoriteVehicleProjectIds;
import de.vw.paso.service.userproperty.SaveAllUserPropertiesDTO;
import de.vw.paso.service.userproperty.SaveUserPropertyDTO;
import de.vw.paso.service.userproperty.UserPropertyDTO;
import de.vw.paso.service.userproperty.UserPropertyListDTO;
import de.vw.paso.service.userproperty.UserPropertyRestService;
import de.vw.paso.user.PropertyType;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class UserPropertyRestClient implements UserPropertyRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public UserPropertyRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public UserPropertyDTO load(PropertyType type) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getUserPropertyRequest = new HttpGet(pasoServerHost + URL + "/" + type);

        return httpClient.execute(getUserPropertyRequest, UserPropertyDTO.class);
    }

    @Override
    public UserPropertyDTO save(SaveUserPropertyDTO saveUserPropertyDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(saveUserPropertyDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + URL);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, UserPropertyDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserPropertyDTO saveOrUpdate(SaveUserPropertyDTO saveUserPropertyDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(saveUserPropertyDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPut = new HttpPut(pasoServerHost + URL);
            httpPut.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPut, UserPropertyDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserPropertyListDTO saveAll(SaveAllUserPropertiesDTO saveAllUserPropertiesDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(saveAllUserPropertiesDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + URL + SAVE_ALL);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, UserPropertyListDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long delete(PropertyType type, String userData) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpDelete httpDelete = new HttpDelete(pasoServerHost + URL + "?type=" + type + "&userData=" + userData);

        return httpClient.execute(httpDelete, Long.class);
    }

    @Override
    public FavoriteVehicleProjectIds getFavoriteVehicleProjectIds() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getUserPropertyRequest = new HttpGet(pasoServerHost + URL + FAV_VEH_PROJ_IDS);

        return httpClient.execute(getUserPropertyRequest, FavoriteVehicleProjectIds.class);
    }

    @Override
    public int deleteExpiredRecentlyUsedVehicleConfigurations() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpDelete httpDelete = new HttpDelete(pasoServerHost + URL + DEL_EXP_CONFIGS);

        return httpClient.execute(httpDelete, int.class);
    }

    @Override
    public void deleteUserData(String userId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpDelete httpDelete = new HttpDelete(pasoServerHost + URL + DEL_USER_DATA + userId);

        httpClient.execute(httpDelete);
    }
}
