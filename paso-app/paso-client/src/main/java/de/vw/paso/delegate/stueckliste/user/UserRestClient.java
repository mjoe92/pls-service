package de.vw.paso.delegate.stueckliste.user;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.service.user.UserListDTO;
import de.vw.paso.service.user.UserRestService;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPut;

public class UserRestClient implements UserRestService {

    private final PasoRestClient httpClient;

    public UserRestClient(PasoRestClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public UserListDTO getAllUser() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getUsersRequest = new HttpGet(pasoServerHost + UserRestService.URL);

        return httpClient.execute(getUsersRequest, UserListDTO.class);
    }

    @Override
    public UserListDTO getAllActiveUsers() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getUsersRequest = new HttpGet(pasoServerHost + UserRestService.URL + ACTIVE_USERS);

        return httpClient.execute(getUsersRequest, UserListDTO.class);
    }

    @Override
    public void enableUser(String id) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPut httpPut = new HttpPut(pasoServerHost + UserRestService.URL + ENABLE_USER + id);

        httpClient.execute(httpPut);
    }

    @Override
    public void disableUser(String id) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPut httpPut = new HttpPut(pasoServerHost + UserRestService.URL + DISABLE_USER + id);

        httpClient.execute(httpPut);
    }

    @Override
    public UserDTO getUserById(String userId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getUsersRequest = new HttpGet(pasoServerHost + UserRestService.URL + USER_BY_ID + userId);

        return httpClient.execute(getUsersRequest, UserDTO.class);
    }

    @Override
    public void resetCostCenterChangedAt(String userId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPut httpPut = new HttpPut(pasoServerHost + UserRestService.URL + RESET_COST_CENTER_CHANGED_AT + userId);

        httpClient.execute(httpPut);
    }
}