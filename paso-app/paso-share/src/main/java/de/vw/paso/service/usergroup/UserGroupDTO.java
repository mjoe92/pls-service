package de.vw.paso.service.usergroup;

import java.util.ArrayList;
import java.util.List;

import de.vw.paso.core.domain.AbstractModifiableDTO;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.service.vehicle.OwnedVehicleConfigDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserGroupDTO extends AbstractModifiableDTO<Long> {

    private Long id;
    private String brand;
    private String name;
    private boolean writeAccess;
    private List<UserDTO> users = new ArrayList<>();
    private List<VehicleConfigDTO> vehicleConfigs = new ArrayList<>();
    private List<OwnedVehicleConfigDTO> ownedVehicleConfigs = new ArrayList<>();

    @Override
    public String toString() {
        return this.getName();
    }
}
