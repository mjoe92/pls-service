package de.vw.paso.pr;

import de.vw.paso.core.domain.AbstractEntity;
import de.vw.paso.service.masterdata.prnumber.PrNumberNameProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PR_NUMBER")
public final class PrNumber extends AbstractEntity<Long> implements PrNumberNameProvider {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", columnDefinition = "char(3)", nullable = false, updatable = false)
    private String name;

    @Column(name = "DESCRIPTION_DE", updatable = false)
    private String descriptionDe;

    @Column(name = "DESCRIPTION_EN", updatable = false)
    private String descriptionEn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PR_FAMILY_ID", nullable = false, updatable = false)
    private PrNumberFamily prNumberFamily;

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescriptionDe() {
        return descriptionDe;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public PrNumberFamily getPrNumberFamily() {
        return prNumberFamily;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescriptionDe(String descriptionDe) {
        this.descriptionDe = descriptionDe;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public void setPrNumberFamily(PrNumberFamily prNumberFamily) {
        this.prNumberFamily = prNumberFamily;
    }
}
