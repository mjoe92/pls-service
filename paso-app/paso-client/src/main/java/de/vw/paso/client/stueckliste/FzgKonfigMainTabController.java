package de.vw.paso.client.stueckliste;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.control.dialog.DialogUtil;
import de.vw.paso.client.control.tablebase.TableColumnHeaderChangeListener;
import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;
import de.vw.paso.client.main.ribbonmenu.costgroup.RibbonMenuCostGroupEvent;
import de.vw.paso.client.main.ribbonmenu.efs.RibbonMenuEfsEvent;
import de.vw.paso.client.main.ribbonmenu.fgset.RibbonMenuFgSetEvent;
import de.vw.paso.client.main.ribbonmenu.fzgkonfig.RibbonMenuFzgKonfigEvent;
import de.vw.paso.client.main.ribbonmenu.partgroup.RibbonMenuPartGroupEvent;
import de.vw.paso.client.main.tab.AbstractMainTabController;
import de.vw.paso.client.main.tab.MainTabPaneController;
import de.vw.paso.client.stammdaten.costgroup.CostGroupChangedEvent;
import de.vw.paso.client.stammdaten.setkey.SetKeyChangedEvent;
import de.vw.paso.client.stueckliste.efs.CostGroupTabController;
import de.vw.paso.client.stueckliste.efs.EfsTabController;
import de.vw.paso.client.stueckliste.efs.FgSetTabController;
import de.vw.paso.client.stueckliste.efs.PartGroupTabController;
import de.vw.paso.client.stueckliste.efs.event.PartListLoadedEvent;
import de.vw.paso.client.stueckliste.fzgkonfig.FzgConfigTabController;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.icon.StuecklisteIcon;
import de.vw.paso.client.valueobject.PartGroupVMO;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.delegate.stueckliste.EfsEditLoadAdapter;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.partlist.domain.PartListViewMode;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.StringConstant;

@FXController(name = "fzg-konfig-main-tab")
public class FzgKonfigMainTabController extends AbstractMainTabController implements Initializable {

    private static final int EXEC_TIME_LOAD_EFS = 2000;

    private final ObjectProperty<PartListViewMode> viewModeEfsProperty;
    private final ObjectProperty<VehicleConfigDTO> vehicleConfig;
    private final Map<Class<?>, TableColumnHeaderChangeListener> boundColumnHeaderListeners;

    private final String partListTabTitle;
    private final String fzgConfigTabTitle;
    private final String fgSetTabTitle;
    private final String costGroupTabTitle;
    private final String partGroupTabTitle;

    @FXML
    private GridPane paneEfsHeader;
    @FXML
    private TabPane fzgConfigTabPane;
    @FXML
    private Tab fzgConfigMainTab;
    @FXML
    private Tab partListTab;
    @FXML
    private Tab fzgConfigTab;
    @FXML
    private Tab fgSetTab;
    @FXML
    private Tab costGroupTab;
    @FXML
    private Tab partGroupTab;

    private MainTabPaneController mainTabPaneController;

    private FzgConfigTabController fzgConfigTabController;

    private EfsTabController efsTabController;
    private FgSetTabController fgSetTabController;
    private CostGroupTabController costGroupTabController;
    private PartGroupTabController partGroupTabController;

    private Brand brand;

    private Runnable closeAction;
    private ChangeListener<Tab> changeTabListener;

    public FzgKonfigMainTabController() {
        viewModeEfsProperty = new SimpleObjectProperty<>(PartListViewMode.VEHICLE_ALL);
        vehicleConfig = new SimpleObjectProperty<>();
        boundColumnHeaderListeners = new HashMap<>();

        fzgConfigTabTitle = I18N.getString("tab.title.konfiguration");
        partListTabTitle = I18N.getString("tab.title.stueckliste");
        fgSetTabTitle = I18N.getString("tab.title.fgSet");
        costGroupTabTitle = I18N.getString("tab.title.costGroup");
        partGroupTabTitle = I18N.getString("tab.title.partGroup");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        fzgConfigMainTab.setGraphic(new ImageView(StuecklisteIcon.EFS_16X16.getImage()));
    }

    @Override
    public Parent getStyleableParent() {
        return fzgConfigTabPane;
    }

    @Override
    public Tab getControl() {
        return fzgConfigMainTab;
    }

    @Override
    public void start() {
        initVehicleConfigTabPane();
        loadVehicleConfig();
    }

    @Override
    public void stop() {
        super.stop();
        fzgConfigTabPane.getSelectionModel().selectedItemProperty().removeListener(changeTabListener);

        if (fzgConfigTabController != null) {
            fzgConfigTabController.stop();
        }

        if (efsTabController != null) {
            efsTabController.stop();
        }

        if (fgSetTabController != null) {
            fgSetTabController.stop();
        }

        if (costGroupTabController != null) {
            costGroupTabController.stop();
        }

        if (partGroupTabController != null) {
            partGroupTabController.stop();
        }

        closeAction.run();
    }

