package de.vw.paso.core.domain;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class AbstractModifiableDTO<ID> extends AbstractDTO<ID> {

    private Timestamp timestampCreate;
    private Timestamp timestampChange;
    private String userCreate;
    private String userChange;
    private Boolean entityChange = false;

    public void setChange(String userId) {
        if (getId() == null || timestampCreate == null) {
            setToInsert(userId);
        } else {
            setToUpdate(userId);
        }

        entityChange = Boolean.TRUE;
    }

    protected void setToInsert(String userId) {
        final Timestamp now = new Timestamp(System.currentTimeMillis());

        this.timestampCreate = now;
        this.timestampChange = now;

        this.userCreate = userId;
        this.userChange = userId;

    }

    protected void setToUpdate(String userId) {
        this.timestampChange = new Timestamp(System.currentTimeMillis());
        this.userChange = userId;
    }

    public Boolean isEntityChange() {
        return entityChange;
    }

    public abstract ID getId();

    public abstract void setId(ID id);

}
