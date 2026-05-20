package de.vw.paso.client.base;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

import de.vw.paso.client.util.EventBus;

/**
 * Abstract {@link Controller} for UI.
 *
 * @implNote Adds the methods from {@link Controller} and implements {@link Initializable},
 * although it does not need to be used with FXML.
 */
public abstract class AbstractController implements Initializable, Controller {

  protected AbstractController() {
    registerEventBus();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void start() {
    // default empty
  }

  protected final void registerEventBus() { // NO_UCD (use private)
    EventBus.getInstance().register(this);
  }

  protected void stop() {
    unregisterEventBus();
  }

  protected final void unregisterEventBus() { // NO_UCD (use private)
    EventBus.getInstance().unregister(this);
  }

}
