package de.vw.paso.client.stammdaten;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.main.ribbonmenu.stammdaten.RibbonMenuMasterDataListener;

public abstract class AbstractMasterDataController<V> extends BaseController<V>
    implements RibbonMenuMasterDataListener {

    private BooleanProperty disablePropertyAdd;
    private BooleanProperty disablePropertyEdit;
    private BooleanProperty disablePropertyRemove;
    private BooleanProperty disablePropertyRefresh;
    private BooleanProperty disablePropertyResetFilters;

    protected static final String DEFAULT_ITEM_NAME = "DEFAULT";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    @Override
    public final BooleanProperty disablePropertyAdd() {
        if (disablePropertyAdd == null) {
            disablePropertyAdd = new SimpleBooleanProperty(disableAdd());
        }

        return disablePropertyAdd;
    }

    @Override
    public final BooleanProperty disablePropertyEdit() {
        if (disablePropertyEdit == null) {
            disablePropertyEdit = new SimpleBooleanProperty(disableEdit());
        }

        return disablePropertyEdit;
    }

    @Override
    public final BooleanProperty disablePropertyRefresh() {
        if (disablePropertyRefresh == null) {
            disablePropertyRefresh = new SimpleBooleanProperty(disableRefresh());
        }

        return disablePropertyRefresh;
    }

    @Override
    public final BooleanProperty disablePropertyRemove() {
        if (disablePropertyRemove == null) {
            disablePropertyRemove = new SimpleBooleanProperty(disableRemove());
        }

        return disablePropertyRemove;
    }

    @Override
    public final BooleanProperty disablePropertyResetFilters() {
        if (disablePropertyResetFilters == null) {
            disablePropertyResetFilters = new SimpleBooleanProperty(true);
        }

        return disablePropertyResetFilters;
    }

    protected boolean disableAdd() {
        return true;
    }

    protected boolean disableEdit() {
        return true;
    }

    protected boolean disableRemove() {
        return true;
    }

    protected boolean disableRefresh() {
        return true;
    }

    protected <T> void handleTableSelection(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        boolean canEdit, canRemove;
        if (newValue == null) {
            canEdit = true;
            canRemove = true;
        } else {
            canEdit = false;
            canRemove = false;
        }

        disablePropertyEdit().set(canEdit);
        disablePropertyRemove().set(canRemove);
    }

    public String getCustomDeleteMessageKey() {
        return "currentStammdatenController.deleteMessage";
    }
}
