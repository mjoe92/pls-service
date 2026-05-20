package de.vw.paso.client.control.gallery;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * @author  eryllan
 * @created 11.08.2015
 * @version $Revision:  $
 */
public class SlideableImageBox extends HBox { // NO_UCD (use default)

	private final ReadOnlyDoubleProperty heightProperty;
	private final ImageView iv;

	public SlideableImageBox(ImageView iv, ReadOnlyDoubleProperty heightProperty) { // NO_UCD (use default)
		this.iv = iv;
		this.heightProperty = heightProperty;

		init();
	}

	private void init() {
		this.getChildren().add(iv);
		this.setAlignment(Pos.CENTER);

		heightProperty.addListener((obs, oldVal, newVal) -> {
			double size = newVal.doubleValue() - 6;
			this.setPrefSize(size, size);
			scaleImage();
		});
	}

	private void scaleImage() {
		double imgWidth = iv.getImage().getWidth();
		double imgHeight = iv.getImage().getHeight();
		if ( imgWidth >= imgHeight ) {
			iv.setFitWidth(heightProperty.get() * 0.9);

			Double scaleHeight = (heightProperty.get() * iv.getImage().getHeight()) / iv.getImage().getWidth();
			iv.setFitHeight(scaleHeight * 0.9);
		} else {
			iv.setFitHeight(heightProperty.get() * 0.9);

			Double scaleWidth = (heightProperty.get() * iv.getImage().getWidth()) / iv.getImage().getHeight();
			iv.setFitWidth(scaleWidth * 0.9);
		}
	}

}
