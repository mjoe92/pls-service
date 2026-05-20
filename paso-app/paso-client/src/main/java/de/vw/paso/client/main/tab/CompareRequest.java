package de.vw.paso.client.main.tab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.vw.paso.client.stueckliste.compare.FGSetCompareTabController;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

class CompareRequest {

    private List<VehicleConfigDTO> vehicleConfigs;
    private VehicleConfigDTO referencingVehicleConfig;

    private Set<Long> configIdsToLoad = new HashSet<>();

    CompareRequest(List<VehicleConfigDTO> configs, VehicleConfigDTO reference) {
        this.vehicleConfigs = configs;
        this.referencingVehicleConfig = reference;
        vehicleConfigs.forEach(e -> configIdsToLoad.add(e.getId()));
        FGSetCompareTabController.vehicleConfigs = new ArrayList<>();
        FGSetCompareTabController.vehicleConfigs.addAll(configs);
    }

    boolean startCompare() {
        return configIdsToLoad.isEmpty();
    }

    VehicleConfigDTO getReferencingVehicleConfig() {
        return referencingVehicleConfig;
    }

    List<VehicleConfigDTO> getVehicleConfigs() {
        return vehicleConfigs;
    }

    public void setLoaded(VehicleConfigDTO vehicleConfig) {
        configIdsToLoad.remove(vehicleConfig.getId());
    }
}
