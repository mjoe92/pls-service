package de.vw.paso.delegate.stueckliste.efsedit;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.partlist.efsedit.EfsElementRestService;

public class EfsElementRestClientHolder {

    private static EfsElementRestService INSTANCE;

    private EfsElementRestClientHolder() {
    }

    public static void setInstance(EfsElementRestService instance) {
        EfsElementRestClientHolder.INSTANCE = instance;
    }

    public static EfsElementRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (EfsElementRestClientHolder.class) {
                INSTANCE = new EfsElementRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
