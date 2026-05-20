package de.vw.paso.exception;

public abstract class AbstractServerValidationException extends ServerException {

    public AbstractServerValidationException(String message) {
        super(message);
    }

    public AbstractServerValidationException(Throwable cause) {
        super(cause);
    }

    public AbstractServerValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract void accept(ServiceConsumer consumer);
}