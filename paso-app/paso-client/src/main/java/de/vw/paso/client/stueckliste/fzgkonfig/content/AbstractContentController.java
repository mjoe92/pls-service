package de.vw.paso.client.stueckliste.fzgkonfig.content;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.validation.Validations;
import de.vw.paso.client.validation.Validator;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.vehicle.VehicleConfigCategory;

public abstract class AbstractContentController extends BaseController<BorderPane> {

  @FXML
  private BorderPane contentPane;

  @FXML
  private ErrorMessagePaneController errorMessagePaneController;

  protected VehicleConfigCategory vehicleConfigCategory;

  private Validations validations;
  private BooleanProperty dirtyProperty;

  private final ObjectProperty<VehicleConfigDTO> vehicleConfig = new SimpleObjectProperty<>() {
    @Override
    protected void invalidated() {
      onVehicleConfigChanged();
    }
  };

  public final void addValidator(final Validator<?> validator) {
    getValidations().addValidator(validator);
  }

  public final BooleanProperty dirtyProperty() {
    if (dirtyProperty == null) {
      dirtyProperty = new SimpleBooleanProperty(false);
    }

    return dirtyProperty;
  }

  @Override
  public BorderPane getControl() {
    return contentPane;
  }

  @Override
  public Parent getStyleableParent() {
    return getControl();
  }

  public final VehicleConfigDTO getVehicleConfig() {
    return vehicleConfigProperty().get();
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    super.initialize(location, resources);

    errorMessagePaneController.errorMessageProperty().bind(getValidations().errorMessageProperty());
  }

  public final void setVehicleConfigCategory(VehicleConfigCategory vehicleConfigCategory) {
    this.vehicleConfigCategory = vehicleConfigCategory;
  }

  public final ReadOnlyBooleanProperty validProperty() {
    return getValidations().validProperty();
  }

  public final ObjectProperty<VehicleConfigDTO> vehicleConfigProperty() {
    return vehicleConfig;
  }

  protected final boolean isEditable() {
    return getVehicleConfig().isEditable();
  }

  protected abstract void onVehicleConfigChanged();

  protected final void setDirty() {
    if (!isEditable()) {
      return;
    }

    forceDirty();
  }

  protected final void forceDirty() {
    dirtyProperty().set(true);
  }

  private Validations getValidations() {
    if (validations == null) {
      validations = new Validations();
    }

    return validations;
  }
}
