package de.vw.paso.partlist.domain;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.vehicle.domain.VehicleConfig;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "VEHICLE_PART_LIST")
public final class VehiclePartList extends AbstractModifiableEntity<Long> implements IPartListChild {

    @Id
    @Column(name = "VEHICLE_PART_LIST_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "vehiclePartList",
            cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.DETACH },
            orphanRemoval = true)
    private VehicleConfig vehicleConfig;

    @Column(name = "REVISION", nullable = false)
    private Long revision = 0L;

    @Column(name = "WEIGHT", columnDefinition = "decimal(15, 3)", nullable = false)
    private Double weight = 0.0D;

    @Column(name = "PRODUCT_KEY_VEHICLE", columnDefinition = "char(4)", nullable = false)
    private String productKeyVehicle;

    @Column(name = "PRODUCT_KEY_MOTOR", columnDefinition = "char(4)")
    private String productKeyMotor;

    @Column(name = "PRODUCT_KEY_GEARBOX", columnDefinition = "char(4)")
    private String productKeyGearbox;

    public void setVehicleConfig(VehicleConfig vehicleConfig) {
        this.vehicleConfig = vehicleConfig;
    }

    @Override
    public Long getVehiclePartListId() {
        return this.getId();
    }

    @Override
    public EfsElement asParent() {
        return null;
    }

    public String getProductKeyVehicle() {
        return productKeyVehicle;
    }

    public String getProductKeyMotor() {
        return productKeyMotor;
    }

    public String getProductKeyGearbox() {
        return productKeyGearbox;
    }

    public void setProductKeyVehicle(String productKeyVehicle) {
        this.productKeyVehicle = productKeyVehicle;
    }

    public void setProductKeyMotor(String productKeyMotor) {
        this.productKeyMotor = productKeyMotor;
    }

    public void setProductKeyGearbox(String productKeyGearbox) {
        this.productKeyGearbox = productKeyGearbox;
    }

    @Override
    public Long getId() {
        return id;
    }

    public VehicleConfig getVehicleConfig() {
        return vehicleConfig;
    }

    public Long getRevision() {
        return revision;
    }

    public Double getWeight() {
        return weight;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setRevision(Long revision) {
        this.revision = revision;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
