package de.vw.paso.client.stueckliste.efs.views.historie.event;

import java.io.Serial;

import javafx.event.Event;
import javafx.event.EventType;

import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementDTO;

/**
 * @author eryllan
 * @since 19.02.2015
 */
public class HistorieUpdateEvent<T extends AbstractTreeItem<AbstractEfsElementDTO>> extends Event {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final EventType<HistorieUpdateEvent<AbstractTreeItem<AbstractEfsElementDTO>>> EFS_ELEMENT_SELECTED = new EventType<>(
            Event.ANY, "EFS_ELEMENT_SELECTED"); // NO_UCD (use default)
    public static final EventType<HistorieUpdateEvent<AbstractTreeItem<AbstractEfsElementDTO>>> EFS_ELEMENTS_CHANGED = new EventType<>(
            Event.ANY, "EFS_ELEMENTS_CHANGED"); // NO_UCD (use default)

    private final AbstractEfsElementDTO efsElement;

    public HistorieUpdateEvent(Object source, EventType<HistorieUpdateEvent<T>> eventType,
            AbstractEfsElementDTO efsElement) {
        super(source, null, eventType);
        this.efsElement = efsElement;
    }

    public AbstractEfsElementDTO getEfsElement() {
        return efsElement;
    }
}