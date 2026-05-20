package de.vw.paso.service.partlist.smartfix;

import java.io.Serializable;

public class SmartFixDTO implements Serializable {

    private Long id;
    private String name;
    private boolean active;
    private String field;
    private String oldValue;
    private String newValue;
    private String description;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public String getField() {
        return field;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
