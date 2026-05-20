package de.vw.paso.service.partlist.efselementhistory;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;

//todo: into record
public final class EfsElementDTOWrapper implements Serializable, IEfsElementForDTO {

    private final AbstractEfsElementDTO efsElement;
    private final AbstractEfsElementMaraDTO efsElementMara;

    public EfsElementDTOWrapper(AbstractEfsElementDTO efsElement, AbstractEfsElementMaraDTO efsElementMara) {
        this.efsElement = efsElement;
        this.efsElementMara = efsElementMara;
    }

    @Override
    @JsonIgnore
    public Type getType() {
        return Type.EFS_ELEMENT_HISTORY;
    }

    @Override
    @JsonIgnore
    public Long getRevision() {
        return Math.max(efsElement.getRevision(), efsElementMara.getRevision());
    }

    @Override
    @JsonIgnore
    public EfsElementDTO getParent() {
        return efsElement.getParent();
    }

    @Override
    @JsonIgnore
    public Long getId() {
        return efsElement.getId();
    }

    @Override
    @JsonIgnore
    public void setId(final Long id) {
        efsElement.setId(id);
    }

    @Override
    @JsonIgnore
    public Integer getDeleted() {
        return efsElement.getDeleted();
    }

    @Override
    @JsonIgnore
    public List<EfsElementDTO> getChildren() {
        return efsElement.getChildren();
    }

    @Override
    @JsonIgnore
    public void setDeleted(final Integer deleted) {
        efsElement.setDeleted(deleted);
    }

    @Override
    @JsonIgnore
    public Boolean isDeleted() {
        return efsElement.isDeleted();
    }

    @Override
    @JsonIgnore
    public String getNodeId() {
        return efsElement.getNodeId();
    }

    @Override
    @JsonIgnore
    public void setNodeId(String nodeId) {
        efsElement.setNodeId(nodeId);
    }

    @Override
    @JsonIgnore
    public String getNodeLabel() {
        return efsElement.getNodeLabel();
    }

    @Override
    @JsonIgnore
    public Integer getNodeLevel() {
        return efsElement.getNodeLevel();
    }

    @Override
    @JsonIgnore
    public Long getTisSort() {
        return efsElement.getTisSort();
    }

    @Override
    @JsonIgnore
    public void setTisSort(final Long tisSort) {
        efsElement.setTisSort(tisSort);
    }

    @JsonIgnore
    public String getNodeType() {
        return efsElement.getNodeType();
    }

    @JsonIgnore
    public String getNodeValueParent() {
        return efsElement.getNodeValueParent();
    }

    @JsonIgnore
    public String getNodeValue() {
        return efsElement.getNodeValue();
    }

    @Override
    @JsonIgnore
    public String getAp() {
        return efsElement.getAp();
    }

    @Override
    @JsonIgnore
    public void setAp(final String ap) {
        efsElement.setAp(ap);
    }

    @Override
    @JsonIgnore
    public Integer getQuantity() {
        return efsElement.getQuantity();
    }

    @Override
    @JsonIgnore
    public void setQuantity(final Integer quantity) {
        efsElement.setQuantity(quantity);
    }

    @Override
    @JsonIgnore
    public String getQuantityUnit() {
        return efsElement.getQuantityUnit();
    }

    @Override
    @JsonIgnore
    public void setQuantityUnit(final String quantityUnit) {
        efsElement.setQuantityUnit(quantityUnit);
    }

    @Override
    @JsonIgnore
    public String getQuantityUnitExtended() {
        return null;
    }

    @Override
    @JsonIgnore
    public WeightControlFlag getWeightControlFlag() {
        return efsElement.getWeightControlFlag();
    }

    @Override
    @JsonIgnore
    public void setWeightControlFlag(final WeightControlFlag weightControlFlag) {
        efsElement.setWeightControlFlag(weightControlFlag);
    }

    @Override
    @JsonIgnore
    public String getPrNumberRule() {
        return efsElement.getPrNumberRule();
    }

