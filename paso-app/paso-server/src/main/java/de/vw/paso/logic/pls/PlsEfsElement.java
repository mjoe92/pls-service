package de.vw.paso.logic.pls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.util.QuantityUnit;
import de.vw.paso.utility.StringConstant;

public class PlsEfsElement {

    private Collection<PlsEfsElement> children;
    private String originNodeId;
    private Integer bomNumber;
    private String product;
    private String originParentNodeId;
    private String partType;
    private WeightControlFlag weightControlFlag;
    private String constructionsGroup;
    private String productStructure;
    private String costGroup;
    private String prNumberRule;
    private String beginDateKey;
    private String endDateKey;
    private Date beginDate;
    private Date endDate;
    private String aggregate;
    private String setKey;
    private String positionVariant;
    private String deletionFlag;
    private Integer quantity = 0;
    private QuantityUnit quantityUnit;
    private String quantityUnitExtended;
    private String baukastenKz;
    private String baukastenStatus;
    private String baukastenNodeId;
    private String wahlweiseFall;
    private Integer wahlweiseNr;
    private String workPackageNumber;
    private String processStatus;
    private String DMURelevant;
    private Integer nodeSort;
    private Integer partSort;
    private String duplicateId;
    private Integer nodeLevel;
    private String nodeType;
    private String materialType;
    private Date pActivationDate;
    private Date konstructureDate;
    private String avonStatus;
    private boolean gap;

    /* Mara data */
    private String partNumber;
    private String partNumberVornummer;
    private String partNumberMittelGruppe;
    private String partNumberEndNumber;
    private String partNumberIndex;
    private String description1De;
    private String description1En;
    private String description2De;
    private String description2En;
    private Double weightCalculatedTe = 0.0D;
    private Date weightCalculatedTeDate;
    private Double weightEstimatedTe = 0.0D;
    private Date weightEstimatedTeDate;
    private Double weightWeightedTe = 0.0D;
    private Date weightWeightedTeDate;
    private Double weightWeightedProd = 0.0D;
    private Date weightWeightedProdDate;
    private String quality;
    private Double materialThickness;
    private Date earliestPVS;
    private Date earliestNS;
    private Date earliestSOP;
    private String seeDrawing;
    private String responsibleConstr1;
    private String responsibleConstr2;
    private String buildSampleApproval;
    private Date buildSampleApprovalTargetDate;
    private String technicallyOkay;
    private Date releaseDateSoll;
    private String designerName;
    private String designerCostGroup;
    private String designerPhoneNumber;
    private Date kStandReleaseDate;
    private Date tioFreiReleaseDate;
    private String drawingStatus;
    private Date drawingDate;
    private String assemblyIndicator;
    private String constructionsState;
    private Double priorizedWeight;
    private String MFPStatus;
    private Double MFPThickness;
    private String kseKz;
    private String weightAcceptedFromEPIS;
    private String nodeLabel;
    private String nodeValue;
    private String nodeValueParent;
    private boolean partFound;
    private boolean maraSet;
    private boolean isEbk;
    private long globalSort;

