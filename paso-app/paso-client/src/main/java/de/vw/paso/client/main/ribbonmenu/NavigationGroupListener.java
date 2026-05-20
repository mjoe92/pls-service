package de.vw.paso.client.main.ribbonmenu;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public interface NavigationGroupListener {

  BooleanProperty disablePropertyCollapseTree = new SimpleBooleanProperty(false);
  BooleanProperty disablePropertyCollapseAllTree = new SimpleBooleanProperty(false);
  BooleanProperty disablePropertyExpandTree = new SimpleBooleanProperty(false);
  BooleanProperty disablePropertyExpandAllTree = new SimpleBooleanProperty(false);
  BooleanProperty disablePropertyClearFilters = new SimpleBooleanProperty(false);
  BooleanProperty disablePropertyResetSorting = new SimpleBooleanProperty(true);

  void handleActionNavigateBack();

  void handleActionNavigateForward();

  void handleActionCollapseTree();

  void handleActionCollapseAllTree();

  void handleActionExpandTree();

  void handleActionExpandAllTree();

  void handleActionClearFilters();

  void handleActionResetSorting();

  BooleanProperty disablePropertyNavigateBack();

  BooleanProperty disablePropertyNavigateForward();

  default BooleanProperty disablePropertyCollapseTree() {
    return disablePropertyCollapseTree;
  }

  default BooleanProperty disablePropertyCollapseAllTree() {
    return disablePropertyCollapseAllTree;
  }

  default BooleanProperty disablePropertyExpandTree() {
    return disablePropertyExpandTree;
  }

  default BooleanProperty disablePropertyExpandAllTree() {
    return disablePropertyExpandAllTree;
  }

  default BooleanProperty disablePropertyClearFilters() {
    return disablePropertyClearFilters;
  }

  default BooleanProperty disablePropertyResetSorting() {
     return disablePropertyResetSorting;
  }

}
