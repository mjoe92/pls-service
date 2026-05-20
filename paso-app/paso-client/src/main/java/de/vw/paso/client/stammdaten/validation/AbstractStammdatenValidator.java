package de.vw.paso.client.stammdaten.validation;

import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;

import de.vw.paso.client.validation.event.PasoValidationEvent;

public abstract class AbstractStammdatenValidator {

    protected void addValidationListener(ObservableValue<?> observableValue) {
        observableValue.addListener(observable -> testValidation());
    }

    protected abstract void testValidation();

    private ObjectProperty<EventHandler<PasoValidationEvent>> pasoValidationProperty = new SimpleObjectProperty<>(this,
            "PasoValidation");

    public void setPasoValidationAction(EventHandler<PasoValidationEvent> handler) {
        pasoValidationProperty.set(handler);
    }

    protected void handleValidation(Map<String, Object[]> messages) {
        pasoValidationProperty.get()
                .handle(new PasoValidationEvent(this, PasoValidationEvent.PASO_VALIDATION, messages));
    }

}
