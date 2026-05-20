package de.vw.paso.client.explorer.vehicleconfig;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableRow;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.UserChecker;
import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.explorer.vehicleconfig.event.FzgKonfigEvent;
import de.vw.paso.client.explorer.vehicleconfig.event.ShowCompareTabEvent;
import de.vw.paso.client.explorer.vehicleconfig.event.ShowEfsTabEvent;
import de.vw.paso.client.explorer.vehicleconfig.event.UpdateVehicleConfigEvent;
import de.vw.paso.client.explorer.vehicleconfig.event.UpdateVehicleConfigEvent.UpdateEventType;
import de.vw.paso.client.explorer.vehicleconfig.tree.DefaultFzgConfigTreeCell;
import de.vw.paso.client.explorer.vehicleconfig.tree.FavoritesFzgConfigTreeConfiguration;
import de.vw.paso.client.explorer.vehicleconfig.tree.FzgKonfigTreeConfiguration;
import de.vw.paso.client.main.ribbonmenu.explorer.RibbonMenuExplorerListener;
import de.vw.paso.client.main.tab.MainTabPaneController;
import de.vw.paso.client.personaldata.UserDataDeletedEvent;
import de.vw.paso.client.stammdaten.FilteringUpdateEvent;
import de.vw.paso.client.stammdaten.fzgprojekt.UpdateVehicleProjectArchivingEvent;
import de.vw.paso.client.stammdaten.fzgprojekt.UpdateVehicleProjectMyConfigEvent;
import de.vw.paso.client.stueckliste.efs.event.FzgStuecklisteGewichtEvent;
import de.vw.paso.client.stueckliste.efs.views.compare.ComparePartListSelectionDialog;
import de.vw.paso.client.stueckliste.efs.views.compare.ComparePartListSelectionDialogResult;
import de.vw.paso.client.stueckliste.fzgkonfig.VehicleConfigChangedEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.delegate.stueckliste.userproperty.UserPropertyRestClientHolder;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.userproperty.SaveUserPropertyDTO;
import de.vw.paso.service.vehicle.ILoadVehicleConfigConsumer;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.user.PropertyType;
import de.vw.paso.utility.DateUtil;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

