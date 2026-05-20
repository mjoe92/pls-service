package de.vw.paso.client.stueckliste.efs.views.inspector.event;

import java.util.Collection;

import javafx.event.Event;
import javafx.event.EventType;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class InspectorEditOfEfsElementSolutionEvent extends Event {

    private static final EventType<InspectorEditOfEfsElementSolutionEvent> INSPECTOR_EDIT_OF_ELEMENT = new EventType<>(
        Event.ANY, "INSPECTOR_EDIT_OF_ELEMENT"); // NO_UCD (use default)

    private final Collection<EfsElementDTO> changedElements;
    private final Long vehicleConfigId;

    public InspectorEditOfEfsElementSolutionEvent(Collection<EfsElementDTO> changedElements, Long vehicleConfigId) {
        super(INSPECTOR_EDIT_OF_ELEMENT);

        this.changedElements = changedElements;
        this.vehicleConfigId = vehicleConfigId;
    }

    public Collection<EfsElementDTO> getChangedElements() {
        return changedElements;
    }

    public Long getVehicleConfigId() {
        return vehicleConfigId;
    }
}
