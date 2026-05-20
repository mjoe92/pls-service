package de.vw.paso.login.client;

import de.vw.paso.client.base.exception.AbstractClientException;
import de.vw.paso.delegate.base.ErrorCode;

public class PasoLoginPropertyException extends AbstractClientException {

    private static final ErrorCode PROPERTIES_LOAD = new ErrorCode("error.properties.load",
            "Failed to load properties");

    public PasoLoginPropertyException(Throwable cause) {
        super(cause, PROPERTIES_LOAD);
    }

    public PasoLoginPropertyException() {
        super(PROPERTIES_LOAD);
    }
}