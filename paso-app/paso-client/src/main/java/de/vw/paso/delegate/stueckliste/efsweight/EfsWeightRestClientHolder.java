package de.vw.paso.delegate.stueckliste.efsweight;

import java.util.Objects;

import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.partlist.efsweight.EfsWeightRestService;

public class EfsWeightRestClientHolder {

    private static EfsWeightRestService INSTANCE;

    private EfsWeightRestClientHolder() {
    }

    public static void setInstance(EfsWeightRestService instance) {
        EfsWeightRestClientHolder.INSTANCE = instance;
    }

    public static EfsWeightRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (EfsWeightRestClientHolder.class) {
                INSTANCE = new EfsWeightRestClient(PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
