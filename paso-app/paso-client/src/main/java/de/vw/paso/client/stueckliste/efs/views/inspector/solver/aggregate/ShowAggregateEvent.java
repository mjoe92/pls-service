package de.vw.paso.client.stueckliste.efs.views.inspector.solver.aggregate;

public class ShowAggregateEvent {

    private final Long vehiclePartListId;

    public ShowAggregateEvent(Long vehiclePartListId) {
        this.vehiclePartListId = vehiclePartListId;
    }

    public Long getVehiclePartListId() {
        return vehiclePartListId;
    }
}
