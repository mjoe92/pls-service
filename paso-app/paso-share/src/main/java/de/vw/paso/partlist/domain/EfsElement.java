package de.vw.paso.partlist.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.StringConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.apache.commons.lang3.StringUtils;

/**
 * Entity that represents an EfsElement.
 * Always part of one {@link VehiclePartList}.
 * Contains conversion methods to and from {@link EfsElementDTO}.
 * <p>
 * Maybe the same as {@link EfsElementImport}.
 * Both classes could be merged at some point.
 *
 * @see EfsElementImport
 */
@Entity
@Table(name = "EFS_ELEMENT")
public final class EfsElement extends AbstractEfsElement implements IPartListChild, ICrumb {

    public static final String SEQ_EFS_ELEMENT = "SEQ_EFS_ELEMENT";

    @Id
    @Column(name = "EFS_ELEMENT_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_EFS_ELEMENT)
    @SequenceGenerator(name = SEQ_EFS_ELEMENT, sequenceName = SEQ_EFS_ELEMENT)
    private Long id;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Transient
    //  @ManyToOne(fetch = FetchType.EAGER)
    //  @JoinColumn(name = FK_PARENT_ID)
    private EfsElement parent;

    @Transient
    //  @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentId")
    private List<EfsElement> children = new ArrayList<>();

    @Column(name = "VEHICLE_PART_LIST_ID")
    private Long vehiclePartListId;

    @Column(name = "DUPLICATE_ID")
    private String duplicateId;

    @Transient
    private Double weight = 0.0;

    @Transient
    private Double nodeWeight = 0.0;

    @Override
    public void setVehiclePartListId(Long vehiclePartList) {
        this.vehiclePartListId = vehiclePartList;
    }

    @Override
    public Long getVehiclePartListId() {
        return vehiclePartListId;
    }

    public void setChildren(Collection<EfsElement> children) {
        this.children.clear();
        this.children.addAll(children);
    }

    @Override
    public IPartListElement.Type getType() {
        return IPartListElement.Type.EFS_ELEMENT;
    }

    @Override
    public String toString() {
        String description = getEfsElementMara().getDescription1De();

        if (getEfsElementMara().getDescription2De() != null && !getEfsElementMara().getDescription2De().trim()
                .equals(StringConstant.EMPTY)) {
            description = description + StringConstant.COMMA_SPACE + getEfsElementMara().getDescription2De();
        }

        return description == null ? super.toString() : description;
    }

    @Override
    public EfsElement getParent() {
        return parent;
    }

    @Override
    public EfsElement asParent() {
        return this;
    }

    public void moveToParent(EfsElement newParent) {
        EfsElement oldParent = getParent();
        if (newParent == oldParent) {
            return;
        }

        if (oldParent != null) {
            oldParent.children.remove(this);
        }

        Long parentId;
        if (newParent == null) {
            parentId = null;
            setParentId(null);
        } else {
            newParent.children.add(this);
            parentId = newParent.getId();
        }

        setParentId(parentId);

        parent = newParent;
    }

    public ICrumb getCrumbParent() {
        return getParent();
    }

    public String getCrumbText() {
        return getDescription1();
    }

    @Transient
    public boolean isMotor() {
        if (getEfsElementMara() == null || getEfsElementMara().getDescription1De() == null
                || getEfsElementMara().getPartNumber() == null) {
            return false;
        }

        return getEfsElementMara().getDescription1De().startsWith("MOTOR") && checkMiddlePart(this)
                && StringUtils.isNotEmpty(getAggregate());
    }

    @Transient
    private boolean checkMiddlePart(EfsElement element) {
        return element != null && element.getEfsElementMara() != null && checkMiddlePart(
                element.getEfsElementMara().getPartNumber());
    }

    @Transient
    private boolean checkMiddlePart(String partNumber) {
        if (StringUtils.isEmpty(partNumber)) {
            return false;
        }

        String actualMiddle = partNumber.substring(3, 6);
        return actualMiddle.equals("100");
    }

    @Transient
    public boolean isGetriebe() {
        return getEfsElementMara() != null && "GETRIEBE".equals(getEfsElementMara().getDescription1De());
    }

    @Transient
    public boolean isRoot() {
        return parent == null;
    }

    @Transient
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }

    @Override
    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    @Override
    public List<EfsElement> getChildren() {
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

    public void setParent(EfsElement parent) {
        this.parent = parent;
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
