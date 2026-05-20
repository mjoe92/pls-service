package de.vw.paso.client.main.ribbonmenu.explorer;

import javafx.beans.property.SimpleBooleanProperty;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonButton;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroup;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.personaldata.PersonalDataManager;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.client.util.icon.ExplorerIcon;
import de.vw.paso.client.util.icon.FilterIcon;
import de.vw.paso.client.util.icon.UserManagementIcon;

public class RibbonMenuExplorer extends RibbonMenu {

    private final RibbonMenuExplorerListener listener;

    public RibbonMenuExplorer(RibbonMenuExplorerListener listener) {
        setText(I18N.getString("tab.explorer.title"));

        this.listener = listener;

        addMenuGroup(createGroupPartList());
        addMenuGroup(createFilter());
        addMenuGroup(createGroupCompare(listener));
        addMenuGroup(createPersonalData());
    }

    private RibbonMenuGroup createGroupPartList() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.stueckliste"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

        RibbonButton buttonNew = new RibbonButton(I18N.getString("ribbonmenubutton.fzgkonfig.neu"),
                ActionIcon.NEW_32X32.getImage());
        buttonNew.setOnAction(e -> listener.handleActionStuecklisteErstellen());
        buttonNew.disableProperty().bind(listener.disablePropertyAddNew());

        RibbonButton buttonEdit = new RibbonButton(I18N.getString("ribbonmenubutton.fzgkonfig.bearbeiten"),
                ActionIcon.EDIT_32X32.getImage());
        buttonEdit.setOnAction(e -> listener.handleActionStuecklisteBearbeiten());
        buttonEdit.disableProperty().bind(listener.disablePropertyStuecklisteEditable());

        RibbonButton buttonDelete = new RibbonButton(I18N.getString("ribbonmenubutton.fzgkonfig.loeschen"),
                ActionIcon.DELETE_32X32.getImage());
        buttonDelete.setOnAction(e -> listener.handleActionStuecklisteLoeschen());
        buttonDelete.disableProperty().bind(listener.disablePropertyStuecklisteDeletable());

        RibbonButton buttonFavorite = new RibbonButton(I18N.getString("ribbonmenubutton.fzgkonfig.favorite"),
                ExplorerIcon.EXPLORER_STAR_32x32.getImage());
        buttonFavorite.setOnAction(e -> listener.handleActionFavorite());
        buttonFavorite.disableProperty().bind(listener.disablePropertyFavorite());

        RibbonButton buttonReload = new RibbonButton(I18N.getString("refresh"), ActionIcon.REFRESH_32X32.getImage());
        buttonReload.setOnAction(e -> listener.handleActionReload());

        RibbonButton buttonChangeOwnerGroup = new RibbonButton(
                I18N.getString("ribbonmenubutton.fzgkonfig.change.ownergroup"), ActionIcon.EDIT_32X32.getImage());
        buttonChangeOwnerGroup.setOnAction(e -> listener.handleActionChangeOwnerGroup());
        buttonChangeOwnerGroup.disableProperty().bind(listener.disablePropertyChangeOwnerGroup());

        itemBox.addButton(buttonNew, buttonEdit, buttonDelete, buttonReload, buttonFavorite, buttonChangeOwnerGroup);

        if (UserProperties.getUser().isAdmin()) {
            RibbonButton buttonReestablish = new RibbonButton(
                    I18N.getString("ribbonmenubutton.fzgkonfig.reset-deletion"),
                    ActionIcon.RESET_DELETION_32X32.getImage());

            buttonReestablish.setOnAction(e -> listener.handleActionReestablish());
            buttonReestablish.disableProperty().bind(listener.disablePropertyReestablish());
            itemBox.addButton(buttonReestablish);
        }

        group.addItemBox(itemBox);

        return group;
    }

    private RibbonMenuGroup createFilter() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.filter"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

        RibbonButton buttonClearFilters = new RibbonButton(I18N.getString("ribbonmenubutton.fzgkonfig.clearFilters"),
                FilterIcon.CLEARFILTERS_32X32.getImage());

        buttonClearFilters.setOnAction(e -> listener.handleActionClearFilters());
        buttonClearFilters.disableProperty().bind(listener.disablePropertyClearFilters());

        itemBox.addButton(buttonClearFilters);

        group.addItemBox(itemBox);

        return group;
    }

    private RibbonMenuGroup createPersonalData() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.privateData"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

        RibbonButton buttonPrivateData = createRibbonButton(UserManagementIcon.USER_DATA_32x32,
                e -> new PersonalDataManager().askDeletePersonalData(UserProperties.getUserId()),
                new SimpleBooleanProperty(false));

        itemBox.addButton(buttonPrivateData);

        group.addItemBox(itemBox);

        return group;
    }
}