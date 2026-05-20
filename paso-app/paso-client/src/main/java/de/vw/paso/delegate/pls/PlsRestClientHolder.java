package de.vw.paso.delegate.pls;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.pls.PlsRestService;

public class PlsRestClientHolder {

    private static PlsRestService INSTANCE;

    private PlsRestClientHolder() {
    }

    public static void setInstance(PlsRestService instance) {
        PlsRestClientHolder.INSTANCE = instance;
    }

    public static PlsRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (PlsRestClientHolder.class) {
                INSTANCE = new PlsRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }

}
