package de.vw.paso.partlist.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.utility.StringConstant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.apache.commons.lang3.StringUtils;

/**
 * Entity that represents an EfsElement.
 * Always part of one {@link VehiclePartList}.
 * <p>
 * May be the same as {@link EfsElement}.
 * Both classes could be merged at some point.
 *
 * @see EfsElement
 */
//todo: remove and replace with existing EfsElement
@Entity
@Table(name = EfsElementImport.TABLE_EFS_ELEMENT)
public class EfsElementImport extends AbstractModifiableEntity<Long> implements Serializable {

    protected static final String TABLE_EFS_ELEMENT = "EFS_ELEMENT";

    private static final String AGGREGATE_GEARBOX = "GETRIEBE";
    private static final String AGGREGATE_MOTOR = "MOTOR";

    private static final String PK_EFS_ELEMENT_ID = "EFS_ELEMENT_ID";
    private static final String FK_PARENT_ID = "PARENT_ID";
    private static final String FK_VEHICLE_PART_LIST_ID = "VEHICLE_PART_LIST_ID";
    private static final String FK_EFS_ELEMENT_MARA_ID = "EFS_ELEMENT_MARA_ID";
    private static final String COLUMN_REVISION = "REVISION";
    private static final String COLUMN_DELETED = "DELETED";
    private static final String COLUMN_NODE_ID = "NODE_ID";
    private static final String COLUMN_NODE_LABEL = "NODE_LABEL";
    private static final String COLUMN_NODE_LEVEL = "NODE_LEVEL";
    private static final String COLUMN_BOM_NUMBER = "BOM_NUMBER";
    private static final String COLUMN_PRODUCT = "PRODUCT";
    private static final String COLUMN_GAP_FLAG = "GAP_FLAG";
    private static final String COLUMN_TIS_SORT = "TIS_SORT";
    private static final String COLUMN_NODE_TYPE = "NODE_TYPE";
    private static final String COLUMN_NODE_VALUE = "NODE_VALUE";
    private static final String COLUMN_NODE_VALUE_PARENT = "NODE_VALUE_PARENT";
    private static final String COLUMN_QUANTITY = "QUANTITY";
    private static final String COLUMN_POSITION_VARIANT = "POSITION_VARIANT";
    private static final String COLUMN_DELETION_FLAG = "DELETION_FLAG";
    private static final String COLUMN_QUANTITY_UNIT = "QUANTITY_UNIT";
    private static final String COLUMN_QUANTITY_UNIT_EXTENDED = "QUANTITY_UNIT_EXTENDED";
    private static final String COLUMN_WEIGHT_CONTROL_FLAG = "WEIGHT_CONTROL_FLAG";
    private static final String COLUMN_CONSTRUCTIONS_GROUP = "CONSTRUCTIONS_GROUP";
    private static final String COLUMN_PRODUCT_STRUCTURE = "PRODUCT_STRUCTURE";
    private static final String COLUMN_COST_GROUP = "COST_GROUP";
    private static final String COLUMN_TI_WH_IMPORT_ID = "TI_WH_IMPORT_ID";
    private static final String COLUMN_AP = "AP";
    private static final String COLUMN_PR_NUMBER_RULE = "PR_NUMBER_RULE";
    private static final String COLUMN_BEGIN_DATE_KEY = "BEGIN_DATE_KEY";
    private static final String COLUMN_END_DATE_KEY = "END_DATE_KEY";
    private static final String COLUMN_BEGIN_DATE = "BEGIN_DATE";
    private static final String COLUMN_END_DATE = "END_DATE";
    private static final String COLUMN_PART_TYPE = "PART_TYPE";
    private static final String COLUMN_AGGREGATE = "AGGREGATE";
    private static final String COLUMN_SET_KEY = "SET_KEY";
    private static final String COLUMN_DUPLICATE_ID = "DUPLICATE_ID";
    private static final String COLUMN_OPTIONAL_FALL = "WAHLWEISE_FALL";
    private static final String COLUMN_OPTIONAL_NR = "WAHLWEISE_NR";
    private static final String COLUMN_BAUKASTEN_FLAG = "BAUKASTEN_FLAG";
    private static final String COLUMN_BAUKASTEN_STATUS = "BAUKASTEN_STATUS";
    private static final String COLUMN_BAUKASTEN_NODE_ID = "BAUKASTEN_NODE_ID";
    private static final String COLUMN_WORK_PACKAGE_NUMBER = "WORK_PACKAGE_NUMBER";
    private static final String COLUMN_PROCESS_STATUS = "PROCESS_STATUS";
    private static final String COLUMN_DMU_RELEVANT = "DMU_RELEVANT";
    private static final String COLUMN_MATERIAL_TYPE = "MATERIAL_TYPE";
    private static final String COLUMN_EARLIEST_PVS = "EARLIEST_PVS";
    private static final String COLUMN_EARLIEST_NS = "EARLIEST_NS";
    private static final String COLUMN_EARLIEST_SOP = "EARLIEST_SOP";
    private static final String COLUMN_P_ACTIVATION_DATE = "P_ACTIVATION_DATE";
    private static final String COLUMN_CONSTRUCTURE_DATE = "KONSTRUCTURE_DATE";
    private static final String COLUMN_AVON_STATUS = "AVON_STATUS";

