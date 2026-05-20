package de.vw.paso.delegate.stammdaten.salesregion;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.masterdata.salesregion.SalesRegionRestService;

public class SalesRegionRestClientHolder {

    private static SalesRegionRestService INSTANCE;

    private SalesRegionRestClientHolder() {
    }

    public static void setInstance(SalesRegionRestService instance) {
        SalesRegionRestClientHolder.INSTANCE = instance;
    }

    public static SalesRegionRestService getInstance() {
        if (INSTANCE == null) {
            synchronized (SalesRegionRestClientHolder.class) {
                INSTANCE = new SalesRegionRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }

        return INSTANCE;
    }
}