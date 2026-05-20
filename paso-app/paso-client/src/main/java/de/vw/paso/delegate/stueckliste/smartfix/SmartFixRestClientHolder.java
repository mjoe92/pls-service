package de.vw.paso.delegate.stueckliste.smartfix;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.partlist.smartfix.SmartFixRestService;

public class SmartFixRestClientHolder {

    private static SmartFixRestService INSTANCE;

    private SmartFixRestClientHolder() {
    }

    public static void setInstance(SmartFixRestService instance) {
        SmartFixRestClientHolder.INSTANCE = instance;
    }

    public static SmartFixRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (SmartFixRestClientHolder.class) {
                INSTANCE = new SmartFixRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }

}
