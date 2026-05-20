package de.vw.paso.client.main.ribbonmenu;

import java.io.IOException;
import java.net.URISyntaxException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.base.AbstractController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuBar;
import de.vw.paso.client.main.ribbonmenu.adminarea.RibbonMenuAdminArea;
import de.vw.paso.client.main.ribbonmenu.adminarea.RibbonMenuAdminAreaEvent;
import de.vw.paso.client.main.ribbonmenu.compare.config.RibbonMenuCompareConfig;
import de.vw.paso.client.main.ribbonmenu.compare.config.RibbonMenuCompareConfigEvent;
import de.vw.paso.client.main.ribbonmenu.compare.costgroup.RibbonMenuCompareCostGroup;
import de.vw.paso.client.main.ribbonmenu.compare.costgroup.RibbonMenuCompareCostGroupEvent;
import de.vw.paso.client.main.ribbonmenu.compare.fgset.RibbonMenuCompareFgSet;
import de.vw.paso.client.main.ribbonmenu.compare.fgset.RibbonMenuCompareFgSetEvent;
import de.vw.paso.client.main.ribbonmenu.compare.partgroup.RibbonMenuComparePartGroup;
import de.vw.paso.client.main.ribbonmenu.compare.partgroup.RibbonMenuComparePartGroupEvent;
import de.vw.paso.client.main.ribbonmenu.compare.partlist.RibbonMenuPartList;
import de.vw.paso.client.main.ribbonmenu.compare.partlist.RibbonMeunComparePartlisEvent;
import de.vw.paso.client.main.ribbonmenu.costgroup.RibbonMenuCostGroup;
import de.vw.paso.client.main.ribbonmenu.costgroup.RibbonMenuCostGroupEvent;
import de.vw.paso.client.main.ribbonmenu.efs.RibbonMenuEfs;
import de.vw.paso.client.main.ribbonmenu.efs.RibbonMenuEfsEvent;
import de.vw.paso.client.main.ribbonmenu.explorer.RibbonMenuExplorer;
import de.vw.paso.client.main.ribbonmenu.explorer.RibbonMenuExplorerEvent;
import de.vw.paso.client.main.ribbonmenu.fgset.RibbonMenuFgSet;
import de.vw.paso.client.main.ribbonmenu.fgset.RibbonMenuFgSetEvent;
import de.vw.paso.client.main.ribbonmenu.fzgkonfig.RibbonMenuFzgKonfig;
import de.vw.paso.client.main.ribbonmenu.fzgkonfig.RibbonMenuFzgKonfigEvent;
import de.vw.paso.client.main.ribbonmenu.partgroup.RibbonMenuPartGroup;
import de.vw.paso.client.main.ribbonmenu.partgroup.RibbonMenuPartGroupEvent;
import de.vw.paso.client.main.ribbonmenu.smartfix.RibbonMenuSmartFix;
import de.vw.paso.client.main.ribbonmenu.smartfix.RibbonMenuSmartFixEvent;
import de.vw.paso.client.main.ribbonmenu.stammdaten.RibbonMenuMasterData;
import de.vw.paso.client.main.ribbonmenu.stammdaten.RibbonMenuStammdatenEvent;
import de.vw.paso.client.main.ribbonmenu.start.RibbonMenuStart;
import de.vw.paso.client.main.ribbonmenu.start.RibbonMenuStartEvent;
import de.vw.paso.client.main.ribbonmenu.tiwhrequestqueue.RibbonMenuTiWhRequestQueue;
import de.vw.paso.client.main.ribbonmenu.tiwhrequestqueue.RibbonMenuTiWhRequestQueueEvent;
import de.vw.paso.client.main.ribbonmenu.usermanagement.RibbonMenuUserManagement;
import de.vw.paso.client.main.ribbonmenu.usermanagement.RibbonMenuUserManagementEvent;
import de.vw.paso.client.personaldata.PersonalDataManager;
import de.vw.paso.client.privacy.PrivacyDialog;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.util.icon.MessageIcon;

public class RibbonMenuController extends AbstractController {

    private final RibbonMenuBar menuBar;
    private final Label privacyLabel;
    private final StackPane stackPane;

    private RibbonMenu menuStart;
    private RibbonMenu menuAdminArea;

    public RibbonMenuController() {
        menuBar = new RibbonMenuBar();
        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);

        privacyLabel = new Label();
        privacyLabel.setGraphic(new ImageView(MessageIcon.INFO_16X16.getImage()));
        privacyLabel.setOnMouseClicked(this::showPrivacyMenu);
        StackPane.setAlignment(privacyLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(privacyLabel, new Insets(5, 5, 0, 0));

        stackPane = new StackPane();
        stackPane.getChildren().addAll(menuBar, privacyLabel);
    }

    public StackPane getControl() {
        return stackPane;
    }

    public RibbonMenuBar getRibbonMenuBar() {
        return menuBar;
    }

    @Subscribe
    public void handleRibbonMenuStartEvent(RibbonMenuStartEvent event) {
        try {
            if (menuStart == null) {
                menuStart = new RibbonMenuStart(event.getListener());
                menuBar.addMenu(menuStart);
            }
            removeMenus();
        } catch (Exception exception) {
            handleException(new RibbonMenuException(exception, RibbonMenuException.EC_LOAD_RIBBONMENU));
        }
    }

