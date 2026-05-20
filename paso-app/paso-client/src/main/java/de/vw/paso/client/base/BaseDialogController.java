package de.vw.paso.client.base;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * More concrete implementation of the {@link AbstractDialogController} with some predefined UI and logic.
 *
 * @param <R>
 *     the return type
 */
public abstract class BaseDialogController<R> extends AbstractDialogController<R> {

    protected static final String LABEL_PARENT = "label.parent";
    protected static final String LABEL_DESCRIPTION = "label.description";

    protected GridPane grid = new GridPane();
    protected Integer rowIndex = 0;
    protected Node commitButton;

    private boolean cancelled = true;

    public boolean isCancelled() {
        return cancelled;
    }

    protected abstract <F> ChangeListener<F> getValidationListener();

    protected abstract <F> ListChangeListener<F> getValidationListenerForList();

    protected boolean isInvalid() {
        return false;
    }

    protected abstract R dialogResult();

    protected void addLabelAndInputFieldToGrid(String label, Control inputField) {
        grid.add(new Label(label), 0, rowIndex);
        grid.add(inputField, 1, rowIndex);

        rowIndex++;
    }

    protected void addLabelAndInputFieldToGrid(String label, Control inputField, int colSpan) {
        grid.add(new Label(label), 0, rowIndex);
        grid.add(inputField, 1, rowIndex, colSpan, 1);

        rowIndex++;
    }

    protected void addLabelAndInputFieldToGrid(Control... inputField) {
        for (int i = 0; i < inputField.length; i++) {
            grid.add(inputField[i], i, rowIndex);
        }
        rowIndex++;
    }

    protected void addLabelAndInputFieldToGrid(Label label, Control inputField, int colSpan) {
        grid.add(label, 0, rowIndex);
        grid.add(inputField, 1, rowIndex, colSpan, 1);

        rowIndex++;
    }

    protected void addValidationListenerToInputField(TextInputControl textField) {
        textField.textProperty().addListener(getValidationListener());
    }

    protected void addValidationListenerToInputField(CheckBox checkBox) {
        checkBox.selectedProperty().addListener(getValidationListener());
    }

    protected void addValidationListenerToInputField(ComboBox<?> comboBox) {
        comboBox.getSelectionModel().selectedIndexProperty().addListener(getValidationListener());
    }

    protected void addValidationListenerToInputField(TableView<?> tableView) {
        tableView.getSelectionModel().selectedItemProperty().addListener(getValidationListener());
    }

    protected void addValidationListenerToInputField(ObservableList<?> list) {
        list.addListener(getValidationListenerForList());
    }

    protected void initComboBox(ComboBox<String> comboBox, List<String> items, String selectItem) {
        comboBox.getItems().addAll(items);
        comboBox.getSelectionModel().select(selectItem);
    }

    /**
     * Initializes the Dialog window.
     *
     * @param title
     *     the title of the Dialog window
     * @param tasks
     *     this will run after the setup of the dialog
     */
    protected void initialize(String title, Runnable tasks) {
        setTitles(title);
        setGridProperties();
        setResultConverter();

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        initializeCommitButton();

        getDialogPane().setContent(grid);

        tasks.run();

        centerDialog();
    }

    private void centerDialog() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Stage dialogStage = (Stage) getDialogPane().getScene().getWindow();
        dialogStage.setX((bounds.getWidth() - dialogStage.getWidth()) / 2 + bounds.getMinX());
        dialogStage.setY((bounds.getHeight() - dialogStage.getHeight()) / 2 + bounds.getMinY());
    }

    protected void setGridProperties() {
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));
    }

    protected void setPromptText(String text, TextInputControl textField) {
        textField.setPromptText(text);
    }

    protected <F> void setPromptText(String text, ComboBox<F> comboBox) {
        comboBox.setPromptText(text);
    }

    protected void setTextToInputField(String text, TextInputControl textField) {
        textField.setText(text);
    }

    private void initializeCommitButton() {
        commitButton = getDialogPane().lookupButton(ButtonType.OK);
        commitButton.setDisable(true);
        commitButton.addEventFilter(ActionEvent.ACTION, event -> {
            cancelled = false;
            if (isInvalid()) {
                event.consume();

                commitButton.setDisable(true);
            }
        });
    }

    private void setTitles(String title) {
        setTitle(title);
        setHeaderText(title);
    }

    private void setResultConverter() {
        setResultConverter(dialogButton -> dialogButton.equals(ButtonType.OK) ? dialogResult() : null);
    }
}
