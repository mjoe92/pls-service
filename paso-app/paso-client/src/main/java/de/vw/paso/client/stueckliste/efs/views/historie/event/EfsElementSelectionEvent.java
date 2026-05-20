package de.vw.paso.client.stueckliste.efs.views.historie.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author  eryllan
 * @created 20.05.2015
 * @version $Revision:  $
 */
public class EfsElementSelectionEvent extends Event {

	private static final long serialVersionUID = 1L;

	public static final EventType<EfsElementSelectionEvent> SELECT_EFS_ELEMENT_IN_TREE = new EventType<>(Event.ANY, "SELECT_EFS_ELEMENT_IN_TREE");

	private final Long efsElementId;

	/**
	 * @param source
	 * @param eventType
	 * @param efsElementId
	 */
	public EfsElementSelectionEvent(Object source, EventType<EfsElementSelectionEvent> eventType, Long efsElementId) {
		super(source, null, eventType);
		this.efsElementId = efsElementId;
	}

	public Long getEfsElementId() {
		return efsElementId;
	}

}
