package de.vw.paso.client.main.ribbonmenu.tiwhrequestqueue;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonButton;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroup;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.client.util.icon.FilterIcon;

public class RibbonMenuTiWhRequestQueue extends RibbonMenu {

    private final RibbonMenuTiWhRequestQueueListener listener;

    public RibbonMenuTiWhRequestQueue(RibbonMenuTiWhRequestQueueListener listener) {
        this.listener = listener;

        setText(I18N.getString("ribbonmenu.tiwhrequestqueue.title"));

        initialize();
    }

    private void initialize() {
        addMenuGroup(createGroupRefresh());
        addMenuGroup(createFilter());
    }

    private RibbonMenuGroup createGroupRefresh() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("load"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
        RibbonButton buttonRefresh = new RibbonButton(I18N.getString("ribbonmenubutton.refresh"),
                ActionIcon.REFRESH_32X32.getImage());

        buttonRefresh.setOnAction(e -> listener.handleActionRefresh());

        itemBox.addButton(buttonRefresh);

        group.addItemBox(itemBox);

        return group;
    }

    private RibbonMenuGroup createFilter() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.filter"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
        RibbonButton buttonClearFilters = new RibbonButton(I18N.getString("ribbonmenubutton.clearFilters"),
                FilterIcon.CLEARFILTERS_32X32.getImage());

        buttonClearFilters.setOnAction(event -> listener.handleActionClearFilters());
        buttonClearFilters.disableProperty().bind(listener.disablePropertyClearFilters());

        itemBox.addButton(buttonClearFilters);

        group.addItemBox(itemBox);

        return group;
    }
}