    public void loadVehicleConfig() {
        if (getVehicleConfig() == null) {
            VehicleConfigDTO vehicleConfig = new VehicleConfigDTO();
            loadTabs(vehicleConfig);
            return;
        }

        ServiceController<VehicleConfigDTO> serviceController = new ServiceController<>();

        serviceController.setOnSucceeded(e -> loadTabs(serviceController.getValue()));
        serviceController.setOnFailed(e -> {
            DialogUtil.showWarnDialog(I18N.getString("warning"), I18N.getString("warning.contact.admin"),
                "warning.contact.access");
            closeAction.run();
        });
        serviceController.start(
            () -> VehicleConfigRestClientHolder.getInstance().loadFzgKonfig(getVehicleConfig().getId()));
    }

    public void reloadElementsWithSetKeys() {
        doAsync(() -> CacheManager.getSetKeys(getVehicleConfig().getSetVersionId()), setKeys -> {
            Set<EfsElementDTO> elementsInPartList = EfsElementResolver.getElementsInPartList(
                getVehicleConfig().getVehiclePartList());
            // update all references to the new part list object, which also contains the new correct set key
            for (EfsElementDTO efsElementDTO : elementsInPartList) {
                efsElementDTO.setVehiclePartListId(getVehicleConfig().getVehiclePartList().getId());
            }

            efsTabController.setSetKeys(CacheManager.getSetKeysAsStrings(setKeys));

            // refresh since the set keys changed
            fgSetTabController.setSetKeys(setKeys);
            fgSetTabController.fillGui(getVehicleConfig(), new ArrayList<>(elementsInPartList));
        });
    }

    @Override
    public AbstractRibbonMenuEvent<?> getRibbonMenuEvent() {
        if (efsTabController == null && fzgConfigTabController == null) {
            return null;
        }

        Tab selectedSubTab = fzgConfigTabPane.getSelectionModel().getSelectedItem();
        String tabText = selectedSubTab.getText();

        if (selectedSubTab.equals(partListTab)) {
            if (efsTabController != null) {
                return new RibbonMenuEfsEvent(efsTabController, tabText);
            }
        } else if (selectedSubTab.equals(fzgConfigTab)) {
            if (fzgConfigTabController == null) {
                loadTabConfiguration();
            }

            return new RibbonMenuFzgKonfigEvent(fzgConfigTabController, tabText);
        } else if (selectedSubTab.equals(fgSetTab)) {
            if (fgSetTabController != null) {
                return new RibbonMenuFgSetEvent(fgSetTabController, tabText);
            }
        } else if (selectedSubTab.equals(costGroupTab)) {
            if (costGroupTabController != null) {
                return new RibbonMenuCostGroupEvent(costGroupTabController, tabText);
            }
        } else if (selectedSubTab.equals(partGroupTab)) {
            if (partGroupTabController != null) {
                return new RibbonMenuPartGroupEvent(partGroupTabController, tabText);
            }
        }

        return null;
    }

    @Subscribe
    private void updateSetKeys(SetKeyChangedEvent event) {
        doAsync(() -> CacheManager.getSetKeys(getVehicleConfig().getSetVersionId()), setKeys -> {
            fgSetTabController.setSetKeys(setKeys);
            efsTabController.setSetKeys(CacheManager.getSetKeysAsStrings(setKeys));

            fgSetTabController.fillGui(getVehicleConfig(),
                new ArrayList<>(EfsElementResolver.getElementsInPartList(getVehicleConfig().getVehiclePartList())));
        });
    }

    @Subscribe
    private void updateCostGroups(CostGroupChangedEvent event) {
        doAsync(() -> CacheManager.getCostGroups(getVehicleConfig().getCostGroupVersion()), costGroups -> {
            costGroupTabController.setCostGroups(costGroups);
            efsTabController.setCostGroups(CacheManager.getCostGroupsAsStrings(costGroups));

            costGroupTabController.fillGui(
                new ArrayList<>(EfsElementResolver.getElementsInPartList(getVehicleConfig().getVehiclePartList())));
        });
    }

    private void loadPartList() {
        ServiceController<List<EfsElementDTO>> serviceController = new ServiceController<>();

        efsTabController.setPlaceholderText(true);
        serviceController.setOnSucceeded(e -> {
            List<EfsElementDTO> efsElements = serviceController.getValue();
            EfsElementResolver.registerElements(efsElements);

            fillGuiEfs(efsElements);
            efsTabController.setPlaceholderText(false);
            EventBus.getInstance().post(new PartListLoadedEvent(getVehicleConfig()));
            fgSetTab.setContent(fgSetTabController.getControl().getContent());
        });
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        serviceController.setStatusMessage(I18N.getString("message.laden.stueckliste"));
        serviceController.setExecutionTime(EXEC_TIME_LOAD_EFS);
        serviceController.start(() -> new EfsEditLoadAdapter().loadPartList(getVehicleConfig().getId()));
    }

