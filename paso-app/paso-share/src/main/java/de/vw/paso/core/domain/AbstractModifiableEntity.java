package de.vw.paso.core.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class AbstractModifiableEntity<ID> extends AbstractEntity<ID> {

    private static final long serialVersionUID = 1L;

    private static final String COLUMN_TIMESTAMP_CREATE = "TIMESTAMP_CREATE";
    private static final String COLUMN_TIMESTAMP_CHANGE = "TIMESTAMP_CHANGE";
    private static final String COLUMN_USER_CREATE = "USER_CREATE";
    private static final String COLUMN_USER_CHANGE = "USER_CHANGE";

    @Getter
    @Setter
    @Column(name = COLUMN_TIMESTAMP_CREATE, updatable = false)
    private Timestamp timestampCreate;

    // Todo - Delete setters... or make it private...
    @Getter
    @Setter
    @Column(name = COLUMN_TIMESTAMP_CHANGE)
    private Timestamp timestampChange;

    @Getter
    @Setter
    @Column(name = COLUMN_USER_CREATE, updatable = false)
    private String userCreate;

    @Getter
    @Setter
    @Column(name = COLUMN_USER_CHANGE)
    private String userChange;

    @Setter
    @Transient
    private Boolean entityChange = false;

    @JsonIgnore
    @Transient
    public void setChange(String userId) {
        if (getId() == null || timestampCreate == null) {
            setToInsert(userId);
        } else {
            setToUpdate(userId);
        }

        entityChange = Boolean.TRUE;
    }

    @JsonIgnore
    @Transient
    protected void setToInsert(String userId) {
        final Timestamp now = new Timestamp(System.currentTimeMillis());

        setTimestampCreate(now);
        setTimestampChange(now);

        setUserCreate(userId);
        setUserChange(userId);
    }

    @JsonIgnore
    @Transient
    protected void setToUpdate(String userId) {
        setTimestampChange(new Timestamp(System.currentTimeMillis()));
        setUserChange(userId);
    }

    @JsonIgnore
    public Boolean isEntityChange() {
        return entityChange;
    }
}
