package de.vw.paso.client.base;

import java.util.function.Consumer;

import javafx.application.Platform;

import de.vw.paso.client.base.service.ServiceCallWithReturn;
import de.vw.paso.client.base.service.ServiceCallWithoutReturn;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.exception.ServiceConsumer;

/**
 * Interface for all Controller like classes. Includes methods to run code asynchronously in the background.
 */
public interface Controller extends ServiceConsumer {

    default <V> void doAsync(ServiceCallWithReturn<V> serviceCall, Consumer<V> onSuccess) {
        doAsync(serviceCall, onSuccess, null);
    }

    default <V> void doAsync(ServiceCallWithReturn<V> serviceCall, Consumer<V> onSuccess, Runnable onFail) {
        ServiceController<V> serviceController = new ServiceController<>();
        serviceController.setOnSucceeded(e -> {
            if (onSuccess != null) {
                onSuccess.accept(serviceController.getValue());
            }
        });

        serviceController.setOnFailed(e -> {
            if (onFail != null) {
                onFail.run();
            }
            handleException(serviceController.getException());
        });

        serviceController.start(serviceCall);
    }

    default void doAsync(ServiceCallWithoutReturn serviceCall) {
        doAsync(serviceCall, null);
    }

    default void doAsync(ServiceCallWithoutReturn serviceCall, Runnable onSuccess) {
        doAsync(serviceCall, onSuccess, null);
    }

    default void doAsync(ServiceCallWithoutReturn serviceCall, Runnable onSuccess, Runnable onFail) {
        ServiceController<Void> serviceController = new ServiceController<>();
        serviceController.setOnSucceeded(stateEvent -> {
            if (onSuccess != null) {
                onSuccess.run();
            }
        });
        serviceController.setOnFailed(stateEvent -> {
            if (onFail != null) {
                onFail.run();
            }

            handleException(serviceController.getException());
        });
        serviceController.start(serviceCall);
    }

    default void handleException(Throwable throwable) {
        Platform.runLater(() -> ExceptionHandler.instance().handleException(throwable, this));
    }
}