package de.vw.paso.partlist.domain;

import java.util.ArrayList;
import java.util.List;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.vehicle.domain.VehicleConfig;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "SET_VERSION")
public class SetVersion extends AbstractModifiableEntity<Long> {

    @Id
    @Column(name = "SET_VERSION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SET_VERSION_NAME")
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
            mappedBy = "setVersion")
    private List<Product> products = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
            mappedBy = "setVersionId")
    private List<VehicleConfig> vehicleConfigs = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<VehicleConfig> getVehicleConfigs() {
        return vehicleConfigs;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setVehicleConfigs(List<VehicleConfig> vehicleConfigs) {
        this.vehicleConfigs = vehicleConfigs;
    }
}
