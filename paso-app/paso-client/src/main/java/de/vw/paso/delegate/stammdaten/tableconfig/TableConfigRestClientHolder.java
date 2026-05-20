package de.vw.paso.delegate.stammdaten.tableconfig;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.tableconfig.TableConfigRestService;

public class TableConfigRestClientHolder {

    private static TableConfigRestService INSTANCE;

    private TableConfigRestClientHolder() {
    }

    public static void setInstance(TableConfigRestService instance) {
        TableConfigRestClientHolder.INSTANCE = instance;
    }

    public static TableConfigRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (TableConfigRestClientHolder.class) {
                INSTANCE = new TableConfigRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
