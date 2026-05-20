package de.vw.paso.service.masterdata;

import de.vw.paso.logic.masterdata.PrNumberManager;
import de.vw.paso.service.masterdata.prnumber.PrNumberListDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberRestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = PrNumberRestService.URL)
public class PrNumberRestController implements PrNumberRestService {

    private static final String SUB_VEHICLE_PROJECT_PATH = "/{vehicleConfigId}";

    private final PrNumberManager prNumberManager;

    public PrNumberRestController(PrNumberManager prNumberManager) {
        this.prNumberManager = prNumberManager;
    }

    @Override
    @GetMapping
    public PrNumberListDTO loadAll() {
        return prNumberManager.loadAll();
    }

    @Override
    @GetMapping(path = SUB_VEHICLE_PROJECT_PATH)
    public PrNumberListDTO loadPrNumbersForConfig(@PathVariable Long vehicleConfigId) {
        return prNumberManager.loadPrNumbersForVehicle(vehicleConfigId);
    }
}
