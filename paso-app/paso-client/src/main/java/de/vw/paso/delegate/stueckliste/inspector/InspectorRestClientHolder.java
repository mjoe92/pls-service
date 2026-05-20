package de.vw.paso.delegate.stueckliste.inspector;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.partlist.inspector.InspectorRestService;

public class InspectorRestClientHolder {

    private static InspectorRestService INSTANCE;

    private InspectorRestClientHolder() {
    }

    public static void setInstance(InspectorRestService instance) {
        InspectorRestClientHolder.INSTANCE = instance;
    }

    public static InspectorRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (InspectorRestClientHolder.class) {
                INSTANCE = new InspectorRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
