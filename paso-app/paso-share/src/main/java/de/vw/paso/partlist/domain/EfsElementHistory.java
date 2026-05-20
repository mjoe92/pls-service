package de.vw.paso.partlist.domain;

import java.util.List;

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
import jakarta.persistence.Transient;

@Entity
@Table(name = "EFS_ELEMENT_HISTORY")
public final class EfsElementHistory extends AbstractEfsElement {

    public static final String SEQ_EFS_ELEMENT_HISTORY = "SEQ_EFS_ELEMENT_HISTORY";

    @Id
    @Column(name = "EFS_ELEMENT_HISTORY_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_EFS_ELEMENT_HISTORY)
    @SequenceGenerator(name = SEQ_EFS_ELEMENT_HISTORY, sequenceName = SEQ_EFS_ELEMENT_HISTORY, allocationSize = 500)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private EfsElement parent;

    @Column(name = "VEHICLE_PART_LIST_ID")
    private Long vehiclePartListId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EFS_ELEMENT_ID", nullable = false, updatable = false)
    private EfsElement efsElement;

    @Transient
    private Double weight = 0.0;

    @Override
    public IPartListElement.Type getType() {
        return IPartListElement.Type.EFS_ELEMENT_HISTORY;
    }

    @Override
    public List<EfsElement> getChildren() {
        return getEfsElement().getChildren();
    }

    @Override
    public Double getNodeWeight() {
        return getEfsElement().getNodeWeight();
    }

    @Override
    public void setNodeWeight(Double nodeWeight) {
        getEfsElement().setNodeWeight(nodeWeight);
    }

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

    public EfsElement getParent() {
        return parent;
    }

    public EfsElement getEfsElement() {
        return efsElement;
    }

    public Double getWeight() {
        return weight;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setParent(EfsElement parent) {
        this.parent = parent;
    }

    public void setEfsElement(EfsElement efsElement) {
        this.efsElement = efsElement;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
