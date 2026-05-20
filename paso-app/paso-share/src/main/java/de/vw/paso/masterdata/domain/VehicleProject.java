package de.vw.paso.masterdata.domain;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.masterdata.Brand;
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
import jakarta.persistence.Table;

@Entity
@Table(name = "VEHICLE_PROJECT")
public final class VehicleProject extends AbstractModifiableEntity<Long> {

    private static final String COLUMN_PRODUCT_KEY = "PRODUCT_KEY";

    @Id
    @Column(name = "VEHICLE_PROJECT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PROJECT_NAME", nullable = false, unique = true)
    private String projectName;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = COLUMN_PRODUCT_KEY, nullable = false)
    private String productKey;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = COLUMN_PRODUCT_KEY, insertable = false, updatable = false)
    private Product product;

    @Column(name = "SALES_KEY", nullable = false)
    private String salesKey;

    @Column(name = "FIRST_MODEL_YEAR", columnDefinition = "int(4)")
    private Integer firstModelYear;

    @Column(name = "PLATFORM", nullable = false)
    private String platform;

    @Enumerated(EnumType.STRING)
    @Column(name = "BRAND_CODE", nullable = false, columnDefinition = "char(2)")
    private Brand brandCode;

    @Column(name = "ARCHIVE", nullable = false)
    private boolean archive;

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getProductKey() {
        return productKey;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    public Product getProduct() {
        return product;
    }

    public String getSalesKey() {
        return salesKey;
    }

    public Integer getFirstModelYear() {
        return firstModelYear;
    }

    public String getPlatform() {
        return platform;
    }

    public Brand getBrandCode() {
        return brandCode;
    }

    public boolean isArchive() {
        return archive;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setSalesKey(String salesKey) {
        this.salesKey = salesKey;
    }

    public void setFirstModelYear(Integer firstModelYear) {
        this.firstModelYear = firstModelYear;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setBrandCode(Brand brandCode) {
        this.brandCode = brandCode;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }
}
