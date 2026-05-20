package de.vw.paso.client.base.service;

import java.util.function.Predicate;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

/**
 * Specialized implementation of the {@link ServiceController} for polling data.
 *
 * @param <V>
 *   the return type
 */
public class PollingServiceController<V> extends ServiceController<V> {

  private volatile BooleanProperty poll;
  private volatile Predicate<V> pollingPredicate;

  private volatile Task<Void> pollTask;

  public final boolean isPoll() {
    return pollProperty().get();
  }

  public final BooleanProperty pollProperty() {
    if (poll == null) {
      poll = new SimpleBooleanProperty(false);
    }
    return poll;
  }

  public void pollWhile(Predicate<V> pollingPredicate) {
    this.pollingPredicate = pollingPredicate;
    setPoll(true);
  }

  public final void setPoll(final boolean poll) {
    pollProperty().set(poll);
  }

  protected void initAndStart() {
    if (isPoll()) {
      start();
    } else {
      super.initAndStart();
    }

    if (isPoll() && pollingPredicate == null) {
      startPollTask();
      pollProperty().addListener((obs, oldVal, newVal) -> {
        if (pollTask != null && !newVal) {
          pollTask.cancel();
        }
      });
    } else if (isPoll()) {
      pollProperty().addListener((obs, oldVal, newVal) -> cancel());
    }
  }

  @Override
  protected V runServiceCall() throws Exception {
    V value = super.runServiceCall();

    while (isPollingPredicateValid(value)) {
      Thread.sleep(getExecutionTime());
      value = super.runServiceCall();
    }

    return value;
  }

  private boolean isPollingPredicateValid(V returnValue) {
    return isPoll() && pollingPredicate != null && pollingPredicate.test(returnValue);
  }

  private void startPollTask() {
    this.pollTask = new Task<>() {
      @Override
      protected Void call() throws Exception {
        Thread.sleep(getExecutionTime());
        Platform.runLater(PollingServiceController.this::restart);
        return null;
      }

      @Override
      protected void succeeded() {
        if (isPoll()) {
          startPollTask();
        }
      }
    };
    getExecutor().execute(pollTask);
  }
}
