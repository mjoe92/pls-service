package de.vw.paso.service.partlist.efsweight;

import de.vw.paso.logic.partlist.EfsWeightManager;
import de.vw.paso.logic.partlist.VehiclePartListManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = EfsWeightRestService.URL)
public class EfsWeightRestController implements EfsWeightRestService {

    private final EfsWeightManager efsWeightManager;
    private final VehiclePartListManager vehiclePartListManager;

    public EfsWeightRestController(EfsWeightManager efsWeightManager, VehiclePartListManager vehiclePartListManager) {
        this.efsWeightManager = efsWeightManager;
        this.vehiclePartListManager = vehiclePartListManager;
    }

    @Override
    @PutMapping(path = "/{vehiclePartListId}")
    public Double updateVehiclePartListWeight(@PathVariable Long vehiclePartListId) {
        Double weight = efsWeightManager.calculateWeight(vehiclePartListId).get(Long.MIN_VALUE);

        vehiclePartListManager.updateWeight(vehiclePartListId, weight);

        return weight;
    }
}
