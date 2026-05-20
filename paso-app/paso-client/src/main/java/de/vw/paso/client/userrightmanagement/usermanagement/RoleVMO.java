package de.vw.paso.client.userrightmanagement.usermanagement;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import de.vw.paso.service.right.RoleDTO;
import lombok.Getter;
import lombok.Setter;

public class RoleVMO {

    @Getter
    @Setter
    private RoleDTO role;

    private BooleanProperty selectedProperty;

    final BooleanProperty selectedProperty() {
        if (selectedProperty == null) {
            selectedProperty = new SimpleBooleanProperty(false);
        }

        return selectedProperty;
    }

    public final boolean isSelected() {
        return selectedProperty().get();
    }

    public final void setSelected(final boolean selected) {
        selectedProperty().set(selected);
    }
}
