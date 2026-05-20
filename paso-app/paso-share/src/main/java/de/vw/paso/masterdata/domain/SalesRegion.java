package de.vw.paso.masterdata.domain;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.utility.StringConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SALES_REGION")
public final class SalesRegion extends AbstractModifiableEntity<String> {

    @Id
    @Column(name = "SALES_REGION_ID", nullable = false, columnDefinition = "char(3)")
    private String id;

    @Column(name = "RELEVANT", nullable = false, columnDefinition = "int(1)")
    private Integer relevant = 0;

    @Column(name = "DESCRIPTION_EN", nullable = false)
    private String descriptionEn = StringConstant.EMPTY;

    @Column(name = "DESCRIPTION_DE", nullable = false)
    private String descriptionDe;

    public String getId() {
        return id;
    }

    public Integer getRelevant() {
        return relevant;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public String getDescriptionDe() {
        return descriptionDe;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRelevant(Integer relevant) {
        this.relevant = relevant;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public void setDescriptionDe(String descriptionDe) {
        this.descriptionDe = descriptionDe;
    }
}