    private void loadSetKeys() {
        doAsync(() -> CacheManager.getSetKeys(getVehicleConfig().getSetVersionId()), setKeys -> {
            fgSetTabController.setSetKeys(setKeys);
            efsTabController.setSetKeys(CacheManager.getSetKeysAsStrings(setKeys));
        });
    }

    private void loadCostGroups() {
        doAsync(() -> CacheManager.getCostGroups(getVehicleConfig().getCostGroupVersion()), costGroups -> {
            costGroupTabController.setCostGroups(costGroups);
            efsTabController.setCostGroups(CacheManager.getCostGroupsAsStrings(costGroups));
        });
    }

    private void loadPartGroups() {
        doAsync(CacheManager::getPartGroups,
            partGroups -> partGroupTabController.setPartGroups(PartGroupVMO.toVMOs(partGroups)));
    }

    private void fillGuiEfs(List<EfsElementDTO> efsElements) {
        efsTabController.fillGui();
        fgSetTabController.fillGui(getVehicleConfig(), efsElements);
        costGroupTabController.fillGui(efsElements);
        partGroupTabController.fillGui(efsElements);
    }

    private void initVehicleConfigTabPane() {
        changeTabListener = this::changeTab;
        fzgConfigTabPane.getSelectionModel().selectedItemProperty().addListener(changeTabListener);
        fzgConfigTabPane.setSide(Side.BOTTOM);
        fzgConfigTabPane.getStyleClass().add("ribbon-menu-tab-pane");

        for (Tab tab : fzgConfigTabPane.getTabs()) {
            tab.setClosable(false);
            tab.getStyleClass().add("ribbon-menu-tab");

            if (tab.equals(partListTab)) {
                tab.setText(partListTabTitle);
            } else if (tab.equals(fzgConfigTab)) {
                tab.setText(fzgConfigTabTitle);
            } else if (tab.equals(fgSetTab)) {
                tab.setText(fgSetTabTitle);
            } else if (tab.equals(costGroupTab)) {
                tab.setText(costGroupTabTitle);
            } else if (tab.equals(partGroupTab)) {
                tab.setText(partGroupTabTitle);
            }
        }
    }

    private void changeTab(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
        selectTabAction();
        changeRibbonMenu(getRibbonMenuEvent());
    }

    private void loadTabs(VehicleConfigDTO vehicleConfig) {
        setVehicleConfig(vehicleConfig);

        setTabTitle();

        if (vehicleConfig.getId() == null || vehicleConfig.getVehiclePartList() == null) {
            partListTab.setDisable(true);
            fgSetTab.setDisable(true);
            costGroupTab.setDisable(true);
            partGroupTab.setDisable(true);

            loadTabConfiguration();
            selectVehicleConfigTab();
        } else {
            loadTabPartList();
            loadTabFgSet();
            loadTabCostGroup();
            loadTabPartGroup();

            selectPartListTab();

            loadSetKeys();
            loadCostGroups();
            loadPartGroups();
            loadPartList();
        }
    }

    private void loadTabConfiguration() {
        fzgConfigTabController = BaseController.load(FzgConfigTabController.class);

        changeRibbonMenu(new RibbonMenuFzgKonfigEvent(fzgConfigTabController, fzgConfigTabTitle));

        fzgConfigTabController.vehicleConfigProperty().bindBidirectional(vehicleConfig);
        fzgConfigTabController.setCloseAction(this.closeAction);
        fzgConfigTabController.setUpdateMainTabAction(e -> setTabTitle());

        fzgConfigTab.setContent(fzgConfigTabController.getControl().getContent());

        fzgConfigTabController.setSelectTabAction(e -> selectTabAction());
        fzgConfigTabController.start();
    }

    private void changeRibbonMenu(AbstractRibbonMenuEvent<?> event) {
        if (event != null) {
            if (fzgConfigMainTab.isSelected()) {
                EventBus.getInstance().post(event);
            }
        }
    }

    private void loadTabPartList() {
        partListTab.setDisable(false);

        efsTabController = BaseController.load(EfsTabController.class);
        efsTabController.setMainTabPaneController(mainTabPaneController);
        efsTabController.setViewModeEfsProperty(viewModeEfsProperty);
        efsTabController.vehicleConfigProperty().bind(vehicleConfig);
        efsTabController.setSelectTabAction(e -> selectTabAction(), this::selectPartListTab);
        efsTabController.setBindedTableList(boundColumnHeaderListeners);
        efsTabController.setEfsHeader(paneEfsHeader);
        efsTabController.start();

        partListTab.setContent(efsTabController.getControl().getContent());
    }

