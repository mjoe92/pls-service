package de.vw.paso.partlist.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "EFS_ELEMENT_MARA")
public final class EfsElementMara extends AbstractEfsElementMara {

    public static final String SEQ_EFS_ELEMENT_MARA = "SEQ_EFS_ELEMENT_MARA";

    @Id
    @Column(name = "EFS_ELEMENT_MARA_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_EFS_ELEMENT_MARA)
    @SequenceGenerator(name = SEQ_EFS_ELEMENT_MARA, sequenceName = SEQ_EFS_ELEMENT_MARA)
    private Long id;

    @Column(name = "VEHICLE_PART_LIST_ID")
    private Long vehiclePartListId;

    @Override
    public Long getVehiclePartListId() {
        return vehiclePartListId;
    }

    @Override
    public void setVehiclePartListId(Long vehiclePartList) {
        this.vehiclePartListId = vehiclePartList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
