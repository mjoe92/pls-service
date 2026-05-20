package de.vw.paso.client.main.ribbonmenu.smartfix;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonButton;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroup;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.client.util.icon.FilterIcon;
import de.vw.paso.client.util.icon.StammdatenIcon;

public class RibbonMenuSmartFix extends RibbonMenu {

    private final RibbonMenuSmartFixTabListener listener;

    public RibbonMenuSmartFix(RibbonMenuSmartFixTabListener listener) {
        this.listener = listener;

        setText(I18N.getString("ribbonmenu.smartfix.title"));

        initialize();
    }

    private void initialize() {
        addMenuGroup(createGroupEdit());
        addMenuGroup(createGroupRefresh());
        addMenuGroup(createFilter());
    }

    private RibbonMenuGroup createGroupEdit() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("edit"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

        RibbonButton buttonAdd = new RibbonButton(I18N.getString("ribbonmenubutton.new"),
                StammdatenIcon.ADD_32X32.getImage());
        buttonAdd.setOnAction(e -> listener.handleActionAdd());
        buttonAdd.disableProperty().bind(listener.disablePropertyAdd());

        RibbonButton buttonEdit = new RibbonButton(I18N.getString("ribbonmenubutton.edit"),
                StammdatenIcon.EDIT_32X32.getImage());
        buttonEdit.setOnAction(e -> listener.handleActionEdit());
        buttonEdit.disableProperty().bind(listener.disablePropertyEdit());

        RibbonButton buttonRemove = new RibbonButton(I18N.getString("ribbonmenubutton.delete"),
                StammdatenIcon.REMOVE_32X32.getImage());
        buttonRemove.setOnAction(e -> listener.handleActionDelete());
        buttonRemove.disableProperty().bind(listener.disablePropertyRemove());

        itemBox.addButton(buttonAdd, buttonEdit, buttonRemove);
        group.addItemBox(itemBox);

        return group;
    }

    private RibbonMenuGroup createGroupRefresh() {
        RibbonMenuGroup group = new RibbonMenuGroup("load");
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
        RibbonButton buttonRefresh = new RibbonButton(I18N.getString("ribbonmenubutton.refresh"),
                ActionIcon.REFRESH_32X32.getImage());

        buttonRefresh.setOnAction(event -> listener.handleActionRefresh());

        itemBox.addButton(buttonRefresh);

        group.addItemBox(itemBox);

        return group;
    }

    private RibbonMenuGroup createFilter() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.filter"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
        RibbonButton buttonClearFilters = new RibbonButton(I18N.getString("ribbonmenubutton.clearFilters"),
                FilterIcon.CLEARFILTERS_32X32.getImage());

        buttonClearFilters.setOnAction(e -> listener.handleActionClearFilters());
        buttonClearFilters.disableProperty().bind(listener.disablePropertyClearFilters());

        itemBox.addButton(buttonClearFilters);

        group.addItemBox(itemBox);

        return group;
    }
}