    @Override
    @JsonIgnore
    public void setPrNumberRule(final String prNumberRule) {
        efsElement.setPrNumberRule(prNumberRule);
    }

    @Override
    @JsonIgnore
    public String getBeginDateKey() {
        return efsElement.getBeginDateKey();
    }

    @Override
    @JsonIgnore
    public void setBeginDateKey(final String beginDateKey) {
        efsElement.setBeginDateKey(beginDateKey);
    }

    @Override
    @JsonIgnore
    public String getEndDateKey() {
        return efsElement.getEndDateKey();
    }

    @Override
    @JsonIgnore
    public void setEndDateKey(final String endDateKey) {
        efsElement.setEndDateKey(endDateKey);
    }

    @Override
    @JsonIgnore
    public Date getBeginDate() {
        return efsElement.getBeginDate();
    }

    @Override
    @JsonIgnore
    public void setBeginDate(final Date beginDate) {
        efsElement.setBeginDate(beginDate);
    }

    @Override
    @JsonIgnore
    public Date getEndDate() {
        return efsElement.getEndDate();
    }

    @Override
    @JsonIgnore
    public void setEndDate(final Date endDate) {
        efsElement.setEndDate(endDate);
    }

    @Override
    @JsonIgnore
    public String getAggregate() {
        return efsElement.getAggregate();
    }

    @Override
    @JsonIgnore
    public void setAggregate(final String aggregate) {
        efsElement.setAggregate(aggregate);
    }

    @Override
    @JsonIgnore
    public Integer getBomNumber() {
        return efsElement.getBomNumber();
    }

    @Override
    @JsonIgnore
    public void setBomNumber(Integer bomNumber) {
        efsElement.setBomNumber(bomNumber);
    }

    @Override
    @JsonIgnore
    public String getProduct() {
        return efsElement.getProduct();
    }

    @Override
    @JsonIgnore
    public String getPartType() {
        return efsElement.getPartType();
    }

    @Override
    @JsonIgnore
    public Integer getBaukasten() {
        return efsElement.getBaukasten();
    }

    @Override
    @JsonIgnore
    public String getBaukastenStatus() {
        return efsElement.getBaukastenStatus();
    }

    @Override
    @JsonIgnore
    public String getBaukastenNodeId() {
        return efsElement.getBaukastenNodeId();
    }

    @Override
    @JsonIgnore
    public String getWorkPackageNumber() {
        return efsElement.getWorkPackageNumber();
    }

    @Override
    @JsonIgnore
    public String getProcessStatus() {
        return efsElement.getProcessStatus();
    }

    @Override
    @JsonIgnore
    public String getDmuRelevant() {
        return efsElement.getDmuRelevant();
    }

    @Override
    @JsonIgnore
    public String getMaterialType() {
        return efsElement.getMaterialType();
    }

    @Override
    @JsonIgnore
    public Date getEarliestPvs() {
        return efsElement.getEarliestPvs();
    }

    @Override
    @JsonIgnore
    public Date getEarliestNs() {
        return efsElement.getEarliestNs();
    }

    @Override
    @JsonIgnore
    public Date getEarliestSop() {
        return efsElement.getEarliestSop();
    }

    @Override
    @JsonIgnore
    public Date getPActivationDate() {
        return efsElement.getPActivationDate();
    }

    @Override
    @JsonIgnore
    public Date getKonstructureDate() {
        return efsElement.getKonstructureDate();
    }

    @Override
    @JsonIgnore
    public String getAvonStatus() {
        return efsElement.getAvonStatus();
    }

    @Override
    @JsonIgnore
    public Double getWeight() {
        return efsElement.getWeight();
    }

    @Override
    @JsonIgnore
    public void setWeight(Double weight) {
        efsElement.setWeight(weight);
    }

    @Override
    @JsonIgnore
    public Double getNodeWeight() {
        return efsElement.getNodeWeight();
    }

