package de.vw.paso.delegate.stammdaten.vehicleprojct;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectRestService;

public class VehicleProjectRestClientHolder {

    private static VehicleProjectRestService INSTANCE;

    private VehicleProjectRestClientHolder() {
    }

    public static void setInstance(VehicleProjectRestService instance) {
        VehicleProjectRestClientHolder.INSTANCE = instance;
    }

    public static VehicleProjectRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (VehicleProjectRestClientHolder.class) {
                INSTANCE = new VehicleProjectRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
