package de.vw.paso.client.stueckliste.fzgkonfig.menu.item;

import java.io.Serializable;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.fzgkonfig.menu.status.VehicleConfigCategoryRegistry;
import de.vw.paso.service.modelimport.ModelDTO;
import de.vw.paso.service.vehicle.VehicleConfigCategoryStatusDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;
import de.vw.paso.vehicle.VehicleConfigCategory;
import de.vw.paso.vehicle.VehicleConfigStatus;

public class FzgKonfigKategorieMenuItem {

    private final Integer index;
    private ReadOnlyIntegerProperty menuIndex;

    private final VehicleConfigCategory vehicleConfigCategory;
    private ReadOnlyObjectProperty<VehicleConfigCategory> category;
    protected ReadOnlyStringProperty categoryText;

    private final ObjectProperty<VehicleConfigDTO> vehicleConfig = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            onVehicleConfigChanged();
        }
    };

    private ObjectProperty<VehicleConfigStatus> vehicleConfigStatus;

    private StringProperty infoText;
    private StringBinding nameBinding;
    private StringBinding modellBinding;
    private StringBinding configruationBinding;

    FzgKonfigKategorieMenuItem(Integer index, VehicleConfigCategory vehicleConfigCategory) {
        this.index = index;
        this.vehicleConfigCategory = vehicleConfigCategory;
    }

    public void updateVehicleConfigStatus(VehicleConfigStatus vehicleConfigStatus) {
        setVehicleConfigStatus(vehicleConfigStatus);

        for (VehicleConfigCategoryStatusDTO categoryStatus : getVehicleConfig().getVehicleConfigCategoryStatus()) {
            if (getVehicleConfigCategory() == categoryStatus.getVehicleConfigCategory()) {
                categoryStatus.setVehicleConfigStatus(vehicleConfigStatus);
                return;
            }
        }
    }

    public final VehicleConfigDTO getVehicleConfig() {
        return vehicleConfigProperty().get();
    }

    public VehicleConfigCategory getVehicleConfigCategory() {
        return vehicleConfigCategoryProperty().get();
    }

    public final ObjectProperty<VehicleConfigStatus> vehicleConfigStatusProperty() {
        if (vehicleConfigStatus == null) {
            vehicleConfigStatus = new SimpleObjectProperty<>(VehicleConfigStatus.INITIAL);
        }

        return vehicleConfigStatus;
    }

    public final VehicleConfigStatus getVehicleConfigStatus() {
        return vehicleConfigStatusProperty().get();
    }

    public final void setVehicleConfigStatus(final VehicleConfigStatus vehicleConfigStatus) {
        vehicleConfigStatusProperty().set(vehicleConfigStatus);
    }

    protected final ObjectProperty<VehicleConfigDTO> vehicleConfigProperty() {
        return vehicleConfig;
    }

    protected final ReadOnlyIntegerProperty menuIndexProperty() {
        if (menuIndex == null) {
            menuIndex = new SimpleIntegerProperty(index);
        }

        return menuIndex;
    }

    protected final ReadOnlyObjectProperty<VehicleConfigCategory> vehicleConfigCategoryProperty() {
        if (category == null) {
            category = new SimpleObjectProperty<>(vehicleConfigCategory);
        }

        return category;
    }

    protected ReadOnlyStringProperty categoryTextProperty() {
        if (categoryText == null) {
            String resBundleStr =
                    "header." + VehicleConfigCategoryRegistry.getControllerClass(getVehicleConfigCategory())
                            .getSimpleName().replace("Controller", StringConstant.EMPTY).toLowerCase();
            categoryText = new SimpleStringProperty(I18N.getString(resBundleStr));
        }

        return categoryText;
    }

    final StringProperty infoTextProperty() {
        if (infoText == null) {
            infoText = new SimpleStringProperty();
        }

        return infoText;
    }

    private void onVehicleConfigChanged() {
        bindInfoText();
    }

    private void bindInfoText() {
        switch (getVehicleConfigCategory()) {
            case FZG_PROJEKT:
                infoTextProperty().bind(nameBinding());
                break;
            case MODELL:
                infoTextProperty().bind(modellBinding());
                break;
            case KONFIGURATION:
                infoTextProperty().bind(configurationBinding());
                break;
            default:
                break;
        }
    }

    private StringBinding nameBinding() {
        if (nameBinding == null) {
            nameBinding = Bindings.createStringBinding(() -> getVehicleConfig().getVehicleProject().getProjectName(),
                    vehicleConfigProperty());
        }

        return nameBinding;
    }

    private StringBinding modellBinding() {
        if (modellBinding == null) {
            modellBinding = Bindings.createStringBinding(this::createModelDescription, vehicleConfigProperty(),
                    vehicleConfigStatusProperty());
        }

        return modellBinding;
    }

    private String createModelDescription() {
        VehicleConfigStatus menuStatus = getVehicleConfigStatus();

        if (menuStatus == VehicleConfigStatus.WAIT) {
            return I18N.getString("import.modelle.angefordert");
        }

        ModelDTO model = getVehicleConfig().getModel();
        return model == null ? I18N.getString("modell.ohne") : model.getDescription();
    }

    private StringBinding configurationBinding() {
        if (configruationBinding == null) {
            configruationBinding = Bindings.createStringBinding(this::createPrNumberCountDescription,
                    vehicleConfigProperty());
        }

        return configruationBinding;
    }

    private String createPrNumberCountDescription() {
        String prNumberString = getVehicleConfig().getPrNumberString();

        Serializable prNumbers = prNumberString == null ? " 0" : (prNumberString.length() / 4 + 1);
        return I18N.getString("prnummern") + prNumbers;
    }
}
