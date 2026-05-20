package de.vw.paso.client.stueckliste.fzgkonfig.menu.item;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.service.PollingServiceController;
import de.vw.paso.client.stueckliste.fzgkonfig.menu.RefreshDatenstandEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.icon.FzgKonfigIcon;
import de.vw.paso.delegate.stueckliste.tiwhimport.TiWhImportRestClientHolder;
import de.vw.paso.service.tiwhimport.TiWhImportDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.status.ImportStatus;
import de.vw.paso.vehicle.VehicleConfigCategory;

@FXController(name = "fzg-konfig-stueckliste-menuitem")
public class FzgKonfigStuecklisteMenuItemController extends FzgKonfigMenuItemController {

  @FXML
  private Label labelFzg;
  @FXML
  private ImageView iconFzg;

  @FXML
  private Label labelMot;
  @FXML
  private ImageView iconMot;

  @FXML
  private Label labelGetr;
  @FXML
  private ImageView iconGetr;

  private ObjectBinding<Image> statusImageFzgBinding;
  private ObjectBinding<Image> statusImageMotBinding;
  private ObjectBinding<Image> statusImageGetrBinding;

  private final BooleanProperty pollFzg = new SimpleBooleanProperty();
  private final BooleanProperty pollMot = new SimpleBooleanProperty();
  private final BooleanProperty pollGetr = new SimpleBooleanProperty();

  @Override
  public void initialize(Integer index, VehicleConfigCategory vehicleConfigCategory,
    ObjectProperty<VehicleConfigDTO> vehicleConfig) {
    super.initialize(index, vehicleConfigCategory, vehicleConfig);

    labelFzg.textProperty().bind(getMenuItem().stuecklisteFzgBinding());
    iconFzg.imageProperty().bind(statusImageFzgBinding());
    labelMot.textProperty().bind(getMenuItem().stuecklisteMotBinding());
    iconMot.imageProperty().bind(statusImageMotBinding());
    labelGetr.textProperty().bind(getMenuItem().stuecklisteGetrBinding());
    iconGetr.imageProperty().bind(statusImageGetrBinding());

    getMenuItem().importStatusFzgProperty().addListener((obs, oldVal, newVal) -> updatePollFzgProperty(newVal));
    getMenuItem().importStatusMotProperty().addListener((obs, oldVal, newVal) -> updatePollMotProperty(newVal));
    getMenuItem().importStatusGetrProperty().addListener((obs, oldVal, newVal) -> updatePollGetrProperty(newVal));
  }

  @Override
  protected FzgKonfigStuecklisteMenuItem createMenuItem(Integer index, VehicleConfigCategory vehicleConfigCategory) {
    return new FzgKonfigStuecklisteMenuItem(index, vehicleConfigCategory);
  }

  @Override
  public FzgKonfigStuecklisteMenuItem getMenuItem() {
    return (FzgKonfigStuecklisteMenuItem) super.getMenuItem();
  }

  private void updatePollFzgProperty(ImportStatus newVal) {
    if (newVal != ImportStatus.REQUESTED) {
      setPollFzg(false);
    }
  }

  private void updatePollMotProperty(ImportStatus newVal) {
    if (newVal != ImportStatus.REQUESTED) {
      setPollMot(false);
    }
  }

  private void updatePollGetrProperty(ImportStatus newVal) {
    if (newVal != ImportStatus.REQUESTED) {
      setPollGetr(false);
    }
  }

  private ObjectBinding<Image> statusImageFzgBinding() {
    if (statusImageFzgBinding == null) {
      statusImageFzgBinding = Bindings.createObjectBinding(() -> {
        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        TiWhImportDTO tiWhImportFzg = vehicleConfig.getTiWhImportVehicle();
        if (tiWhImportFzg == null) {
          return null;
        }

        return getImportStatusImage(getMenuItem().getImportStatusFzg());
      }, getMenuItem().importStatusFzgProperty());
    }
    return statusImageFzgBinding;
  }

  private ObjectBinding<Image> statusImageMotBinding() {
    if (statusImageMotBinding == null) {
      statusImageMotBinding = Bindings.createObjectBinding(() -> {
        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        TiWhImportDTO tiWhImportMot = vehicleConfig.getTiWhImportMotor();
        if (tiWhImportMot == null) {
          return null;
        }
        return getImportStatusImage(getMenuItem().getImportStatusMot());
      }, getMenuItem().importStatusMotProperty());
    }
    return statusImageMotBinding;
  }

  private ObjectBinding<Image> statusImageGetrBinding() {
    if (statusImageGetrBinding == null) {
      statusImageGetrBinding = Bindings.createObjectBinding(() -> {
        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        TiWhImportDTO tiWhImportGetr = vehicleConfig.getTiWhImportGearbox();
        if (tiWhImportGetr == null) {
          return null;
        }
        return getImportStatusImage(getMenuItem().getImportStatusGetr());
      }, getMenuItem().importStatusGetrProperty());
    }
    return statusImageGetrBinding;
  }

