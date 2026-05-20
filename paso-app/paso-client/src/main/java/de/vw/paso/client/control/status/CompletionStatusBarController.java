package de.vw.paso.client.control.status;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;

@FXController(name = "completion-status-bar")
public class CompletionStatusBarController extends BaseController<HBox> {

    @FXML
    private HBox completionStatusBar;

    @FXML
    private Label labelSummary;

    @FXML
    private Label labelMessage;

    @FXML
    private Hyperlink hyperlinkAction;

    private StringProperty messageProperty;
    private StringProperty summaryProperty;
    private StringProperty actionLinkProperty;
    private ObjectProperty<EventHandler<MouseEvent>> actionLinkEventProperty;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        labelMessage.textProperty().bind(messageProperty());
        labelSummary.textProperty().bind(summaryProperty());
        hyperlinkAction.textProperty().bind(actionLinkProperty());
        hyperlinkAction.onMouseReleasedProperty().bind(actionLinkEventProperty());
    }

    @Override
    public HBox getControl() {
        return completionStatusBar;
    }

    @Override
    public Parent getStyleableParent() {
        return getControl();
    }

    public final StringProperty messageProperty() {
        if (messageProperty == null) {
            messageProperty = new SimpleStringProperty();
        }

        return this.messageProperty;
    }

    public String getMessage() {
        return messageProperty.get();
    }

    public void setMessage(final String value) {
        messageProperty.set(value);
    }

    public final StringProperty summaryProperty() {
        if (summaryProperty == null) {
            summaryProperty = new SimpleStringProperty();
        }

        return this.summaryProperty;
    }

    public String getSummary() {
        return summaryProperty.get();
    }

    public void setSummary(final String value) {
        summaryProperty.set(value);
    }

    public final StringProperty actionLinkProperty() {
        if (actionLinkProperty == null) {
            actionLinkProperty = new SimpleStringProperty();
        }

        return this.actionLinkProperty;
    }

    public String getActionLink() {
        return actionLinkProperty.get();
    }

    public void setActionLink(final String value) {
        actionLinkProperty.set(value);
    }

    public final ObjectProperty<EventHandler<MouseEvent>> actionLinkEventProperty() {
        if (actionLinkEventProperty == null) {
            actionLinkEventProperty = new SimpleObjectProperty<>();
        }

        return this.actionLinkEventProperty;
    }

    public EventHandler<MouseEvent> getActionLinkEvent() {
        return actionLinkEventProperty.get();
    }

    public void setActionLinkEvent(final EventHandler<MouseEvent> eventHandler) {
        actionLinkEventProperty.set(eventHandler);
    }

}
