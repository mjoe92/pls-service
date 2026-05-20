package de.vw.paso.client.control.label;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**            
 * @author  eryllan
 * @created 23.01.2015
 * @version $Revision:  $
 */
public class CustomLabel extends Label {

    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

	private final Image icon;
	private String languageProperty;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

	public CustomLabel(String text, String languageProperty, Image icon) {
		super(text, new ImageView(icon));

		this.languageProperty = languageProperty;
		this.icon = icon;
	}

    /***************************************************************************
     *                                                                         *
     * Getters                                                                 *
     *                                                                         *
     **************************************************************************/ 

	public Image getIcon() {
		return icon;
	}

	public String getLanguageProperty() {
		return languageProperty;
	}

}
