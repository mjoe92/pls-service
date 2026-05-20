package de.vw.paso.delegate.right;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.right.AddRolesToUserDTO;
import de.vw.paso.service.right.AddUsersToRoleDTO;
import de.vw.paso.service.right.RightManagementRestService;
import de.vw.paso.service.right.RoleListDTO;
import de.vw.paso.service.user.UserListDTO;
import de.vw.paso.utility.StringConstant;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class RightManagementRestClient implements RightManagementRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public RightManagementRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public RoleListDTO getAllRoles() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGet = new HttpGet(pasoServerHost + URL);

        return httpClient.execute(httpGet, RoleListDTO.class);
    }

    @Override
    public RoleListDTO getRolesForUser(String userId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGet = new HttpGet(pasoServerHost + URL + ROLES_FOR_USER + StringConstant.SLASH + userId);

        return httpClient.execute(httpGet, RoleListDTO.class);
    }

    @Override
    public UserListDTO getUsersForRole(Long roleId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGet = new HttpGet(pasoServerHost + URL + USERS_FOR_ROLE + StringConstant.SLASH + roleId);

        return httpClient.execute(httpGet, UserListDTO.class);
    }

    @Override
    public void addUsersToRole(AddUsersToRoleDTO addUsersToRoleDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(addUsersToRoleDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPut = new HttpPut(pasoServerHost + URL + ADD_USERS_TO_ROLE);
            httpPut.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            httpClient.execute(httpPut);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addRolesToUser(AddRolesToUserDTO addRolesToUserDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(addRolesToUserDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPut = new HttpPut(pasoServerHost + URL + ADD_ROLES_TO_USER);
            httpPut.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            httpClient.execute(httpPut);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeRoleFromUser(String userId, Long roleId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPut httpPut = new HttpPut(
                pasoServerHost + URL + REMOVE_ROLE_FROM_USER + "?userId=" + userId + "&roleId=" + roleId);

        httpClient.execute(httpPut);
    }

    @Override
    public void removeAllRolesFromUser(String userId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPut httpPut = new HttpPut(
                pasoServerHost + URL + REMOVE_ALL_ROLES_FROM_USER + StringConstant.SLASH + userId);

        httpClient.execute(httpPut);
    }

    @Override
    public void removeAllUsersFromRole(Long roleId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPut httpPut = new HttpPut(
                pasoServerHost + URL + REMOVE_ALL_USERS_FROM_ROLE + StringConstant.SLASH + roleId);

        httpClient.execute(httpPut);
    }
}
