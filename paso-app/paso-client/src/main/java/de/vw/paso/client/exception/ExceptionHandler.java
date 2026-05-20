package de.vw.paso.client.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.MissingResourceException;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.exception.AbstractClientException;
import de.vw.paso.client.control.dialog.DialogUtil;
import de.vw.paso.client.util.SSLUtil;
import de.vw.paso.delegate.base.ErrorCode;
import de.vw.paso.exception.AbstractServerValidationException;
import de.vw.paso.exception.IServerException;
import de.vw.paso.exception.PlsRestException;
import de.vw.paso.exception.ServiceConsumer;
import de.vw.paso.utility.StringCommonTermsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    private static final String PLS_ERROR_BASE_KEY = "pls.error.";
    private static final ErrorCode PROXY_NOT_AUTHORIZED = new ErrorCode("PROXY_NOT_AUTHORIZED",
            "Client is not authorized");

    private static ExceptionHandler instance;

    private boolean dialogActive;

    private ExceptionHandler() {
    }

    public static ExceptionHandler instance() {
        if (instance == null) {
            instance = new ExceptionHandler();
        }

        return instance;
    }

    public void handleException(Throwable throwable, ServiceConsumer... consumers) {
        LOG.error("Exception occurred", throwable);

        if (throwable instanceof AbstractServerValidationException validationException) {
            for (ServiceConsumer consumer : consumers) {
                try {
                    validationException.accept(consumer);
                    return;
                } catch (ClassCastException ignored) {
                }
            }
        }
        if (throwable instanceof IServerException serverException) {
            String baseKey = serverException.getMessageKey();
            showDefaultErrorDialog(baseKey, getStackTrace(throwable));

            return;
        }

        if (throwable instanceof PlsRestException pre) {
            String key = PLS_ERROR_BASE_KEY + pre.getErrorCode().getErrorCode().toLowerCase();
            showDefaultErrorDialog(key, getStackTrace(throwable));

            return;
        }

        AbstractClientException exc = getAbstractException(throwable);
        if (PROXY_NOT_AUTHORIZED.equals(exc.getErrorCode())) {
            showReloginDialog(exc);

            return;
        }

        if (exc.getNestedThrowables() != null) {
            for (int i = 0; i < exc.getNestedThrowables().length; i++) {
                LOG.error("Nested Exception", exc.getNestedThrowables()[i]);
            }
        }

        String errorCodeKey = exc.getErrorCode().getKey().toLowerCase();
        showDefaultErrorDialog(errorCodeKey, getStackTrace(throwable));
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);

        return sw.toString();
    }

    private AbstractClientException getAbstractException(Throwable t) {
        if (t instanceof InvocationTargetException invocationTargetException) {
            t = invocationTargetException.getTargetException();
        }

        if (t instanceof AbstractClientException clientException) {
            return clientException;
        }

        if (t.getCause() instanceof AbstractClientException clientException) {
            return clientException;
        }

        return AbstractClientException.wrap(t);
    }

    private String getMessage(String baseKey, String termKey) {
        try {
            return I18N.getString(baseKey + "." + termKey);
        } catch (MissingResourceException exc) {
            return I18N.getString("default.exception." + termKey);
        }
    }

    private void showDefaultErrorDialog(String errorCodeKey, String contentKey) {
        String title = getMessage(errorCodeKey, StringCommonTermsUtil.TITLE_LOW_CASE);
        String header = getMessage(errorCodeKey, StringCommonTermsUtil.HEADER_LOW_CASE);
        String message = getMessage(errorCodeKey, StringCommonTermsUtil.TEXT_LOW_CASE);

        showDialog(title, header, message, contentKey);
    }

    private void showDefaultWarningDialog(String errorCodeKey) {
        String title = getMessage(errorCodeKey, StringCommonTermsUtil.TITLE_LOW_CASE);
        String header = getMessage(errorCodeKey, StringCommonTermsUtil.HEADER_LOW_CASE);
        String message = getMessage(errorCodeKey, StringCommonTermsUtil.TEXT_LOW_CASE);

        DialogUtil.showWarnDialog(title, header, message);
    }

    private void showDialog(String title, String header, String message, String content) {
        if (dialogActive) {
            return;
        }

        dialogActive = true;
        DialogUtil.showErrorDialog(title, header, message, content);
        dialogActive = false;
    }

    private void showReloginDialog(AbstractClientException exc) {
        if (dialogActive) {
            return;
        }

        dialogActive = true;
        String errorCode = exc.getErrorCode().getKey().toLowerCase();
        showDefaultWarningDialog(errorCode);

        try {
            SSLUtil.loadPkiCardAndSetSSLFactory();
        } catch (Exception e) {
            dialogActive = false;
            showDialog("Unknown Error", "Unknown Error", "An unknown error occurred", getStackTrace(e));
        } finally {
            dialogActive = false;
        }
    }
}