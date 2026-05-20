package de.vw.paso.delegate.authservice;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.auth.AuthRestService;
import de.vw.paso.service.auth.AuthenticatedUserDTO;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class AuthRestClient implements AuthRestService {

    private final PasoRestClient httpClient;

    public AuthRestClient(PasoRestClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public AuthenticatedUserDTO getPasoJwt(String code) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getAuthenticatedUserRequest = new HttpGet(pasoServerHost + AuthRestService.URL + "?code=" + code);

        return httpClient.execute(getAuthenticatedUserRequest, AuthenticatedUserDTO.class);
    }
}
