package de.vw.paso.partlist.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.vw.paso.core.domain.AbstractModifiableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@MappedSuperclass
public abstract class AbstractEfsElement extends AbstractModifiableEntity<Long> implements IEfsElement {

    public static final String COLUMN_REVISION = "REVISION";
    public static final String COLUMN_DELETED = "DELETED";
    public static final String COLUMN_NODE_ID = "NODE_ID";
    public static final String COLUMN_NODE_LABEL = "NODE_LABEL";
    public static final String COLUMN_NODE_LEVEL = "NODE_LEVEL";
    public static final String COLUMN_BOM_NUMBER = "BOM_NUMBER";
    public static final String COLUMN_PRODUCT = "PRODUCT";
    public static final String COLUMN_GAP_FLAG = "GAP_FLAG";
    public static final String COLUMN_TIS_SORT = "TIS_SORT";
    public static final String COLUMN_NODE_TYPE = "NODE_TYPE";
    public static final String COLUMN_NODE_VALUE_PARENT = "NODE_VALUE_PARENT";
    public static final String COLUMN_NODE_VALUE = "NODE_VALUE";
    public static final String COLUMN_QUANTITY = "QUANTITY";
    public static final String COLUMN_QUANTITY_UNIT = "QUANTITY_UNIT";
    public static final String COLUMN_QUANTITY_UNIT_EXTENDED = "QUANTITY_UNIT_EXTENDED";
    public static final String COLUMN_WEIGHT_CONTROL_FLAG = "WEIGHT_CONTROL_FLAG";
    public static final String COLUMN_CONSTRUCTIONS_GROUP = "CONSTRUCTIONS_GROUP";
    public static final String COLUMN_PRODUCT_STRUCTURE = "PRODUCT_STRUCTURE";
    private static final String COLUMN_POSITION_VARIANT = "POSITION_VARIANT";
    private static final String COLUMN_DELETION_FLAG = "DELETION_FLAG";
    public static final String COLUMN_COST_GROUP = "COST_GROUP";
    public static final String COLUMN_TI_WH_IMPORT_ID = "TI_WH_IMPORT_ID";
    public static final String COLUMN_AP = "AP";
    public static final String COLUMN_PR_NUMBER_RULE = "PR_NUMBER_RULE";
    public static final String COLUMN_BEGIN_DATE_KEY = "BEGIN_DATE_KEY";
    public static final String COLUMN_END_DATE_KEY = "END_DATE_KEY";
    public static final String COLUMN_BEGIN_DATE = "BEGIN_DATE";
    public static final String COLUMN_END_DATE = "END_DATE";
    public static final String COLUMN_PART_TYPE = "PART_TYPE";
    public static final String COLUMN_AGGREGATE = "AGGREGATE";
    public static final String COLUMN_SET_KEY = "SET_KEY";
    public static final String FK_EFS_ELEMENT_MARA_ID = "EFS_ELEMENT_MARA_ID";
    public static final String COLUMN_WAHLWEISE_FALL = "WAHLWEISE_FALL";
    public static final String COLUMN_WAHLWEISE_NR = "WAHLWEISE_NR";
    public static final String COLUMN_BAUKASTEN_FLAG = "BAUKASTEN_FLAG";
    public static final String COLUMN_BAUKASTEN_STATUS = "BAUKASTEN_STATUS";
    public static final String COLUMN_BAUKASTEN_NODE_ID = "BAUKASTEN_NODE_ID";
    public static final String COLUMN_WORK_PACKAGE_NUMBER = "WORK_PACKAGE_NUMBER";
    public static final String COLUMN_PROCESS_STATUS = "PROCESS_STATUS";
    public static final String COLUMN_DMU_RELEVANT = "DMU_RELEVANT";
    public static final String COLUMN_MATERIAL_TYPE = "MATERIAL_TYPE";
    public static final String COLUMN_EARLIEST_PVS = "EARLIEST_PVS";
    public static final String COLUMN_EARLIEST_NS = "EARLIEST_NS";
    public static final String COLUMN_EARLIEST_SOP = "EARLIEST_SOP";
    public static final String COLUMN_P_ACTIVATION_DATE = "P_ACTIVATION_DATE";
    public static final String COLUMN_KONSTRUCTURE_DATE = "KONSTRUCTURE_DATE";
    public static final String COLUMN_AVON_STATUS = "AVON_STATUS";
    public static final String COLUMN_COG_X = "COG_X";
    public static final String COLUMN_COG_Y = "COG_Y";
    public static final String COLUMN_COG_Z = "COG_Z";

    public abstract EfsElement getParent();

    public abstract Long getVehiclePartListId();

    public abstract void setVehiclePartListId(Long vehiclePartListId);

    @Column(name = COLUMN_REVISION, nullable = false)
    private Long revision = 0L;

    @Column(name = COLUMN_DELETED, columnDefinition = "int(1)", nullable = false)
    private Integer deleted = 0;

    @Column(name = COLUMN_NODE_ID)
    private String nodeId;

    @Column(name = COLUMN_NODE_LABEL)
    private String nodeLabel;

    @Column(name = COLUMN_NODE_LEVEL, columnDefinition = "int(11)")
    private Integer nodeLevel;

    @Column(name = COLUMN_BOM_NUMBER, columnDefinition = "int(11)")
    private Integer bomNumber;

    @Column(name = COLUMN_PRODUCT)
    private String product;

