package de.vw.paso.delegate.stueckliste.partlistviewgroup;

import java.util.Objects;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.partlist.partlistviewgroup.PartListViewGroupRestService;

public class PartListViewGroupRestClientHolder {

    private static PartListViewGroupRestService INSTANCE;

    private PartListViewGroupRestClientHolder() {
    }

    public static void setInstance(PartListViewGroupRestService instance) {
        PartListViewGroupRestClientHolder.INSTANCE = instance;
    }

    public static PartListViewGroupRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (PartListViewGroupRestClientHolder.class) {
                INSTANCE = new PartListViewGroupRestClient(PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
