package de.vw.paso.partlist.domain;

import de.vw.paso.core.domain.AbstractEntity;
import de.vw.paso.utility.StringConstant;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = SetKey.TABLE_SET_KEY)
public final class SetKey extends AbstractEntity<SetKeyVersionPK> implements Comparable<SetKey> {

    public static final int SET_KEY_MAX_LENGTH = 3;

    static final String TABLE_SET_KEY = "SET_KEY";

    private static final String FK_PARENT = "PARENT";
    private static final String FK_VERSION = "SET_VERSION_ID";
    private static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_PARENT = "PARENT";

    @EmbeddedId
    private SetKeyVersionPK id;

    @Column(name = COLUMN_DESCRIPTION, nullable = false)
    private String description;

    @Column(name = COLUMN_PARENT, columnDefinition = "char(" + SET_KEY_MAX_LENGTH + ")")
    private String parentSetKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({ @JoinColumn(name = FK_PARENT, insertable = false, updatable = false),
            @JoinColumn(name = FK_VERSION, insertable = false, updatable = false) })
    private SetKey parent;

    public SetKey() {
    }

    public SetKey(String setKey, long setVersionId) {
        id = new SetKeyVersionPK();
        id.setSetKey(setKey);
        id.setVersion(setVersionId);
    }

    public SetKey(String setKey, String description, String parent, long setVersionId) {
        this(setKey, setVersionId);
        this.description = description;
        this.parentSetKey = parent;
    }

    public String getSetKey() {
        return id == null ? null : id.getSetKey();
    }

    public void setSetKey(String setKey) {
        if (id == null) {
            SetKeyVersionPK setKeyVersionPK = new SetKeyVersionPK();
            setKeyVersionPK.setSetKey(setKey);
            setId(setKeyVersionPK);

            return;
        }

        id.setSetKey(setKey);
    }

    public Long getVersion() {
        return id == null ? null : id.getVersion();
    }

    public void setVersion(Long version) {
        if (id == null) {
            SetKeyVersionPK setKeyVersionPK = new SetKeyVersionPK();
            setKeyVersionPK.setVersion(version);
            setId(setKeyVersionPK);

            return;
        }

        getId().setVersion(version);
    }

    @Override
    public int compareTo(SetKey other) {
        String setKey = getSetKey();
        String otherSetKey = other.getSetKey();

        if (setKey.startsWith(StringConstant.LESS_THAN) && !otherSetKey.startsWith(StringConstant.LESS_THAN)) {
            return 1;
        }

        if (otherSetKey.startsWith(StringConstant.LESS_THAN) && !setKey.startsWith(StringConstant.LESS_THAN)) {
            return -1;
        }

        String setKeyPath = buildSortingPath(this);
        String otherSetKeyPath = buildSortingPath(other);
        return setKeyPath.compareTo(otherSetKeyPath);
    }

    private static String buildSortingPath(SetKey setKey) {
        if (setKey == null) {
            return StringConstant.EMPTY;
        }

        return setKey.getParent() == null ? setKey.getSetKey() :
                buildSortingPath(setKey.getParent()) + StringConstant.DOT + setKey.getSetKey();
    }

    @Override
    public String toString() {
        return getSetKey();
    }

    @Override
    public SetKeyVersionPK getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getParentSetKey() {
        return parentSetKey;
    }

    public SetKey getParent() {
        return parent;
    }

    @Override
    public void setId(SetKeyVersionPK id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParentSetKey(String parentSetKey) {
        this.parentSetKey = parentSetKey;
    }

    public void setParent(SetKey parent) {
        this.parent = parent;
    }
}
