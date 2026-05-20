package de.vw.paso.client.stueckliste.efs.views;

import javafx.event.Event;
import javafx.event.EventType;

public class EfsViewTabEvent extends Event {

    public static final EventType<EfsViewTabEvent> ADD_EFS_VIEW_TAB = new EventType<>(Event.ANY,
        "ADD_EFS_VIEW_TAB");
    public static final EventType<EfsViewTabEvent> REMOVE_EFS_VIEW_TAB = new EventType<>(Event.ANY,
        "REMOVE_EFS_VIEW_TAB");
    public static final EventType<EfsViewTabEvent> RESIZE_EFS_VIEW_TAB = new EventType<>(Event.ANY,
        "RESIZE_EFS_VIEW_TAB");

    private final int tabCount;

    public EfsViewTabEvent(Object source, EventType<EfsViewTabEvent> eventType, int tabCount) {
        super(source, null, eventType);
        this.tabCount = tabCount;
    }

    public int getTabCount() { // NO_UCD (use default)
        return tabCount;
    }

}
