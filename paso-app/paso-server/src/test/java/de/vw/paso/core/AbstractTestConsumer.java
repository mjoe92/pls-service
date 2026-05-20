package de.vw.paso.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.function.Supplier;

import de.vw.paso.exception.AbstractServerValidationException;
import de.vw.paso.exception.IServerException;
import de.vw.paso.exception.ServerException;
import de.vw.paso.exception.ServiceConsumer;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.SupplierWithException;
import de.vw.paso.utility.VoidWithException;

public abstract class AbstractTestConsumer<V> implements ServiceConsumer {

    private Throwable caughtException;
    private V result;

    public V getResult() {
        return getResult(null);
    }

    public V getResult(Class<? extends Exception> expectedException) {
        assertException(expectedException);
        return result;
    }

    protected void handle(ServerException exception) {
        caughtException = exception;
    }

    protected void handle(ServerException exception, V entity) {
        caughtException = exception;
        setResult(entity);
    }

    protected void registerResult(Supplier<List<EfsElementDTO>> function) {
        try {
            EfsElementResolver.registerElements(function.get());
        } catch (RuntimeException e) {
            final Throwable cause = e.getCause();
            if (cause == null || !(cause instanceof IServerException
                    || cause.getClass() == IllegalArgumentException.class)) {
                throw e;
            }
        }

    }

    protected void run(VoidWithException voidCall) {
        caughtException = null;
        result = null;
        try {
            voidCall.run();
            handle(null);
        } catch (final AbstractServerValidationException exception) {
            exception.accept(this);
        } catch (final ServerException exception) {
            handle(exception);
        } catch (final Throwable exception) {
            caughtException = exception;
        }
    }

    protected void run(SupplierWithException<V> supplier) {

        run(() -> setResult(supplier.get()));
    }

    private void assertException(Class<? extends Exception> expected) {
        if (expected != null) {
            if (caughtException == null) {
                fail("Expected exception: " + expected.getSimpleName());
            }
            assertEquals(expected, caughtException.getClass());
        } else {
            if (caughtException != null) {
                throw new RuntimeException(caughtException);
            }
        }
    }

    private void setResult(V result) {
        this.result = result;
    }
}
