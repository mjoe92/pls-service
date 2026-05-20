package de.vw.paso.client.stueckliste.fzgkonfig;

import java.net.URL;
import java.util.Collection;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.control.treetable.TreeFilteringUpdateEvent;
import de.vw.paso.client.main.ribbonmenu.fzgkonfig.RibbonMenuFzgKonfigEvent;
import de.vw.paso.client.main.ribbonmenu.fzgkonfig.RibbonMenuFzgKonfigListener;
import de.vw.paso.client.main.tab.AbstractMainTabController;
import de.vw.paso.client.main.tab.MainTabPaneController;
import de.vw.paso.client.stammdaten.FilteringUpdateEvent;
import de.vw.paso.client.stueckliste.FzgKonfigMainTabController;
import de.vw.paso.client.stueckliste.event.SelectStuecklisteTabEvent;
import de.vw.paso.client.stueckliste.fzgkonfig.content.AbstractContentController;
import de.vw.paso.client.stueckliste.fzgkonfig.content.fzgprojekt.FzgProjektController;
import de.vw.paso.client.stueckliste.fzgkonfig.content.konfiguration.ConfigurationController;
import de.vw.paso.client.stueckliste.fzgkonfig.content.konfiguration.PrNumberTreeItem;
import de.vw.paso.client.stueckliste.fzgkonfig.content.konfiguration.PrNumberTreeItemObject;
import de.vw.paso.client.stueckliste.fzgkonfig.content.modell.ModelController;
import de.vw.paso.client.stueckliste.fzgkonfig.content.zusammenfassung.ZusammenfassungController;
import de.vw.paso.client.stueckliste.fzgkonfig.event.UpdateMainTabTitleEvent;
import de.vw.paso.client.stueckliste.fzgkonfig.menu.FzgConfigMenuController;
import de.vw.paso.client.stueckliste.fzgkonfig.menu.status.VehicleConfigCategoryRegistry;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.delegate.stueckliste.efsedit.EfsElementRestClientHolder;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.pls.Status;
import de.vw.paso.service.vehicle.ICreateVehiclePartListConsumer;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.vehicle.VehicleConfigCategory;

