package de.vw.paso.exception;

public class AdminPermissionException extends SecurityException {

    public AdminPermissionException() {
        super("Requires admin permission!");
    }
}