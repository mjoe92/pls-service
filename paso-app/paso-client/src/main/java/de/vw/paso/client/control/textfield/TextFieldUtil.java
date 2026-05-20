package de.vw.paso.client.control.textfield;

import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public final class TextFieldUtil {

  private static final Duration FADE_DURATION = Duration.millis(200);

  private TextFieldUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static void createClearableTextField(PasoCustomTextField<?> textField) {
    setupClearButtonField(textField, textField.rightProperty());
  }

  private static void setButtonState(TextField inputField, FadeTransition fader) {
    String text = inputField.getText();
    boolean isTextEmpty = text == null || text.isEmpty();
    boolean isButtonVisible = fader.getNode().getOpacity() > 0;

    if (isTextEmpty && isButtonVisible) {
      setButtonVisible(fader, false);
    } else if (!isTextEmpty && !isButtonVisible) {
      setButtonVisible(fader, true);
    }
  }

  private static void setButtonVisible(final FadeTransition fader, boolean visible) {
    fader.setFromValue(visible ? 0.0 : 1.0);
    fader.setToValue(visible ? 1.0 : 0.0);
    fader.play();
  }

  private static void setupClearButtonField(TextField inputField, ObjectProperty<Node> rightProperty) {
    inputField.getStyleClass().add("clearable-field");

    Region clearButton = new Region();
    clearButton.getStyleClass().addAll("graphic");
    StackPane clearButtonPane = new StackPane(clearButton);
    clearButtonPane.getStyleClass().addAll("clear-button");
    clearButtonPane.setOpacity(0.0);
    clearButtonPane.setCursor(Cursor.DEFAULT);
    clearButtonPane.setOnMouseReleased(e -> inputField.clear());

    rightProperty.set(clearButtonPane);

    final FadeTransition fader = new FadeTransition(FADE_DURATION, clearButtonPane);
    fader.setCycleCount(1);

    inputField.textProperty().addListener(e -> setButtonState(inputField, fader));
  }

}