  private Image getImportStatusImage(ImportStatus importStatus) {
    return switch (importStatus) {
      case REQUESTED -> FzgKonfigIcon.WAIT_16X16.getImage();
      case IMPORTED -> FzgKonfigIcon.OK_16X16.getImage();
      case NO_DATA -> FzgKonfigIcon.WARN_16X16.getImage();
      case ERROR -> FzgKonfigIcon.ERROR_16X16.getImage();
    };
  }

  private BooleanProperty pollFzgProperty() {
    return this.pollFzg;
  }

  private void setPollFzg(final boolean pollFzg) {
    this.pollFzgProperty().set(pollFzg);
  }

  private BooleanProperty pollMotProperty() {
    return this.pollMot;
  }

  private void setPollMot(final boolean pollMot) {
    this.pollMotProperty().set(pollMot);
  }

  private BooleanProperty pollGetrProperty() {
    return this.pollGetr;
  }

  private void setPollGetr(final boolean pollGetr) {
    this.pollGetrProperty().set(pollGetr);
  }

  @Override
  protected void poll() {
    pollDatenstand();

    pollFzgStuecklisteImportStatus();
    pollMotStuecklisteImportStatus();
    pollGetrStuecklisteImportStatus();
  }

  private void pollFzgStuecklisteImportStatus() {
    setPollFzg(false);
    VehicleConfigDTO vehicleConfig = getVehicleConfig();
    if (vehicleConfig.getTiWhImportVehicle() != null
      && vehicleConfig.getTiWhImportVehicle().getImportStatus() == ImportStatus.REQUESTED) {
      PollingServiceController<ImportStatus> serviceController = new PollingServiceController<>();
      serviceController.setOnSucceeded(e -> refreshDatenstandFzg(serviceController.getValue()));
      serviceController.setOnFailed(e -> handleException(serviceController.getException()));
      serviceController.setExecutionTime(WAIT_POLL);
      serviceController.pollWhile(importStatus -> ImportStatus.REQUESTED.getStatus() == importStatus.getStatus());

      serviceController.start(
        () -> TiWhImportRestClientHolder.getInstance().loadImportStatus(vehicleConfig.getTiWhImportVehicle().getId()));
      pollFzgProperty().bindBidirectional(serviceController.pollProperty());
    }
  }

  private void refreshDatenstandFzg(ImportStatus importStatus) {
    getMenuItem().setImportStatusFzg(importStatus);
    refreshDatenstand();
  }

  private void pollMotStuecklisteImportStatus() {
    setPollMot(false);
    VehicleConfigDTO vehicleConfig = getVehicleConfig();
    if (vehicleConfig.getTiWhImportMotor() != null
      && vehicleConfig.getTiWhImportMotor().getImportStatus() == ImportStatus.REQUESTED) {
      PollingServiceController<ImportStatus> serviceController = new PollingServiceController<>();
      serviceController.setOnSucceeded(e -> refreshDatenstandMot(serviceController.getValue()));
      serviceController.setOnFailed(e -> handleException(serviceController.getException()));
      serviceController.setExecutionTime(WAIT_POLL);
      serviceController.pollWhile(importStatus -> ImportStatus.REQUESTED.getStatus() == importStatus.getStatus());
      serviceController.start(
        () -> TiWhImportRestClientHolder.getInstance().loadImportStatus(vehicleConfig.getTiWhImportMotor().getId()));
      pollMotProperty().bindBidirectional(serviceController.pollProperty());
    }
  }

  private void refreshDatenstandMot(ImportStatus importStatus) {
    getMenuItem().setImportStatusMot(importStatus);
    refreshDatenstand();
  }

  private void pollGetrStuecklisteImportStatus() {
    setPollGetr(false);
    VehicleConfigDTO vehicleConfig = getVehicleConfig();
    if (vehicleConfig.getTiWhImportGearbox() != null
      && vehicleConfig.getTiWhImportGearbox().getImportStatus() == ImportStatus.REQUESTED) {
      PollingServiceController<ImportStatus> serviceController = new PollingServiceController<>();
      serviceController.setOnSucceeded(e -> refreshDatenstandGetr(serviceController.getValue()));
      serviceController.setOnFailed(e -> handleException(serviceController.getException()));
      serviceController.setExecutionTime(WAIT_POLL);
      serviceController.pollWhile(importStatus -> ImportStatus.REQUESTED.getStatus() == importStatus.getStatus());
      serviceController.start(
        () -> TiWhImportRestClientHolder.getInstance().loadImportStatus(vehicleConfig.getTiWhImportGearbox().getId()));
      pollGetrProperty().bindBidirectional(serviceController.pollProperty());
    }
  }

  private void refreshDatenstandGetr(ImportStatus importStatus) {
    getMenuItem().setImportStatusGetr(importStatus);
    refreshDatenstand();
  }

  private void refreshDatenstand() {
    EventBus.getInstance().post(new RefreshDatenstandEvent());
  }
}
