package de.vw.paso.client.stammdaten.fzgprojekt;

import javafx.event.Event;
import javafx.event.EventType;

public class UpdateVehicleProjectArchivingEvent extends Event {

  public static final EventType<UpdateVehicleProjectArchivingEvent> UPDATE_VEHICLE_PROJECT_ARCHIVING =
    new EventType<>(Event.ANY, "UPDATE_VEHICLE_PROJECT_ARCHIVING");

  public UpdateVehicleProjectArchivingEvent() {
    super(UPDATE_VEHICLE_PROJECT_ARCHIVING);
  }
}
