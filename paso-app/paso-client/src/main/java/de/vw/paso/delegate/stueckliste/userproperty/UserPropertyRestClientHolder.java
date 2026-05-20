package de.vw.paso.delegate.stueckliste.userproperty;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.userproperty.UserPropertyRestService;

public class UserPropertyRestClientHolder {

    private static UserPropertyRestService INSTANCE;

    private UserPropertyRestClientHolder() {
    }

    public static void setInstance(UserPropertyRestService instance) {
        UserPropertyRestClientHolder.INSTANCE = instance;
    }

    public static UserPropertyRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (UserPropertyRestClientHolder.class) {
                INSTANCE = new UserPropertyRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
