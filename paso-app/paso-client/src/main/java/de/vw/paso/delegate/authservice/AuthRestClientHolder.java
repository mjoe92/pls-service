package de.vw.paso.delegate.authservice;

import java.util.Objects;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.auth.AuthRestService;

public class AuthRestClientHolder {

    private static AuthRestService INSTANCE;

    private AuthRestClientHolder() {
    }

    public static void setInstance(AuthRestService instance) {
        AuthRestClientHolder.INSTANCE = instance;
    }

    public static AuthRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (AuthRestClientHolder.class) {
                INSTANCE = new AuthRestClient(PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
