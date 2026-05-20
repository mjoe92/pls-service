package de.vw.paso.client.main.ribbonmenu.fzgkonfig;

import javafx.beans.property.BooleanProperty;

/**            
 * @author  eryllan
 * @created 21.04.2015
 * @version $Revision:  $
 */
public interface RibbonMenuFzgKonfigListener {

	/**************************************************************************
	 * 
	 * Event Handler
	 * 
	 **************************************************************************/

	void handleActionErstelleStueckliste();

	void handleActionClearFilters();

	/**************************************************************************
	 * 
	 * Event Handler Properties
	 * 
	 **************************************************************************/

	public BooleanProperty disablePropertyErstelleStueckliste();

	public BooleanProperty disablePropertyClearFilters();

}
