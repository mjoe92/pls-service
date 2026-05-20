package de.vw.paso.delegate.usergroupservice;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.user.UserListDTO;
import de.vw.paso.service.usergroup.UserGroupDTO;
import de.vw.paso.service.usergroup.UserGroupListDTO;
import de.vw.paso.service.usergroup.UserGroupRestService;
import de.vw.paso.service.vehicle.VehicleConfigListDTO;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class UserGroupRestClient implements UserGroupRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public UserGroupRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public UserGroupListDTO getAllUserGroups() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGet = new HttpGet(pasoServerHost + UserGroupRestService.URL);

        return httpClient.execute(httpGet, UserGroupListDTO.class);
    }

    @Override
    public void saveUserGroup(UserGroupDTO userGroup) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(userGroup);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + UserGroupRestService.URL);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            httpClient.execute(httpPost);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addVehicleConfigToUserGroup(Long userGroupId, Long vehicleConfigId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPut httpPut = new HttpPut(
                pasoServerHost + UserGroupRestService.URL + ADD_NEW_CONFIG + "/user-group/" + userGroupId
                        + "/vehicle-config/" + vehicleConfigId);

        httpClient.execute(httpPut);
    }

    @Override
    public VehicleConfigListDTO getVehicleConfigsFromUserGroup(Long userGroupId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGet = new HttpGet(pasoServerHost + UserGroupRestService.URL + CONFIGS + "/" + userGroupId);

        return httpClient.execute(httpGet, VehicleConfigListDTO.class);
    }

    @Override
    public UserListDTO getGroupUsers(Long userGroupId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGet = new HttpGet(pasoServerHost + UserGroupRestService.URL + USERS + "/" + userGroupId);

        return httpClient.execute(httpGet, UserListDTO.class);
    }
}