package de.vw.paso.client.stueckliste.efs.control;

import java.io.IOException;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.util.FxmlException;

public abstract class BaseAggregateTextFieldControl extends GridPane {

    @FXML
    private TextField textField;

    protected abstract String getFxmlFile();

    protected BaseAggregateTextFieldControl() {
        loadFxml();
        init();
    }

    protected void init() {
        //default empty
    }

    private void loadFxml() {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getFxmlFile()));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (final IOException exception) {
            throw new FxmlException(exception);
        }
    }

    public TextField getTextField() {
        return textField;
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(final String value) {
        textProperty().set(value);
    }

    public StringProperty textProperty() {
        return textField.textProperty();
    }

    public final boolean isEditable() {
        return textField.isEditable();
    }

    public final void setEditable(final boolean value) {
        textField.setEditable(value);
    }

}
