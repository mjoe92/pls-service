package de.vw.paso.client.util;

import de.vw.paso.client.base.I18N;
import de.vw.paso.pls.Status;

public final class StatusUtil {

    private StatusUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getName(Status status) {
        return switch (status) {
            case INCOMPLETE -> I18N.getString("status.incomplete");
            case PENDING -> I18N.getString("status.pending");
            case READY -> I18N.getString("status.ready");
            case COMPLETE -> I18N.getString("status.complete");
            case ERROR -> I18N.getString("status.error");
        };
    }
}
