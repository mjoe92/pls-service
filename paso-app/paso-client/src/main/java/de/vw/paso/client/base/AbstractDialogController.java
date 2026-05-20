package de.vw.paso.client.base;

import de.vw.paso.client.base.dialog.PasoDialog;

/**
 * Abstract {@link Controller} for dialogs.
 *
 * @param <R>
 *   the return type
 * @implNote Extends the {@link PasoDialog} and adds the methods from {@link Controller}.
 */
public abstract class AbstractDialogController<R> extends PasoDialog<R> implements Controller {

  protected AbstractDialogController() {
    setResizable(true);
  }

}
