package de.vw.paso.client.main.ribbonmenu.usermanagement;

import javafx.beans.property.BooleanProperty;

public interface RibbonMenuUserManagementListener {

  void handleActionRefresh();

  void handleActionClearFilters();

  BooleanProperty disablePropertyRefresh();

  BooleanProperty disablePropertyClearFilters();

}
