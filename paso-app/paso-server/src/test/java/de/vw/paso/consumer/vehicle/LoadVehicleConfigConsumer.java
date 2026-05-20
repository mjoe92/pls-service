package de.vw.paso.consumer.vehicle;

import java.util.List;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.vehicle.ILoadVehicleConfigConsumer;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.service.vehicle.VehicleConfigRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadVehicleConfigConsumer extends AbstractTestConsumer<List<VehicleConfigDTO>>
        implements ILoadVehicleConfigConsumer {

    @Autowired
    private VehicleConfigRestService service;

    public void loadVehicleConfigByBrand(Brand brand) {
        run(() -> (service.loadVehicleConfigByBrand(brand.name())).vehicleConfigDTOList());
    }

    public void loadVehicleConfigByProjectId(Long vehicleProjectId) {
        run(() -> (service.loadVehicleConfigByProjectId(vehicleProjectId)).vehicleConfigDTOList());
    }
}
