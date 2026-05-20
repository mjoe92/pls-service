package de.vw.paso.delegate.stueckliste.efsriss;

import java.util.Objects;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.partlist.efsriss.EfsRissRestService;

public class EfsRissRestClientHolder {

    private static EfsRissRestService INSTANCE;

    private EfsRissRestClientHolder() {
    }

    public static void setInstance(EfsRissRestService instance) {
        EfsRissRestClientHolder.INSTANCE = instance;
    }

    public static EfsRissRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (EfsRissRestClientHolder.class) {
                INSTANCE = new EfsRissRestClient(PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
