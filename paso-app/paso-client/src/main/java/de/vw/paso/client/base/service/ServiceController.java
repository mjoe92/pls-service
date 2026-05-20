package de.vw.paso.client.base.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.main.statusbar.MainStatusBarEvent;
import de.vw.paso.client.util.EventBus;

/**
 * {@link Service} with some more PASO utilities build around, which should be used to make a service call in the background.
 *
 * @param <V>
 *   the return type
 */
public class ServiceController<V> extends Service<V> {

  private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();

  private volatile long executionTime = -1;
  private volatile String statusMessage;

  private ServiceCallWithReturn<V> serviceCall;

  public ServiceController() {
    EventBus.getInstance().register(this);
    setExecutor(EXECUTOR_SERVICE);
  }

  public final long getExecutionTime() {
    return executionTime;
  }

  public final String getStatusMessage() {
    return statusMessage;
  }

  public final void setExecutionTime(long executionTime) {
    this.executionTime = executionTime;
  }

  public final void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  public void start(final ServiceCallWithReturn<V> serviceCall) {
    this.serviceCall = serviceCall;
    initAndStart();
  }

  public void start(final ServiceCallWithoutReturn serviceCall) {
    this.serviceCall = () -> {
      serviceCall.run();
      return null;
    };
    initAndStart();
  }

  @Override
  protected final Task<V> createTask() {
    return new Task<>() {
      @Override
      protected V call() throws Exception {
        try {
          return runServiceCall();
        } finally {
          EventBus.getInstance().unregister(ServiceController.this);
        }
      }
    };
  }

  protected void initAndStart() {
    if (executionTime > 0) {
      Task<V> statusBarTask = createStatusBarTask();
      startStatusBarTask(statusBarTask);
    }

    start();
  }

  protected V runServiceCall() throws Exception {
    return serviceCall.run();
  }

  private Task<V> createStatusBarTask() {
    return new Task<>() {
      @Override
      protected V call() throws Exception {
        updateStatusBar();
        return null;
      }

      private void updateStatusBar() throws InterruptedException {
        updateProgress(0, getExecutionTime());
        String message = getStatusMessage();
        if (message == null) {
          message = I18N.getString("message.load.generic");
        }
        updateMessage(message);

        for (int i = 0; i < getExecutionTime(); i = i + 100) {
          updateProgress(i, getExecutionTime());
          Thread.sleep(100);
        }
        updateProgress(getExecutionTime(), getExecutionTime());
      }
    };
  }

  private void startStatusBarTask(Task<V> statusBarTask) {
    EventBus.getInstance().post(new MainStatusBarEvent(this, statusBarTask));
    getExecutor().execute(statusBarTask);
  }

}
