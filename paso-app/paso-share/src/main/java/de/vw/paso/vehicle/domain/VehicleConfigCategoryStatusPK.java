package de.vw.paso.vehicle.domain;

import java.io.Serializable;

import de.vw.paso.vehicle.VehicleConfigCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

//todo: instancing should be possible only in VehicleConfigCategoryStatus -> setId()?
@Embeddable
public final class VehicleConfigCategoryStatusPK implements Serializable {

    private static final String PK_VEHICLE_CONFIG_CATEGORY = "VEHICLE_CONFIG_CATEGORY";

    @SuppressWarnings("unused")
    private Long vehicleConfigId;

    @Enumerated(EnumType.STRING)
    @Column(name = PK_VEHICLE_CONFIG_CATEGORY, length = 20, nullable = false, updatable = false,
            columnDefinition = "varchar(20)")
    private VehicleConfigCategory vehicleConfigCategory;

    public Long getVehicleConfigId() {
        return vehicleConfigId;
    }

    public VehicleConfigCategory getVehicleConfigCategory() {
        return vehicleConfigCategory;
    }

    public void setVehicleConfigId(Long vehicleConfigId) {
        this.vehicleConfigId = vehicleConfigId;
    }

    public void setVehicleConfigCategory(VehicleConfigCategory vehicleConfigCategory) {
        this.vehicleConfigCategory = vehicleConfigCategory;
    }
}
