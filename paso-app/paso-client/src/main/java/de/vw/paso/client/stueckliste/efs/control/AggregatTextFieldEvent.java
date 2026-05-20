package de.vw.paso.client.stueckliste.efs.control;

import javafx.event.Event;
import javafx.event.EventType;

import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItem;

public class AggregatTextFieldEvent extends Event {

    public static final EventType<AggregatTextFieldEvent> AGGREGAT_EVENT_TYPE = new EventType<>(Event.ANY, "AGGREGAT");

    private EfsElementTreeItem aggregat;

    public AggregatTextFieldEvent(Object source, EfsElementTreeItem efsElementTreeItem) {
        super(source, null, AGGREGAT_EVENT_TYPE);
        this.aggregat = efsElementTreeItem;
    }

    public EfsElementTreeItem getAggregat() {
        return aggregat;
    }
}
