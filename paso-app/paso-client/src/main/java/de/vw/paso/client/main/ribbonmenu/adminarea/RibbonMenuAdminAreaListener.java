package de.vw.paso.client.main.ribbonmenu.adminarea;

import javafx.beans.property.BooleanProperty;

public interface RibbonMenuAdminAreaListener {

    void handleActionStartUserManagement();

    void handleActionStartTiWhRequestQueue();

    void handleActionStartSmartFixView();

    void handleActionStartMbtImport();

    void handleActionStartStammdaten();

    BooleanProperty toggleDisableNonAdminArea();
}