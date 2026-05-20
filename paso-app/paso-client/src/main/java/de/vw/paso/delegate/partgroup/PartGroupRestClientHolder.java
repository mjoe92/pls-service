package de.vw.paso.delegate.partgroup;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.masterdata.partgroup.PartGroupRestService;

public class PartGroupRestClientHolder {

    private static PartGroupRestService INSTANCE;

    private PartGroupRestClientHolder() {
    }

    public static void setInstance(PartGroupRestService instance) {
        PartGroupRestClientHolder.INSTANCE = instance;
    }

    public static PartGroupRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (PartGroupRestClientHolder.class) {
                INSTANCE = new PartGroupRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }

}
