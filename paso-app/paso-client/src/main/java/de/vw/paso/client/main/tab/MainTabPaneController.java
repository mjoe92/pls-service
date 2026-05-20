package de.vw.paso.client.main.tab;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.PasoApplication;
import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.dialog.DialogUtil;
import de.vw.paso.client.exception.ControllerException;
import de.vw.paso.client.explorer.ExplorerTabController;
import de.vw.paso.client.explorer.vehicleconfig.event.FzgKonfigEvent;
import de.vw.paso.client.explorer.vehicleconfig.event.ShowCompareTabEvent;
import de.vw.paso.client.explorer.vehicleconfig.event.ShowEfsTabEvent;
import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;
import de.vw.paso.client.main.ribbonmenu.adminarea.RibbonMenuAdminAreaEvent;
import de.vw.paso.client.main.ribbonmenu.adminarea.RibbonMenuAdminAreaListener;
import de.vw.paso.client.main.ribbonmenu.start.RibbonMenuStartEvent;
import de.vw.paso.client.main.ribbonmenu.start.RibbonMenuStartListener;
import de.vw.paso.client.smartfix.SmartFixTabController;
import de.vw.paso.client.stammdaten.MasterDataTabController;
import de.vw.paso.client.stueckliste.FzgKonfigMainTabController;
import de.vw.paso.client.stueckliste.compare.ComparePartListMainTabController;
import de.vw.paso.client.stueckliste.compare.ReopenCompareTabsEvent;
import de.vw.paso.client.stueckliste.efs.event.PartListLoadedEvent;
import de.vw.paso.client.stueckliste.event.SelectEfsElementOnEfsTabEvent;
import de.vw.paso.client.tiwh.TiWhRequestQueueTabController;
import de.vw.paso.client.userrightmanagement.UserRightManagementTabController;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.util.desktop.DesktopLinkCreator;
import de.vw.paso.delegate.buildinfo.BuildInfoRestClientHolder;
import de.vw.paso.delegate.mbtimport.MbtImportRestClientHolder;
import de.vw.paso.delegate.stueckliste.userproperty.UserPropertyRestClientHolder;
import de.vw.paso.service.buildinfo.ServerBuildInfoDTO;
import de.vw.paso.service.userproperty.SaveAllUserPropertiesDTO;
import de.vw.paso.service.userproperty.SaveUserPropertyDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.user.PropertyType;
import de.vw.paso.utility.StringConstant;

