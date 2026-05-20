package de.vw.paso.client.stueckliste.efs.views;

import java.net.URL;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumnBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.exception.ControllerException;
import de.vw.paso.client.stueckliste.efs.tree.SingleVehiclePartListController;
import de.vw.paso.client.stueckliste.efs.views.historie.HistoryTabController;
import de.vw.paso.client.stueckliste.efs.views.historie.RevisionTabController;
import de.vw.paso.client.stueckliste.efs.views.historie.event.EfsElementSearchEvent;
import de.vw.paso.client.stueckliste.efs.views.historie.event.EfsElementSelectionEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.InspectorTabController;
import de.vw.paso.client.stueckliste.efs.views.inspector.event.InspectorJumpToElementEvent;
import de.vw.paso.client.stueckliste.efs.views.suche.SearchTabController;
import de.vw.paso.delegate.stueckliste.inspector.InspectorRestClientHolder;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.inspector.InspectorIgnoreDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

@FXController(name = "efs-view-tab-pane")
public class EfsViewTabPaneController extends BaseController<TabPane> {

    private final Map<EfsViewTabType, AbstractEfsViewTabController> typeToTabMap;
    private final ObjectProperty<EventHandler<EfsViewTabEvent>> propertyEfsViewTabAction;
    private final ObjectProperty<EventHandler<EfsElementSelectionEvent>> efsSelectionProperty;
    private final ObjectProperty<EventHandler<EfsElementSearchEvent>> efsSearchProperty;

    private EventHandler<InspectorJumpToElementEvent> inspectorJumpToHandler;

    @FXML
    private TabPane tabPaneEfsView;

    public EfsViewTabPaneController() {
        typeToTabMap = new HashMap<>();
        propertyEfsViewTabAction = new SimpleObjectProperty<>(this, "EfsViewTabAction");
        efsSelectionProperty = new SimpleObjectProperty<>(this, "EfsElementHistory");
        efsSearchProperty = new SimpleObjectProperty<>(this, "EfsElementSearch");
    }

    @Override
    public TabPane getControl() {
        return tabPaneEfsView;
    }

    @Override
    public Parent getStyleableParent() {
        return getControl();
    }

