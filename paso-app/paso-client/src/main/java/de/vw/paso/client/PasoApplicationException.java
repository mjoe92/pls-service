package de.vw.paso.client;

import de.vw.paso.client.base.exception.AbstractClientException;
import de.vw.paso.delegate.base.ErrorCode;

public class PasoApplicationException extends AbstractClientException {

    static final ErrorCode START_PASO = new ErrorCode("error.start.paso", "Failed to start PASO.");

    PasoApplicationException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }
}