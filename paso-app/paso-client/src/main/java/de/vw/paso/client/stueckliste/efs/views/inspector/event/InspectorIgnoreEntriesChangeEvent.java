package de.vw.paso.client.stueckliste.efs.views.inspector.event;

import javafx.event.Event;
import javafx.event.EventType;

public class InspectorIgnoreEntriesChangeEvent extends Event {

    private static final EventType<InspectorIgnoreEntriesChangeEvent> INSPECTOR_EDIT_OF_IGNORE_ENTRIES = new EventType<>(
        Event.ANY, "INSPECTOR_EDIT_OF_IGNORE_ENTRIES");

    public InspectorIgnoreEntriesChangeEvent() {
        super(INSPECTOR_EDIT_OF_IGNORE_ENTRIES);
    }
}
