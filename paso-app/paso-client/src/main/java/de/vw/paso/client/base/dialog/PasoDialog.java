package de.vw.paso.client.base.dialog;

import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import de.vw.paso.client.PasoApplication;

/**
 * Implementation of the JavaFX {@link Dialog} with PASO specific customizations:
 * <ul>
 *   <li>
 *      Always sets the main stage as owner.
 *   </li>
 * </ul>
 *
 * @param <R>
 *     the return type
 */
public class PasoDialog<R> extends Dialog<R> {

    public PasoDialog() {
        initOwner();
    }

    private void initOwner() {
        Stage stage = PasoApplication.getInstance().getStage();
        if (stage != null && stage.getScene() != null) {
            initOwner(stage);
        }
    }
}
