package de.vw.paso.client.stammdaten.setkey;

import javafx.event.Event;
import javafx.event.EventType;

public class SetKeyChangedEvent extends Event {

    private static final EventType<SetKeyChangedEvent> EDIT_OF_SET = new EventType<>(Event.ANY, "EDIT_OF_SET");

    public SetKeyChangedEvent() {
        super(EDIT_OF_SET);
    }
}