@FXController(name = "main-tab-pane")
public class MainTabPaneController extends BaseController<TabPane>
        implements Initializable, RibbonMenuStartListener, RibbonMenuAdminAreaListener {

    private static final Map<Tab, AbstractMainTabController> TAB_CONTROLLER_MAP = new HashMap<>();

    private final Map<Long, Tab> vehicleConfigMainControllerMap;

    @FXML
    private TabPane mainTabPane;

    private ExplorerTabController explorerTab;
    private MasterDataTabController masterDataTab;
    private UserRightManagementTabController userManagementTab;
    private TiWhRequestQueueTabController tiWhRequestQueueTab;
    private SmartFixTabController smartFixTabController;
    private CompareRequest compareRequest;

    private BooleanProperty disableNonUserAdminArea;

    public MainTabPaneController() {
        vehicleConfigMainControllerMap = new HashMap<>();
    }

    public static Map<Tab, AbstractMainTabController> getMapTabController() {
        return TAB_CONTROLLER_MAP;
    }

    @Override
    public TabPane getControl() {
        return mainTabPane;
    }

    @Override
    public Parent getStyleableParent() {
        return mainTabPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        mainTabPane.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleTabSelectionChange(newValue));

        changeRibbonMenu(new RibbonMenuStartEvent(this));
        changeRibbonMenu(new RibbonMenuAdminAreaEvent(this));

        handleActionStartExplorer();
    }

    @Override
    public void handleActionStartMbtImport() {
        doAsync(() -> MbtImportRestClientHolder.getInstance().importData(), this::updateBuildInfo);
    }

    @Override
    public void handleActionStartExplorer() {
        if (explorerTab == null) {
            explorerTab = load(ExplorerTabController.class);
            explorerTab.setParentMainTabPaneController(this);
            initTab(explorerTab);
        } else {
            mainTabPane.getSelectionModel().select(explorerTab.getControl());
        }
    }

    @Override
    public void handleActionStartStammdaten() {
        if (masterDataTab == null) {
            masterDataTab = load(MasterDataTabController.class);
            initTab(masterDataTab);
        } else {
            mainTabPane.getSelectionModel().select(masterDataTab.getControl());
        }
    }

    @Override
    public void handleActionStartUserManagement() {
        if (userManagementTab == null) {
            userManagementTab = load(UserRightManagementTabController.class);
            initTab(userManagementTab);
        } else {
            mainTabPane.getSelectionModel().select(userManagementTab.getControl());
        }
    }

    @Override
    public void handleActionStartSmartFixView() {
        if (smartFixTabController == null) {
            smartFixTabController = load(SmartFixTabController.class);
            initTab(smartFixTabController);
        } else {
            mainTabPane.getSelectionModel().select(smartFixTabController.getControl());
        }
    }

    @Override
    public void handleActionStartTiWhRequestQueue() {
        if (tiWhRequestQueueTab == null) {
            tiWhRequestQueueTab = load(TiWhRequestQueueTabController.class);
            initTab(tiWhRequestQueueTab);
        } else {
            mainTabPane.getSelectionModel().select(tiWhRequestQueueTab.getControl());
        }
    }

    @Override
    public BooleanProperty toggleDisableNonAdminArea() {
        if (disableNonUserAdminArea == null) {
            disableNonUserAdminArea = new SimpleBooleanProperty(false);
        }

        boolean disable = !UserProperties.getUser().isAdmin();
        disableNonUserAdminArea.set(disable);

        return disableNonUserAdminArea;
    }

    @Override
    public void handleActionRefreshRights() {
        UserProperties.reloadUser();

        boolean disable = toggleDisableNonAdminArea().get();
        if (disable) {
            closeTab(tiWhRequestQueueTab, userManagementTab, smartFixTabController, masterDataTab);
        }
    }

    @Override
    public void handleActionDesktopLink() {
        try {
            DesktopLinkCreator.createPasoDesktopLink();
        } catch (IOException e) {
            handleException(e);
        }
    }

    public List<Long> getOpenPartListIDs() {
        Collection<Tab> tabs = mainTabPane.getTabs();
        List<Long> orderedController = new ArrayList<>(tabs.size());
        for (Tab tab : tabs) {
            AbstractMainTabController ctrl = TAB_CONTROLLER_MAP.get(tab);
            if (ctrl instanceof FzgKonfigMainTabController fzgConfigController) {
                Long vehicleConfigId = fzgConfigController.getVehicleConfig().getId();
                orderedController.add(vehicleConfigId);
            }
        }

        return orderedController;
    }

    @Subscribe
    private void handleActionStartNewFzgConfig(FzgKonfigEvent event) {
        try {
            for (AbstractMainTabController control : TAB_CONTROLLER_MAP.values()) {
                if (control instanceof FzgKonfigMainTabController fzgConfigController
                        && fzgConfigController.getVehicleConfig().getId() == null) {
                    mainTabPane.getSelectionModel().select(control.getControl());
                    return;
                }
            }

            FzgKonfigMainTabController controller = load(FzgKonfigMainTabController.class);
            controller.setMainTabPaneController(this);
            controller.setVehicleConfig(null);
            controller.setBrand(event.getBrand());
            setCloseAction(controller);
            initTab(controller);
        } catch (ControllerException ex) {
            handleException(ex);
        }
    }

    @Subscribe
    private void handleActionStartEfs(ShowEfsTabEvent event) {
        VehicleConfigDTO vehicleConfig = event.vehicleConfig();
        addTabEfs(vehicleConfig, event.partListCreated(), false);

        doAsync(() -> UserPropertyRestClientHolder.getInstance()
                        .save(new SaveUserPropertyDTO(PropertyType.RECENTLY_USED, vehicleConfig.getId().toString())),
                r -> explorerTab.fireRecentlyUsedConfigEvent());
        UserProperties.setRecentlyUsedSetVersionId(vehicleConfig.getSetVersionId());
    }

    @Subscribe
    private void handleActionStartCompare(ShowCompareTabEvent event) {
        for (VehicleConfigDTO config : event.getVehicleConfigs()) {
            addTabEfs(config, false, true);
        }

        List<String> recentlyUsedIds = event.getVehicleConfigs().stream().map(vc -> vc.getId().toString()).toList();

        doAsync(() -> UserPropertyRestClientHolder.getInstance()
                        .saveAll(new SaveAllUserPropertiesDTO(PropertyType.RECENTLY_USED, recentlyUsedIds)),
                r -> explorerTab.fireRecentlyUsedConfigEvent());
        compareRequest = new CompareRequest(event.getVehicleConfigs(), event.getReferenceVehicleConfig());
    }

    @Subscribe
    private void eventReopenCompareTabs(ReopenCompareTabsEvent event) {
        for (VehicleConfigDTO config : event.vehicleConfigs()) {
            addTabEfs(config, false, true);
        }
    }

    @Subscribe
    private void handlePartListLoaded(PartListLoadedEvent event) {
        if (compareRequest != null) {
            compareRequest.setLoaded(event.getVehicleConfig());
            if (compareRequest.startCompare()) {
                addTabCompare(compareRequest.getVehicleConfigs(), compareRequest.getReferencingVehicleConfig());
                compareRequest = null;
            }
        }
    }

    @Subscribe
    private void onSelectEfsElementOnEfsTabEvent(SelectEfsElementOnEfsTabEvent event) {
        if (event.getVehicleConfigId() != null && event.isClearFilter()) {
            Tab tab = vehicleConfigMainControllerMap.get(event.getVehicleConfigId());
            if (tab != null) {
                mainTabPane.getSelectionModel().select(tab);
            }
        }
    }

    private void handleTabSelectionChange(Tab selectedTab) {
        AbstractRibbonMenuEvent<?> event;
        if (selectedTab == null) {
            event = new RibbonMenuStartEvent(this);
        } else {
            AbstractMainTabController controller = TAB_CONTROLLER_MAP.get(selectedTab);
            event = controller == null ? null : controller.getRibbonMenuEvent();
        }

        changeRibbonMenu(event);
    }

    private void setCloseAction(FzgKonfigMainTabController controller) {
        controller.setCloseAction(() -> {
            VehicleConfigDTO vehicleConfig = controller.getVehicleConfig();
            if (vehicleConfig != null) {
                vehicleConfigMainControllerMap.remove(vehicleConfig.getId());
            }

            mainTabPane.getTabs().remove(controller.getControl());
            handleOnTabClosed(controller.getControl());
        });
    }

    private void addTabCompare(List<VehicleConfigDTO> vehicleConfigs, VehicleConfigDTO vehicleConfig) {
        ComparePartListMainTabController controller = load(ComparePartListMainTabController.class);
        controller.setData(vehicleConfigs, vehicleConfig);
        initTab(controller);
    }

    private void addTabEfs(VehicleConfigDTO vehicleConfig, boolean reloadExistingTab, boolean isCompare) {
        for (AbstractMainTabController control : TAB_CONTROLLER_MAP.values()) {
            if (control instanceof FzgKonfigMainTabController tabController
                    && tabController.getVehicleConfig().getId() != null && tabController.getVehicleConfig().getId()
                    .equals(vehicleConfig.getId())) {
                selectController(vehicleConfig, reloadExistingTab, isCompare, control, tabController);
                return;
            }
        }

        loadTabController(vehicleConfig, isCompare);
    }

    private void selectController(VehicleConfigDTO vehicleConfig, boolean reloadExistingTab, boolean isCompare,
            AbstractMainTabController control, FzgKonfigMainTabController tabController) {
        if (!isCompare) {
            mainTabPane.getSelectionModel().select(control.getControl());
        }

        if (reloadExistingTab) {
            tabController.loadVehicleConfig();
            return;
        }

        EventBus.getInstance().post(new PartListLoadedEvent(vehicleConfig));
    }

    private void loadTabController(VehicleConfigDTO vehicleConfig, boolean isCompare) throws ControllerException {
        FzgKonfigMainTabController controller = load(FzgKonfigMainTabController.class);
        controller.setMainTabPaneController(this);
        controller.setVehicleConfig(vehicleConfig);
        setCloseAction(controller);

        if (isCompare) {
            controller.start();
            addTabWithoutSelection(controller);
        } else {
            initTab(controller);
        }

        vehicleConfigMainControllerMap.put(vehicleConfig.getId(), controller.getControl());
    }

    private void initTab(AbstractMainTabController controller) {
        controller.start();
        addTab(controller);
    }

    private void addTab(AbstractMainTabController controller) {
        Tab tab = controller.getControl();
        tab.setId(StringConstant.EMPTY + controller.hashCode());
        tab.setOnClosed(e -> handleOnTabClosed(tab));

        TAB_CONTROLLER_MAP.put(tab, controller);

        if (controller.equals(explorerTab)) {
            mainTabPane.getTabs().addFirst(tab);
        } else if (controller instanceof ComparePartListMainTabController) {
            mainTabPane.getTabs().add(1, tab);
        } else {
            mainTabPane.getTabs().add(tab);
        }

        setContextMenu(tab, controller);

        mainTabPane.getSelectionModel().select(tab);
    }

    private void setContextMenu(Tab tab, AbstractMainTabController controller) {
        MenuItem closeItem = new MenuItem(I18N.getString("tab.contextmenu.close.one"));
        closeItem.setOnAction(e -> closeTab(tab));

        MenuItem closeAllItem = new MenuItem(I18N.getString("tab.contextmenu.close.all"));
        closeAllItem.setOnAction(e -> closeAllTabs());

        MenuItem closeOthersItem = new MenuItem(I18N.getString("tab.contextmenu.close.others"));
        closeOthersItem.setOnAction(e -> closeOtherTabs(tab));

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(closeItem, closeAllItem, closeOthersItem);

        if (controller instanceof ComparePartListMainTabController comparePartListController) {
            MenuItem openComparedPartLists = new MenuItem(I18N.getString("tab.contextmenu.compare.reopen"));
            openComparedPartLists.setOnAction(event -> EventBus.getInstance()
                    .post(new ReopenCompareTabsEvent(comparePartListController.getComparedVehicleConfigs())));
            contextMenu.getItems().add(openComparedPartLists);
        }

        tab.setContextMenu(contextMenu);
    }

    private void closeTab(AbstractMainTabController... controllers) {
        for (AbstractMainTabController controller : controllers) {
            if (controller != null) {
                closeTab(controller.getControl());
            }
        }
    }

    private void closeTab(Tab tab) {
        mainTabPane.getTabs().remove(tab);
        tab.getOnClosed().handle(null);
    }

    private void closeAllTabs() {
        Collection<Tab> mainTabs = mainTabPane.getTabs();
        Collection<Tab> toClose = new ArrayList<>(mainTabs.size());
        for (Tab mainTab : mainTabs) {
            if (!mainTab.equals(explorerTab.getControl())) {
                mainTab.getOnClosed().handle(null);
                toClose.add(mainTab);
            }
        }

        mainTabs.removeAll(toClose);
    }

    private void closeOtherTabs(Tab tab) {
        Collection<Tab> mainTabs = mainTabPane.getTabs();
        Collection<Tab> toClose = new ArrayList<>(mainTabs.size());
        for (Tab mainTab : mainTabs) {
            if (!mainTab.equals(tab) && !mainTab.equals(explorerTab.getControl())) {
                mainTab.getOnClosed().handle(null);
                toClose.add(mainTab);
            }
        }

        mainTabs.removeAll(toClose);
    }

    private void addTabWithoutSelection(AbstractMainTabController controller) {
        Tab tab = controller.getControl();
        tab.setId(StringConstant.EMPTY + controller.hashCode());
        TAB_CONTROLLER_MAP.put(tab, controller);
        tab.setOnClosed(e -> handleOnTabClosed(tab));

        setContextMenu(tab, controller);
        mainTabPane.getTabs().add(tab);
    }

    private void handleOnTabClosed(Tab tab) {
        AbstractMainTabController controller = TAB_CONTROLLER_MAP.remove(tab);
        if (controller == explorerTab) {
            explorerTab = null;
        } else if (controller == masterDataTab) {
            masterDataTab = null;
        } else if (controller == userManagementTab) {
            userManagementTab = null;
        } else if (controller == tiWhRequestQueueTab) {
            tiWhRequestQueueTab = null;
        } else if (controller == smartFixTabController) {
            smartFixTabController = null;
        }

        // todo: fix memory leak: remove reference to controller so that the single vehicle part list controller can be garbage collected
        tab.setOnClosed(null);

        if (controller != null) {
            controller.stop();
        }
    }

    private void changeRibbonMenu(AbstractRibbonMenuEvent<?> event) {
        if (event != null) {
            EventBus.getInstance().post(event);
        }
    }

    private void updateBuildInfo() {
        ServerBuildInfoDTO buildInfo = BuildInfoRestClientHolder.getInstance().getBuildInfo();
        PasoApplication.getMainController().getMainStatusBarController().setBuildInfo(buildInfo);

        DialogUtil.showWarnDialog(I18N.getString("warning"), I18N.getString("mbt.import.dialog.header"),
                I18N.getString("mbt.import.dialog.content"));
    }
}
