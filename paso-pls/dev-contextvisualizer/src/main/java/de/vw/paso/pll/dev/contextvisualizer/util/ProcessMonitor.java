package de.vw.paso.pll.dev.contextvisualizer.util;

import java.util.ArrayList;
import java.util.List;

public class ProcessMonitor {

  private List<ProcessUpdateListener> listeners = new ArrayList<>();

  public void addListener(ProcessUpdateListener l) {
    listeners.add(l);
  }

  public void setCurrentStep( String step) {
    for (ProcessUpdateListener l : listeners) {
      l.currentStepChange(step);
    }
  }
}
