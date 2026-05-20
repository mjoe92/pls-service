package de.vw.paso.client.main.ribbonmenu;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public interface SummaryGroupListener {

  BooleanProperty disablePropertyCloseSummary = new SimpleBooleanProperty(false);

  void handleActionCloseSummary();

  default BooleanProperty disablePropertyCloseSummary() {
    return disablePropertyCloseSummary;
  }

}
