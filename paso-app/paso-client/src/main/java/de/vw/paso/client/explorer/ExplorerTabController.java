package de.vw.paso.client.explorer;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.control.dialog.DialogUtil;
import de.vw.paso.client.control.textfield.PasoCustomTextFieldClearable;
import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObj;
import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObjType;
import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigurationController;
import de.vw.paso.client.explorer.vehicleconfig.event.UpdateVehicleConfigEvent;
import de.vw.paso.client.explorer.vehicleconfig.tree.DefaultFzgKonfigTreeConfiguration;
import de.vw.paso.client.explorer.vehicleconfig.tree.FavoritesFzgConfigTreeConfiguration;
import de.vw.paso.client.explorer.vehicleconfig.tree.FzgKonfigTreeConfiguration;
import de.vw.paso.client.explorer.vehicleconfig.tree.ProductKeyFzgKonfigTreeConfiguration;
import de.vw.paso.client.main.ribbonmenu.explorer.RibbonMenuExplorerEvent;
import de.vw.paso.client.main.ribbonmenu.explorer.RibbonMenuExplorerListener;
import de.vw.paso.client.main.tab.AbstractMainTabController;
import de.vw.paso.client.main.tab.MainTabPaneController;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.util.icon.ExplorerIcon;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.delegate.stueckliste.userproperty.UserPropertyRestClientHolder;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

