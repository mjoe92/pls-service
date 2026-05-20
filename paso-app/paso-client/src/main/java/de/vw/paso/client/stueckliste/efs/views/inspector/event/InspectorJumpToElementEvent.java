package de.vw.paso.client.stueckliste.efs.views.inspector.event;

import javafx.event.Event;
import javafx.event.EventType;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class InspectorJumpToElementEvent extends Event {

    private static final EventType<InspectorJumpToElementEvent> INSPECTOR_JUMP_TO_EFS_ELEMENT = new EventType<>(
        Event.ANY, "INSPECTOR_JUMP_TO_EFS_ELEMENT"); // NO_UCD (use default)

    private final EfsElementDTO element;

    public InspectorJumpToElementEvent(EfsElementDTO element) {
        super(INSPECTOR_JUMP_TO_EFS_ELEMENT);
        this.element = element;
    }

    public EfsElementDTO getElement() {
        return element;
    }
}
