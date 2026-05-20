package de.vw.paso.delegate.mbtimport;

import java.util.Objects;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.mbt.MbtRestService;

public class MbtImportRestClientHolder {

    private static MbtRestService INSTANCE;

    private MbtImportRestClientHolder() {
    }

    public static void setInstance(MbtRestService instance) {
        MbtImportRestClientHolder.INSTANCE = instance;
    }

    public static MbtRestService getInstance() {
        synchronized (MbtImportRestClientHolder.class) {
            if (Objects.isNull(INSTANCE)) {
                INSTANCE = new MbtImportRestClient(PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
