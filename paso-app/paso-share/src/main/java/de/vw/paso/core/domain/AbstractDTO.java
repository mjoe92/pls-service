package de.vw.paso.core.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractDTO<ID> implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract ID getId();

    public abstract void setId(ID id);

}
