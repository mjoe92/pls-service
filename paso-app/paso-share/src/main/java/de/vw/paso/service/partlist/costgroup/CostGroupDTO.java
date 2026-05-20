package de.vw.paso.service.partlist.costgroup;

import java.util.Objects;

import com.google.common.primitives.Longs;

import de.vw.paso.utility.StringConstant;

public class CostGroupDTO implements Comparable<CostGroupDTO> {

    public static final long UNKNOWN_COST_GROUP_VERSION = -1;
    public static final long EMPTY_COST_GROUP_KEY_VERSION = -99;

    private String costGroupName;
    private Long version;
    private String description;
    private String parentCostGroupName;
    private CostGroupDTO parent;

    public CostGroupDTO() {
    }

    public CostGroupDTO(String costGroup, long version) {
        this.costGroupName = costGroup;
        this.version = version;
    }

    public CostGroupDTO(String costGroup, String description, String parent, Long version) {
        this.costGroupName = costGroup;
        this.description = description;
        this.parentCostGroupName = parent;

        // to avoid NPE here, since setting the set version is not possible from the ui
        this.version = Objects.requireNonNullElse(version, 0L);
    }

    public CostGroupDTO(String costGroup, String description, String parentName, String version) {
        this.costGroupName = costGroup;
        this.description = description;
        this.parentCostGroupName = parentName;
        this.version = Longs.tryParse(version);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CostGroupDTO && version != null && costGroupName != null) {
            return version.equals(((CostGroupDTO) obj).getVersion()) && costGroupName.equals(
                (((CostGroupDTO) obj).getCostGroupName()));
        }

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    private CostGroupVersionPKDTO getId() {
        return new CostGroupVersionPKDTO(getCostGroupName(), getVersion());
    }

    @Override
    public int compareTo(CostGroupDTO other) {
        String costGroupKey = getCostGroupName();
        String otherCostGroupKey = other.getCostGroupName();

        if (costGroupKey.startsWith(StringConstant.LESS_THAN) && !otherCostGroupKey.startsWith(
            StringConstant.LESS_THAN)) {
            return 1;
        }

        return
            otherCostGroupKey.startsWith(StringConstant.LESS_THAN) && !costGroupKey.startsWith(StringConstant.LESS_THAN)
                ? -1 : costGroupKey.compareTo(otherCostGroupKey);
    }

    @Override
    public String toString() {
        return "CostGroupDTO{" + "costGroupName='" + costGroupName + '\'' + ", version=" + version + ", description='"
            + description + '\'' + ", parentCostGroupName='" + parentCostGroupName + '\'' + ", parent=" + parent + '}';
    }

    public String getCostGroupName() {
        return costGroupName;
    }

    public Long getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getParentCostGroupName() {
        return parentCostGroupName;
    }

    public CostGroupDTO getParent() {
        return parent;
    }

    public void setCostGroupName(String costGroupName) {
        this.costGroupName = costGroupName;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParentCostGroupName(String parentCostGroupName) {
        this.parentCostGroupName = parentCostGroupName;
    }

    public void setParent(CostGroupDTO parent) {
        this.parent = parent;
    }
}
