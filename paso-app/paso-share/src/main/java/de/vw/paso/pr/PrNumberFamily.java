package de.vw.paso.pr;

import java.util.ArrayList;
import java.util.List;

import de.vw.paso.core.domain.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "PR_NUMBER_FAMILY")
public final class PrNumberFamily extends AbstractEntity<Long> {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", columnDefinition = "char", nullable = false, updatable = false)
    private String name;

    @Column(name = "DESCRIPTION_DE", updatable = false)
    private String descriptionDe;

    @Column(name = "DESCRIPTION_EN", updatable = false)
    private String descriptionEn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "prNumberFamily",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @Transient
    private List<PrNumber> prNumbers = new ArrayList<>();

    @Override
    @Transient
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public List<PrNumber> getPrNumbers() {
        return prNumbers;
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

    public void setPrNumbers(List<PrNumber> prNumbers) {
        this.prNumbers = prNumbers;
    }
}
