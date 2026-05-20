package de.vw.paso.client.valueobject;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.service.right.RoleDTO;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.utility.StringConstant;

public class UserVMO {

    private final StringProperty userIdProperty = new SimpleStringProperty(this, "userId");
    private final StringProperty firstNameProperty = new SimpleStringProperty(this, "firstName");
    private final StringProperty lastNameProperty = new SimpleStringProperty(this, "lastName");
    private final StringProperty emailProperty = new SimpleStringProperty(this, "email");
    private final StringProperty rolesProperty = new SimpleStringProperty(this, "roles");
    private final BooleanProperty activeProperty = new SimpleBooleanProperty(this, "active");
    private final BooleanProperty selectedProperty = new SimpleBooleanProperty(this, "selected");
    private final StringProperty inactivityInfoProperty = new SimpleStringProperty(this, "inactivityInfo");

    public final String getUserId() {
        return userIdProperty.getValue();
    }

    public final void setUserId(String value) {
        userIdProperty.setValue(value);
    }

    public final StringProperty userIdProperty() {
        return userIdProperty;
    }

    public final String getFirstName() {
        return firstNameProperty.getValue();
    }

    public final StringProperty firstNameProperty() {
        return firstNameProperty;
    }

    public final String getLastName() {
        return lastNameProperty.getValue();
    }

    public final StringProperty lastNameProperty() {
        return lastNameProperty;
    }

    public final String getEmail() {
        return emailProperty.getValue();
    }

    public final StringProperty emailProperty() {
        return emailProperty;
    }

    public final StringProperty rolesProperty() {
        return rolesProperty;
    }

    public final boolean isSelected() {
        return selectedProperty().get();
    }

    public final void setSelected(boolean selected) {
        selectedProperty().set(selected);
    }

    public final BooleanProperty selectedProperty() {
        return selectedProperty;
    }

    public final boolean isActive() {
        return activeProperty().get();
    }

    public final void setActive(boolean active) {
        activeProperty().set(active);
    }

    public final BooleanProperty activeProperty() {
        return activeProperty;
    }

    public String getInactivityInfo() {
        return inactivityInfoProperty.getValue();
    }

    public StringProperty inactivityInfoProperty() {
        return inactivityInfoProperty;
    }

    public static UserVMO toVMO(UserDTO user) {
        UserVMO vmo = new UserVMO();
        vmo.userIdProperty.set(user.getId());
        vmo.firstNameProperty.set(user.getFirstName());
        vmo.lastNameProperty.set(user.getLastName());
        vmo.emailProperty.set(user.getEmail());
        vmo.activeProperty.set(user.getActive());
        vmo.selectedProperty.set(false);
        vmo.inactivityInfoProperty.set(user.getInactivityInfo());

        String roles = user.getRoles().stream().map(RoleDTO::getName).sorted()
                .collect(Collectors.joining(StringConstant.COMMA_SPACE));
        vmo.rolesProperty.set(roles);

        return vmo;
    }

    public static List<UserVMO> toVMOs(Collection<UserDTO> user) {
        return user.stream().map(UserVMO::toVMO).collect(Collectors.toList());
    }
}