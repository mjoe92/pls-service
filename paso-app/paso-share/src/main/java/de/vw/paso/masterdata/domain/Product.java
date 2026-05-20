package de.vw.paso.masterdata.domain;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.partlist.domain.SetVersion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRODUCT")
public final class Product extends AbstractModifiableEntity<String> {

    private static final String COLUMN_SET_VERSION_ID = "SET_VERSION_ID";

    public static final long INITIAL_SET_VERSION_ID = 1L;

    @Id
    @Column(name = "PRODUCT_KEY", unique = true)
    private String productKey;

    @Column(name = "PRODUCT_TYPE")
    private String productType;

    @Column(name = COLUMN_SET_VERSION_ID, nullable = false)
    private Long setVersionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = COLUMN_SET_VERSION_ID, insertable = false, updatable = false)
    private SetVersion setVersion;

    @Override
    public String getId() {
        return productKey;
    }

    @Override
    public void setId(String productKey) {
        this.productKey = productKey;
    }

    public String getProductKey() {
        return productKey;
    }

    public String getProductType() {
        return productType;
    }

    public Long getSetVersionId() {
        return setVersionId;
    }

    public SetVersion getSetVersion() {
        return setVersion;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setSetVersionId(Long setVersionId) {
        this.setVersionId = setVersionId;
    }

    public void setSetVersion(SetVersion setVersion) {
        this.setVersion = setVersion;
    }
}
