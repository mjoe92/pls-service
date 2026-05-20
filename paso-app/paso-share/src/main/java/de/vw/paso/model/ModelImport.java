package de.vw.paso.model;

import java.util.HashSet;
import java.util.Set;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.masterdata.domain.SalesRegion;
import de.vw.paso.status.ImportStatus;
import de.vw.paso.tiwh.IDatenstand;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "MODEL_IMPORT")
public final class ModelImport extends AbstractModifiableEntity<Long> implements IDatenstand {

    @Id
    @Column(name = "MODEL_IMPORT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SALES_KEY", columnDefinition = "char(2)", nullable = false, updatable = false)
    private String salesKey;

    @Column(name = "MODEL_YEAR", columnDefinition = "int(4)", nullable = false, updatable = false)
    private Integer modelYear;

    @Column(name = "IMPORT_STATUS", length = 11, nullable = false, columnDefinition = "varchar(11)")
    @Enumerated(EnumType.STRING)
    private ImportStatus importStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SALES_REGION_ID", nullable = false, updatable = false)
    private SalesRegion salesRegion;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "modelImport",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Set<Model> models = new HashSet<>();

    @Override
    public Long getId() {
        return id;
    }

    public String getSalesKey() {
        return salesKey;
    }

    public Integer getModelYear() {
        return modelYear;
    }

    @Override
    public ImportStatus getImportStatus() {
        return importStatus;
    }

    public SalesRegion getSalesRegion() {
        return salesRegion;
    }

    public Set<Model> getModels() {
        return models;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setSalesKey(String salesKey) {
        this.salesKey = salesKey;
    }

    public void setModelYear(Integer modelYear) {
        this.modelYear = modelYear;
    }

    @Override
    public void setImportStatus(ImportStatus importStatus) {
        this.importStatus = importStatus;
    }

    public void setSalesRegion(SalesRegion salesRegion) {
        this.salesRegion = salesRegion;
    }

    public void setModels(Set<Model> models) {
        this.models = models;
    }
}
