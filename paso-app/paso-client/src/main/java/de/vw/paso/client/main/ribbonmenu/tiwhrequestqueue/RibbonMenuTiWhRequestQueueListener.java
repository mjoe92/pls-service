package de.vw.paso.client.main.ribbonmenu.tiwhrequestqueue;

import javafx.beans.property.BooleanProperty;

public interface RibbonMenuTiWhRequestQueueListener {

  default void handleActionRefresh() {
  }

  default void handleActionClearFilters() {
  }

  BooleanProperty disablePropertyClearFilters();
}
