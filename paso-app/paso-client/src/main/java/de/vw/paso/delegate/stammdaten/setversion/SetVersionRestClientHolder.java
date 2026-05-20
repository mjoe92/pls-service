package de.vw.paso.delegate.stammdaten.setversion;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.masterdata.setversion.SetVersionRestService;

public class SetVersionRestClientHolder {

    private static SetVersionRestService INSTANCE;

    private SetVersionRestClientHolder() {
    }

    public static void setInstance(SetVersionRestService instance) {
        SetVersionRestClientHolder.INSTANCE = instance;
    }

    public static SetVersionRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (SetVersionRestClientHolder.class) {
                INSTANCE = new SetVersionRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
