package de.vw.paso.consumer.partlistwizard;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.service.masterdata.prnumber.ILoadPRNumberForVehicleConsumer;
import de.vw.paso.service.masterdata.prnumber.PrNumberListDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadPRNumberForVehicleConsumer extends AbstractTestConsumer<PrNumberListDTO>
        implements ILoadPRNumberForVehicleConsumer {

    @Autowired
    private PrNumberRestService prNumberRestService;

    @Override
    public void loadPrNumbersForVehicle(Long vehicleProjectId) {
        run(() -> prNumberRestService.loadPrNumbersForConfig(vehicleProjectId));
    }
}
