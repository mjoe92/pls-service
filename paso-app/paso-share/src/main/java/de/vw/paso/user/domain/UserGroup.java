package de.vw.paso.user.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.vehicle.domain.VehicleConfig;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "USER_GROUP")
public class UserGroup extends AbstractModifiableEntity<Long> {

    public static final String USER_ID = "USER_ID";
    public static final String USER_GROUP_ID = "USER_GROUP_ID";
    public static final String VEHICLE_CONFIG_ID = "VEHICLE_CONFIG_ID";

    @Id
    @Column(name = USER_GROUP_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BRAND")
    private String brand;

    @Column(name = "USER_GROUP_NAME")
    private String name;

    @Column(name = "WRITE_ACCESS")
    private boolean writeAccess;

    @JoinTable(name = "USER_GROUP_USER",
            joinColumns = { @JoinColumn(name = USER_GROUP_ID, referencedColumnName = USER_GROUP_ID) },
            inverseJoinColumns = { @JoinColumn(name = USER_ID, referencedColumnName = USER_ID) })
    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @JoinTable(name = "USER_GROUP_VEHICLE_CONFIG",
            joinColumns = { @JoinColumn(name = USER_GROUP_ID, referencedColumnName = USER_GROUP_ID) },
            inverseJoinColumns = { @JoinColumn(name = VEHICLE_CONFIG_ID, referencedColumnName = VEHICLE_CONFIG_ID) })
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<VehicleConfig> vehicleConfigs = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ownerGroup")
    private Set<VehicleConfig> ownedVehicleConfigs = new HashSet<>();

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long aLong) {
        this.id = aLong;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public void setVehicleConfigs(Collection<VehicleConfig> vehicleConfigs) {
        this.vehicleConfigs.clear();
        this.vehicleConfigs.addAll(vehicleConfigs);
    }

    public void setOwnedVehicleConfigs(Collection<VehicleConfig> ownedVehicleConfigs) {
        this.ownedVehicleConfigs.clear();
        this.ownedVehicleConfigs.addAll(ownedVehicleConfigs);
    }

    public String getBrand() {
        return brand;
    }

    public String getName() {
        return name;
    }

    public boolean isWriteAccess() {
        return writeAccess;
    }

    public List<User> getUsers() {
        return users;
    }

    public Set<VehicleConfig> getVehicleConfigs() {
        return vehicleConfigs;
    }

    public Set<VehicleConfig> getOwnedVehicleConfigs() {
        return ownedVehicleConfigs;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWriteAccess(boolean writeAccess) {
        this.writeAccess = writeAccess;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setVehicleConfigs(Set<VehicleConfig> vehicleConfigs) {
        this.vehicleConfigs = vehicleConfigs;
    }
}
