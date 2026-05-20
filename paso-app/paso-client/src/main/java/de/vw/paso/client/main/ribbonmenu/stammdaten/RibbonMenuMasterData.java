package de.vw.paso.client.main.ribbonmenu.stammdaten;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonButton;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroup;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.util.icon.FilterIcon;
import de.vw.paso.client.util.icon.StammdatenIcon;

public class RibbonMenuMasterData extends RibbonMenu {

    private final RibbonMenuMasterDataListener listener;

    public RibbonMenuMasterData(RibbonMenuMasterDataListener listener) {
        setText(I18N.getString("ribbonmenu.stammdaten.title"));
        this.listener = listener;

        createGroupEdit();
        createGroupRefresh();
        createGroupFilter();
    }

    private void createGroupEdit() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.bearbeiten"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

        RibbonButton buttonAdd = new RibbonButton(I18N.getString("ribbonmenubutton.neu"),
            StammdatenIcon.ADD_32X32.getImage());
        buttonAdd.setOnAction(e -> listener.handleActionAdd());
        buttonAdd.disableProperty().bind(listener.disablePropertyAdd());

        RibbonButton buttonEdit = new RibbonButton(I18N.getString("ribbonmenubutton.bearbeiten"),
            StammdatenIcon.EDIT_32X32.getImage());
        buttonEdit.setOnAction(e -> listener.handleActionEdit());
        buttonEdit.disableProperty().bind(listener.disablePropertyEdit());

        RibbonButton buttonRemove = new RibbonButton(I18N.getString("ribbonmenubutton.loeschen"),
            StammdatenIcon.REMOVE_32X32.getImage());
        buttonRemove.setOnAction(e -> listener.handleActionDelete());
        buttonRemove.disableProperty().bind(listener.disablePropertyRemove());

        itemBox.addButton(buttonAdd, buttonEdit, buttonRemove);
        group.addItemBox(itemBox);

        addMenuGroup(group);
    }

    private void createGroupRefresh() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("load"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

        RibbonButton buttonRefresh = new RibbonButton(I18N.getString("refresh"),
            StammdatenIcon.REFRESH_32X32.getImage());
        buttonRefresh.setOnAction(e -> listener.handleActionRefresh());
        buttonRefresh.disableProperty().bind(listener.disablePropertyRefresh());

        itemBox.addButton(buttonRefresh);
        group.addItemBox(itemBox);

        addMenuGroup(group);
    }

    private void createGroupFilter() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.filter"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

        RibbonButton buttonResetFilters = new RibbonButton(I18N.getString("ribbonmenubutton.clearFilters"),
            FilterIcon.CLEARFILTERS_32X32.getImage());
        buttonResetFilters.setOnAction(e -> listener.handleActionResetFilters());
        buttonResetFilters.disableProperty().bind(listener.disablePropertyResetFilters());

        itemBox.addButton(buttonResetFilters);
        group.addItemBox(itemBox);

        addMenuGroup(group);
    }
}
