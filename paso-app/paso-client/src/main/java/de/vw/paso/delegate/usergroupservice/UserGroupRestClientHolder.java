package de.vw.paso.delegate.usergroupservice;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.usergroup.UserGroupRestService;

public class UserGroupRestClientHolder {

    private static UserGroupRestService INSTANCE;

    private UserGroupRestClientHolder() {
    }

    public static void setInstance(UserGroupRestService instance) {
        UserGroupRestClientHolder.INSTANCE = instance;
    }

    public static UserGroupRestService getInstance() {
        if (INSTANCE == null) {
            synchronized (UserGroupRestClientHolder.class) {
                INSTANCE = new UserGroupRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }

        return INSTANCE;
    }
}