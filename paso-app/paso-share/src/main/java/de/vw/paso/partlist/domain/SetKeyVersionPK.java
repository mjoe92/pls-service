package de.vw.paso.partlist.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SetKeyVersionPK {

    private static final String PK_SET_KEY = "SET_KEY";
    private static final String PK_VERSION = "SET_VERSION_ID";

    @Column(name = PK_SET_KEY, columnDefinition = "char(" + SetKey.SET_KEY_MAX_LENGTH
        + ")", nullable = false, updatable = false)
    private String setKey;

    @Column(name = PK_VERSION, nullable = false, updatable = false)
    private Long version;

    public String getSetKey() {
        return setKey;
    }

    public Long getVersion() {
        return version;
    }

    public void setSetKey(String setKey) {
        this.setKey = setKey;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SetKeyVersionPK that = (SetKeyVersionPK) o;
        return Objects.equals(setKey, that.setKey) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(setKey, version);
    }
}