    @Override
    @JsonIgnore
    public void setNodeWeight(Double nodeWeight) {
        efsElement.setNodeWeight(nodeWeight);
    }

    @Override
    @JsonIgnore
    public String getPartNumber() {
        return efsElementMara.getPartNumber();
    }

    @Override
    @JsonIgnore
    public String getDescription1() {
        return efsElementMara.getDescription1De();
    }

    @Override
    @JsonIgnore
    public String getDescription2() {
        return efsElementMara.getDescription2De();
    }

    @JsonIgnore
    public Double getGewichtGewogenFE() {
        return efsElementMara.getWeightWeightedTe();
    }

    @JsonIgnore
    public Double getGewichtGewogenProd() {
        return efsElementMara.getWeightWeightedProd();
    }

    @JsonIgnore
    public Double getGewichtBerechnetFE() {
        return efsElementMara.getWeightCalculatedTe();
    }

    @JsonIgnore
    public Double getGewichtGeschaetztFE() {
        return efsElementMara.getWeightEstimatedTe();
    }

    @JsonIgnore
    public String getZeichnungSt() {
        return efsElementMara.getDrawingStatus();
    }

    @JsonIgnore
    public Date getZeichnungDt() {
        return efsElementMara.getDrawingDate();
    }

    @Override
    @JsonIgnore
    public Integer getGap() {
        return efsElement.getGap();
    }

    @Override
    @JsonIgnore
    public String getSetKey() {
        return efsElement.getSetKey();
    }

    @Override
    @JsonIgnore
    public void setSetKey(final String setKey) {
        efsElement.setSetKey(setKey);
    }

    @Override
    @JsonIgnore
    public String getCostGroup() {
        return efsElement.getCostGroup();
    }

    @Override
    @JsonIgnore
    public void setCostGroup(final String costGroup) {
        efsElement.setCostGroup(costGroup);
    }

    @Override
    @JsonIgnore
    public String getConstructionsGroup() {
        return efsElement.getConstructionsGroup();
    }

    @Override
    @JsonIgnore
    public String getProductStructure() {
        return efsElement.getProductStructure();
    }

    @Override
    @JsonIgnore
    public String getPositionVariant() {
        return efsElement.getPositionVariant();
    }

    @Override
    @JsonIgnore
    public String getDeletionFlag() {
        return efsElement.getDeletionFlag();
    }

    @Override
    @JsonIgnore
    public Double getCogX() {
        return efsElement.getCogX();
    }

    @Override
    @JsonIgnore
    public void setCogX(Double x) {
        efsElement.setCogX(x);
    }

    @Override
    @JsonIgnore
    public Double getCogY() {
        return efsElement.getCogY();
    }

    @Override
    @JsonIgnore
    public void setCogY(Double y) {
        efsElement.setCogY(y);
    }

    @Override
    @JsonIgnore
    public Double getCogZ() {
        return efsElement.getCogZ();
    }

    @Override
    @JsonIgnore
    public void setCogZ(Double z) {
        efsElement.setCogZ(z);
    }

    @Override
    @JsonIgnore
    public String getWahlweiseFall() {
        return efsElement.getWahlweiseFall();
    }

    @Override
    @JsonIgnore
    public void setWahlweiseFall(String fall) {
        efsElement.setWahlweiseFall(fall);
    }

    @Override
    @JsonIgnore
    public Integer getWahlweiseNr() {
        return efsElement.getWahlweiseNr();
    }

    @Override
    @JsonIgnore
    public void setWahlweiseNr(Integer nr) {
        efsElement.setWahlweiseNr(nr);
    }

    @Override
    @JsonIgnore
    public Long getVehiclePartListId() {
        return efsElement.getVehiclePartListId();
    }

    public AbstractEfsElementDTO getEfsElement() {
        return efsElement;
    }

    @Override
    public AbstractEfsElementMaraDTO getEfsElementMara() {
        return efsElementMara;
    }
}
