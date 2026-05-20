package de.vw.paso.service.partlist.efselementhistory;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.vw.paso.core.domain.AbstractModifiableDTO;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import de.vw.paso.utility.EfsWeightUtil;

public abstract class AbstractEfsElementDTO extends AbstractModifiableDTO<Long> implements IEfsElementForDTO {

    private Long revision = 0L;
    private Integer deleted = 0;
    private String nodeId;
    private String nodeLabel;
    private Integer nodeLevel;
    private Integer bomNumber;
    private String product;
    private Integer gap = 0;
    private Long tisSort;
    private String nodeType;
    private String nodeValueParent;
    private String nodeValue;
    private Integer quantity;
    private String quantityUnit;
    private String quantityUnitExtended;
    private WeightControlFlag weightControlFlag;
    private String constructionsGroup;
    private String productStructure;
    private String positionVariant;
    private String deletionFlag;
    private String costGroup;
    private Long tiWhImportId;
    private String ap = "";
    private String prNumberRule;
    private String beginDateKey;
    private String endDateKey;
    private Date beginDate;
    private Date endDate;
    private String partType;
    private String aggregate;
    private String setKey;
    private EfsElementMaraDTO efsElementMara;
    private String wahlweiseFall;
    private Integer wahlweiseNr;
    private Integer baukasten = 0;
    private String baukastenStatus;
    private String baukastenNodeId;
    private String workPackageNumber;
    private String processStatus;
    private String dmuRelevant;
    private String materialType;
    private Date earliestPvs;
    private Date earliestNs;
    private Date earliestSop;
    private Date pActivationDate;
    private Date konstructureDate;
    private String avonStatus;
    private Double cogX;
    private Double cogY;
    private Double cogZ;

    public abstract EfsElementDTO getParent();

    public abstract Long getVehiclePartListId();

    public abstract void setVehiclePartListId(Long vehiclePartListId);

    @JsonIgnore
    public String getPartNumber() {
        return getEfsElementMara().getPartNumber();
    }

    @JsonIgnore
    @Override
    public String getDescription1() {
        return getEfsElementMara().getDescription1De();
    }

    @JsonIgnore
    @Override
    public String getDescription2() {
        return getEfsElementMara().getDescription2De();
    }

    @JsonIgnore
    @Override
    public Boolean isDeleted() {
        return DELETED_FLAG.equals(getDeleted());
    }

    @JsonIgnore
    public String getFormattedPartNumber() {
        return getEfsElementMara().getFormattedPartNumber();
    }

    @JsonIgnore
    public double getTotalWeight() {
        return EfsWeightUtil.getMostPriorizedWeight(efsElementMara).getWeight() * quantity;
    }

    public void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    @Override
    public Long getRevision() {
        return revision;
    }

