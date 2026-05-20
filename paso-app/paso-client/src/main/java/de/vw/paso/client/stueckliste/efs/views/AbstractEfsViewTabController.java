package de.vw.paso.client.stueckliste.efs.views;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.stueckliste.efs.views.historie.event.EfsElementSelectionEvent;

public abstract class AbstractEfsViewTabController extends BaseController<Tab> {

    private final ObjectProperty<EventHandler<EfsElementSelectionEvent>> efsSelectionProperty;

    public AbstractEfsViewTabController() {
        efsSelectionProperty = new SimpleObjectProperty<>(this, "EfsElement");
    }

    protected abstract EfsViewTabType getType();

    protected final ObjectProperty<EventHandler<EfsElementSelectionEvent>> efsSelectionProperty() {
        return efsSelectionProperty;
    }

    protected final void setEfsSelectionAction(EventHandler<EfsElementSelectionEvent> handler) {
        efsSelectionProperty().set(handler);
    }
}