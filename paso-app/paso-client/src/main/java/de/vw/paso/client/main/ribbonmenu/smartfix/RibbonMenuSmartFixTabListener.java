package de.vw.paso.client.main.ribbonmenu.smartfix;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;

public interface RibbonMenuSmartFixTabListener {

  void handleActionAdd();

  void handleActionEdit();

  void handleActionDelete();

  void handleActionSetActive();

  void handleActionRefresh();

  void handleActionClearFilters();

  BooleanProperty disablePropertyClearFilters();

  ObservableValue<? extends Boolean> disablePropertyAdd();

  ObservableValue<? extends Boolean> disablePropertyEdit();

  ObservableValue<? extends Boolean> disablePropertySetActive();

  ObservableValue<? extends Boolean> disablePropertyRemove();
}
