package de.vw.paso.client.stueckliste.efs.views.historie.event;

import javafx.event.Event;
import javafx.event.EventType;

import de.vw.paso.client.stueckliste.efs.views.suche.SearchTabController;

public class EfsElementSearchEvent extends Event {

    public static final EventType<EfsElementSearchEvent> EFS_ELEMENT_SUCHE = new EventType<>(Event.ANY,
            "EFS_ELEMENT_SUCHE"); // NO_UCD (use default)

    private final SearchTabController searchTabController;
    private final String searchTerm;

    public EfsElementSearchEvent(Object source, EventType<EfsElementSearchEvent> eventType,
            SearchTabController searchTabController, String searchTerm) { // NO_UCD (use default)
        super(source, null, eventType);
        this.searchTabController = searchTabController;
        this.searchTerm = searchTerm;
    }

    public SearchTabController getSearchTabController() { // NO_UCD (use default)
        return searchTabController;
    }

    public String getSearchTerm() { // NO_UCD (use default)
        return searchTerm;
    }
}