package de.vw.paso.client.stueckliste.efs.views.inspector.event;

import javafx.event.Event;
import javafx.event.EventType;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class ShowElementInInspectorEvent extends Event {

    private static final EventType<InspectorJumpToElementEvent> SHOW_ELEMENT_IN_INSPECTOR = new EventType<>(Event.ANY,
        "SHOW_ELEMENT_IN_INSPECTOR"); // NO_UCD (use default)

    private final EfsElementDTO element;

    public ShowElementInInspectorEvent(EfsElementDTO ele) {
        super(SHOW_ELEMENT_IN_INSPECTOR);
        element = ele;
    }

    public EfsElementDTO getElement() {
        return element;
    }
}
