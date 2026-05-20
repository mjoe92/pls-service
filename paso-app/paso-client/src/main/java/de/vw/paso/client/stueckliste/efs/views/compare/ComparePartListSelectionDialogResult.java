package de.vw.paso.client.stueckliste.efs.views.compare;

import java.util.List;

import de.vw.paso.service.vehicle.VehicleConfigDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ComparePartListSelectionDialogResult {

    private List<VehicleConfigDTO> selectedVehicleConfigs;
    private VehicleConfigDTO referenceVehicleConfig;

}