    @Override
    public Integer getDeleted() {
        return deleted;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String getNodeLabel() {
        return nodeLabel;
    }

    @Override
    public Integer getNodeLevel() {
        return nodeLevel;
    }

    @Override
    public Integer getBomNumber() {
        return bomNumber;
    }

    @Override
    public String getProduct() {
        return product;
    }

    @Override
    public Integer getGap() {
        return gap;
    }

    @Override
    public Long getTisSort() {
        return tisSort;
    }

    @Override
    public String getNodeType() {
        return nodeType;
    }

    @Override
    public String getNodeValueParent() {
        return nodeValueParent;
    }

    @Override
    public String getNodeValue() {
        return nodeValue;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public String getQuantityUnit() {
        return quantityUnit;
    }

    @Override
    public String getQuantityUnitExtended() {
        return quantityUnitExtended;
    }

    @Override
    public WeightControlFlag getWeightControlFlag() {
        return weightControlFlag;
    }

    @Override
    public String getConstructionsGroup() {
        return constructionsGroup;
    }

    @Override
    public String getProductStructure() {
        return productStructure;
    }

    @Override
    public String getPositionVariant() {
        return positionVariant;
    }

    @Override
    public String getDeletionFlag() {
        return deletionFlag;
    }

    @Override
    public String getCostGroup() {
        return costGroup;
    }

    public Long getTiWhImportId() {
        return tiWhImportId;
    }

    @Override
    public String getAp() {
        return ap;
    }

    @Override
    public String getPrNumberRule() {
        return prNumberRule;
    }

    @Override
    public String getBeginDateKey() {
        return beginDateKey;
    }

    @Override
    public String getEndDateKey() {
        return endDateKey;
    }

    @Override
    public Date getBeginDate() {
        return beginDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public String getPartType() {
        return partType;
    }

    @Override
    public String getAggregate() {
        return aggregate;
    }

    @Override
    public String getSetKey() {
        return setKey;
    }

    @Override
    public EfsElementMaraDTO getEfsElementMara() {
        return efsElementMara;
    }

    @Override
    public String getWahlweiseFall() {
        return wahlweiseFall;
    }

    @Override
    public Integer getWahlweiseNr() {
        return wahlweiseNr;
    }

    @Override
    public Integer getBaukasten() {
        return baukasten;
    }

    @Override
    public String getBaukastenStatus() {
        return baukastenStatus;
    }

    @Override
    public String getBaukastenNodeId() {
        return baukastenNodeId;
    }

    @Override
    public String getWorkPackageNumber() {
        return workPackageNumber;
    }

    @Override
    public String getProcessStatus() {
        return processStatus;
    }

    @Override
    public String getDmuRelevant() {
        return dmuRelevant;
    }

    @Override
    public String getMaterialType() {
        return materialType;
    }

    @Override
    public Date getEarliestPvs() {
        return earliestPvs;
    }

    @Override
    public Date getEarliestNs() {
        return earliestNs;
    }

    @Override
    public Date getEarliestSop() {
        return earliestSop;
    }

    public Date getPActivationDate() {
        return pActivationDate;
    }

    @Override
    public Date getKonstructureDate() {
        return konstructureDate;
    }

    @Override
    public String getAvonStatus() {
        return avonStatus;
    }

    @Override
    public Double getCogX() {
        return cogX;
    }

    @Override
    public Double getCogY() {
        return cogY;
    }

    @Override
    public Double getCogZ() {
        return cogZ;
    }

    public void setRevision(Long revision) {
        this.revision = revision;
    }

    @Override
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setNodeLabel(String nodeLabel) {
        this.nodeLabel = nodeLabel;
    }

    public void setNodeLevel(Integer nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    @Override
    public void setBomNumber(Integer bomNumber) {
        this.bomNumber = bomNumber;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setGap(Integer gap) {
        this.gap = gap;
    }

    @Override
    public void setTisSort(Long tisSort) {
        this.tisSort = tisSort;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public void setNodeValueParent(String nodeValueParent) {
        this.nodeValueParent = nodeValueParent;
    }

    public void setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public void setQuantityUnitExtended(String quantityUnitExtended) {
        this.quantityUnitExtended = quantityUnitExtended;
    }

    @Override
    public void setWeightControlFlag(WeightControlFlag weightControlFlag) {
        this.weightControlFlag = weightControlFlag;
    }

    public void setConstructionsGroup(String constructionsGroup) {
        this.constructionsGroup = constructionsGroup;
    }

    public void setProductStructure(String productStructure) {
        this.productStructure = productStructure;
    }

    public void setPositionVariant(String positionVariant) {
        this.positionVariant = positionVariant;
    }

    public void setDeletionFlag(String deletionFlag) {
        this.deletionFlag = deletionFlag;
    }

    @Override
    public void setCostGroup(String costGroup) {
        this.costGroup = costGroup;
    }

    public void setTiWhImportId(Long tiWhImportId) {
        this.tiWhImportId = tiWhImportId;
    }

    @Override
    public void setAp(String ap) {
        this.ap = ap;
    }

    @Override
    public void setPrNumberRule(String prNumberRule) {
        this.prNumberRule = prNumberRule;
    }

    @Override
    public void setBeginDateKey(String beginDateKey) {
        this.beginDateKey = beginDateKey;
    }

    @Override
    public void setEndDateKey(String endDateKey) {
        this.endDateKey = endDateKey;
    }

    @Override
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }

    @Override
    public void setSetKey(String setKey) {
        this.setKey = setKey;
    }

    public void setEfsElementMara(EfsElementMaraDTO efsElementMara) {
        this.efsElementMara = efsElementMara;
    }

    @Override
    public void setWahlweiseFall(String wahlweiseFall) {
        this.wahlweiseFall = wahlweiseFall;
    }

    @Override
    public void setWahlweiseNr(Integer wahlweiseNr) {
        this.wahlweiseNr = wahlweiseNr;
    }

    public void setBaukasten(Integer baukasten) {
        this.baukasten = baukasten;
    }

    public void setBaukastenStatus(String baukastenStatus) {
        this.baukastenStatus = baukastenStatus;
    }

    public void setBaukastenNodeId(String baukastenNodeId) {
        this.baukastenNodeId = baukastenNodeId;
    }

    public void setWorkPackageNumber(String workPackageNumber) {
        this.workPackageNumber = workPackageNumber;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public void setDmuRelevant(String dmuRelevant) {
        this.dmuRelevant = dmuRelevant;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public void setEarliestPvs(Date earliestPvs) {
        this.earliestPvs = earliestPvs;
    }

    public void setEarliestNs(Date earliestNs) {
        this.earliestNs = earliestNs;
    }

    public void setEarliestSop(Date earliestSop) {
        this.earliestSop = earliestSop;
    }

    public void setPActivationDate(Date pActivationDate) {
        this.pActivationDate = pActivationDate;
    }

    public void setKonstructureDate(Date konstructureDate) {
        this.konstructureDate = konstructureDate;
    }

    public void setAvonStatus(String avonStatus) {
        this.avonStatus = avonStatus;
    }

    @Override
    public void setCogX(Double cogX) {
        this.cogX = cogX;
    }

    @Override
    public void setCogY(Double cogY) {
        this.cogY = cogY;
    }

    @Override
    public void setCogZ(Double cogZ) {
        this.cogZ = cogZ;
    }
}
