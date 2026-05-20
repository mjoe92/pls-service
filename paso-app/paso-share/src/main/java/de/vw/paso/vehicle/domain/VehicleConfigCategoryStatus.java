package de.vw.paso.vehicle.domain;

import de.vw.paso.core.domain.AbstractEntity;
import de.vw.paso.vehicle.VehicleConfigCategory;
import de.vw.paso.vehicle.VehicleConfigStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "VEHICLE_CONFIG_CATEGORY_STATUS")
public final class VehicleConfigCategoryStatus extends AbstractEntity<VehicleConfigCategoryStatusPK> {

    @EmbeddedId
    private VehicleConfigCategoryStatusPK id;

    @MapsId("vehicleConfigId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VEHICLE_CONFIG_ID", nullable = false)
    private VehicleConfig vehicleConfig;

    @Enumerated(EnumType.STRING)
    @Column(name = "VEHICLE_CONFIG_STATUS", length = 12, nullable = false, columnDefinition = "varchar(12)")
    private VehicleConfigStatus vehicleConfigStatus;

    public void setVehicleConfigStatus(VehicleConfigStatus vehicleConfigStatus) {
        this.vehicleConfigStatus = vehicleConfigStatus;
    }

    public VehicleConfigCategory getVehicleConfigCategory() {
        return getId().getVehicleConfigCategory();
    }

    public void setVehicleConfig(VehicleConfig vehicleConfig) {
        this.vehicleConfig = vehicleConfig;
    }

    public VehicleConfigCategoryStatusPK getId() {
        return id;
    }

    public VehicleConfig getVehicleConfig() {
        return vehicleConfig;
    }

    public VehicleConfigStatus getVehicleConfigStatus() {
        return vehicleConfigStatus;
    }

    public void setId(VehicleConfigCategoryStatusPK id) {
        this.id = id;
    }
}