@FXController(name = "fzg-config")
public class VehicleConfigurationController extends BaseController<Tab>
        implements RibbonMenuExplorerListener, ILoadVehicleConfigConsumer {

    public static final String DD_MM_YYYY = "dd.MM.yyyy";

    @FXML
    private Tab tab;
    @FXML
    private TreeView<VehicleConfigTreeObj> vehicleTree;

    private VehicleConfigurationTable configurationTable;
    private Collection<VehicleProjectDTO> vehicleProjects;
    private Map<Long, Long> configCountMap;
    private List<VehicleConfigDTO> allVehicleConfigs = new ArrayList<>();
    private List<Long> favoriteVehicleConfigIds;
    private List<VehicleConfigDTO> deletedVehicleConfigs = new ArrayList<>();
    private List<VehicleConfigDTO> recentlyUsedVehicleConfigs;
    private PasoWildCardPattern patternSearchTerm;
    private PasoWildCardPattern patternSearchTermTableFzgConfig;

    private BooleanProperty disablePropertyNew;
    private BooleanProperty disablePropertyEdit;
    private BooleanProperty disablePropertyDeletable;
    private BooleanProperty disablePropertyChangeOwnerGroup;
    private BooleanProperty disablePropertyReestablish;
    private BooleanProperty disablePropertyFavorite;
    private BooleanProperty disablePropertyClearFilters;
    private BooleanProperty disablePropertyCompare;

    private FzgKonfigTreeConfiguration vehicleConfigTreeConfiguration;
    private MainTabPaneController mainTabPaneController;

    private final ObservableList<VehicleConfigDTO> visibleVehicleConfigs = FXCollections.observableArrayList();

    private Consumer<VehicleConfigTreeObj> onNodeSelected;

    private boolean externallySelected;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        vehicleTree.setFixedCellSize(24);
        vehicleTree.setCellFactory(param -> new DefaultFzgConfigTreeCell<>());

        vehicleTree.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleFzgConfigTreeObjSelected(newValue));

        vehicleTree.setOnMouseClicked(this::handleTreeFzgConfigMouseClicked);
        tab.setClosable(false);

        initTableFzgConfig();
    }

    @Override
    public Tab getControl() {
        return tab;
    }

    @Override
    public Parent getStyleableParent() {
        return vehicleTree;
    }

    @Override
    public void handleActionStuecklisteErstellen() {
        TreeItem<VehicleConfigTreeObj> selectedItem = vehicleTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            EventBus.getInstance().post(new FzgKonfigEvent());
            return;
        }

        VehicleConfigTreeObj treeObj = selectedItem.getValue();
        if (treeObj.isBrand()) {
            EventBus.getInstance().post(new FzgKonfigEvent(treeObj.getBrand()));
        } else if (treeObj.isVehicleProject()) {
            EventBus.getInstance().post(new FzgKonfigEvent(treeObj.getVehicleProject()));
        } else {
            handleException(new RuntimeException("New fzgKonfig: Invalid treeItem specified"));
        }
    }

    @Override
    public void handleActionStuecklisteBearbeiten() {
        for (VehicleConfigDTO vehicleConfig : getSelectedFzgConfigsFromTable()) {
            if (vehicleConfig != null) {
                EventBus.getInstance().post(new ShowEfsTabEvent(vehicleConfig, false));
            }
        }
    }

    @Override
    public void handleActionFavorite() {
        TreeItem<VehicleConfigTreeObj> selectedTreeItem = vehicleTree.getSelectionModel().getSelectedItem();

        if (selectedTreeItem == null) {
            return;
        }

        VehicleConfigTreeObj selectedTreeObject = selectedTreeItem.getValue();

        if ((selectedTreeObject == null) || !selectedTreeObject.isVehicleProject()) {
            return;
        }

        toggleFavorite(selectedTreeItem);

        vehicleTree.refresh();
    }

    @Override
    public void handleActionReload() {
        reloadVehicleMyConfigurations();
    }

    @Override
    public void handleActionClearFilters() {
        configurationTable.clearFilters();
        disablePropertyClearFilters().set(true);
    }

    @Override
    public void handleActionShowCompareDialog() {
        List<VehicleConfigDTO> vehicleConfigs;
        List<VehicleConfigDTO> selectedConfigs = configurationTable.getSelectionModel().getSelectedItems();
        TreeItem<VehicleConfigTreeObj> selectedItem = vehicleTree.getSelectionModel().getSelectedItem();

        vehicleConfigs = VehicleConfigRestClientHolder.getInstance().loadNonDeletedVehicleConfigs()
                .vehicleConfigDTOList();

        List<VehicleConfigDTO> finalVehicleConfigs = vehicleConfigs.stream()
                .filter(e -> e.getVehiclePartList() != null && e.getDeletionDate() == null).toList();

        ComparePartListSelectionDialog dialog = new ComparePartListSelectionDialog(selectedConfigs, selectedItem,
                finalVehicleConfigs, mainTabPaneController.getOpenPartListIDs());

        Optional<ComparePartListSelectionDialogResult> result = dialog.showAndWait();
        dialog.unregisterEventBus();
        result.ifPresent(comparePartListSelectionDialogResult -> EventBus.getInstance()
                .post(new ShowCompareTabEvent(comparePartListSelectionDialogResult.getSelectedVehicleConfigs(),
                        comparePartListSelectionDialogResult.getReferenceVehicleConfig())));
    }

    @Override
    public void handleActionReestablish() {
        VehicleConfigDTO currentSelectedItem = vehicleTree.getSelectionModel().getSelectedItem().getValue()
                .getVehicleConfig();

        if (currentSelectedItem.getDeletionDate() == null) {
            return;
        }

        doAsync(() -> VehicleConfigRestClientHolder.getInstance().resetDeletion(currentSelectedItem.getId()),
                this::reloadVehicleMyConfigurations);
    }

    @Override
    public BooleanProperty disablePropertyReestablish() {
        if (disablePropertyReestablish == null) {
            disablePropertyReestablish = new SimpleBooleanProperty(true);
        }

        return disablePropertyReestablish;
    }

    @Override
    public BooleanProperty disablePropertyChangeOwnerGroup() {
        if (disablePropertyChangeOwnerGroup == null) {
            disablePropertyChangeOwnerGroup = new SimpleBooleanProperty(true);
        }

        return disablePropertyChangeOwnerGroup;
    }

    @Override
    public BooleanProperty disablePropertyStuecklisteDeletable() {
        if (disablePropertyDeletable == null) {
            disablePropertyDeletable = new SimpleBooleanProperty(true);
        }

        return disablePropertyDeletable;
    }

    @Override
    public BooleanProperty disablePropertyStuecklisteEditable() {
        if (disablePropertyEdit == null) {
            disablePropertyEdit = new SimpleBooleanProperty(true);
        }

        return disablePropertyEdit;
    }

    @Override
    public BooleanProperty disablePropertyAddNew() {
        if (disablePropertyNew == null) {
            disablePropertyNew = new SimpleBooleanProperty(false);
        }

        return disablePropertyNew;
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

    public void setConfiguration(FzgKonfigTreeConfiguration vehicleConfigTreeConfiguration) {
        this.vehicleConfigTreeConfiguration = vehicleConfigTreeConfiguration;

        tab.setText(vehicleConfigTreeConfiguration.getTitle());

        vehicleTree.setCellFactory(param -> vehicleConfigTreeConfiguration.createCell());
    }

    public void fireRecentlyUsedConfigEvent() {
        EventBus.getInstance().post(new UpdateVehicleProjectMyConfigEvent());
    }

    public VehicleConfigurationTable getFzgConfigControl() {
        if (configurationTable == null) {
            configurationTable = new VehicleConfigurationTable();
            configurationTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }

        return configurationTable;
    }

    public void setVehicles(List<VehicleConfigDTO> recentlyUsedVehicleConfigs, List<VehicleConfigDTO> deletedProjects,
            Collection<VehicleProjectDTO> vehicleProjects, Map<Long, Long> configCountMap, List<Long> favoriteIds) {
        this.recentlyUsedVehicleConfigs = recentlyUsedVehicleConfigs;
        this.deletedVehicleConfigs = deletedProjects;
        this.vehicleProjects = vehicleProjects;
        this.configCountMap = configCountMap;
        this.favoriteVehicleConfigIds = favoriteIds;

        createTree();
    }

    public void setTreeSearchText(String searchTerm) {
        try {
            patternSearchTerm = new PasoWildCardPattern(Objects.requireNonNullElse(searchTerm, StringConstant.EMPTY));
        } catch (Exception exception) {
            handleException(exception);
        }
        createTree();
        if (StringUtils.isEmpty(searchTerm)) {
            vehicleTree.getRoot().setExpanded(true);
        } else {
            expand(vehicleTree.getRoot(), true);
        }
    }

    public void setWorkAreaSearchText(String searchTerm) {
        try {
            this.patternSearchTermTableFzgConfig = new PasoWildCardPattern(searchTerm);
        } catch (Exception exception) {
            handleException(exception);
        }

        fillTableFzgConfig();
    }

    public void tabSelected() {
        handleFzgConfigTreeObjSelected(vehicleTree.getSelectionModel().getSelectedItem());

        // runLater() so we request focus then everything is done and visible.
        Platform.runLater(() -> vehicleTree.requestFocus());
    }

    public void selectNode(VehicleConfigTreeObj obj) {
        if (obj == null) {
            externallySelected = true;
            vehicleTree.getSelectionModel().clearSelection();
            externallySelected = false;
            return;
        }

        TreeItem<VehicleConfigTreeObj> currentSelectedItem = vehicleTree.getSelectionModel().getSelectedItem();
        if (currentSelectedItem != null && isNodeEqual(currentSelectedItem, obj)) {
            return;
        }

        boolean selected = selectNode(vehicleTree.getRoot(), obj);
        if (!selected) {
            externallySelected = true;
            vehicleTree.getSelectionModel().clearSelection();
            externallySelected = false;
        }
    }

    @Subscribe
    private void onUpdateVehicleConfigEvent(UpdateVehicleConfigEvent event) {
        TreeItem<VehicleConfigTreeObj> selectedTreeItem = vehicleTree.getSelectionModel().getSelectedItem();
        VehicleConfigTreeObj selectedItem = selectedTreeItem != null ? selectedTreeItem.getValue() : null;
        if (selectedItem != null) {
            Map<Long, VehicleConfigDTO> updatedConfigMap = new HashMap<>();
            for (VehicleConfigDTO config : event.getVehicleConfig()) {
                if (isSameBrand(selectedItem, config)) {
                    updatedConfigMap.put(config.getId(), config);
                } else if (isSameProject(selectedItem, config)) {
                    updatedConfigMap.put(config.getId(), config);
                } else if (selectedItem.isRecentlyUsedGroup()) {
                    updatedConfigMap.put(config.getId(), config);
                }
            }
            allVehicleConfigs.removeIf(e -> updatedConfigMap.containsKey(e.getId()));
            if (!UpdateEventType.DELETE.equals(event.getUpdateEventType())) {
                allVehicleConfigs.addAll(updatedConfigMap.values());
            }
        }
        fillTableFzgConfig();

        reloadVehicleMyConfigurations();
        vehicleTree.refresh();
    }

    @Subscribe
    private void refreshFzgConfigGewicht(FzgStuecklisteGewichtEvent event) {
        Long vehiclePartListId = event.vehiclePartListId();

        for (VehicleConfigDTO vehicleConfig : configurationTable.getItems()) {
            VehiclePartListDTO vehiclePartList = vehicleConfig.getVehiclePartList();
            if (vehiclePartList != null && vehiclePartListId.equals(vehiclePartList.getId())) {
                Double gewicht = event.gewicht();
                vehiclePartList.setWeight(gewicht);
                break;
            }
        }

        configurationTable.refresh();
    }

    @Subscribe
    private void onUpdateVehicleProjectArchivingEvent(UpdateVehicleProjectArchivingEvent event) {
        vehicleProjects = CacheManager.getVehicleProjects();

        if (vehicleTree.getSelectionModel().getSelectedItem() != null && getControl().isSelected()) {
            VehicleConfigTreeObj selectedItem = vehicleTree.getSelectionModel().getSelectedItem().getValue();
            Boolean expand = (vehicleTree.getSelectionModel().getSelectedItem().isLeaf()
                    || vehicleTree.getSelectionModel().getSelectedItem().isExpanded());

            createTree();
            restoreSelectionAfterRecreationOfTree(selectedItem, getControl().getText(), expand, false);
        } else if (getControl().isSelected()) {
            createTree();
        }
    }

    @Subscribe
    private void onUpdateVehicleProjectMyConfigEvent(UpdateVehicleProjectMyConfigEvent event) {
        reloadVehicleMyConfigurations();
    }

    @Subscribe
    private void onFilteringUpdateEvent(FilteringUpdateEvent event) {
        disablePropertyClearFilters().setValue(configurationTable.getColumnToPredicateDataMap().isEmpty());
    }

    @Subscribe
    private void onUserDataDeletedEvent(UserDataDeletedEvent event) {
        vehicleConfigTreeConfiguration.deleteUserData(vehicleTree.getRoot());
        if (FavoritesFzgConfigTreeConfiguration.class.isAssignableFrom(vehicleConfigTreeConfiguration.getClass())) {
            vehicleTree.getSelectionModel().clearSelection();
        }
    }

    @Subscribe
    private void onVehicleConfigChangedEvent(VehicleConfigChangedEvent event) {
        updateConfigurations(event.vehicleConfig());
        reloadVehicleMyConfigurations();
    }

    private void handleFzgConfigTreeObjSelected(TreeItem<VehicleConfigTreeObj> newValue) {
        if (externallySelected) {
            return;
        }

        updateVehicleTreeDisablement();

        if (newValue != null) {
            if (newValue.getValue().isBrand()) {
                showConfigurationsForBrand(newValue.getValue().getBrand());
            } else if (newValue.getValue().isVehicleProject()) {
                showConfigurationsForVehicleProject(newValue.getValue().getVehicleProject().getId());
            } else if (newValue.getValue().isProductKey()) {
                showConfigurationsForVehicleProject(newValue.getValue().getProductKey());
            } else if (newValue.getValue().isFavoritesGroup()) {
                showConfigurationForFavorites(newValue.getChildren());
            } else if (newValue.getValue().isRecentlyUsedGroup()) {
                showConfigForVehicleConfigs(newValue);
            } else {
                setConfigurations(new ArrayList<>());
            }
        }

        if (onNodeSelected != null && !externallySelected) {
            onNodeSelected.accept(newValue != null ? newValue.getValue() : null);
        }
    }

    private void showConfigForVehicleConfigs(TreeItem<VehicleConfigTreeObj> newValue) {
        if (newValue.getValue().isVehicleConfig()) {
            showConfigurationForVehicleConfigs(List.of(newValue), newValue.getValue().getVehicleConfig().getId());
        } else {
            showConfigurationForVehicleConfigs(newValue.getChildren());
        }
    }

    private void showConfigurationsForBrand(Brand brand) {
        doAsync(() -> VehicleConfigRestClientHolder.getInstance().loadVehicleConfigByBrand(brand.name())
                .vehicleConfigDTOList(), this::setConfigurations);
    }

    private void showConfigurationsForVehicleProject(Long vehicleProjectId) {
        doAsync(() -> VehicleConfigRestClientHolder.getInstance().loadVehicleConfigByProjectId(vehicleProjectId)
                .vehicleConfigDTOList(), this::setConfigurations);
    }

    private void showConfigurationsForVehicleProject(String productKey) {
        doAsync(() -> VehicleConfigRestClientHolder.getInstance().loadVehicleConfigByProductKey(productKey)
                .vehicleConfigDTOList(), this::setConfigurations);
    }

    private void showConfigurationForFavorites(ObservableList<TreeItem<VehicleConfigTreeObj>> list) {
        List<Long> vehicleProjectIds = list.stream().map(treeItem -> treeItem.getValue().getVehicleProject().getId())
                .toList();

        doAsync(() -> VehicleConfigRestClientHolder.getInstance()
                .loadVehicleConfigByProjectIds(vehicleProjectIds.stream().map(Object::toString).toList())
                .vehicleConfigDTOList(), this::setConfigurations);
    }

    private void showConfigurationForVehicleConfigs(List<TreeItem<VehicleConfigTreeObj>> list) {
        showConfigurationForVehicleConfigs(list, null);
    }

    private void showConfigurationForVehicleConfigs(List<TreeItem<VehicleConfigTreeObj>> list, Long configIdToSelect) {
        List<Long> vehicleConfigIds = list.stream().map(treeItem -> treeItem.getValue().getVehicleConfig().getId())
                .toList();

        doAsync(() -> VehicleConfigRestClientHolder.getInstance()
                        .loadVehicleConfigs(vehicleConfigIds.stream().map(Object::toString).toList()).vehicleConfigDTOList(),
                e -> setConfigurations(e, configIdToSelect));
    }

    private void handleTreeFzgConfigMouseClicked(MouseEvent mouseEvent) {
        TreeItem<VehicleConfigTreeObj> selectedItem = vehicleTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        VehicleConfigTreeObj configTreeObj = selectedItem.getValue();
        if (configTreeObj == null) {
            return;
        }

        if (mouseEvent.getClickCount() == 1 && configTreeObj.isVehicleProject()) {
            if (mouseEvent.getPickResult().getIntersectedNode() instanceof ImageView) {
                toggleFavorite(selectedItem);
            }
        } else if (mouseEvent.getClickCount() == 2 && configTreeObj.isVehicleConfig()) {
            VehicleConfigDTO vehicleConfig = configTreeObj.getVehicleConfig();
            EventBus.getInstance().post(new ShowEfsTabEvent(vehicleConfig, false));
        }
    }

    private void initTableFzgConfig() {
        getFzgConfigControl().getSelectionModel().getSelectedItems()
                .addListener((InvalidationListener) observable -> updateTable());
        getFzgConfigControl().setRowFactory(tv -> {
            TableRow<VehicleConfigDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty()) {
                    return;
                }

                if (event.getButton() == MouseButton.PRIMARY) {
                    if (event.getClickCount() == 2) {
                        handleActionStuecklisteBearbeiten();
                    }
                }
            });
            return row;
        });

        getFzgConfigControl().setItems(visibleVehicleConfigs);
    }

    private void updateTable() {
        ObservableList<VehicleConfigDTO> selectedItems = getFzgConfigControl().getSelectionModel().getSelectedItems();

        boolean deletable = !selectedItems.isEmpty();
        boolean editable = !selectedItems.isEmpty();
        boolean changeable = !selectedItems.isEmpty();
        for (VehicleConfigDTO selectedItem : selectedItems) {
            deletable = deletable && UserChecker.isAdminOrUser(selectedItem.getUserCreate());
            editable = editable && UserChecker.isAdminOrInUserGroup(selectedItem.getOwnerGroup());
            changeable = changeable && UserChecker.isAdminOrInUserGroup(selectedItem.getOwnerGroup());
        }

        disablePropertyStuecklisteEditable().set(!editable);
        disablePropertyStuecklisteDeletable().set(!deletable);
        disablePropertyChangeOwnerGroup().set(!changeable);
    }

    private void updateVehicleTreeDisablement() {
        TreeItem<VehicleConfigTreeObj> selectedItem = vehicleTree.getSelectionModel().getSelectedItem();

        boolean establishable = false;
        boolean favorite = false;
        if (selectedItem != null) {
            VehicleConfigTreeObj value = selectedItem.getValue();

            favorite = value.isVehicleProject();

            VehicleConfigDTO vehicleConfig = value.getVehicleConfig();
            if (vehicleConfig != null) {
                establishable = vehicleConfig.getDeletionDate() != null && UserChecker.isAdminOrInUserGroups(
                        vehicleConfig.getUserGroups());
            }
        }

        disablePropertyReestablish().set(!establishable);
        disablePropertyFavorite().set(!favorite);
    }

    private void toggleFavorite(TreeItem<VehicleConfigTreeObj> treeItem) {
        PropertyType type = PropertyType.FAVORITE_PROJECTS;
        String vehicleProjectId = treeItem.getValue().getVehicleProject().getId().toString();
        boolean isBecomeFavorite = !treeItem.getValue().isFavorite();

        if (isBecomeFavorite) {
            doAsync(() -> UserPropertyRestClientHolder.getInstance()
                            .save(new SaveUserPropertyDTO(type, vehicleProjectId)),
                    r -> EventBus.getInstance().post(new UpdateVehicleProjectMyConfigEvent()));

            favoriteVehicleConfigIds.add(treeItem.getValue().getVehicleProject().getId());
        } else {
            doAsync(() -> UserPropertyRestClientHolder.getInstance().delete(type, vehicleProjectId),
                    r -> EventBus.getInstance().post(new UpdateVehicleProjectMyConfigEvent()));

            favoriteVehicleConfigIds.remove(treeItem.getValue().getVehicleProject().getId());
        }

        treeItem.getValue().toggleFavorite();

    }

    private void createTree(List<VehicleConfigDTO> deletedVehicleConfigs) {
        this.deletedVehicleConfigs = deletedVehicleConfigs;
        createTree();
    }

    private void createTree() {
        TreeItem<VehicleConfigTreeObj> treeItemRoot = vehicleConfigTreeConfiguration.createTree(
                recentlyUsedVehicleConfigs, deletedVehicleConfigs, vehicleProjects, configCountMap, patternSearchTerm,
                favoriteVehicleConfigIds);

        vehicleTree.setRoot(treeItemRoot);
        vehicleTree.setShowRoot(false);
        vehicleTree.getRoot().setExpanded(true);
    }

    private boolean matchFzgConfigTableFzgConfig(VehicleConfigDTO vehicleConfig) {
        return patternSearchTermTableFzgConfig == null || (vehicleConfig.getVehicleProject() != null
                && patternSearchTermTableFzgConfig.matches(vehicleConfig.getVehicleProject().getProjectName()) != null)
                || patternSearchTermTableFzgConfig.matches(vehicleConfig.getName()) != null || (
                vehicleConfig.getValidDate() != null && patternSearchTermTableFzgConfig.matches(
                        DateUtil.formatDate(vehicleConfig.getValidDate(), DD_MM_YYYY)) != null) || (
                vehicleConfig.getModel() != null && (
                        patternSearchTermTableFzgConfig.matches(vehicleConfig.getModel().getModelKey()) != null || (
                                vehicleConfig.getModel().getModelImport().getSalesRegion() != null &&
                                        patternSearchTermTableFzgConfig.matches(
                                                vehicleConfig.getModel().getModelImport().getSalesRegion().id())
                                                != null) || (
                                vehicleConfig.getModel().getModelImport().getModelYear() != null &&
                                        patternSearchTermTableFzgConfig.matches(
                                                vehicleConfig.getModel().getModelImport().getModelYear().toString())
                                                != null)
                                || patternSearchTermTableFzgConfig.matches(vehicleConfig.getModel().getDescription())
                                != null || patternSearchTermTableFzgConfig.matches(
                                DateUtil.formatDate(vehicleConfig.getModel().getBeginDate(), DD_MM_YYYY)) != null ||
                                patternSearchTermTableFzgConfig.matches(
                                        DateUtil.formatDate(vehicleConfig.getModel().getEndDate(), DD_MM_YYYY))
                                        != null)) || patternSearchTermTableFzgConfig.matches(
                DateUtil.formatDate(vehicleConfig.getTimestampCreate(), DD_MM_YYYY) + StringConstant.SPACE_SLASH_SPACE
                        + vehicleConfig.getUserCreate()) != null;
    }

    private List<VehicleConfigDTO> getSelectedFzgConfigsFromTable() {
        return getFzgConfigControl().getSelectionModel().getSelectedItems();
    }

    private <S> void expand(TreeItem<S> item, boolean expand) {
        item.setExpanded(expand);
        ObservableList<TreeItem<S>> children = item.getChildren();
        for (TreeItem<S> element : children) {
            expand(element, expand);
        }
    }

    private void setConfigurations(List<VehicleConfigDTO> configs) {
        setConfigurations(configs, null);
    }

    private void setConfigurations(List<VehicleConfigDTO> configs, Long configToSelect) {
        allVehicleConfigs = configs;
        long[] prevSelectedItemIds = getFzgConfigControl().getSelectionModel().getSelectedItems().stream()
                .map(VehicleConfigDTO::getId).mapToLong(Long::longValue).toArray();

        fillTableFzgConfig();

        selectTableItems(configToSelect, prevSelectedItemIds);
    }

    private void selectTableItems(Long configToSelect, long[] selectedItemIds) {
        if (configToSelect != null) {
            VehicleConfigDTO select = Stream.concat(configurationTable.getItems().stream(),
                            deletedVehicleConfigs.stream()).filter(e -> e.getId().equals(configToSelect)).findFirst()
                    .orElse(null);
            configurationTable.getSelectionModel().select(select);
            configurationTable.scrollTo(select);
            configurationTable.requestFocus();
            return;
        }

        if (selectedItemIds.length == 0) {
            return;
        }

        int[] prevSelectedIndices = new int[selectedItemIds.length];
        List<Long> configIds = configurationTable.getItems().stream().map(VehicleConfigDTO::getId).toList();
        for (int i = 0; i < selectedItemIds.length; i++) {
            prevSelectedIndices[i] = configIds.indexOf(selectedItemIds[i]);
        }

        int firstItemIndex = prevSelectedIndices[0];
        configurationTable.getSelectionModel().selectIndices(firstItemIndex, prevSelectedIndices);
        configurationTable.scrollTo(firstItemIndex);
        configurationTable.requestFocus();
    }

    private void updateConfigurations(VehicleConfigDTO vehicleConfig) {
        VehicleConfigDTO selectedConfig = configurationTable.getSelectionModel().getSelectedItem();
        Long idToSelect = selectedConfig != null ? selectedConfig.getId() : null;
        allVehicleConfigs.removeIf(e -> e.getId().equals(vehicleConfig.getId()));
        allVehicleConfigs.add(vehicleConfig);
        fillTableFzgConfig();

        VehicleConfigDTO select = configurationTable.getItems().stream().filter(e -> e.getId().equals(idToSelect))
                .findFirst().orElse(null);
        configurationTable.getSelectionModel().select(select);
    }

    private void fillTableFzgConfig() {
        visibleVehicleConfigs.clear();

        if (allVehicleConfigs != null) {
            for (VehicleConfigDTO vehicleConfig : allVehicleConfigs) {
                addVehicleConfigToTable(vehicleConfig);
            }

            getFzgConfigControl().sort();
        }
    }

    private void addVehicleConfigToTable(VehicleConfigDTO vehicleConfig) {
        if (matchFzgConfigTableFzgConfig(vehicleConfig) && (
                (UserProperties.getUser().isAdmin() && vehicleTree.getSelectionModel().getSelectedItem() != null && (
                        vehicleTree.getSelectionModel().getSelectedItem().getValue().getVehicleProject() != null
                                && vehicleConfig.getDeletionDate() == null
                                || vehicleTree.getSelectionModel().getSelectedItem().getValue().getVehicleConfig()
                                != null)) || vehicleConfig.getDeletionDate() == null)) {
            visibleVehicleConfigs.add(vehicleConfig);
        }

        getFzgConfigControl().sort();
    }

    private boolean isSameBrand(VehicleConfigTreeObj selectedItem, VehicleConfigDTO newVehicleConfig) {
        return selectedItem.isBrand() && selectedItem.getBrand().getBrandName()
                .equals(newVehicleConfig.getVehicleProject().getBrandCode().getBrandName());
    }

    private boolean isSameProject(VehicleConfigTreeObj selectedItem, VehicleConfigDTO newVehicleConfig) {
        return selectedItem.isVehicleProject() && selectedItem.getVehicleProject().getId()
                .equals(newVehicleConfig.getVehicleProject().getId());
    }

    private void reloadVehicleMyConfigurations() {
        boolean isMyConfigurationTab = getControl().getText().equals(I18N.getString("tab.favorites.title"));
        if (isMyConfigurationTab) {
            doAsync(() -> VehicleConfigRestClientHolder.getInstance().loadVehicleConfigByRecentlyUsed()
                    .vehicleConfigDTOList(), result -> {
                recentlyUsedVehicleConfigs.clear();
                recentlyUsedVehicleConfigs.addAll(result);

                TreeItem<VehicleConfigTreeObj> selectedItem = vehicleTree.getSelectionModel().getSelectedItem();

                externallySelected = true;
                createTree();
                externallySelected = false;

                if (selectedItem != null) {
                    restoreSelectionAfterRecreationOfTree(selectedItem.getValue(), getControl().getText(), true, true);
                }
            });
            if (UserProperties.getUser().isAdmin()) {
                doAsync(() -> VehicleConfigRestClientHolder.getInstance().loadDeletedVehicleConfigs()
                        .vehicleConfigDTOList(), result -> createTree(result));
            }
        } else {
            updateFavoriteIcons(vehicleTree.getRoot());
            vehicleTree.refresh();
        }
    }

    private void updateFavoriteIcons(TreeItem<VehicleConfigTreeObj> item) {
        if (item.getValue() != null && item.getValue().isVehicleProject()) {
            item.getValue().setFavorite(favoriteVehicleConfigIds.contains(item.getValue().getVehicleProject().getId()));
        }

        for (TreeItem<VehicleConfigTreeObj> vehicleConfigTreeObjTreeItem : item.getChildren()) {
            updateFavoriteIcons(vehicleConfigTreeObjTreeItem);
        }
    }

    private void restoreSelectionAfterRecreationOfTree(VehicleConfigTreeObj selectedItem, String tabText,
            Boolean expand, boolean updateRow) {
        ObservableList<TreeItem<VehicleConfigTreeObj>> rootChildren = vehicleTree.getRoot().getChildren();

        if (tabText.matches(I18N.getString("tab.brand.title"))) {
            selectAndExpandBrand(selectedItem, expand, updateRow, rootChildren);
        } else if (tabText.matches(I18N.getString("tab.productid.title"))) {
            selectAndExpandByProductId(selectedItem, expand, updateRow, rootChildren);
        } else if (tabText.matches(I18N.getString("tab.favorites.title"))) {
            if (selectedItem.isFavoritesGroup()) {
                selectAndExpandFavoritGroup(selectedItem, expand);
            } else if (selectedItem.isRecentlyUsedGroup()) {
                TreeItem<VehicleConfigTreeObj> recentlyUsedGroupItem = vehicleTree.getRoot().getChildren().get(1);
                //If there is no vehicleConfig, then it is the root item that is selected
                if (selectedItem.getVehicleConfig() == null) {
                    selectAndExpand(recentlyUsedGroupItem, expand);
                } else {
                    TreeItem<VehicleConfigTreeObj> foundItem = findInRecentlyUsed(recentlyUsedGroupItem, selectedItem);
                    if (foundItem != null) {
                        selectAndExpand(foundItem, expand);
                    }
                }
            }
        }
    }

    private void selectAndExpandFavoritGroup(VehicleConfigTreeObj selectedItem, Boolean expand) {
        TreeItem<VehicleConfigTreeObj> favoritesGroupItem = vehicleTree.getRoot().getChildren().getFirst();
        // if there is no vehicleConfig, then it is the root item that is selected
        if (selectedItem.getVehicleConfig() == null) {
            selectAndExpand(favoritesGroupItem, expand);
        } else {
            TreeItem<VehicleConfigTreeObj> foundItem = findInFavorites(favoritesGroupItem, selectedItem);
            if (foundItem != null) {
                selectAndExpand(foundItem, expand);
            }
        }
    }

    private void selectAndExpandByProductId(VehicleConfigTreeObj selectedItem, Boolean expand, boolean updateRow,
            ObservableList<TreeItem<VehicleConfigTreeObj>> rootChildren) {
        for (TreeItem<VehicleConfigTreeObj> productIdItem : rootChildren) {
            if (selectedItem.getVehicleProject() != null) {
                if (productIdItem.getValue().getProductKey()
                        .matches(selectedItem.getVehicleProject().getProductKey())) {
                    if (updateRow) {
                        restoreLeafSelection(productIdItem, selectedItem, expand);
                    } else {
                        selectAndExpand(productIdItem, expand);
                    }
                }
            } else {
                if (productIdItem.getValue().getProductKey().matches(selectedItem.getProductKey())) {
                    selectAndExpand(productIdItem, expand);
                }
            }
        }
    }

    private void selectAndExpandBrand(VehicleConfigTreeObj selectedItem, Boolean expand, boolean updateRow,
            ObservableList<TreeItem<VehicleConfigTreeObj>> rootChildren) {
        for (TreeItem<VehicleConfigTreeObj> brandItem : rootChildren) {
            if (selectedItem.getVehicleProject() != null) {
                if (brandItem.getValue().getBrand().getBrandName()
                        .matches(selectedItem.getVehicleProject().getBrandCode().getBrandName())) {
                    if (updateRow) {
                        restoreLeafSelection(brandItem, selectedItem, expand);
                    } else {
                        selectAndExpand(brandItem, expand);
                    }
                }
            } else {
                if (brandItem.getValue().getBrand().getBrandName().matches(selectedItem.getBrand().getBrandName())) {
                    selectAndExpand(brandItem, expand);
                }
            }
        }
    }

    private TreeItem<VehicleConfigTreeObj> findInFavorites(TreeItem<VehicleConfigTreeObj> favoriteItem,
            VehicleConfigTreeObj selectedItem) {
        if (favoriteItem == null) {
            return null;
        }
        for (TreeItem<VehicleConfigTreeObj> item : favoriteItem.getChildren()) {
            if (Objects.equals(item.getValue().getVehicleProject().getId(), selectedItem.getVehicleProject().getId())) {
                return item;
            }
        }
        return null;
    }

    private TreeItem<VehicleConfigTreeObj> findInRecentlyUsed(TreeItem<VehicleConfigTreeObj> recently,
            VehicleConfigTreeObj selectedItem) {
        if (recently == null) {
            return null;
        }

        for (TreeItem<VehicleConfigTreeObj> item : recently.getChildren()) {
            if (Objects.equals(item.getValue().getVehicleConfig().getId(), selectedItem.getVehicleConfig().getId())) {
                return item;
            }
        }

        return null;
    }

    private void restoreLeafSelection(TreeItem<VehicleConfigTreeObj> parentTreeItem,
            VehicleConfigTreeObj selectedTreeObject, boolean expand) {
        for (TreeItem<VehicleConfigTreeObj> treeItem : parentTreeItem.getChildren()) {
            if (treeItem.getValue().getVehicleProject().getId()
                    .equals(selectedTreeObject.getVehicleProject().getId())) {
                selectAndExpand(treeItem, expand);

                break;
            }
        }
    }

    private void selectAndExpand(TreeItem<VehicleConfigTreeObj> productIdItem, boolean expand) {
        productIdItem.setExpanded(expand);
        vehicleTree.getSelectionModel().select(productIdItem);
    }

    private boolean selectNode(TreeItem<VehicleConfigTreeObj> node, VehicleConfigTreeObj obj) {
        if (isNodeEqual(node, obj)) {
            externallySelected = true;
            vehicleTree.getSelectionModel().select(node);
            if (!getControl().isSelected()) {
                vehicleTree.scrollTo(vehicleTree.getRow(node));
            }

            externallySelected = false;
            return true;
        }

        for (TreeItem<VehicleConfigTreeObj> ti : node.getChildren()) {
            if (selectNode(ti, obj)) {
                return true;
            }
        }

        return false;
    }

    private boolean isNodeEqual(TreeItem<VehicleConfigTreeObj> ti, VehicleConfigTreeObj obj) {
        VehicleConfigTreeObj treeObject = ti.getValue();
        return treeObject != null && treeObject.isVehicleProject() && obj.isVehicleProject()
                && treeObject.getVehicleProject().getId().equals(obj.getVehicleProject().getId());
    }

    public TreeView<VehicleConfigTreeObj> getVehicleTree() {
        return vehicleTree;
    }

    public void setMainTabPaneController(MainTabPaneController mainTabPaneController) {
        this.mainTabPaneController = mainTabPaneController;
    }

    public void setOnNodeSelected(Consumer<VehicleConfigTreeObj> onNodeSelected) {
        this.onNodeSelected = onNodeSelected;
    }
}
