package de.vw.paso.client.stueckliste.fzgkonfig.event;

import javafx.event.Event;
import javafx.event.EventType;

/**   
 * @author  eryllan
 * @created 26.10.2015
 * @version $Revision:  $
 */
public class UpdateMainTabTitleEvent extends Event {

	private static final long serialVersionUID = 1L;

	public static final EventType<UpdateMainTabTitleEvent> UPDATE_MAIN_TAB_TITLE = new EventType<>(Event.ANY, "UpdateMainTabTitleEvent"); // NO_UCD (use default)

	public UpdateMainTabTitleEvent(Object source, EventType<UpdateMainTabTitleEvent> eventType) { // NO_UCD (use default)
		super(source, null, eventType);
	}

}
