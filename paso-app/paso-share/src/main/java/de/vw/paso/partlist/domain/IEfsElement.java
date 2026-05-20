package de.vw.paso.partlist.domain;

import java.util.Date;
import java.util.List;

public interface IEfsElement extends IPartListElement<Long> {

    Integer DELETED_FLAG = 1;
    Integer GAP_FLAG = 1;

    EfsElement getParent();

    AbstractEfsElementMara getEfsElementMara();

    Integer getDeleted();

    List<EfsElement> getChildren();

    void setDeleted(Integer deleted);

    Boolean isDeleted();

    Long getRevision();

    Integer getGap();

    String getAp();

    void setAp(String ap);

    String getCostGroup();

    void setCostGroup(String costGroup);

    String getConstructionsGroup();

    String getProductStructure();

    String getPositionVariant();

    String getDeletionFlag();

    String getQuantityUnit();

    void setQuantityUnit(String quantityUnit);

    String getQuantityUnitExtended();

    WeightControlFlag getWeightControlFlag();

    void setWeightControlFlag(WeightControlFlag weightControlFlag);

    String getBeginDateKey();

    void setBeginDateKey(String beginDateKey);

    String getEndDateKey();

    void setEndDateKey(String endDateKey);

    Long getTisSort();

    void setTisSort(Long tisSort);

    String getAggregate();

    void setAggregate(String aggregate);

    Integer getBomNumber();

    void setBomNumber(Integer bomNumber);

    String getProduct();

    String getPartType();

    Integer getBaukasten();

    String getBaukastenStatus();

    String getBaukastenNodeId();

    String getWorkPackageNumber();

    String getProcessStatus();

    String getDmuRelevant();

    String getMaterialType();

    Date getEarliestPvs();

    Date getEarliestNs();

    Date getEarliestSop();

    Date getPActivationDate();

    Date getKonstructureDate();

    String getAvonStatus();

    Double getWeight();

    void setWeight(Double weight);

    Double getNodeWeight();

    void setNodeWeight(Double nodeWeight);

    Double getCogX();

    void setCogX(Double x);

    Double getCogY();

    void setCogY(Double y);

    Double getCogZ();

    void setCogZ(Double z);

    String getNodeId();

    void setNodeId(String nodeId);

    String getWahlweiseFall();

    void setWahlweiseFall(String fall);

    Integer getWahlweiseNr();

    void setWahlweiseNr(Integer nr);

    Long getVehiclePartListId();
}
