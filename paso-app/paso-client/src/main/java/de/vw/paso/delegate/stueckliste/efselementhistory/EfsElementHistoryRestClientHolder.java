package de.vw.paso.delegate.stueckliste.efselementhistory;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.partlist.efselementhistory.EfsElementHistoryRestService;

public class EfsElementHistoryRestClientHolder {

    private static EfsElementHistoryRestService INSTANCE;

    private EfsElementHistoryRestClientHolder() {
    }

    public static void setInstance(EfsElementHistoryRestService instance) {
        EfsElementHistoryRestClientHolder.INSTANCE = instance;
    }

    public static EfsElementHistoryRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (EfsElementHistoryRestClientHolder.class) {
                INSTANCE = new EfsElementHistoryRestClient(ObjectMapperHolder.getInstance(),
                        PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }

}