    @Subscribe
    public void handleRibbonMenuAdminAreaEvent(RibbonMenuAdminAreaEvent event) {
        try {
            if (menuAdminArea == null) {
                menuAdminArea = new RibbonMenuAdminArea(event.getListener());
                menuBar.addMenu(menuAdminArea);
            }
            removeMenus();
        } catch (Exception exception) {
            handleException(new RibbonMenuException(exception, RibbonMenuException.EC_LOAD_RIBBONMENU));
        }
    }

    @Subscribe
    public void handleRibbonMenuExplorerEvent(RibbonMenuExplorerEvent event) {
        changeRibbonMenu(new RibbonMenuExplorer(event.getListener()));
    }

    @Subscribe
    public void handleRibbonMenuStammdatenEvent(RibbonMenuStammdatenEvent event) {
        changeRibbonMenu(new RibbonMenuMasterData(event.getListener()));
    }

    @Subscribe
    public void handleRibbonMenuTiWhRequestQueueEvent(RibbonMenuTiWhRequestQueueEvent event) {
        changeRibbonMenu(new RibbonMenuTiWhRequestQueue(event.getListener()));
    }

    @Subscribe
    public void handleRibbonMenuUserManagementEvent(RibbonMenuUserManagementEvent event) {
        changeRibbonMenu(new RibbonMenuUserManagement(event.getListener()));
    }

    @Subscribe
    public void handleRibbonMenuEfsEvent(RibbonMenuEfsEvent event) {
        changeRibbonMenu(new RibbonMenuEfs(event.getListener(), event.getTitle()));
    }

    @Subscribe
    public void handleRibbonMenuFzgConfigEvent(RibbonMenuFzgKonfigEvent event) {
        changeRibbonMenu(new RibbonMenuFzgKonfig(event.getListener(), event.getTitle()));
    }

    @Subscribe
    public void handleRibbonMenuFgSetEvent(RibbonMenuFgSetEvent event) {
        changeRibbonMenu(new RibbonMenuFgSet(event.getListener(), event.getTitle()));
    }

    @Subscribe
    public void handleRibbonMenuCostGroupEvent(RibbonMenuCostGroupEvent event) {
        changeRibbonMenu(new RibbonMenuCostGroup(event.getListener(), event.getTitle()));
    }

    @Subscribe
    public void handleRibbonMenuPartGroupEvent(RibbonMenuPartGroupEvent event) {
        changeRibbonMenu(new RibbonMenuPartGroup(event.getListener(), event.getTitle()));
    }

    @Subscribe
    public void handleRibbonMenuCompareEvent(RibbonMenuCompareFgSetEvent event) {
        changeRibbonMenu(new RibbonMenuCompareFgSet(event.getListener(), event.getTitle()));
    }

    @Subscribe
    public void handleRibbonMenuCompareEvent(RibbonMeunComparePartlisEvent event) {
        changeRibbonMenu(new RibbonMenuPartList(event.getListener(), event.getTitle()));
    }

    @Subscribe
    public void handleRibbonMenuCompareConfigEvent(RibbonMenuCompareConfigEvent event) {
        changeRibbonMenu(new RibbonMenuCompareConfig(event.getListener(), event.getTitle()));
    }

    @Subscribe
    public void handleRibbonMenuCompareCostGroupEvent(RibbonMenuCompareCostGroupEvent event) {
        changeRibbonMenu(new RibbonMenuCompareCostGroup(event.getListener(), event.getTitle()));
    }

    @Subscribe
    public void handleRibbonMenuComparePartGroupEvent(RibbonMenuComparePartGroupEvent event) {
        changeRibbonMenu(new RibbonMenuComparePartGroup(event.getListener(), event.getTitle()));
    }

    @Subscribe
    public void handleRibbonMenuSmartFix(RibbonMenuSmartFixEvent event) {
        changeRibbonMenu(new RibbonMenuSmartFix(event.getListener()));
    }

    private void changeRibbonMenu(RibbonMenu menu) {
        try {
            removeMenus();
            menuBar.addMenu(menu);
        } catch (Exception exception) {
            handleException(new RibbonMenuException(exception, RibbonMenuException.EC_LOAD_RIBBONMENU));
        }
    }

    private void showPrivacyMenu(MouseEvent mouseEvent) {
        MenuItem showDisclaimerMenuItem = new MenuItem(I18N.getString("statusbar.privacy.menu.showprivacypolicy"));
        showDisclaimerMenuItem.setOnAction(a -> {
            try {
                new PrivacyDialog().loadPrivacyPolicy();
            } catch (URISyntaxException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        MenuItem deletePersonalDataMenuItem = new MenuItem(I18N.getString("statusbar.privacy.menu.deleteData"));
        deletePersonalDataMenuItem.setOnAction(
                actionEvent -> new PersonalDataManager().askDeletePersonalData(UserProperties.getUserId()));

        ContextMenu privacyMenu = new ContextMenu();
        privacyMenu.getItems().addAll(showDisclaimerMenuItem, deletePersonalDataMenuItem);
        privacyMenu.show(privacyLabel, mouseEvent.getScreenX(), mouseEvent.getScreenY());
    }

    private void removeMenus() {
        menuBar.getTabs().removeIf(tab -> tab != menuStart && tab != menuAdminArea);
    }
}