    public Collection<PlsEfsElement> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }

        return children;
    }

    public void setChildren(Collection<PlsEfsElement> children) {
        this.children = children;
    }

    public String getOriginNodeId() {
        return originNodeId;
    }

    public void setOriginNodeId(String originNodeId) {
        this.originNodeId = originNodeId;
    }

    public Integer getBomNumber() {
        return bomNumber;
    }

    public void setBomNumber(Integer bomNumber) {
        this.bomNumber = bomNumber;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getOriginParentNodeId() {
        return originParentNodeId;
    }

    public void setOriginParentNodeId(String originParentNodeId) {
        this.originParentNodeId = originParentNodeId;
    }

    public String getPartType() {
        return partType;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }

    public WeightControlFlag getWeightControlFlag() {
        return weightControlFlag;
    }

    public void setWeightControlFlag(WeightControlFlag weightControlFlag) {
        this.weightControlFlag = weightControlFlag;
    }

    public String getConstructionsGroup() {
        return constructionsGroup;
    }

    public void setConstructionsGroup(String constructionsGroup) {
        this.constructionsGroup = constructionsGroup;
    }

    public String getProductStructure() {
        return productStructure;
    }

    public void setProductStructure(String productStructure) {
        this.productStructure = productStructure;
    }

    public String getCostGroup() {
        return costGroup;
    }

    public void setCostGroup(String costGroup) {
        this.costGroup = costGroup;
    }

    public String getPrNumberRule() {
        return prNumberRule;
    }

    public void setPrNumberRule(String prNumberRule) {
        this.prNumberRule = prNumberRule;
    }

    public String getBeginDateKey() {
        return beginDateKey;
    }

    public void setBeginDateKey(String beginDateKey) {
        this.beginDateKey = beginDateKey;
    }

    public String getEndDateKey() {
        return endDateKey;
    }

    public void setEndDateKey(String endDateKey) {
        this.endDateKey = endDateKey;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAggregate() {
        return aggregate;
    }

    public void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    public String getSetKey() {
        return setKey;
    }

    public void setSetKey(String setKey) {
        this.setKey = setKey;
    }

    public String getPositionVariant() {
        return positionVariant;
    }

    public void setPositionVariant(String positionVariant) {
        this.positionVariant = positionVariant;
    }

    public String getDeletionFlag() {
        return deletionFlag;
    }

    public void setDeletionFlag(String deletionFlag) {
        this.deletionFlag = deletionFlag;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public QuantityUnit getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(QuantityUnit quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public String getQuantityUnitExtended() {
        return quantityUnitExtended;
    }

    public void setQuantityUnitExtended(String s) {
        this.quantityUnitExtended = s;
    }

    public String getBaukastenStatus() {
        return baukastenStatus;
    }

    public void setBaukastenStatus(String baukastenStatus) {
        this.baukastenStatus = baukastenStatus;
    }

    public String getBaukastenNodeId() {
        return baukastenNodeId;
    }

    public void setBaukastenNodeId(String baukastenNodeId) {
        this.baukastenNodeId = baukastenNodeId;
    }

    public String getBaukastenKz() {
        return baukastenKz;
    }

    public void setBaukastenKz(String baukastenKz) {
        this.baukastenKz = baukastenKz;
    }

    public String getWahlweiseFall() {
        return wahlweiseFall;
    }

    public void setWahlweiseFall(String wahlweiseFall) {
        this.wahlweiseFall = wahlweiseFall;
    }

    public Integer getWahlweiseNr() {
        return wahlweiseNr;
    }

    public void setWahlweiseNr(Integer wahlweiseNr) {
        this.wahlweiseNr = wahlweiseNr;
    }

    public String getWorkPackageNumber() {
        return workPackageNumber;
    }

    public void setWorkPackageNumber(String workPackageNumber) {
        this.workPackageNumber = workPackageNumber;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getDMURelevant() {
        return DMURelevant;
    }

    public void setDMURelevant(String DMURelevant) {
        this.DMURelevant = DMURelevant;
    }

    public Integer getNodeSort() {
        return nodeSort;
    }

    public void setNodeSort(Integer nodeSort) {
        this.nodeSort = nodeSort;
    }

    public Integer getPartSort() {
        return partSort;
    }

    public void setPartSort(Integer partSort) {
        this.partSort = partSort;
    }

    public String getDuplicateId() {
        return duplicateId;
    }

    public void setDuplicateId(String dupId) {
        this.duplicateId = dupId;
    }

    public Integer getNodeLevel() {
        return nodeLevel;
    }

    public void setNodeLevel(Integer nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getMaterialType() {
        return materialType;
    }

    public Date getpActivationDate() {
        return pActivationDate;
    }

    public void setpActivationDate(Date pActivationDate) {
        this.pActivationDate = pActivationDate;
    }

    public Date getKonstructureDate() {
        return konstructureDate;
    }

    public void setKonstructureDate(Date konstructureDate) {
        this.konstructureDate = konstructureDate;
    }

    public String getAvonStatus() {
        return avonStatus;
    }

    public void setAvonStatus(String avonStatus) {
        this.avonStatus = avonStatus;
    }

    public boolean isGap() {
        return gap;
    }

    public void setGap(boolean gap) {
        this.gap = gap;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartNumberVornummer() {
        return partNumberVornummer;
    }

    public void setPartNumberVornummer(String partNumberVornummer) {
        this.partNumberVornummer = partNumberVornummer;
    }

    public String getPartNumberMittelGruppe() {
        return partNumberMittelGruppe;
    }

    public void setPartNumberMittelGruppe(String partNumberMittelGruppe) {
        this.partNumberMittelGruppe = partNumberMittelGruppe;
    }

    public String getPartNumberEndNumber() {
        return partNumberEndNumber;
    }

    public void setPartNumberEndNumber(String partNumberEndNumber) {
        this.partNumberEndNumber = partNumberEndNumber;
    }

    public String getPartNumberIndex() {
        return partNumberIndex;
    }

    public void setPartNumberIndex(String partNumberIndex) {
        this.partNumberIndex = partNumberIndex;
    }

    public String getDescription1De() {
        return description1De;
    }

    public void setDescription1De(String description1De) {
        this.description1De = description1De;
    }

    public String getDescription1En() {
        return description1En;
    }

    public void setDescription1En(String description1En) {
        this.description1En = description1En;
    }

    public String getDescription2De() {
        return description2De;
    }

    public void setDescription2De(String description2De) {
        this.description2De = description2De;
    }

    public String getDescription2En() {
        return description2En;
    }

    public void setDescription2En(String description2En) {
        this.description2En = description2En;
    }

    public Double getWeightCalculatedTe() {
        return weightCalculatedTe;
    }

    public void setWeightCalculatedTe(Double weightCalculatedTe) {
        this.weightCalculatedTe = weightCalculatedTe;
    }

    public Double getWeightEstimatedTe() {
        return weightEstimatedTe;
    }

    public void setWeightEstimatedTe(Double weightEstimatedTe) {
        this.weightEstimatedTe = weightEstimatedTe;
    }

    public Double getWeightWeightedTe() {
        return weightWeightedTe;
    }

    public void setWeightWeightedTe(Double weightWeightedTe) {
        this.weightWeightedTe = weightWeightedTe;
    }

    public Double getWeightWeightedProd() {
        return weightWeightedProd;
    }

    public void setWeightWeightedProd(Double weightWeightedProd) {
        this.weightWeightedProd = weightWeightedProd;
    }

    public Date getWeightCalculatedTeDate() {
        return weightCalculatedTeDate;
    }

    public void setWeightCalculatedTeDate(Date weightCalculatedTeDate) {
        this.weightCalculatedTeDate = weightCalculatedTeDate;
    }

    public Date getWeightEstimatedTeDate() {
        return weightEstimatedTeDate;
    }

    public void setWeightEstimatedTeDate(Date weightEstimatedTeDate) {
        this.weightEstimatedTeDate = weightEstimatedTeDate;
    }

    public Date getWeightWeightedTeDate() {
        return weightWeightedTeDate;
    }

    public void setWeightWeightedTeDate(Date weightWeightedTeDate) {
        this.weightWeightedTeDate = weightWeightedTeDate;
    }

    public Date getWeightWeightedProdDate() {
        return weightWeightedProdDate;
    }

    public void setWeightWeightedProdDate(Date weightWeightedProdDate) {
        this.weightWeightedProdDate = weightWeightedProdDate;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Double getMaterialThickness() {
        return materialThickness;
    }

    public void setMaterialThickness(Double materialThickness) {
        this.materialThickness = materialThickness;
    }

    public Date getEarliestPVS() {
        return earliestPVS;
    }

    public void setEarliestPVS(Date earliestPVS) {
        this.earliestPVS = earliestPVS;
    }

    public Date getEarliestNS() {
        return earliestNS;
    }

    public void setEarliestNS(Date earliestNS) {
        this.earliestNS = earliestNS;
    }

    public Date getEarliestSOP() {
        return earliestSOP;
    }

    public void setEarliestSOP(Date earliestSOP) {
        this.earliestSOP = earliestSOP;
    }

    public String getSeeDrawing() {
        return seeDrawing;
    }

    public void setSeeDrawing(String seeDrawing) {
        this.seeDrawing = seeDrawing;
    }

    public String getResponsibleConstr1() {
        return responsibleConstr1;
    }

    public void setResponsibleConstr1(String responsibleConstr1) {
        this.responsibleConstr1 = responsibleConstr1;
    }

    public String getResponsibleConstr2() {
        return responsibleConstr2;
    }

    public void setResponsibleConstr2(String responsibleConstr2) {
        this.responsibleConstr2 = responsibleConstr2;
    }

    public String getBuildSampleApproval() {
        return buildSampleApproval;
    }

    public void setBuildSampleApproval(String buildSampleApproval) {
        this.buildSampleApproval = buildSampleApproval;
    }

    public Date getBuildSampleApprovalTargetDate() {
        return buildSampleApprovalTargetDate;
    }

    public void setBuildSampleApprovalTargetDate(Date buildSampleApprovalTargetDate) {
        this.buildSampleApprovalTargetDate = buildSampleApprovalTargetDate;
    }

    public String getTechnicallyOkay() {
        return technicallyOkay;
    }

    public void setTechnicallyOkay(String technicallyOkay) {
        this.technicallyOkay = technicallyOkay;
    }

    public Date getReleaseDateSoll() {
        return releaseDateSoll;
    }

    public void setReleaseDateSoll(Date releaseDateSoll) {
        this.releaseDateSoll = releaseDateSoll;
    }

    public String getDesignerName() {
        return designerName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }

    public String getDesignerCostGroup() {
        return designerCostGroup;
    }

    public void setDesignerCostGroup(String designerCostGroup) {
        this.designerCostGroup = designerCostGroup;
    }

    public String getDesignerPhoneNumber() {
        return designerPhoneNumber;
    }

    public void setDesignerPhoneNumber(String designerPhoneNumber) {
        this.designerPhoneNumber = designerPhoneNumber;
    }

    public Date getkStandReleaseDate() {
        return kStandReleaseDate;
    }

    public void setkStandReleaseDate(Date kStandReleaseDate) {
        this.kStandReleaseDate = kStandReleaseDate;
    }

    public Date getTioFreiReleaseDate() {
        return tioFreiReleaseDate;
    }

    public void setTioFreiReleaseDate(Date tioFreiReleaseDate) {
        this.tioFreiReleaseDate = tioFreiReleaseDate;
    }

    public String getDrawingStatus() {
        return drawingStatus;
    }

    public void setDrawingStatus(String drawingStatus) {
        this.drawingStatus = drawingStatus;
    }

    public Date getDrawingDate() {
        return drawingDate;
    }

    public void setDrawingDate(Date drawingDate) {
        this.drawingDate = drawingDate;
    }

    public String getConstructionsState() {
        return constructionsState;
    }

    public void setConstructionsState(String constructionsState) {
        this.constructionsState = constructionsState;
    }

    public String getAssemblyIndicator() {
        return assemblyIndicator;
    }

    public void setAssemblyIndicator(String assemblyIndicator) {
        this.assemblyIndicator = assemblyIndicator;
    }

    public void setPriorizedWeight(Double priorizedWeight) {
        this.priorizedWeight = priorizedWeight;
    }

    public String getMFPStatus() {
        return MFPStatus;
    }

    public void setMFPStatus(String MFPStatus) {
        this.MFPStatus = MFPStatus;
    }

    public Double getMFPThickness() {
        return MFPThickness;
    }

    public void setMFPThickness(Double MFPThickness) {
        this.MFPThickness = MFPThickness;
    }

    public String getKseKz() {
        return kseKz;
    }

    public void setKseKz(String kseKz) {
        this.kseKz = kseKz;
    }

    public String getWeightAcceptedFromEPIS() {
        return weightAcceptedFromEPIS;
    }

    public void setWeightAcceptedFromEPIS(String weightAcceptedFromEPIS) {
        this.weightAcceptedFromEPIS = weightAcceptedFromEPIS;
    }

    public String getNodeLabel() {
        return nodeLabel;
    }

    public void setNodeLabel(String nodeLabel) {
        this.nodeLabel = nodeLabel;
    }

    public String getNodeValueParent() {
        return nodeValueParent;
    }

    public void setNodeValueParent(String nodeValueParent) {
        this.nodeValueParent = nodeValueParent;
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }

    public boolean isPartFound() {
        return partFound;
    }

    public void setPartFound(boolean partFound) {
        this.partFound = partFound;
    }

    public boolean isMaraSet() {
        return maraSet;
    }

    public void setMaraSet(boolean maraSet) {
        this.maraSet = maraSet;
    }

    public boolean isEbk() {
        return isEbk;
    }

    public void setEbk(boolean ebk) {
        isEbk = ebk;
    }

    public long getGlobalSort() {
        return globalSort;
    }

    public void setGlobalSort(long sortvalue) {
        globalSort = sortvalue;
    }

    @Override
    public String toString() {
        return partNumber + StringConstant.SPACE + description1De + StringConstant.SPACE + description2De;
    }
}
