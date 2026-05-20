package de.vw.paso.client.stueckliste.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author  eryllan
 * @created 19.02.2015
 * @version $Revision:  $
 */
public class SelectStuecklisteTabEvent extends Event {

	private static final long serialVersionUID = 1L;

	public static final EventType<SelectStuecklisteTabEvent> SELECTED_FZG_KONFIG_TAB = new EventType<>(Event.ANY, "FZG_KONFIG_TAB_SELECTED"); // NO_UCD (use default)
	public static final EventType<SelectStuecklisteTabEvent> SELECTED_EFS_TAB = new EventType<>(Event.ANY, "EFS_TAB_SELECTED"); // NO_UCD (use default)


	public SelectStuecklisteTabEvent(Object source, EventType<SelectStuecklisteTabEvent> eventType) {
		super(source, null, eventType);
	}

}
