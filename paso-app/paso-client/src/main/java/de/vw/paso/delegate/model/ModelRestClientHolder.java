package de.vw.paso.delegate.model;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.modelimport.ModelRestService;

public class ModelRestClientHolder {

    private static ModelRestService INSTANCE;

    private ModelRestClientHolder() {
    }

    public static void setInstance(ModelRestService instance) {
        ModelRestClientHolder.INSTANCE = instance;
    }

    public static ModelRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (ModelRestClientHolder.class) {
                INSTANCE = new ModelRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
