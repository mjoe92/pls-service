package de.vw.paso.client.util;

import com.google.common.eventbus.SubscriberExceptionHandler;
import de.vw.paso.client.exception.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventBus extends com.google.common.eventbus.EventBus {

  private static Logger log = LoggerFactory.getLogger(EventBus.class);

  private static EventBus instance;

  public static EventBus getInstance() {
    if (instance == null) {
      SubscriberExceptionHandler handler = (exception, context) -> {
        log.error("Could not execute event {} in {}", context.getEvent(), context.getSubscriber());
        ExceptionHandler.instance().handleException(exception);
      };
      instance = new EventBus(handler);
    }
    return instance;
  }

  public EventBus(SubscriberExceptionHandler handler) {
    super(handler);
  }
}
