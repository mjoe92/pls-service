package de.vw.paso.partlist.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = FilteredOutEfsElement.TABLE_FILTERED_OUT_EFS_ELEMENT)
public class FilteredOutEfsElement extends AbstractEfsElement {

    public static final String TABLE_FILTERED_OUT_EFS_ELEMENT = "FILTERED_OUT_EFS_ELEMENT";
    public static final String PK_FILTERED_OUT_EFS_ELEMENT_ID = "FILTERED_OUT_EFS_ELEMENT_ID";
    public static final String COLUMN_REASON = "REASON";
    public static final String COLUMN_VEHICLE_CONFIG_ID = "VEHICLE_CONFIG_ID";

    @Id
    @Column(name = PK_FILTERED_OUT_EFS_ELEMENT_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COLUMN_REASON)
    private String reason;

    @Column(name = COLUMN_VEHICLE_CONFIG_ID)
    private Long vehicleConfigId;

    @Transient
    private Double weight = 0.0;

    @Override
    public EfsElement getParent() {
        return null;
    }

    @Override
    public List<EfsElement> getChildren() {
        return null;
    }

    @Override
    public Double getNodeWeight() {
        return null;
    }

    @Override
    public void setNodeWeight(Double nodeWeight) {

    }

    @Override
    public Long getVehiclePartListId() {
        return null;
    }

    @Override
    public void setVehiclePartListId(Long vehiclePartList) {

    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long aLong) {
        id = aLong;
    }

    public String getReason() {
        return reason;
    }

    public Long getVehicleConfigId() {
        return vehicleConfigId;
    }

    @Override
    public Double getWeight() {
        return weight;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setVehicleConfigId(Long vehicleConfigId) {
        this.vehicleConfigId = vehicleConfigId;
    }

    @Override
    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
