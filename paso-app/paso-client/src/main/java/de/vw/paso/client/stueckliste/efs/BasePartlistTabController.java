package de.vw.paso.client.stueckliste.efs;

import javafx.fxml.Initializable;

import de.vw.paso.client.main.tab.AbstractMainTabController;
import de.vw.paso.client.main.tab.MainTabPaneController;
import de.vw.paso.service.user.VehiclePartListDTO;

public abstract class BasePartlistTabController extends AbstractMainTabController implements Initializable {

    private VehiclePartListDTO vehiclePartList;

    private MainTabPaneController mainTabPaneController;

    public final Long getVehiclePartListId() {
        return vehiclePartList.getVehiclePartListId();
    }

    public VehiclePartListDTO getVehiclePartList() {
        return vehiclePartList;
    }

    public void setVehiclePartList(VehiclePartListDTO vehiclePartList) {
        this.vehiclePartList = vehiclePartList;
    }

    public MainTabPaneController getMainTabPaneController() {
        return mainTabPaneController;
    }

    public void setMainTabPaneController(MainTabPaneController mainTabPaneController) {
        this.mainTabPaneController = mainTabPaneController;
    }
}
