package de.vw.paso.partlist.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "EFS_ELEMENT_MARA_HISTORY")
public final class EfsElementMaraHistory extends AbstractEfsElementMara {

    public static final String SEQ_EFS_ELEMENT_HISTORY = "SEQ_EFS_ELEMENT_MARA_HISTORY";

    @Id
    @Column(name = "EFS_ELEMENT_MARA_HISTORY_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_EFS_ELEMENT_HISTORY)
    @SequenceGenerator(name = SEQ_EFS_ELEMENT_HISTORY, sequenceName = SEQ_EFS_ELEMENT_HISTORY, allocationSize = 500)
    private Long id;

    @Column(name = "VEHICLE_PART_LIST_ID")
    private Long vehiclePartListId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EFS_ELEMENT_MARA_ID", nullable = false, updatable = false)
    private EfsElementMara efsElementMara;

    @Override
    public void setVehiclePartListId(Long vehiclePartList) {
        this.vehiclePartListId = vehiclePartList;
    }

    @Override
    public Long getVehiclePartListId() {
        return vehiclePartListId;
    }

    public Long getId() {
        return id;
    }

    public EfsElementMara getEfsElementMara() {
        return efsElementMara;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEfsElementMara(EfsElementMara efsElementMara) {
        this.efsElementMara = efsElementMara;
    }
}