    @Column(name = COLUMN_GAP_FLAG, nullable = false)
    private Integer gap = 0;

    @Column(name = COLUMN_TIS_SORT, columnDefinition = "int(6)")
    private Long tisSort;

    @Column(name = COLUMN_NODE_TYPE, length = 8)
    private String nodeType;

    @Column(name = COLUMN_NODE_VALUE_PARENT, length = 40)
    private String nodeValueParent;

    @Column(name = COLUMN_NODE_VALUE, length = 40)
    private String nodeValue;

    @Column(name = COLUMN_QUANTITY, nullable = false)
    private Integer quantity;

    @Column(name = COLUMN_QUANTITY_UNIT, columnDefinition = "char(3)", nullable = false)
    private String quantityUnit;

    @Column(name = COLUMN_QUANTITY_UNIT_EXTENDED, columnDefinition = "char(1)")
    private String quantityUnitExtended;

    @Column(name = COLUMN_WEIGHT_CONTROL_FLAG)
    @Enumerated(EnumType.STRING)
    private WeightControlFlag weightControlFlag;

    @Column(name = COLUMN_CONSTRUCTIONS_GROUP, columnDefinition = "char(1)")
    private String constructionsGroup;

    @Column(name = COLUMN_PRODUCT_STRUCTURE, length = 3)
    private String productStructure;

    @Column(name = COLUMN_POSITION_VARIANT)
    private String positionVariant;

    @Column(name = COLUMN_DELETION_FLAG)
    private String deletionFlag;

    @Column(name = COLUMN_COST_GROUP, length = 4)
    private String costGroup;

    @Column(name = COLUMN_TI_WH_IMPORT_ID)
    private Long tiWhImportId;

    @Column(name = COLUMN_AP, length = 10, nullable = false)
    private String ap = "";

    @Column(name = COLUMN_PR_NUMBER_RULE, length = 200)
    private String prNumberRule;

    @Column(name = COLUMN_BEGIN_DATE_KEY, length = 11)
    private String beginDateKey;

    @Column(name = COLUMN_END_DATE_KEY, length = 11)
    private String endDateKey;

    @Column(name = COLUMN_BEGIN_DATE)
    @Temporal(TemporalType.DATE)
    private Date beginDate;

    @Column(name = COLUMN_END_DATE)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = COLUMN_PART_TYPE)
    private String partType;

    @Column(name = COLUMN_AGGREGATE, length = 4)
    private String aggregate;

    @Column(name = COLUMN_SET_KEY, columnDefinition = "char(3)")
    private String setKey;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = FK_EFS_ELEMENT_MARA_ID, nullable = false)
    private EfsElementMara efsElementMara;

    @Column(name = COLUMN_WAHLWEISE_FALL)
    private String wahlweiseFall;

    @Column(name = COLUMN_WAHLWEISE_NR)
    private Integer wahlweiseNr;

    @Column(name = COLUMN_BAUKASTEN_FLAG, columnDefinition = "int(1)")
    private Integer baukasten = 0;

    @Column(name = COLUMN_BAUKASTEN_STATUS, length = 1)
    private String baukastenStatus;

    @Column(name = COLUMN_BAUKASTEN_NODE_ID, length = 32)
    private String baukastenNodeId;

    @Column(name = COLUMN_WORK_PACKAGE_NUMBER, length = 6)
    private String workPackageNumber;

    @Column(name = COLUMN_PROCESS_STATUS, length = 1)
    private String processStatus;

    @Column(name = COLUMN_DMU_RELEVANT, length = 2)
    private String dmuRelevant;

    @Column(name = COLUMN_MATERIAL_TYPE, length = 4)
    private String materialType;

    @Column(name = COLUMN_EARLIEST_PVS)
    @Temporal(TemporalType.DATE)
    private Date earliestPvs;

    @Column(name = COLUMN_EARLIEST_NS)
    @Temporal(TemporalType.DATE)
    private Date earliestNs;

    @Column(name = COLUMN_EARLIEST_SOP)
    @Temporal(TemporalType.DATE)
    private Date earliestSop;

    @Column(name = COLUMN_P_ACTIVATION_DATE)
    @Temporal(TemporalType.DATE)
    private Date pActivationDate;

    @Column(name = COLUMN_KONSTRUCTURE_DATE)
    @Temporal(TemporalType.DATE)
    private Date konstructureDate;

    @Column(name = COLUMN_AVON_STATUS, length = 4)
    private String avonStatus;

    @Column(name = COLUMN_COG_X, columnDefinition = "decimal(10, 3)")
    private Double cogX;

    @Column(name = COLUMN_COG_Y, columnDefinition = "decimal(10, 3)")
    private Double cogY;

    @Column(name = COLUMN_COG_Z, columnDefinition = "decimal(10, 3)")
    private Double cogZ;

    @JsonIgnore
    @Override
    public Boolean isDeleted() {
        return DELETED_FLAG.equals(getDeleted());
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
    public String getPartNumber() {
        return getEfsElementMara().getPartNumber();
    }

    @JsonIgnore
    public String getFormattedPartNumber() {
        return getEfsElementMara().getFormatedPartNumber();
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
    public EfsElementMara getEfsElementMara() {
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

    @Override
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
    public void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    @Override
    public void setSetKey(String setKey) {
        this.setKey = setKey;
    }

    public void setEfsElementMara(EfsElementMara efsElementMara) {
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
