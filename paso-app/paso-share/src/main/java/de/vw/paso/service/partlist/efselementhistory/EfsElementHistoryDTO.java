package de.vw.paso.service.partlist.efselementhistory;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.vw.paso.partlist.domain.IPartListElement;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class EfsElementHistoryDTO extends AbstractEfsElementDTO {

    private Long id;
    private EfsElementDTO parent;
    private Long vehiclePartListId;
    private EfsElementDTO efsElement;
    @JsonIgnore
    private Double weight = 0.0;

    @JsonIgnore
    public IPartListElement.Type getType() {
        return IPartListElement.Type.EFS_ELEMENT_HISTORY;
    }

    @JsonIgnore
    public List<EfsElementDTO> getChildren() {
        return efsElement.getChildren();
    }

    @JsonIgnore
    public Double getNodeWeight() {
        return efsElement.getNodeWeight();
    }

    @JsonIgnore
    public void setNodeWeight(Double nodeWeight) {
        efsElement.setNodeWeight(nodeWeight);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public EfsElementDTO getParent() {
        return parent;
    }

    @Override
    public Long getVehiclePartListId() {
        return vehiclePartListId;
    }

    public EfsElementDTO getEfsElement() {
        return efsElement;
    }

    @Override
    public Double getWeight() {
        return weight;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setParent(EfsElementDTO parent) {
        this.parent = parent;
    }

    @Override
    public void setVehiclePartListId(Long vehiclePartListId) {
        this.vehiclePartListId = vehiclePartListId;
    }

    public void setEfsElement(EfsElementDTO efsElement) {
        this.efsElement = efsElement;
    }

    @Override
    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
