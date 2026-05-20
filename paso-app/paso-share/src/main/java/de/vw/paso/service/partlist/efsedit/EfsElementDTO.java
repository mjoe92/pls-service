package de.vw.paso.service.partlist.efsedit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.vw.paso.partlist.domain.ICrumb;
import de.vw.paso.partlist.domain.IPartListChildDTO;
import de.vw.paso.partlist.domain.IPartListElement;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementDTO;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

public class EfsElementDTO extends AbstractEfsElementDTO implements IPartListChildDTO, ICrumb {

    public static final String AGGREGAT_GETRIEBE = "GETRIEBE";
    public static final String AGGREGAT_MOTOR = "MOTOR";
    public static final Integer DELETED_FLAG = 1;

    private Long id;
    private Long parentId;
    @JsonIgnore
    private EfsElementDTO parent;
    @JsonIgnore
    private List<EfsElementDTO> children;
    private Long vehiclePartListId;
    private String duplicateId;
    @JsonIgnore
    private Double weight;
    @JsonIgnore
    private Double nodeWeight;

    public EfsElementDTO() {
        weight = 0.0;
        nodeWeight = 0.0;
    }

    @JsonIgnore
    @Override
    public ICrumb getCrumbParent() {
        return this.getParent();
    }

    @JsonIgnore
    @Override
    public String getCrumbText() {
        return this.getEfsElementMara().getDescription1De();
    }

    @JsonIgnore
    public EfsElementDTO asParent() {
        return this;
    }

    @JsonIgnore
    public boolean isGetriebe() {
        EfsElementMaraDTO mara = getEfsElementMara();
        if (mara == null || mara.getDescription1De() == null || mara.getPartNumber() == null || StringUtils.isEmpty(
                getAggregate())) {
            return false;
        }

        return AGGREGAT_GETRIEBE.equals(mara.getDescription1De());
    }

    @JsonIgnore
    public boolean isMotor() {
        EfsElementMaraDTO mara = getEfsElementMara();
        if (mara == null || mara.getDescription1De() == null || mara.getPartNumber() == null || StringUtils.isEmpty(
                getAggregate())) {
            return false;
        }

        return mara.getDescription1De().startsWith(AGGREGAT_MOTOR) && checkMiddlePart(this) && StringUtils.isNotEmpty(
                getAggregate());
    }

    @JsonIgnore
    private boolean checkMiddlePart(EfsElementDTO element) {
        if (element == null || element.getEfsElementMara() == null) {
            return false;
        }

        return checkMiddlePart(element.getEfsElementMara().getPartNumber());
    }

    @JsonIgnore
    private boolean checkMiddlePart(String partNumber) {
        if (StringUtils.isEmpty(partNumber)) {
            return false;
        }

        String actualMiddle = partNumber.substring(3, 6);
        return actualMiddle.equals("100");
    }

    @JsonIgnore
    public boolean isRoot() {
        return parent == null;
    }

    @JsonIgnore
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }

    @JsonIgnore
    public boolean isWeightRelevant() {
        if (isRoot()) {
            return false;
        }

        if ("SAMMLER".equals(getDescription1())) {
            return false;
        }

        if (WeightControlFlag.YES == getWeightControlFlag() || WeightControlFlag.TEMP == getWeightControlFlag()) {
            return true;
        }

        return isLeaf() && getWeightControlFlag() == null;
    }

    @JsonIgnore
    public boolean hasWeight() {
        EfsElementMaraDTO mara = getEfsElementMara();
        return hasValue(mara.getWeightCalculatedTe()) || hasValue(mara.getWeightEstimatedTe()) || hasValue(
                mara.getWeightWeightedProd()) || hasValue(mara.getWeightWeightedTe());
    }

    @JsonIgnore
    public boolean hasNodeWeight() {
        return hasValue(getNodeWeight());
    }

    @JsonIgnore
    private boolean hasValue(Number number) {
        return number != null && number.doubleValue() != 0d;
    }

    @JsonIgnore
    public Boolean isDeleted() {
        return DELETED_FLAG.equals(getDeleted());
    }

    @Override
    public String toString() {
        String description = getEfsElementMara().getDescription1De();

        if (getEfsElementMara().getDescription2De() != null && !StringConstant.EMPTY.equals(
                getEfsElementMara().getDescription2De().trim())) {
            description = description + StringConstant.COMMA_SPACE + getEfsElementMara().getDescription2De();
        }

        return description == null ? super.toString() : description;
    }

    @Override
    @JsonIgnore
    public Type getType() {
        return IPartListElement.Type.EFS_ELEMENT;
    }

    @JsonIgnore
    public List<EfsElementDTO> getAllChildren() {
        List<EfsElementDTO> childrenList = new ArrayList<>();
        getAllChildren(this, childrenList);

        return childrenList;
    }

    @JsonIgnore
    private void getAllChildren(EfsElementDTO root, List<EfsElementDTO> currList) {
        currList.add(root);
        Collection<EfsElementDTO> childrenList = root.getChildren();
        if (childrenList != null) {
            for (EfsElementDTO child : childrenList) {
                getAllChildren(child, currList);
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    @Override
    public EfsElementDTO getParent() {
        return parent;
    }

    @Override
    public Long getVehiclePartListId() {
        return vehiclePartListId;
    }

    @Override
    public List<EfsElementDTO> getChildren() {
        return children;
    }

    public String getDuplicateId() {
        return duplicateId;
    }

    @Override
    public Double getWeight() {
        return weight;
    }

    @Override
    public Double getNodeWeight() {
        return nodeWeight;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setParent(EfsElementDTO parent) {
        this.parent = parent;
    }

    public void setChildren(List<EfsElementDTO> children) {
        this.children = children;
    }

    @Override
    public void setVehiclePartListId(Long vehiclePartListId) {
        this.vehiclePartListId = vehiclePartListId;
    }

    public void setDuplicateId(String duplicateId) {
        this.duplicateId = duplicateId;
    }

    @Override
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public void setNodeWeight(Double nodeWeight) {
        this.nodeWeight = nodeWeight;
    }
}