@FXController(name = "explorer-tab")
public class ExplorerTabController extends AbstractMainTabController
    implements Initializable, RibbonMenuExplorerListener {

    @FXML
    private Tab explorerTab;
    @FXML
    private PasoCustomTextFieldClearable explorerSearch;
    @FXML
    private TabPane explorerTabPane;
    @FXML
    private GridPane explorerWorkAreaSearchPane;
    @FXML
    private BorderPane explorerWorkArea;
    @FXML
    private PasoCustomTextFieldClearable explorerWorkAreaSearch;

    private final Map<Tab, VehicleConfigurationController> tabToControllerMap;

    private BooleanProperty disablePropertyPartListNew;
    private BooleanProperty disablePropertyFavorite;
    private BooleanProperty disablePropertyClearFilters;
    private BooleanProperty disablePropertyCompare;
    private BooleanProperty disablePropertyPartListEditable;
    private BooleanProperty disablePropertyChangeOwnerGroup;
    private BooleanProperty disablePropertyReestablish;
    private BooleanProperty disablePropertyPartListDeletable;

    private MainTabPaneController parentMainTabPaneController;

    private VehicleConfigurationController fzgConfigByVehicleController;
    private VehicleConfigurationController fzgConfigByProductKeyController;
    private VehicleConfigurationController fzgConfigByMyConfigurationsController;

    private List<VehicleProjectDTO> vehicleProjects;
    private Map<Long, Long> configCountMap;

    public ExplorerTabController() {
        super();
        tabToControllerMap = new HashMap<>();
    }

    @Override
    public void initialize(URL location, java.util.ResourceBundle resources) {
        super.initialize(location, resources);

        explorerTab.setText(I18N.getString("tab.explorer.title"));
        explorerTabPane.setSide(Side.LEFT);
        explorerTabPane.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> handleOnTabChange());
        explorerTab.setGraphic(new ImageView(ExplorerIcon.EXPLORER_16X16.getImage()));
        explorerSearch.textProperty().addListener((observable, oldValue, newValue) -> handleExplorerSearch());
        explorerWorkAreaSearchPane.setVisible(false);
        explorerWorkAreaSearch.textProperty()
            .addListener((observable, oldValue, newValue) -> handleExplorerWorkAreaSearch());
    }

    private void handleOnTabChange() {
        disablePropertyStuecklisteEditable().unbind();
        disablePropertyStuecklisteEditable().bind(getSelectedController().disablePropertyStuecklisteEditable());

        disablePropertyStuecklisteDeletable().unbind();
        disablePropertyStuecklisteDeletable().bind(getSelectedController().disablePropertyStuecklisteDeletable());

        disablePropertyFavorite().unbind();
        disablePropertyFavorite().bind(getSelectedController().disablePropertyFavorite());

        disablePropertyClearFilters().unbind();
        disablePropertyClearFilters().bind(getSelectedController().disablePropertyClearFilters());

        disablePropertyReestablish().unbind();
        disablePropertyReestablish().bind(getSelectedController().disablePropertyReestablish());

        explorerWorkAreaSearchPane.setVisible(true);
        explorerWorkArea.setCenter(getSelectedController().getFzgConfigControl());

        getSelectedController().tabSelected();
    }

    private void handleExplorerSearch() {
        fzgConfigByVehicleController.setTreeSearchText(explorerSearch.getText());
        fzgConfigByProductKeyController.setTreeSearchText(explorerSearch.getText());
        fzgConfigByMyConfigurationsController.setTreeSearchText(explorerSearch.getText());
    }

    private void handleExplorerWorkAreaSearch() {
        getSelectedController().setWorkAreaSearchText(explorerWorkAreaSearch.getText());
    }

    @Override
    public Tab getControl() {
        return explorerTab;
    }

    @Override
    public Parent getStyleableParent() {
        return explorerTabPane;
    }

    @Override
    public void start() {
        loadFzgConfig();
    }

    private void loadFzgConfig() {
        fzgConfigByMyConfigurationsController = createFzgControllerByConfiguration(
            new FavoritesFzgConfigTreeConfiguration());
        disablePropertyStuecklisteEditable().bind(
            fzgConfigByMyConfigurationsController.disablePropertyStuecklisteEditable());
        disablePropertyStuecklisteDeletable().bind(
            fzgConfigByMyConfigurationsController.disablePropertyStuecklisteDeletable());

        fzgConfigByVehicleController = createFzgControllerByConfiguration(new DefaultFzgKonfigTreeConfiguration());
        fzgConfigByProductKeyController = createFzgControllerByConfiguration(
            new ProductKeyFzgKonfigTreeConfiguration());

        loadVehicleProjects();
    }

    private VehicleConfigurationController createFzgControllerByConfiguration(
        FzgKonfigTreeConfiguration configuration) {
        VehicleConfigurationController vehicleConfigurationController = load(VehicleConfigurationController.class);
        vehicleConfigurationController.setConfiguration(configuration);
        vehicleConfigurationController.setMainTabPaneController(parentMainTabPaneController);
        vehicleConfigurationController.start();

        initExplorerController(vehicleConfigurationController);
        addTab(vehicleConfigurationController);

        return vehicleConfigurationController;
    }

    private void loadVehicleProjects() {
        doAsync(this::loadVehicleProject, this::handleVehiclesFzgConfigs);
    }

    private void handleVehiclesFzgConfigs(List<Long> favoriteIds) {
        try {
            VehicleConfigTreeObjType type = null;
            if (explorerTabPane.getSelectionModel().getSelectedIndex() == 0) {
                TreeItem<VehicleConfigTreeObj> selectedMyConfigItem = fzgConfigByMyConfigurationsController.getVehicleTree()
                    .getSelectionModel().getSelectedItem();
                if (selectedMyConfigItem != null) {
                    type = selectedMyConfigItem.getValue().getVehicleConfigTreeObjType();
                }
            }

            List<VehicleConfigDTO> recentlyUsedVehicleConfigs = loadVehicleConfigs(
                () -> VehicleConfigRestClientHolder.getInstance().loadVehicleConfigByRecentlyUsed()
                    .vehicleConfigDTOList()).get();

            List<VehicleConfigDTO> deletedConfigs = loadVehicleConfigs(
                () -> UserProperties.getUser().isAdmin() ? VehicleConfigRestClientHolder.getInstance()
                    .loadDeletedVehicleConfigs().vehicleConfigDTOList() : List.of()).get();

            fzgConfigByMyConfigurationsController.setVehicles(recentlyUsedVehicleConfigs, deletedConfigs,
                vehicleProjects, configCountMap, favoriteIds);

            fzgConfigByVehicleController.setVehicles(recentlyUsedVehicleConfigs, deletedConfigs, vehicleProjects,
                configCountMap, favoriteIds);

            fzgConfigByProductKeyController.setVehicles(recentlyUsedVehicleConfigs, deletedConfigs, vehicleProjects,
                configCountMap, favoriteIds);

            handleTabSelection(type);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private List<Long> loadVehicleProject() {
        configCountMap = VehicleConfigRestClientHolder.getInstance().loadConfigurationCountForVehicleProject()
            .countMap();
        vehicleProjects = CacheManager.getVehicleProjects();

        return UserPropertyRestClientHolder.getInstance().getFavoriteVehicleProjectIds().ids();
    }

    private void initExplorerController(VehicleConfigurationController controller) {
        controller.setOnNodeSelected(value -> {
            for (VehicleConfigurationController cont : tabToControllerMap.values()) {
                if (cont != controller) {
                    cont.selectNode(value);
                }
            }
        });
    }

    private void addTab(VehicleConfigurationController controller) {
        Tab tab = controller.getControl();

        tabToControllerMap.put(tab, controller);
        explorerTabPane.getTabs().add(tab);
    }

    private void handleTabSelection(VehicleConfigTreeObjType selectedMyConfigItem) {
        if (selectedMyConfigItem != null) {
            for (TreeItem<VehicleConfigTreeObj> group : fzgConfigByMyConfigurationsController.getVehicleTree().getRoot()
                .getChildren()) {
                if (group.getValue().getVehicleConfigTreeObjType() == selectedMyConfigItem) {
                    fzgConfigByMyConfigurationsController.getVehicleTree().getSelectionModel().select(group);
                    return;
                }
            }
        }

        if (explorerTabPane.getSelectionModel().getSelectedItem() == null) {
            explorerTabPane.getSelectionModel().select(fzgConfigByVehicleController.getControl());
        }
    }

    private Future<List<VehicleConfigDTO>> loadVehicleConfigs(Callable<List<VehicleConfigDTO>> vehicleConfigSupplier) {
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            return executorService.submit(vehicleConfigSupplier);
        }
    }

    @Override
    public RibbonMenuExplorerEvent getRibbonMenuEvent() {
        return new RibbonMenuExplorerEvent(this);
    }

    @Override
    public BooleanProperty disablePropertyAddNew() {
        if (disablePropertyPartListNew == null) {
            disablePropertyPartListNew = new SimpleBooleanProperty(false);
        }

        return disablePropertyPartListNew;
    }

    @Override
    public BooleanProperty disablePropertyStuecklisteEditable() {
        if (disablePropertyPartListEditable == null) {
            disablePropertyPartListEditable = new SimpleBooleanProperty(true);
        }

        return disablePropertyPartListEditable;
    }

    @Override
    public BooleanProperty disablePropertyStuecklisteDeletable() {
        if (disablePropertyPartListDeletable == null) {
            disablePropertyPartListDeletable = new SimpleBooleanProperty(false);
        }

        return disablePropertyPartListDeletable;
    }

    @Override
    public BooleanProperty disablePropertyFavorite() {
        if (disablePropertyFavorite == null) {
            disablePropertyFavorite = new SimpleBooleanProperty(true);
        }

        return disablePropertyFavorite;
    }

    @Override
    public BooleanProperty disablePropertyClearFilters() {
        if (disablePropertyClearFilters == null) {
            disablePropertyClearFilters = new SimpleBooleanProperty(true);
        }

        return disablePropertyClearFilters;
    }

    @Override
    public BooleanProperty disablePropertyCompare() {
        if (disablePropertyCompare == null) {
            disablePropertyCompare = new SimpleBooleanProperty(false);
        }

        return disablePropertyCompare;
    }

    @Override
    public BooleanProperty disablePropertyChangeOwnerGroup() {
        if (disablePropertyChangeOwnerGroup == null) {
            disablePropertyChangeOwnerGroup = new SimpleBooleanProperty(true);
        }

        return disablePropertyChangeOwnerGroup;
    }

    @Override
    public BooleanProperty disablePropertyReestablish() {
        if (disablePropertyReestablish == null) {
            disablePropertyReestablish = new SimpleBooleanProperty(true);
        }

        return disablePropertyReestablish;
    }

    @Override
    public void handleActionStuecklisteErstellen() {
        fzgConfigByVehicleController.handleActionStuecklisteErstellen();
    }

    @Override
    public void handleActionStuecklisteBearbeiten() {
        if (getSelectedController() != null) {
            getSelectedController().handleActionStuecklisteBearbeiten();
        }
    }

    @Override
    public void handleActionStuecklisteLoeschen() {
        if (getSelectedController() == null || !confirmFzgConfigDeletion()) {
            return;
        }

        for (VehicleConfigDTO vehicleConfig : getSelectedController().getFzgConfigControl().getSelectionModel()
            .getSelectedItems()) {
            if (vehicleConfig == null) {
                continue;
            }

            doAsync(() -> VehicleConfigRestClientHolder.getInstance().deleteVehicleConfig(vehicleConfig.getId()),
                () -> onDeleteSuccessful(vehicleConfig));
        }
    }

    @Override
    public void handleActionFavorite() {
        if (getSelectedController() != null) {
            getSelectedController().handleActionFavorite();
        }
    }

    @Override
    public void handleActionClearFilters() {
        if (getSelectedController() != null) {
            getSelectedController().handleActionClearFilters();
        }
    }

    @Override
    public void handleActionReestablish() {
        if (getSelectedController() != null) {
            getSelectedController().handleActionReestablish();
        }
    }

    @Override
    public void handleActionReload() {
        loadVehicleProjects();
    }

    @Override
    public void handleActionShowCompareDialog() {
        getSelectedController().handleActionShowCompareDialog();
    }

    @Override
    public void handleActionChangeOwnerGroup() {
        OwnerGroupManagementDialog ownerGroupManagementDialog = createGroupManagementDialog(
            getSelectedController().getFzgConfigControl().getSelectionModel().getSelectedItem());

        if (ownerGroupManagementDialog.isCancelled()) {
            return;
        }

        VehicleConfigRestClientHolder.getInstance().saveFzgKonfig(ownerGroupManagementDialog.dialogResult());
    }

    public void fireRecentlyUsedConfigEvent() {
        fzgConfigByMyConfigurationsController.fireRecentlyUsedConfigEvent();
    }

    private void onDeleteSuccessful(VehicleConfigDTO vehicleConfig) {
        Long oldCount = configCountMap.get(vehicleConfig.getVehicleProject().getId());
        if (oldCount != null) {
            configCountMap.put(vehicleConfig.getVehicleProject().getId(), oldCount - 1);
        }

        EventBus.getInstance()
            .post(new UpdateVehicleConfigEvent(vehicleConfig, UpdateVehicleConfigEvent.UpdateEventType.DELETE));
    }

    private boolean confirmFzgConfigDeletion() {
        ButtonType btnYes = new ButtonType(I18N.getString("button.ok"), ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType(I18N.getString("button.cancel"), ButtonBar.ButtonData.OK_DONE);

        ButtonType showConfirmationDialog = DialogUtil.showConfirmationDialog(Alert.AlertType.CONFIRMATION,
            I18N.getString("delete.prompt.title"), I18N.getString("delete.prompt.header"),
            I18N.getString("delete.prompt.text"), btnYes, btnCancel);

        return showConfirmationDialog == btnYes;
    }

    private VehicleConfigurationController getSelectedController() {
        return tabToControllerMap.get(explorerTabPane.getSelectionModel().getSelectedItem());
    }

    private void openGroupManagementDialog(OwnerGroupManagementDialog dialog,
        Consumer<Optional<VehicleConfigDTO>> callback) {
        callback.accept(dialog.showAndWait());
    }

    private OwnerGroupManagementDialog createGroupManagementDialog(VehicleConfigDTO vehicleConfigDTO) {
        OwnerGroupManagementDialog ownerGroupManagementDialog = new OwnerGroupManagementDialog(vehicleConfigDTO);
        openGroupManagementDialog(ownerGroupManagementDialog, result -> result.ifPresent(
            groups -> fzgConfigByVehicleController.getFzgConfigControl().getSelectionModel().select(vehicleConfigDTO)));
        return ownerGroupManagementDialog;
    }

    public void setParentMainTabPaneController(MainTabPaneController parentMainTabPaneController) {
        this.parentMainTabPaneController = parentMainTabPaneController;
    }
}
