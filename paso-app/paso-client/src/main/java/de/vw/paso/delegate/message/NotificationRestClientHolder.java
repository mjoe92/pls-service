package de.vw.paso.delegate.message;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.service.message.NotificationRestService;

public class NotificationRestClientHolder {

    private static NotificationRestService INSTANCE;

    private NotificationRestClientHolder() {
    }

    public static void setInstance(NotificationRestService instance) {
        NotificationRestClientHolder.INSTANCE = instance;
    }

    public static NotificationRestService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (NotificationRestClientHolder.class) {
                INSTANCE = new NotificationRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
            }
        }
        return INSTANCE;
    }
}
