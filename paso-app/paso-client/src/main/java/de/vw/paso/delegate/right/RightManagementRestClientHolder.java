package de.vw.paso.delegate.right;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.right.RightManagementRestService;

public class RightManagementRestClientHolder {

    private static RightManagementRestService INSTANCE;

    private RightManagementRestClientHolder() {
    }

    public static void setInstance(RightManagementRestService instance) {
        RightManagementRestClientHolder.INSTANCE = instance;
    }

    public static RightManagementRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (RightManagementRestClientHolder.class) {
                INSTANCE = new RightManagementRestClient(ObjectMapperHolder.getInstance(),
                        PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
