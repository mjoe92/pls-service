package de.vw.paso.client.main.ribbonmenu.stammdaten;

import javafx.beans.property.BooleanProperty;

public interface RibbonMenuMasterDataListener {

    void handleActionAdd();

    void handleActionEdit();

    void handleActionDelete();

    void handleActionRefresh();

    void handleActionResetFilters();

    BooleanProperty disablePropertyAdd();

    BooleanProperty disablePropertyEdit();

    BooleanProperty disablePropertyRemove();

    BooleanProperty disablePropertyRefresh();

    BooleanProperty disablePropertyResetFilters();
}