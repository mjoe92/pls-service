package de.vw.paso.core.domain;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity<ID> implements Serializable {

    public abstract ID getId();

    public abstract void setId(ID id);
}