package de.vw.paso.client.stueckliste.efs.views.compare;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import de.vw.paso.service.vehicle.VehicleConfigDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class ComparePartListItem {

    @Getter
    @Setter
    private VehicleConfigDTO vehicleConfig;

    private BooleanProperty selectedProperty;

    public ComparePartListItem(VehicleConfigDTO vc) {
        vehicleConfig = vc;
    }

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
