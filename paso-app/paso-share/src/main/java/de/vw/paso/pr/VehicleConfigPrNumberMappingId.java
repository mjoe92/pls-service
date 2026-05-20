package de.vw.paso.pr;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class VehicleConfigPrNumberMappingId {

    @Column(name = "VEHICLE_CONFIG_ID", nullable = false, updatable = false)
    private Long vehicleConfigId;

    @Column(name = "PR_ASSIGNMENT_ID", nullable = false, updatable = false)
    private Long prAssignmentId;

    Long getVehicleConfigId() {
        return vehicleConfigId;
    }

    Long getPrAssignmentId() {
        return prAssignmentId;
    }

    void setVehicleConfigId(Long vehicleConfigId) {
        this.vehicleConfigId = vehicleConfigId;
    }

    void setPrAssignmentId(Long prAssignmentId) {
        this.prAssignmentId = prAssignmentId;
    }
}
