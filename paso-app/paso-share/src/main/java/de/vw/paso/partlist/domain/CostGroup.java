package de.vw.paso.partlist.domain;

import com.google.common.primitives.Longs;
import de.vw.paso.core.domain.AbstractEntity;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Table(name = CostGroup.TABLE_COST_GROUP)
public final class CostGroup extends AbstractEntity<CostGroupVersionPK> implements Comparable<CostGroup> {

    protected static final String TABLE_COST_GROUP = "COST_GROUP";

    private static final long serialVersionUID = 1L;

    private static final String FK_PARENT = "PARENT";
    private static final String FK_VERSION = "VERSION";
    private static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_PARENT = "PARENT";

    @Getter
    @Setter
    @EmbeddedId
    private CostGroupVersionPK id;

    @Getter
    @Setter
    @Column(name = COLUMN_DESCRIPTION, length = 4000, updatable = false)
    private String description;

    @Getter
    @SuppressWarnings("unused")
    @Column(name = COLUMN_PARENT, columnDefinition = "char(4)")
    private String parentCostGroup;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = FK_PARENT, insertable = false, updatable = false),
            @JoinColumn(name = FK_VERSION, insertable = false, updatable = false) })
    private CostGroup parent;

    public CostGroup(final String costGroup, final long version) {
        id = new CostGroupVersionPK(costGroup, version);
    }

    public CostGroup(final String costGroup, final String description, final String parent, final String version) {
        this.id = new CostGroupVersionPK(costGroup, Longs.tryParse(version));
        this.description = description;
        this.parentCostGroup = parent;
    }

    public CostGroup(CostGroupDTO newCostGroup) {
        this(newCostGroup.getCostGroupName(), newCostGroup.getDescription(), newCostGroup.getParentCostGroupName(),
                newCostGroup.getVersion().toString());
    }

    public String getCostGroup() {
        if (getId() == null) {
            return null;
        }
        return getId().getCostGroup();
    }

    public Long getVersion() {
        return getId().getVersion();
    }

    @PrePersist
    @PreUpdate
    private void setParentCostGroup() {
        if (getParent() != null) {
            parentCostGroup = getParent().getCostGroup();
        }
    }

    @Override
    public int compareTo(CostGroup other) {

        String costGroupKey = this.getCostGroup();
        String otherCostGroupKey = other.getCostGroup();

        if (costGroupKey.startsWith("<") && !otherCostGroupKey.startsWith("<")) {
            return 1;
        }

        if (otherCostGroupKey.startsWith("<") && !costGroupKey.startsWith("<")) {
            return -1;
        }
        return costGroupKey.compareTo(otherCostGroupKey);
    }
}
