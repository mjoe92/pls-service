package de.vw.paso.delegate.stammdaten.prnumber;

import java.util.Objects;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.masterdata.prnumber.PrNumberRestService;

public class PrNumberRestClientHolder {

    private static PrNumberRestService INSTANCE;

    private PrNumberRestClientHolder() {
    }

    public static void setInstance(PrNumberRestService instance) {
        PrNumberRestClientHolder.INSTANCE = instance;
    }

    public static PrNumberRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (PrNumberRestClientHolder.class) {
                INSTANCE = new PrNumberRestClient(PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
