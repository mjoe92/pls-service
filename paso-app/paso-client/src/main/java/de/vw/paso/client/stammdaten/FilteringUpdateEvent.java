package de.vw.paso.client.stammdaten;

import javafx.event.Event;
import javafx.event.EventType;

public class FilteringUpdateEvent extends Event {
 
    private static final EventType<FilteringUpdateEvent> FILTERING_UPDATE_EVENT = new EventType<>(Event.ANY,
            "FILTERING_UPDATE_EVENT");

    public FilteringUpdateEvent() {
        super(FILTERING_UPDATE_EVENT);
    }
}