    @Id
    @Column(name = PK_EFS_ELEMENT_ID)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EfsElement.SEQ_EFS_ELEMENT)
    @SequenceGenerator(name = EfsElement.SEQ_EFS_ELEMENT, sequenceName = EfsElement.SEQ_EFS_ELEMENT)
    private Long id;

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

    @Column(name = COLUMN_NODE_VALUE, length = 40)
    private String nodeValue;

    @Column(name = COLUMN_NODE_VALUE_PARENT, length = 40)
    private String nodeValueParent;

    @Column(name = COLUMN_QUANTITY, nullable = false)
    private Integer quantity;

    @Column(name = COLUMN_POSITION_VARIANT)
    private String positionVariant;

    @Column(name = COLUMN_DELETION_FLAG)
    private String deletionFlag;

    @Column(name = COLUMN_QUANTITY_UNIT, columnDefinition = "char(3)", nullable = false)
    private String quantityUnit;

    @Column(name = COLUMN_QUANTITY_UNIT_EXTENDED, columnDefinition = "char(1)")
    private String quantityUnitExtended;

    @Column(name = COLUMN_WEIGHT_CONTROL_FLAG, columnDefinition = "char(1)")
    @Enumerated(EnumType.STRING)
    private WeightControlFlag weightControlFlag;

    @Column(name = COLUMN_CONSTRUCTIONS_GROUP, columnDefinition = "char(1)")
    private String constructionsGroup;

    @Column(name = COLUMN_PRODUCT_STRUCTURE, length = 3)
    private String productStructure;

    @Column(name = COLUMN_COST_GROUP, length = 4)
    private String costGroup;

    @Column(name = COLUMN_TI_WH_IMPORT_ID)
    private Long tiWhImportId;

    @Column(name = COLUMN_AP, length = 10, nullable = false)
    private String ap = StringConstant.EMPTY;

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

    @Column(name = COLUMN_DUPLICATE_ID)
    private String duplicateId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = FK_EFS_ELEMENT_MARA_ID, nullable = false)
    private EfsElementMara efsElementMara;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = FK_PARENT_ID)
    private EfsElementImport parent;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", cascade = CascadeType.ALL)
    private Collection<EfsElementImport> children;

    @Column(name = FK_VEHICLE_PART_LIST_ID)
    private Long vehiclePartListId;

    @Column(name = COLUMN_OPTIONAL_FALL)
    private String wahlweiseFall;

    @Column(name = COLUMN_OPTIONAL_NR)
    private Integer wahlweiseNr;

    @Column(name = COLUMN_BAUKASTEN_FLAG)
    private Integer baukastenFlag;

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

    @Column(name = COLUMN_CONSTRUCTURE_DATE)
    @Temporal(TemporalType.DATE)
    private Date konstructureDate;

    @Column(name = COLUMN_AVON_STATUS, length = 4)
    private String avonStatus;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean isGetriebe() {
        if (efsElementMara == null || efsElementMara.getDescription1De() == null
                || efsElementMara.getPartNumber() == null || StringUtils.isEmpty(aggregate)) {
            return false;
        }

        return AGGREGATE_GEARBOX.equals(efsElementMara.getDescription1De());
    }

    @JsonIgnore
    public boolean isMotor() {
        return efsElementMara != null && efsElementMara.getDescription1De() != null
                && efsElementMara.getPartNumber() != null && !StringUtils.isEmpty(aggregate)
                && efsElementMara.getDescription1De().startsWith(AGGREGATE_MOTOR) && checkMiddlePart(this)
                && StringUtils.isNotEmpty(aggregate);
    }

    @JsonIgnore
    private boolean checkMiddlePart(EfsElementImport element) {
        return element != null && element.efsElementMara != null && checkMiddlePart(
                element.efsElementMara.getPartNumber());
    }

    @JsonIgnore
    private boolean checkMiddlePart(String partNumber) {
        if (StringUtils.isEmpty(partNumber)) {
            return false;
        }

        String actualMiddle = partNumber.substring(3, 6);
        return actualMiddle.equals("100");
    }

    public Long getRevision() {
        return revision;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeLabel() {
        return nodeLabel;
    }

    public Integer getNodeLevel() {
        return nodeLevel;
    }

    public Integer getBomNumber() {
        return bomNumber;
    }

    public String getProduct() {
        return product;
    }

    public Integer getGap() {
        return gap;
    }

    public Long getTisSort() {
        return tisSort;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public String getNodeValueParent() {
        return nodeValueParent;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getPositionVariant() {
        return positionVariant;
    }

    public String getDeletionFlag() {
        return deletionFlag;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public String getQuantityUnitExtended() {
        return quantityUnitExtended;
    }

    public WeightControlFlag getWeightControlFlag() {
        return weightControlFlag;
    }

    public String getConstructionsGroup() {
        return constructionsGroup;
    }

    public String getProductStructure() {
        return productStructure;
    }

    public String getCostGroup() {
        return costGroup;
    }

    public Long getTiWhImportId() {
        return tiWhImportId;
    }

    public String getAp() {
        return ap;
    }

    public String getPrNumberRule() {
        return prNumberRule;
    }

    public String getBeginDateKey() {
        return beginDateKey;
    }

    public String getEndDateKey() {
        return endDateKey;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getPartType() {
        return partType;
    }

    public String getAggregate() {
        return aggregate;
    }

    public String getSetKey() {
        return setKey;
    }

    public String getDuplicateId() {
        return duplicateId;
    }

    public EfsElementMara getEfsElementMara() {
        return efsElementMara;
    }

    public EfsElementImport getParent() {
        return parent;
    }

    public Collection<EfsElementImport> getChildren() {
        return children;
    }

    public Long getVehiclePartListId() {
        return vehiclePartListId;
    }

    public String getWahlweiseFall() {
        return wahlweiseFall;
    }

    public Integer getWahlweiseNr() {
        return wahlweiseNr;
    }

    public Integer getBaukastenFlag() {
        return baukastenFlag;
    }

    public String getBaukastenStatus() {
        return baukastenStatus;
    }

    public String getBaukastenNodeId() {
        return baukastenNodeId;
    }

    public String getWorkPackageNumber() {
        return workPackageNumber;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public String getDmuRelevant() {
        return dmuRelevant;
    }

    public String getMaterialType() {
        return materialType;
    }

    public Date getEarliestPvs() {
        return earliestPvs;
    }

    public Date getEarliestNs() {
        return earliestNs;
    }

    public Date getEarliestSop() {
        return earliestSop;
    }

    public Date getPActivationDate() {
        return pActivationDate;
    }

    public Date getKonstructureDate() {
        return konstructureDate;
    }

    public String getAvonStatus() {
        return avonStatus;
    }

    public void setRevision(Long revision) {
        this.revision = revision;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setNodeLabel(String nodeLabel) {
        this.nodeLabel = nodeLabel;
    }

    public void setNodeLevel(Integer nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    public void setBomNumber(Integer bomNumber) {
        this.bomNumber = bomNumber;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setGap(Integer gap) {
        this.gap = gap;
    }

    public void setTisSort(Long tisSort) {
        this.tisSort = tisSort;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public void setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }

    public void setNodeValueParent(String nodeValueParent) {
        this.nodeValueParent = nodeValueParent;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPositionVariant(String positionVariant) {
        this.positionVariant = positionVariant;
    }

    public void setDeletionFlag(String deletionFlag) {
        this.deletionFlag = deletionFlag;
    }

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

    public void setCostGroup(String costGroup) {
        this.costGroup = costGroup;
    }

    public void setTiWhImportId(Long tiWhImportId) {
        this.tiWhImportId = tiWhImportId;
    }

    public void setAp(String ap) {
        this.ap = ap;
    }

    public void setPrNumberRule(String prNumberRule) {
        this.prNumberRule = prNumberRule;
    }

    public void setBeginDateKey(String beginDateKey) {
        this.beginDateKey = beginDateKey;
    }

    public void setEndDateKey(String endDateKey) {
        this.endDateKey = endDateKey;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }

    public void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    public void setSetKey(String setKey) {
        this.setKey = setKey;
    }

    public void setDuplicateId(String duplicateId) {
        this.duplicateId = duplicateId;
    }

    public void setEfsElementMara(EfsElementMara efsElementMara) {
        this.efsElementMara = efsElementMara;
    }

    public void setParent(EfsElementImport parent) {
        this.parent = parent;
    }

    public void setChildren(Collection<EfsElementImport> children) {
        this.children = children;
    }

    public void setVehiclePartListId(Long vehiclePartListId) {
        this.vehiclePartListId = vehiclePartListId;
    }

    public void setWahlweiseFall(String wahlweiseFall) {
        this.wahlweiseFall = wahlweiseFall;
    }

    public void setWahlweiseNr(Integer wahlweiseNr) {
        this.wahlweiseNr = wahlweiseNr;
    }

    public void setBaukastenFlag(Integer baukastenFlag) {
        this.baukastenFlag = baukastenFlag;
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
}
