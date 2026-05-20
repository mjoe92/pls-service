package de.vw.paso.client.main.ribbonmenu.explorer;

import javafx.beans.property.BooleanProperty;

import de.vw.paso.client.main.ribbonmenu.CompareGroupListener;

public interface RibbonMenuExplorerListener extends CompareGroupListener {

  default void handleActionStuecklisteBearbeiten() {
  }

  default void handleActionStuecklisteErstellen() {
  }

  default void handleActionStuecklisteLoeschen() {
  }

  default void handleActionReload() {
  }

  default void handleActionFavorite() {
  }

  default void handleActionClearFilters() {
  }

  default void handleActionChangeOwnerGroup() {
  }

  void handleActionReestablish();

  BooleanProperty disablePropertyAddNew();

  BooleanProperty disablePropertyStuecklisteEditable();

  BooleanProperty disablePropertyFavorite();

  BooleanProperty disablePropertyClearFilters();

  BooleanProperty disablePropertyChangeOwnerGroup();

  BooleanProperty disablePropertyReestablish();

  BooleanProperty disablePropertyStuecklisteDeletable();
}
