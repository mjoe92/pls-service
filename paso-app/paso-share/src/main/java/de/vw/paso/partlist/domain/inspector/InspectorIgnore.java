package de.vw.paso.partlist.domain.inspector;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(InspectorIgnorePK.class)
@Table(name = "efs_inspector_ignore")
public class InspectorIgnore implements Serializable {

    @Id
    @Column(name = "INSPECTOR_ENTRY_TYPE", columnDefinition = "varchar(50)")
    @Enumerated(EnumType.STRING)
    private InspectorEntryType type;

    @Id
    @Column(name = "EFS_ELEMENT_ID")
    private Long efsElementId;

    public InspectorEntryType getType() {
        return type;
    }

    public Long getEfsElementId() {
        return efsElementId;
    }

    public void setType(InspectorEntryType type) {
        this.type = type;
    }

    public void setEfsElementId(Long efsElementId) {
        this.efsElementId = efsElementId;
    }
}
