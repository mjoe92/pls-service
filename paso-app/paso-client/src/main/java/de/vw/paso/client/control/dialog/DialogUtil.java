package de.vw.paso.client.control.dialog;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.dialog.PasoAlert;
import de.vw.paso.utility.StringConstant;

public final class DialogUtil {

    private DialogUtil() {
        // noop
    }

    public static ButtonType showConfirmationDialog(AlertType alertType, String title, String headerText,
        String contentText, ButtonType... elements) {
        Alert dialog = new PasoAlert(alertType);
        if (title != null) {
            dialog.setTitle(title);
        }

        if (headerText != null) {
            dialog.setHeaderText(headerText);
        }

        if (contentText != null) {
            dialog.setContentText(contentText);
        }

        if (elements.length > 0) {
            dialog.getButtonTypes().setAll(elements);
        }

        Optional<ButtonType> result = dialog.showAndWait();
        return result.orElse(ButtonType.CLOSE);
    }

    public static void showErrorDialog(String title, String header, String message, String content) {
        Alert dialog = new PasoAlert(AlertType.ERROR);
        if (title != null) {
            dialog.setTitle(title);
        }

        if (header != null) {
            dialog.setHeaderText(header);
        }

        Button copyEmailButton = new Button(I18N.getString("error-dialog-copy-email"));
        copyEmailButton.onMouseClickedProperty().setValue(e -> copy());

        if (message != null) {
            dialog.setContentText(message);
        }

        GridPane excContent = new GridPane();
        excContent.setMaxWidth(Double.MAX_VALUE);

        Label contactLabel = new Label(I18N.getString("error-dialog-contact"));
        excContent.add(contactLabel, 0, 0);

        Label errorMessageLabel = new Label(I18N.getString("error-dialog-error-message"));
        excContent.add(errorMessageLabel, 0, 1);

        TextArea textArea = createTextArea(content);
        excContent.add(textArea, 0, 2);

        VBox dialogContent = new VBox(excContent, copyEmailButton);

        dialog.getDialogPane().setExpandableContent(dialogContent);
        dialog.showAndWait();
    }

    private static TextArea createTextArea(String content) {
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        return textArea;
    }

    private static void copy() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString("joost.vink@volkswagen.de");
        clipboard.setContent(clipboardContent);
    }

    public static void showWarnDialog(String title, String headerText, String contentText) {
        Alert dialog = new PasoAlert(AlertType.WARNING);
        if (title != null) {
            dialog.setTitle(title);
        }

        if (headerText != null) {
            dialog.setHeaderText(headerText);
        }

        if (contentText != null) {
            dialog.setContentText(contentText);
        }

        dialog.showAndWait();
    }

    public static ButtonType showDeleteDialog(int size) {
        String titleKey, messageKey;
        if (size == 1) {
            titleKey = "dialog.position.delete.title";
            messageKey = "dialog.position.delete.text";
        } else {
            titleKey = "dialog.positionen.delete.title";
            messageKey = "dialog.positionen.delete.text";
        }

        return DialogUtil.showConfirmationDialog(AlertType.CONFIRMATION, I18N.getString(titleKey), StringConstant.EMPTY,
            I18N.getString(messageKey), ButtonType.YES, ButtonType.NO);
    }
}