    @Override
    public void start() {
        //nothing to do
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        KeyCombination keyCombo = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        getControl().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            Tab selectedTab = getControl().getSelectionModel().getSelectedItem();
            if (selectedTab != null && keyCombo.match(keyEvent)) {
                tabPaneEfsView.getTabs().remove(selectedTab);
                typeToTabMap.entrySet().removeIf(entry -> entry.getValue().getControl().equals(selectedTab));

                autoResize(EfsViewTabEvent.REMOVE_EFS_VIEW_TAB, getTabCount());
            }
        });
    }

    private void autoResize(EventType<EfsViewTabEvent> tabEvent, int count) {
        EfsViewTabEvent resizeEvent = new EfsViewTabEvent(this, tabEvent, count);
        efsViewTabActionProperty().get().handle(resizeEvent);
    }

    public void handleActionShowHistory(EfsElementDTO efsElement) {
        AbstractEfsViewTabController opened = typeToTabMap.get(EfsViewTabType.HISTORY);
        if (opened != null) {
            tabPaneEfsView.getSelectionModel().select(opened.getControl());
            return;
        }

        try {
            HistoryTabController historyController = load(HistoryTabController.class);
            historyController.initEfsElement(efsElement, this::addTab);

            tabPaneEfsView.getSelectionModel().select(historyController.getControl());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void handleActionReloadHistory(EfsElementDTO efsElement) {
        AbstractEfsViewTabController opened = typeToTabMap.get(EfsViewTabType.HISTORY);
        if (opened == null) {
            return;
        }

        HistoryTabController historyTabController = (HistoryTabController) opened;
        historyTabController.initEfsElement(efsElement, null);
    }

    public void handleActionShowRevisions(VehicleConfigDTO vehicleConfig, boolean selectTab) {
        AbstractEfsViewTabController opened = typeToTabMap.get(EfsViewTabType.REVISION);
        if (opened != null) {
            tabPaneEfsView.getSelectionModel().select(opened.getControl());
            return;
        }

        try {
            RevisionTabController revisionTabController = load(RevisionTabController.class);
            revisionTabController.setEfsSelectionAction(efsSelectionProperty().get());

            revisionTabController.initRevisions(vehicleConfig.getVehiclePartList(), this::addTab);

            if (selectTab) {
                tabPaneEfsView.getSelectionModel().select(revisionTabController.getControl());
            }
        } catch (Exception exception) {
            handleException(exception);
        }
    }

    public void handleActionReloadRevisions(VehicleConfigDTO vehicleConfig) {
        AbstractEfsViewTabController opened = typeToTabMap.get(EfsViewTabType.REVISION);
        if (opened == null) {
            return;
        }

        handleActionShowRevisions(vehicleConfig, false);
    }

    public void handleActionShowSearch(SingleVehiclePartListController partListController) {
        AbstractEfsViewTabController opened = typeToTabMap.get(EfsViewTabType.SEARCH);
        if (opened != null) {
            tabPaneEfsView.getSelectionModel().select(opened.getControl());
            return;
        }

        try {
            SearchTabController searchTabController = load(SearchTabController.class);
            searchTabController.setParentController(partListController);
            searchTabController.setEfsSearchAction(efsSearchProperty().get());
            searchTabController.setEfsSelectionAction(efsSelectionProperty().get());

            List<String> visibleColumns = partListController.getEfsTreeTableView().getVisibleLeafColumns().stream()
                .map(TableColumnBase::getText).collect(Collectors.toList());
            searchTabController.setVisibleColumns(visibleColumns);

            addTab(searchTabController);
        } catch (ControllerException exception) {
            handleException(exception);
        }
    }

    public void handleActionShowInspector(VehicleConfigDTO vehicleConfig, EfsElementDTO root) {
        InspectorTabController opened = (InspectorTabController) typeToTabMap.get(EfsViewTabType.INSPECTOR);
        if (opened != null) {
            tabPaneEfsView.getSelectionModel().select(opened.getControl());
            opened.refreshInspectorItems(root, null);

            return;
        }

        InspectorTabController controller = load(InspectorTabController.class);
        registerSubController(controller);

        controller.setOnJumpToElementHandler(inspectorJumpToHandler);
        controller.setVehicleConfig(vehicleConfig);

        ServiceController<Collection<InspectorIgnoreDTO>> serviceController = new ServiceController<>();

        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        serviceController.setStatusMessage(I18N.getString("inspector.loading"));
        serviceController.setExecutionTime(5000);
        serviceController.start(
            () -> InspectorRestClientHolder.getInstance().loadIgnoreEntries(vehicleConfig.getVehiclePartList().getId())
                .inspectorIgnoredList());
        serviceController.setOnSucceeded(e -> {
            try {
                controller.refreshInspectorItems(root, serviceController.getValue());

                addTab(controller);
            } catch (ConcurrentModificationException exc) {
                // intentionally left blank; prevent user interaction before view init
            }
        });
    }

    public void reloadInspector(VehicleConfigDTO vehicleConfig) {
        AbstractEfsViewTabController opened = typeToTabMap.get(EfsViewTabType.INSPECTOR);
        if (opened == null) {
            return;
        }

        InspectorTabController inspectorTabController = (InspectorTabController) opened;
        inspectorTabController.setVehicleConfig(vehicleConfig);
        inspectorTabController.refreshInspectorItems();
    }

    private void addTab(AbstractEfsViewTabController controller) {
        autoResize(EfsViewTabEvent.ADD_EFS_VIEW_TAB, getTabCount() + 1);

        tabPaneEfsView.getTabs().add(controller.getControl());

        controller.getControl().setOnClosed(event -> onTabClosed(controller));

        cacheTabController(controller);

        tabPaneEfsView.getSelectionModel().select(controller.getControl());

        autoResize(EfsViewTabEvent.RESIZE_EFS_VIEW_TAB, getTabCount());
    }

    private void cacheTabController(AbstractEfsViewTabController controller) {
        controller.getControl().setId(String.valueOf(controller.hashCode()));

        typeToTabMap.put(controller.getType(), controller);
    }

    private void onTabClosed(AbstractEfsViewTabController controller) {
        controller.stop();
        unregisterSubController(controller);
        typeToTabMap.remove(controller.getType());

        autoResize(EfsViewTabEvent.REMOVE_EFS_VIEW_TAB, getTabCount());

        autoResize(EfsViewTabEvent.RESIZE_EFS_VIEW_TAB, getTabCount());
    }

    private int getTabCount() {
        return tabPaneEfsView.getTabs().size();
    }

    private ObjectProperty<EventHandler<EfsViewTabEvent>> efsViewTabActionProperty() {
        return propertyEfsViewTabAction;
    }

    public void setViewTabAction(EventHandler<EfsViewTabEvent> handler) {
        efsViewTabActionProperty().set(handler);
    }

    public ObjectProperty<EventHandler<EfsElementSelectionEvent>> efsSelectionProperty() {
        return efsSelectionProperty;
    }

    public void setEfsSelectionAction(EventHandler<EfsElementSelectionEvent> handler) {
        efsSelectionProperty().set(handler);
    }

    public ObjectProperty<EventHandler<EfsElementSearchEvent>> efsSearchProperty() { // NO_UCD (use private)
        return efsSearchProperty;
    }

    public void setEfsSearchAction(EventHandler<EfsElementSearchEvent> handler) { // NO_UCD (use private)
        efsSearchProperty().set(handler);
    }

    public void setSearchEfsElements(SearchTabController searchTabController, List<EfsElementDTO> searchEfsElements) {
        searchTabController.setSearchEfsElements(searchEfsElements);
    }

    public void setInspectorJumpToHandler(EventHandler<InspectorJumpToElementEvent> handler) {
        inspectorJumpToHandler = handler;
    }
}
