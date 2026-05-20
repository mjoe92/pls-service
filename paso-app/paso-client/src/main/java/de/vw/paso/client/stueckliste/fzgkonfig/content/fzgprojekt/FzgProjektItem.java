package de.vw.paso.client.stueckliste.fzgkonfig.content.fzgprojekt;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;

public class FzgProjektItem {

    private final VehicleProjectDTO vehicleProject;

    private BooleanProperty selected;

    public FzgProjektItem(final VehicleProjectDTO vehicleProject) {
        this.vehicleProject = vehicleProject;
    }

    public VehicleProjectDTO getVehicleProject() {
        return vehicleProject;
    }

    public Long getId() {
        return getVehicleProject().getId();
    }

    public String getProjectName() {
        return getVehicleProject().getProjectName();
    }

    public String getProjectDescription() {
        return getVehicleProject().getDescription();
    }

    public String getProductKey() {
        return getVehicleProject().getProductKey();
    }

    public String getSalesKey() {
        return getVehicleProject().getSalesKey();
    }

    public Integer getFirstModelYear() {
        return getVehicleProject().getFirstModelYear();
    }

    public String getPlatform() {
        return getVehicleProject().getPlatform();
    }

    final BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new SimpleBooleanProperty();
        }

        return selected;
    }

    public final boolean isSelected() {
        return selectedProperty().get();
    }

    public final void setSelected(final boolean selected) {
        selectedProperty().set(selected);
    }

}
