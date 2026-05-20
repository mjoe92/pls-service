package de.vw.paso.client.valueobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import de.vw.paso.service.user.UserDTO;
import de.vw.paso.service.usergroup.UserGroupDTO;
import de.vw.paso.service.vehicle.OwnedVehicleConfigDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserGroupVMO {

    private LongProperty userGroupIdProperty = new SimpleLongProperty(this, "userGroupId");
    private StringProperty userGroupNameProperty = new SimpleStringProperty(this, "userGroupName");
    private StringProperty brandProperty = new SimpleStringProperty(this, "brand");
    private BooleanProperty writeAccessProperty = new SimpleBooleanProperty(this, "writeAccess");
    private BooleanProperty selectedProperty = new SimpleBooleanProperty(this, "selected");
    private ListProperty<UserDTO> users = new SimpleListProperty<>(this, "user");
    private ListProperty<VehicleConfigDTO> vehicleConfigs = new SimpleListProperty<>(this, "vehicleConfigDTOList");
    private ListProperty<OwnedVehicleConfigDTO> ownedVehicleConfigs = new SimpleListProperty<>(this,
            "ownedVehicleConfigs");

    public Long getUserGroupId() {
        return userGroupIdProperty.getValue();
    }

    public void setUserGroupId(Long userGroupId) {
        this.userGroupIdProperty.set(userGroupId);
    }

    public boolean isSelected() {
        return selectedProperty.get();
    }

    public BooleanProperty selectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(boolean selectedProperty) {
        this.selectedProperty.set(selectedProperty);
    }

    public String getUserGroupName() {
        return userGroupNameProperty.getValue();
    }

    public StringProperty userGroupNameProperty() {
        return userGroupNameProperty;
    }

    public String getBrand() {
        return brandProperty.get();
    }

    public StringProperty brandProperty() {
        return brandProperty;
    }

    public void setBrand(String brandProperty) {
        this.brandProperty.set(brandProperty);
    }

    public boolean isWriteAccess() {
        return writeAccessProperty.getValue();
    }

    public BooleanProperty writeAccessProperty() {
        return writeAccessProperty;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupNameProperty.set(userGroupName);
    }

    public void setWriteAccess(boolean writeAccess) {
        this.writeAccessProperty.set(writeAccess);
    }

    public List<UserDTO> getUsers() {
        if (users.size() == 0) {
            return new ArrayList<>();
        }
        return users.getValue();
    }

    public void setUsers(List<UserDTO> users) {
        if (users != null) {
            this.users.set(FXCollections.observableList(users));
        }
    }

    public List<VehicleConfigDTO> getVehicleConfigs() {
        if (vehicleConfigs.size() == 0) {
            return new ArrayList<>();
        }
        return vehicleConfigs.getValue();
    }

    public void setVehicleConfigs(List<VehicleConfigDTO> vehicleConfigs) {
        if (vehicleConfigs != null) {
            this.vehicleConfigs.set(FXCollections.observableList(vehicleConfigs));
        }
    }

    public List<OwnedVehicleConfigDTO> getOwnedVehicleConfigs() {
        if (ownedVehicleConfigs.size() == 0) {
            return new ArrayList<>();
        }
        return ownedVehicleConfigs.getValue();
    }

    public void setOwnedVehicleConfigs(List<OwnedVehicleConfigDTO> vehicleConfigs) {
        if (vehicleConfigs != null) {
            this.ownedVehicleConfigs.set(FXCollections.observableList(vehicleConfigs));
        }
    }

    public static UserGroupVMO toVMO(UserGroupDTO userGroup) {
        UserGroupVMO userGroupVMO = new UserGroupVMO();
        userGroupVMO.setUserGroupId(userGroup.getId());
        userGroupVMO.setUserGroupName(userGroup.getName());
        userGroupVMO.setBrand(userGroup.getBrand());
        userGroupVMO.setWriteAccess(userGroup.isWriteAccess());
        userGroupVMO.selectedProperty.set(false);
        userGroupVMO.setUsers(userGroup.getUsers());
        userGroupVMO.setVehicleConfigs(userGroup.getVehicleConfigs());
        userGroupVMO.setOwnedVehicleConfigs(userGroup.getOwnedVehicleConfigs());

        return userGroupVMO;
    }

    public static List<UserGroupVMO> toVMOs(Collection<UserGroupDTO> userGroups) {
        return userGroups.stream().map(UserGroupVMO::toVMO).collect(Collectors.toList());
    }

    public static UserGroupDTO toUserGroup(UserGroupVMO userGroupVMO) {
        UserGroupDTO userGroup = new UserGroupDTO();
        userGroup.setId(userGroupVMO.getUserGroupId() == 0 ? null : userGroupVMO.getUserGroupId());
        userGroup.setName(userGroupVMO.getUserGroupName());
        userGroup.setBrand(userGroupVMO.getBrand());
        userGroup.setWriteAccess(userGroupVMO.isWriteAccess());
        userGroup.setUsers(userGroupVMO.getUsers());
        userGroup.setVehicleConfigs(userGroupVMO.getVehicleConfigs());
        userGroup.setOwnedVehicleConfigs(userGroupVMO.getOwnedVehicleConfigs());

        return userGroup;
    }
}
