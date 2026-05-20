package de.vw.paso.exception;

public class PlsServiceException extends ServerException {

    public PlsServiceException(String message) {
        super(message);
    }

    public PlsServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public PlsServiceException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String getMessageKey() {
        return "pls.PlsServiceException";
    }
}
