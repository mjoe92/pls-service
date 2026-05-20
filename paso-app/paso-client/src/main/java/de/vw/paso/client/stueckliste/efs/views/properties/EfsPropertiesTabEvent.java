package de.vw.paso.client.stueckliste.efs.views.properties;

import javafx.event.Event;
import javafx.event.EventType;

public class EfsPropertiesTabEvent extends Event {
  private static final long serialVersionUID = 1L;

  public static final EventType<EfsPropertiesTabEvent> ADD_PROPERTIES_TAB = new EventType<>(Event.ANY, "ADD_PROPERTIES_TAB"); // NO_UCD (use default)
  public static final EventType<EfsPropertiesTabEvent> REMOVE_PROPERTIES_TAB = new EventType<>(Event.ANY, "REMOVE_PROPERTIES_TAB"); // NO_UCD (use default)
  public static final EventType<EfsPropertiesTabEvent> RESIZE_PROPERTIES_TAB = new EventType<>(Event.ANY, "RESIZE_PROPERTIES_TAB"); // NO_UCD (use default)

  private final int tabCount;

  public EfsPropertiesTabEvent(Object source, EventType<EfsPropertiesTabEvent> eventType, int tabCount) { // NO_UCD (use default)
    super(source, null, eventType);
    this.tabCount = tabCount;
  }

  public int getTabCount() { // NO_UCD (use default)
    return tabCount;
  }

}
