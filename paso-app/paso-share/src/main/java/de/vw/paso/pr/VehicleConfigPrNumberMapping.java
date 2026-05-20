package de.vw.paso.pr;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "VEHICLE_CONFIG_PR_NUMBER_MAPPING")
public class VehicleConfigPrNumberMapping {

    @EmbeddedId
    private VehicleConfigPrNumberMappingId id;

    public Long getVehicleConfigId() {
        return id.getVehicleConfigId();
    }

    public Long getPrAssignmentId() {
        return id.getPrAssignmentId();
    }

    public void setId(Long configId, Long prAssignmentId) {
        id = new VehicleConfigPrNumberMappingId();
        id.setVehicleConfigId(configId);
        id.setPrAssignmentId(prAssignmentId);
    }
}
