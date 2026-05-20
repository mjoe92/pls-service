package de.vw.paso.client.stammdaten.costgroup;

import javafx.event.Event;
import javafx.event.EventType;

public class CostGroupChangedEvent extends Event {

  private static final EventType<CostGroupChangedEvent> EDIT_OF_COST_GROUP =
    new EventType<>(Event.ANY, "EDIT_OF_COST_GROUP");

  public CostGroupChangedEvent() {
    super(EDIT_OF_COST_GROUP);
  }
}
