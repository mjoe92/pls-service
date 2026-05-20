package de.vw.paso.pr;

import java.time.LocalDate;

import de.vw.paso.core.domain.AbstractEntity;
import de.vw.paso.masterdata.domain.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ZO_PR_NUMBER_PKZ")
public class PrNumberAssignment extends AbstractEntity<Long> {

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PKZ", nullable = false, updatable = false)
    private Product product;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "PR_NUMBER_ID", nullable = false, updatable = false)
    private PrNumber prNumber;

    @Column(name = "EINSATZ", updatable = false)
    private LocalDate startDate;

    @Column(name = "EINSATZSCHL", columnDefinition = "char(11)", updatable = false)
    private String startKey;

    @Column(name = "ENTFALL", updatable = false)
    private LocalDate endDate;

    @Column(name = "ENTFALLSCHL", columnDefinition = "char(11)", updatable = false)
    private String endKey;

    @Column(name = "DESCRIPTION", columnDefinition = "char(255)", updatable = false)
    private String description;

    @Column(name = "STATUS", columnDefinition = "char(3)", updatable = false)
    private String status;

    @Override
    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public PrNumber getPrNumber() {
        return prNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getStartKey() {
        return startKey;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getEndKey() {
        return endKey;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setPrNumber(PrNumber prNumber) {
        this.prNumber = prNumber;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setStartKey(String startKey) {
        this.startKey = startKey;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setEndKey(String endKey) {
        this.endKey = endKey;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
