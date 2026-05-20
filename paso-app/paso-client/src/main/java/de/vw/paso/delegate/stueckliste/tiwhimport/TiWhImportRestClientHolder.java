package de.vw.paso.delegate.stueckliste.tiwhimport;

import java.util.Objects;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.tiwhimport.TiWhImportRestService;

public class TiWhImportRestClientHolder {

    private static TiWhImportRestService INSTANCE;

    private TiWhImportRestClientHolder() {
    }

    public static void setInstance(TiWhImportRestService instance) {
        TiWhImportRestClientHolder.INSTANCE = instance;
    }

    public static TiWhImportRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (TiWhImportRestClientHolder.class) {
                INSTANCE = new TiWhImportRestClient(PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }

}
