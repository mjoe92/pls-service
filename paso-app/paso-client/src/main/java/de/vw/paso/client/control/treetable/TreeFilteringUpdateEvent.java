package de.vw.paso.client.control.treetable;

import javafx.event.Event;
import javafx.event.EventType;

public class TreeFilteringUpdateEvent extends Event {

  private static final EventType<TreeFilteringUpdateEvent> TREE_FILTERING_UPDATE_EVENT =
    new EventType<>(Event.ANY, "TREE_FILTERING_UPDATE_EVENT");

  TreeFilteringUpdateEvent() {
    super(TREE_FILTERING_UPDATE_EVENT);
  }
}
