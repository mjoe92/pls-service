package de.vw.paso.client.base.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import de.vw.paso.client.PasoApplication;

/**
 * Implementation of the JavaFX {@link Alert} with PASO specific customizations:
 * <ul>
 *   <li>
 *      Always sets the main stage as owner.
 *   </li>
 * </ul>
 */
public final class PasoAlert extends Alert {

  public PasoAlert(AlertType alertType) {
    super(alertType);

    initOwner();
  }

  public PasoAlert(AlertType alertType, String text, ButtonType... buttonTypes) {
    super(alertType, text, buttonTypes);

    initOwner();
  }

  private void initOwner() {
    Stage stage = PasoApplication.getInstance().getStage();
    if (stage != null && stage.getScene() != null) {
      initOwner(stage);
    }
  }
}
