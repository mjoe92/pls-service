package de.vw.paso.model;

import java.util.Date;

import de.vw.paso.core.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "MODEL")
public final class Model extends AbstractEntity<Long> {

    @Id
    @Column(name = "MODEL_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MODEL_IMPORT_ID", nullable = false, updatable = false)
    private ModelImport modelImport;

    @Column(name = "MODEL_KEY", length = 6, nullable = false, updatable = false)
    private String modelKey;

    @Column(name = "DESCRIPTION", nullable = false, updatable = false)
    private String description;

    @Column(name = "STATUS", length = 10, nullable = false, updatable = false)
    private String status;

    @Column(name = "MODEL_VERSION", length = 2, nullable = false, updatable = false)
    private String modelVersion;

    @Column(name = "BEGIN_DATE", nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private Date beginDate;

    @Column(name = "END_DATE", nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Override
    public Long getId() {
        return id;
    }

    public ModelImport getModelImport() {
        return modelImport;
    }

    public String getModelKey() {
        return modelKey;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setModelImport(ModelImport modelImport) {
        this.modelImport = modelImport;
    }

    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
