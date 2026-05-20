package de.vw.paso.service.partlist.setkey;

import de.vw.paso.utility.StringConstant;

public class SetKeyDTO implements Comparable<SetKeyDTO> {

    public static final long UNKNOWN_SET_KEY_VERSION = -1;
    public static final long EMPTY_SET_KEY_VERSION = -99;
    public static final String NOT_RELEVANT_SET_KEY = "I1";
    public static final int DESCRIPTION_MAX_LENGTH = 255;
    public static final int SET_KEY_MAX_LENGTH = 3;

    private String setKeyName;
    private String description;
    private String parentName;
    private SetKeyDTO parentSetKey;
    private Long setVersionId;

    public SetKeyDTO() {
    }

    public SetKeyDTO(String setKeyName, String description, String parentName, Long setVersionId) {
        this.setKeyName = setKeyName;
        this.description = description;
        this.parentName = parentName;
        this.setVersionId = setVersionId;
    }

    @Override
    public String toString() {
        return setKeyName;
    }

    @Override
    public int compareTo(SetKeyDTO other) {
        String setKeyName = getSetKeyName();
        String otherSetKeyName = other.getSetKeyName();

        if (setKeyName.startsWith(StringConstant.LESS_THAN) && !otherSetKeyName.startsWith(StringConstant.LESS_THAN)) {
            return 1;
        }

        if (otherSetKeyName.startsWith(StringConstant.LESS_THAN) && !setKeyName.startsWith(StringConstant.LESS_THAN)) {
            return -1;
        }

        String setKeyPath = buildSortingPath(this);
        String otherSetKeyPath = buildSortingPath(other);
        return setKeyPath.compareTo(otherSetKeyPath);
    }

    private String buildSortingPath(SetKeyDTO setKeyDTO) {
        if (setKeyDTO == null) {
            return StringConstant.EMPTY;
        }

        if (setKeyDTO.getParentSetKey() == null) {
            return setKeyDTO.getSetKeyName();
        }

        return buildSortingPath(setKeyDTO.getParentSetKey()) + StringConstant.DOT + setKeyDTO.getSetKeyName();
    }

    public String getSetKeyName() {
        return setKeyName;
    }

    public String getDescription() {
        return description;
    }

    public String getParentName() {
        return parentName;
    }

    public SetKeyDTO getParentSetKey() {
        return parentSetKey;
    }

    public Long getSetVersionId() {
        return setVersionId;
    }

    public void setSetKeyName(String setKeyName) {
        this.setKeyName = setKeyName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setParentSetKey(SetKeyDTO parentSetKey) {
        this.parentSetKey = parentSetKey;
    }

    public void setSetVersionId(Long setVersionId) {
        this.setVersionId = setVersionId;
    }
}
