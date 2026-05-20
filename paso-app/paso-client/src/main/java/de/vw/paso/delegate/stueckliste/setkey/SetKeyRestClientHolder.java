package de.vw.paso.delegate.stueckliste.setkey;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.partlist.setkey.SetKeyRestService;

public class SetKeyRestClientHolder {

    private static SetKeyRestService INSTANCE;

    private SetKeyRestClientHolder() {
    }

    public static void setInstance(SetKeyRestService instance) {
        SetKeyRestClientHolder.INSTANCE = instance;
    }

    public static SetKeyRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (SetKeyRestClientHolder.class) {
                INSTANCE = new SetKeyRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }

}
