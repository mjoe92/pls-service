package de.vw.paso.delegate.stueckliste.costgroup;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.partlist.costgroup.CostGroupRestService;

public class CostGroupRestClientHolder {

    private static CostGroupRestService INSTANCE;

    private CostGroupRestClientHolder() {
    }

    public static void setInstance(CostGroupRestService instance) {
        CostGroupRestClientHolder.INSTANCE = instance;
    }

    public static CostGroupRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (CostGroupRestClientHolder.class) {
                INSTANCE = new CostGroupRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
