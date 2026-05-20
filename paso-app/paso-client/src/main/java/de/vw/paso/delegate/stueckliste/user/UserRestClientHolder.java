package de.vw.paso.delegate.stueckliste.user;

import java.util.Objects;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.user.UserRestService;

public class UserRestClientHolder {

    private static UserRestService INSTANCE;

    private UserRestClientHolder() {
    }

    public static void setInstance(UserRestService instance) {
        UserRestClientHolder.INSTANCE = instance;
    }

    public static UserRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (UserRestClientHolder.class) {
                INSTANCE = new UserRestClient(PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