@FXController(name = "fzg-konfig-tab")
public class FzgConfigTabController extends AbstractMainTabController
        implements Initializable, RibbonMenuFzgKonfigListener, ICreateVehiclePartListConsumer {

    private final ObjectProperty<VehicleConfigDTO> vehicleConfig;
    private final ObjectProperty<EventHandler<UpdateMainTabTitleEvent>> updateMainTabActionProperty;
    private final ObjectProperty<EventHandler<Event>> selectTabEventHandler;

    @FXML
    private Tab tabFzgConfig;
    @FXML
    private BorderPane mainPane;
    @FXML
    private StackPane contentPane;
    @FXML
    private FzgConfigMenuController fzgConfigMenuController;

    private Runnable closeAction;

    private BooleanProperty disablePropertyCreatePartList;
    private BooleanProperty disablePropertyClearFilters;

    private AbstractContentController lastContentController;
    private Long lastSetVersionId;

    public FzgConfigTabController() {
        vehicleConfig = new SimpleObjectProperty<>();
        updateMainTabActionProperty = new SimpleObjectProperty<>(this, "UpdateMainTabEvent");
        selectTabEventHandler = new SimpleObjectProperty<>(this, "tabSelectionEventHandler");
    }

    @Override
    public void createVehiclePartList(long vehicleConfigId) {
        ServiceController<VehicleConfigDTO> serviceController = new ServiceController<>();
        serviceController.setOnSucceeded(e -> createEfs(serviceController.getValue()));
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        serviceController.setExecutionTime(100);
        serviceController.start(
                () -> VehicleConfigRestClientHolder.getInstance().createVehiclePartList(vehicleConfigId));
    }

    @Override
    public BooleanProperty disablePropertyClearFilters() {
        if (disablePropertyClearFilters == null) {
            disablePropertyClearFilters = new SimpleBooleanProperty(true);
        }

        return disablePropertyClearFilters;
    }

    @Override
    public BooleanProperty disablePropertyErstelleStueckliste() {
        if (disablePropertyCreatePartList == null) {
            disablePropertyCreatePartList = new SimpleBooleanProperty(true);
        }

        return disablePropertyCreatePartList;
    }

    @Override
    public Tab getControl() {
        return tabFzgConfig;
    }

    @Override
    public RibbonMenuFzgKonfigEvent getRibbonMenuEvent() {
        return new RibbonMenuFzgKonfigEvent(this, this.getControl().getText());
    }

    @Override
    public Parent getStyleableParent() {
        return mainPane;
    }

    @Override
    public void handleActionClearFilters() {
        AbstractContentController contentController = lastContentController;

        if (contentController instanceof ConfigurationController configurationController) {
            configurationController.getTableviewPrNummern().clearFilters();

            TreeItem<PrNumberTreeItemObject> root = configurationController.getTableviewPrNummern().getRoot();
            if (root instanceof PrNumberTreeItem) {
                configurationController.sortAndSetTree(
                        (PrNumberTreeItem) configurationController.getTableviewPrNummern().getRoot());
            }
        } else if (contentController instanceof ModelController modelController) {
            modelController.getTreeTableViewModell().clearFilters();
            modelController.getTableViewModelImport().clearFilters();
        } else if (contentController instanceof FzgProjektController fzgProjektController) {
            fzgProjektController.getFzgProjektTableView().clearFilters();
        }

        disablePropertyClearFilters().set(true);
    }

    @Override
    public void handleActionErstelleStueckliste() {
        createVehiclePartList(getVehicleConfig().getId());
    }

    @Subscribe
    private void handleFilteringUpdateEvent(FilteringUpdateEvent event) {
        AbstractContentController contentController = this.lastContentController;

        if (contentController instanceof ModelController modelController) {
            boolean disable =
                    !modelController.getTreeTableViewModell().isFiltered() && modelController.getTableViewModelImport()
                            .getColumnToPredicateDataMap().isEmpty();
            disablePropertyClearFilters().set(disable);
        } else if (contentController instanceof FzgProjektController fzgProjektController) {
            disablePropertyClearFilters().set(
                    fzgProjektController.getFzgProjektTableView().getColumnToPredicateDataMap().isEmpty());
        }
    }

    @Subscribe
    private void handleTreeFilteringUpdateEvent(TreeFilteringUpdateEvent event) {
        AbstractContentController contentController = lastContentController;

        if (contentController instanceof ConfigurationController configurationController) {
            disablePropertyClearFilters().set(!configurationController.getTableviewPrNummern().isFiltered());
        } else if (contentController instanceof ModelController modelController) {
            boolean disable =
                    !modelController.getTreeTableViewModell().isFiltered() && modelController.getTableViewModelImport()
                            .getColumnToPredicateDataMap().isEmpty();
            disablePropertyClearFilters().set(disable);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        fzgConfigMenuController.selectedVehicleConfigCategoryBinding()
                .addListener((obs, oldVal, newVal) -> handleContentChange());
    }

    public final ObjectProperty<EventHandler<Event>> selectTabEventHandler() { // NO_UCD (use private)
        return selectTabEventHandler;
    }

    public void setCloseAction(Runnable closeAction) {
        this.closeAction = closeAction;
    }

    public final void setSelectTabAction(EventHandler<Event> handler) { // NO_UCD (use private)
        selectTabEventHandler().set(handler);
    }

    public final void setUpdateMainTabAction(EventHandler<UpdateMainTabTitleEvent> handler) {
        updateMainTabActionProperty().set(handler);
    }

    @Override
    public void start() {
        fzgConfigMenuController.vehicleConfigProperty().bindBidirectional(vehicleConfig);
        fzgConfigMenuController.setCloseAction(this.closeAction);
        fzgConfigMenuController.start();

        disablePropertyErstelleStueckliste().bind(fzgConfigMenuController.allStatusOkProperty());
    }

    @Override
    public void stop() {
        super.stop();
        lastContentController = null;
        fzgConfigMenuController.stop();
        vehicleConfig.unbind();
        disablePropertyCreatePartList.unbind();
    }

    public final ObjectProperty<VehicleConfigDTO> vehicleConfigProperty() {
        return vehicleConfig;
    }

    private void createEfs(VehicleConfigDTO vehicleConfig) {
        this.vehicleConfig.set(vehicleConfig);

        ServiceController<VehiclePartList> serviceController = new ServiceController<>();
        serviceController.setOnSucceeded(e -> finishEfsCreate());
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        serviceController.setStatusMessage(I18N.getString("message.erstellen.stueckliste"));
        serviceController.setExecutionTime(5000);
        serviceController.start(() -> EfsElementRestClientHolder.getInstance().createEfs(vehicleConfig));
    }

    private void finishEfsCreate() {
        selectTabEventHandler().get()
                .handle(new SelectStuecklisteTabEvent(this, SelectStuecklisteTabEvent.SELECTED_EFS_TAB));
    }

    private AbstractContentController getContentController(VehicleConfigCategory menuKategorie) {
        AbstractContentController contentController = BaseController.load(
                VehicleConfigCategoryRegistry.getControllerClass(menuKategorie));
        contentController.setVehicleConfigCategory(menuKategorie);
        contentController.vehicleConfigProperty().bind(vehicleConfig);
        initController(contentController);

        return contentController;
    }

    private VehicleConfigDTO getVehicleConfig() {
        return vehicleConfig.get();
    }

    private void handleContentChange() {
        fzgConfigMenuController.saveVehicleConfig(null);

        disablePropertyClearFilters().set(true);

        fzgConfigMenuController.validProperty().unbind();
        fzgConfigMenuController.dirtyProperty().unbind();
        fzgConfigMenuController.validProperty().set(false);
        fzgConfigMenuController.dirtyProperty().set(false);

        if (lastContentController != null) {
            lastContentController.stop();
        }

        VehicleConfigCategory menuCategory = fzgConfigMenuController.selectedVehicleConfigCategoryBinding().get();

        lastContentController = getContentController(menuCategory);

        setContent(lastContentController.getControl());

        lastContentController.start();

        fzgConfigMenuController.validProperty().bind(lastContentController.validProperty());
        fzgConfigMenuController.dirtyProperty().bind(lastContentController.dirtyProperty());
    }

    private void initController(AbstractContentController contentController) {
        if (contentController instanceof ZusammenfassungController zusammenfassungController) {
            lastSetVersionId = getVehicleConfig().getSetVersionId();
            zusammenfassungController.dirtyProperty()
                    .addListener(inv -> saveChangesReloadSet(zusammenfassungController));
        }
    }

    private void saveChangesReloadSet(ZusammenfassungController controller) {
        if (!controller.dirtyProperty().get()) {
            return;
        }

        fzgConfigMenuController.saveVehicleConfig(() -> reloadSetVersions(controller));
    }

    private void reloadSetVersions(ZusammenfassungController controller) {
        controller.dirtyProperty().set(false);

        VehicleConfigDTO selectedConfig = getVehicleConfig();
        if (Status.COMPLETE != selectedConfig.getStatus()) {
            return;
        }

        if (Objects.equals(selectedConfig.getSetVersionId(), lastSetVersionId)) {
            return;
        }

        lastSetVersionId = selectedConfig.getSetVersionId();

        Collection<AbstractMainTabController> controllers = MainTabPaneController.getMapTabController().values();
        for (AbstractMainTabController control : controllers) {
            if (control instanceof FzgKonfigMainTabController fzgKonfigMainTabController) {
                fzgKonfigMainTabController.reloadElementsWithSetKeys();
                return;
            }
        }
    }

    private void setContent(BorderPane gridPane) {
        contentPane.getChildren().setAll(gridPane);
    }

    private ObjectProperty<EventHandler<UpdateMainTabTitleEvent>> updateMainTabActionProperty() {
        return updateMainTabActionProperty;
    }
}
