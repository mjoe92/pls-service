package de.vw.paso.delegate.buildinfo;

import java.util.Objects;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.buildinfo.BuildInfoRestService;

public class BuildInfoRestClientHolder {

    private static BuildInfoRestService INSTANCE;

    private BuildInfoRestClientHolder() {
    }

    public static void setInstance(BuildInfoRestService instance) {
        BuildInfoRestClientHolder.INSTANCE = instance;
    }

    public static BuildInfoRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (BuildInfoRestClientHolder.class) {
                INSTANCE = new BuildInfoRestClient(PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