    private void loadTabFgSet() {
        fgSetTab.setDisable(false);

        fgSetTabController = BaseController.load(FgSetTabController.class);

        fgSetTabController.setMainTabPaneController(mainTabPaneController);
        fgSetTabController.setAnsichtEfsProperty(viewModeEfsProperty);
        fgSetTabController.setVehiclePartList(getVehicleConfig().getVehiclePartList());
        fgSetTabController.setSelectTabAction(e -> selectTabAction());
        fgSetTabController.setBindedTableList(boundColumnHeaderListeners);
        fgSetTabController.start();

        fgSetTab.setContent(fgSetTabController.getControl().getContent());
    }

    private void loadTabCostGroup() {
        costGroupTab.setDisable(false);

        costGroupTabController = BaseController.load(CostGroupTabController.class);

        costGroupTabController.setMainTabPaneController(mainTabPaneController);
        costGroupTabController.setAnsichtEfsProperty(viewModeEfsProperty);
        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        costGroupTabController.setVehiclePartList(vehicleConfig.getVehiclePartList());
        costGroupTabController.setSelectTabAction(e -> selectTabAction());
        costGroupTabController.setBindedTableList(boundColumnHeaderListeners);
        costGroupTabController.start();

        costGroupTab.setContent(costGroupTabController.getControl().getContent());
    }

    private void loadTabPartGroup() {
        partGroupTab.setDisable(false);

        partGroupTabController = BaseController.load(PartGroupTabController.class);

        partGroupTabController.setMainTabPaneController(mainTabPaneController);
        partGroupTabController.setAnsichtEfsProperty(viewModeEfsProperty);
        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        partGroupTabController.setVehiclePartList(vehicleConfig.getVehiclePartList());
        partGroupTabController.setSelectTabAction(e -> selectTabAction());
        partGroupTabController.setBindedTableList(boundColumnHeaderListeners);
        partGroupTabController.start();

        partGroupTab.setContent(partGroupTabController.getControl().getContent());
    }

    private void selectVehicleConfigTab() {
        fzgConfigTabPane.getSelectionModel().select(fzgConfigTab);
    }

    private void selectPartListTab() {
        if (efsTabController == null) {
            loadTabPartList();
        }

        fzgConfigTabPane.getSelectionModel().select(partListTab);
    }

    private void selectFgSetTab() {
        if (fgSetTabController == null) {
            loadTabFgSet();
        }

        //    fzgKonfigTabPane.getSelectionModel().select(fgSetTab);
    }

    private void selectCostGroupTab() {
        if (costGroupTabController == null) {
            loadTabCostGroup();
        }

        fzgConfigTabPane.getSelectionModel().select(costGroupTab);
    }

    private void selectPartGroupTab() {
        if (partGroupTabController == null) {
            loadTabCostGroup();
        }

        fzgConfigTabPane.getSelectionModel().select(partGroupTab);
    }

    private void setTabTitle() {
        VehicleConfigDTO vehicleConfig = getVehicleConfig();

        String tabTitle;
        if (vehicleConfig.getVehicleProject() != null && vehicleConfig.getName() != null) {
            tabTitle = vehicleConfig.getVehicleProject().getProjectName() + StringConstant.SPACE_SLASH_SPACE
                + vehicleConfig.getVehicleProject().getProductKey() + StringConstant.SPACE_DASH_SPACE
                + vehicleConfig.getName();
        } else {
            tabTitle = I18N.getString("tab.title.konfiguration.neu");
        }

        fzgConfigMainTab.setText(tabTitle);
    }

    private void selectTabAction() {
        Tab selectedSubTab = fzgConfigTabPane.getSelectionModel().getSelectedItem();

        if (selectedSubTab.equals(partListTab)) {
            selectPartListTab();
        } else if (selectedSubTab.equals(fzgConfigTab)) {
            selectVehicleConfigTab();
        } else if (selectedSubTab.equals(fgSetTab)) {
            selectFgSetTab();
        } else if (selectedSubTab.equals(costGroupTab)) {
            selectCostGroupTab();
        } else if (selectedSubTab.equals(partGroupTab)) {
            selectPartGroupTab();
        }
    }

    public void setCloseAction(Runnable closeAction) {
        this.closeAction = closeAction;
    }

    public final VehicleConfigDTO getVehicleConfig() {
        return vehicleConfig.get();
    }

    public final void setVehicleConfig(VehicleConfigDTO vehicleConfig) {
        this.vehicleConfig.set(vehicleConfig);
    }

    public Brand getBrand() {
        return brand;
    }

    public void setMainTabPaneController(MainTabPaneController mainTabPaneController) {
        this.mainTabPaneController = mainTabPaneController;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
}
