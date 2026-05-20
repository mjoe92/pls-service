package de.vw.paso.partlist.domain.smartfix;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = SmartFix.TABLE_NAME)
public class SmartFix implements Serializable {

    public final static String TABLE_NAME = "smart_fix";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_ACTIVE = "active";
    public final static String COLUMN_FIELD = "field";
    public final static String COLUMN_OLD_VALUE = "old_value";
    public final static String COLUMN_NEW_VALUE = "new_value";
    public final static String COLUMN_DESCRIPTION = "description";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COLUMN_NAME)
    private String name;

    @Column(name = COLUMN_ACTIVE, columnDefinition = "int(1)")
    private boolean active;

    @Column(name = COLUMN_FIELD)
    private String field;

    @Column(name = COLUMN_OLD_VALUE)
    private String oldValue;

    @Column(name = COLUMN_NEW_VALUE)
    private String newValue;

    @Column(name = COLUMN_DESCRIPTION)
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