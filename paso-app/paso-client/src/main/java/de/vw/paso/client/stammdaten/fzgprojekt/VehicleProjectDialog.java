package de.vw.paso.client.stammdaten.fzgprojekt;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.valueobject.VehicleProjectVMO;
import de.vw.paso.delegate.stammdaten.setversion.SetVersionRestClientHolder;
import de.vw.paso.delegate.stammdaten.vehicleprojct.VehicleProjectRestClientHolder;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;
import de.vw.paso.service.masterdata.vehicleproject.UpdatedVehicleProjectSetVersionDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;

public class VehicleProjectDialog extends BaseDialogController<UpdatedVehicleProjectSetVersionDTO> {

  private static String TITLE = "update.setversion.title";
  private static String UPDATE_LABEL = "update.setversion.label";
  private static String WARNING_LABEL = "update.setversion.warning";

  private final UpdatedVehicleProjectSetVersionDTO vehicleProjectDTO;

  private final ComboBox<String> possibleDefaultSetVersions;

  private final Label label;

  private final VehicleProjectVMO originalValue;

  private List<String> setVersions;

  public VehicleProjectDialog(VehicleProjectVMO project, List<String> possibleVehicleProjectCollisions) {
    Future<List<String>> setVersionFuture = loadSetVersions();

    vehicleProjectDTO = new UpdatedVehicleProjectSetVersionDTO();
    vehicleProjectDTO.setSetVersion(project.setVersionNameProperty().getValue());
    vehicleProjectDTO.setVehicleProjectId(project.getId());
    originalValue = project;

    String title = I18N.getString(TITLE);
    String labelString = I18N.getString(UPDATE_LABEL);

    label = new Label(labelString);

    try {
      setVersions = setVersionFuture.get();
      possibleDefaultSetVersions = new ComboBox<>(FXCollections.observableList(setVersions));
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }

    super.initialize(title, () -> {
      grid.setHgap(2);
      grid.setVgap(2);
      grid.setPadding(new Insets(20, 20, 20, 20));

      if (possibleDefaultSetVersions.getItems().stream()
        .anyMatch(item -> item.equals(vehicleProjectDTO.getSetVersion()))) {
        possibleDefaultSetVersions.setValue(originalValue.setVersionNameProperty().getValue());
      }

      possibleDefaultSetVersions.valueProperty().addListener(getValidationListener());

      addLabelAndInputFieldToGrid(label, possibleDefaultSetVersions, 2);

      //TODO: solve this better
      if (!possibleVehicleProjectCollisions.isEmpty()) {
        String warning = String.join(", ", possibleVehicleProjectCollisions);

        if (warning.length() > 20) {
          warning = warning.substring(0, 20) + "...";
        }

        addLabelAndInputFieldToGrid(I18N.getString(WARNING_LABEL), new Label(warning), 2);
      }
    });
  }

  @Override
  protected ChangeListener<String> getValidationListener() {
    return (observable, oldValue, newValue) -> commitButton.setDisable(
      possibleDefaultSetVersions.getValue().isEmpty() || possibleDefaultSetVersions.getValue()
        .equals(originalValue.setVersionNameProperty().getValue()));
  }

  @Override
  protected ListChangeListener getValidationListenerForList() {
    return null;
  }

  @Override
  protected boolean isInvalid() {
    return false;
  }

  @Override
  protected UpdatedVehicleProjectSetVersionDTO dialogResult() {
    vehicleProjectDTO.setSetVersion(possibleDefaultSetVersions.getValue());
    return vehicleProjectDTO;
  }

  private Future<List<String>> loadSetVersions() {
    try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
      return executorService.submit(
        () -> setVersions = SetVersionRestClientHolder.getInstance().loadSetVersions().setVersions().stream()
          .map(SetVersionDTO::getName).toList());
    }
  }

  public static void showDialog(VehicleProjectVMO projectVMO, Runnable onSucceedHandler,
    List<String> possibleVehicleProjectCollisions) {
    VehicleProjectDialog dialog = new VehicleProjectDialog(projectVMO, possibleVehicleProjectCollisions);
    dialog.showAndWait().ifPresent(res -> {
      ServiceController<VehicleProjectDTO> task = new ServiceController<>();
      task.setOnFailed(e -> ExceptionHandler.instance().handleException(task.getException()));
      task.setOnSucceeded(event -> onSucceedHandler.run());
      task.start(() -> VehicleProjectRestClientHolder.getInstance().updateVehicleProjectSetVersion(res));
    });
  }
}
