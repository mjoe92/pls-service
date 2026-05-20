package de.vw.paso.pll.dev.contextvisualizer.util.event;

import java.util.ArrayList;
import java.util.List;

public class EventBus {

  public interface EventBusListener {
    void onEvent(Topic topic, Object obj);
  }

  private static List<EventBusListener> listeners = new ArrayList<>();

  public static void register(EventBusListener l) {
    listeners.add(l);
  }

  public static void publish(Topic topic, Object obj) {
    listeners.forEach(e -> e.onEvent(topic, obj));
  }
}
