package de.vw.paso.client.validation.event;

import java.util.Map;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author eryllan
 * @version $Revision:  $
 * @created 13.08.2015
 */
public class PasoValidationEvent extends Event {

    private static final long serialVersionUID = 1L;

    public static final EventType<PasoValidationEvent> PASO_VALIDATION = new EventType<>(Event.ANY,
            "PASO_VALIDATION"); // NO_UCD (use default)

    // ResourceBundleKey + Formatatierungs-Argumente
    private final Map<String, Object[]> messages;

    /**
     * @param source
     * @param eventType
     * @param efsElementId
     */
    public PasoValidationEvent(Object source, EventType<PasoValidationEvent> eventType,
            Map<String, Object[]> messages) { // NO_UCD (use default)
        super(source, null, eventType);
        this.messages = messages;
    }

    public Map<String, Object[]> getMessages() {
        return messages;
    }

}
