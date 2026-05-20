package de.vw.paso.client.stueckliste.efs.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItem;

public class AggregatTextFieldControl extends BaseAggregateTextFieldControl {

    private static final String FXML_FILE = "aggregat-text-field.fxml";

    @FXML
    private Button buttonShowAggregat;

    private ObjectProperty<EventHandler<AggregatTextFieldEvent>> propertyOnAction;
    private EfsElementTreeItem aggregat;

    @Override
    protected String getFxmlFile() {
        return FXML_FILE;
    }

    @Override
    protected void init() {
        initButtonAction();
        setButtonGraphic();
    }

    private void initButtonAction() {
        propertyOnAction = new SimpleObjectProperty<>(this, "AggregatTextFieldControl");

        buttonShowAggregat.setOnAction(
                e -> onActionProperty().get().handle(new AggregatTextFieldEvent(this, getAggregat())));
    }

    private void setButtonGraphic() {
        final Label label = new Label("\uD83D\uDD0D");
        buttonShowAggregat.setGraphic(label);
    }

    public EfsElementTreeItem getAggregat() {
        return aggregat;
    }

    public void setAggregat(final EfsElementTreeItem aggregat) {
        this.aggregat = aggregat;

        textProperty().set((aggregat == null) ? null : aggregat.getDescription2());
    }

    public final ObjectProperty<EventHandler<AggregatTextFieldEvent>> onActionProperty() {
        return propertyOnAction;
    }

    public final void setOnAction(final EventHandler<AggregatTextFieldEvent> handler) {
        propertyOnAction.set(handler);
    }

    public final EventHandler<AggregatTextFieldEvent> getOnAction() {
        return propertyOnAction.get();
    }

}
