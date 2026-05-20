package de.vw.paso.client.stueckliste.efs.views.inspector.event;

import java.util.List;

import javafx.event.Event;
import javafx.event.EventType;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class InspectorDeleteEfsElementSolutionEvent extends Event {

    private static final EventType<InspectorDeleteEfsElementSolutionEvent> INSPECTOR_DELETE_ELEMENT = new EventType<>(
        Event.ANY, "INSPECTOR_DELETE_OF_ELEMENT"); // NO_UCD (use default)

    private final List<EfsElementDTO> deletingElements;
    private final Long vehicleConfigId;

    public InspectorDeleteEfsElementSolutionEvent(List<EfsElementDTO> deletingElements, Long vehicleConfigId) {
        super(INSPECTOR_DELETE_ELEMENT);

        this.deletingElements = deletingElements;
        this.vehicleConfigId = vehicleConfigId;
    }

    public List<EfsElementDTO> getDeletingElements() {
        return deletingElements;
    }

    public Long getVehicleConfigId() {
        return vehicleConfigId;
    }
}
