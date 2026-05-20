package de.vw.paso.client.stueckliste.fzgkonfig.content;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import org.apache.commons.lang3.StringUtils;

@FXController(name = "error-message-pane")
public class ErrorMessagePaneController extends BaseController<StackPane> {

  @FXML
  private StackPane errorMessagePane;
  @FXML
  private Label message;

  private StringProperty errorMessage = new SimpleStringProperty();

  @Override
  public StackPane getControl() {
    return errorMessagePane;
  }

  @Override
  public Pane getStyleableParent() {
    return getControl();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    super.initialize(location, resources);
    bindErrorMessageProperty();
  }

  private void bindErrorMessageProperty() {
    message.textProperty().bind(errorMessageProperty());
    message.textProperty().addListener((obs, oldVal, newVal) -> showErrorMessages(newVal));
    showErrorMessages(null);
  }

  private void showErrorMessages(String newVal) {
    getControl().setVisible(!StringUtils.isEmpty(newVal));
  }

  @Override
  public void start() {
  }

  public final StringProperty errorMessageProperty() {
    if (errorMessage == null) {
      errorMessage = new SimpleStringProperty();
    }
    return this.errorMessage;
  }

}
