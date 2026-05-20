package de.vw.paso.delegate.fzgkonfig;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.vehicle.VehicleConfigRestService;

public class VehicleConfigRestClientHolder {

    private static VehicleConfigRestService INSTANCE;

    private VehicleConfigRestClientHolder() {
    }

    public static void setInstance(VehicleConfigRestService instance) {
        VehicleConfigRestClientHolder.INSTANCE = instance;
    }

    public static VehicleConfigRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (VehicleConfigRestClientHolder.class) {
                INSTANCE = new VehicleConfigRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
