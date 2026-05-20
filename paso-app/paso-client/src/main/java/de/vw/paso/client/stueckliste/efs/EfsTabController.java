package de.vw.paso.client.stueckliste.efs;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.control.tablebase.TableColumnHeaderChangeListener;
import de.vw.paso.client.main.ribbonmenu.efs.DisplayMode;
import de.vw.paso.client.main.ribbonmenu.efs.RibbonMenuEfsEvent;
import de.vw.paso.client.main.ribbonmenu.efs.RibbonMenuEfsListener;
import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.client.stueckliste.efs.event.FzgStuecklisteGewichtEvent;
import de.vw.paso.client.stueckliste.efs.tree.InspectorItemCounter;
import de.vw.paso.client.stueckliste.efs.tree.SingleVehiclePartListController;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabEvent;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabPaneController;
import de.vw.paso.client.stueckliste.efs.views.PartPropertiesViewTabPaneController;
import de.vw.paso.client.stueckliste.efs.views.aggregate.AggregateTabController;
import de.vw.paso.client.stueckliste.efs.views.aggregate.RowAction;
import de.vw.paso.client.stueckliste.efs.views.historie.event.EfsElementSearchEvent;
import de.vw.paso.client.stueckliste.efs.views.historie.event.EfsElementSelectionEvent;
import de.vw.paso.client.stueckliste.efs.views.historie.event.HistorieUpdateEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.event.InspectorEditOfEfsElementSolutionEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.event.ShowElementInInspectorEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.aggregate.ShowAggregateEvent;
import de.vw.paso.client.stueckliste.efs.views.properties.EfsPropertiesTabEvent;
import de.vw.paso.client.stueckliste.event.RevertRevisionEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.delegate.pls.PlsRestClientHolder;
import de.vw.paso.partlist.domain.PartListViewMode;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementDTO;
import de.vw.paso.service.pls.CreateSubPartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.Pair;

@FXController(name = "efs-tab")
public class EfsTabController extends BasePartlistTabController implements RibbonMenuEfsListener {

    private final ObjectProperty<VehicleConfigDTO> vehicleConfig;
    private final ObjectProperty<EventHandler<Event>> selectTabEventHandler;

    @FXML
    private Tab tabEfs;
    @FXML
    private SplitPane splitPaneEfsProperties;
    @FXML
    private SplitPane splitPaneEfsView;
    @FXML
    private BorderPane borderPaneTab;
    @FXML
    private SingleVehiclePartListController efsTreeController;

    private EfsViewTabPaneController efsViewTabPaneController;

    private PartPropertiesViewTabPaneController partPropertiesViewTabPaneController;

    private BooleanProperty disablePropertyShowSearch;
    private BooleanProperty disablePropertyShowInspector;

    private ObjectProperty<PartListViewMode> viewModeEfsProperty;

    private EventHandler<EfsElementSelectionEvent> selectEfsElementEventHandler;
    private EventHandler<EfsElementSearchEvent> efsElementSearchEvent;

    public EfsTabController() {
        vehicleConfig = new SimpleObjectProperty<>();
        selectTabEventHandler = new SimpleObjectProperty<>(this, "tabSelectionEventHandler");
    }

    @Override
    public void initialize(URL location, java.util.ResourceBundle resources) {
        super.initialize(location, resources);

        registerSubController(efsTreeController);

        efsTreeController.setParentController(this);
    }

    @Override
    public void start() {
        loadEfsViewTabPane();
        loadPartPropertiesViewTabPane();

        registerEventHandlers();

        createScrollToFirstColumnListener();
        createScrollToLastColumnListener();
        createExpandTreeItemListener();
        createCollapseTreeItemListener();

        efsTreeController.vehicleConfigProperty().bind(vehicleConfig);
        efsTreeController.initializeEfsHeader(viewModeEfsProperty);
        efsTreeController.setDefaultVisibleColumns();
        efsTreeController.bindTableColumnHeader();
    }

    @Override
    public void stop() {
        super.stop();
        EfsElementResolver.cleanPartList(getVehicleConfig().getVehiclePartList());
    }

    public void handleActionReloadHistory(HistorieUpdateEvent<AbstractTreeItem<AbstractEfsElementDTO>> item) {
        if (item.getEventType() == HistorieUpdateEvent.EFS_ELEMENT_SELECTED) {
            efsViewTabPaneController.handleActionReloadHistory((EfsElementDTO) item.getEfsElement());
        }

        if (item.getEventType() == HistorieUpdateEvent.EFS_ELEMENTS_CHANGED) {
            efsViewTabPaneController.handleActionReloadRevisions(getVehicleConfig());
        }
    }

    public void handleActionSelectEfsElement(EfsElementSelectionEvent elementSelectionEvent) {
        efsTreeController.selectElementById(elementSelectionEvent.getEfsElementId());
    }

    public void fillGui() {
        efsTreeController.populatePartListTable();
    }

    public void setSetKeys(Collection<String> setKeys) {
        efsTreeController.setSetKeys(setKeys);

        efsViewTabPaneController.reloadInspector(getVehicleConfig());
    }

    public void setCostGroups(Collection<String> costGroups) {
        efsTreeController.setCostGroups(costGroups);
    }

    public void setEfsHeader(GridPane paneEfsHeader) {
        efsTreeController.setPaneEfsHeader(paneEfsHeader);
    }

    @Override
    public Tab getControl() {
        return tabEfs;
    }

    @Override
    public Parent getStyleableParent() {
        return borderPaneTab;
    }

    public void setPlaceholderText(boolean isLoading) {
        efsTreeController.setTablePlaceholderPropertyText(isLoading);
    }

    @Override
    public RibbonMenuEfsEvent getRibbonMenuEvent() {
        return new RibbonMenuEfsEvent(this, getControl().getText());
    }

    @Override
    public void handleActionNewEfsElement() {
        efsTreeController.handleActionNewEfsElement();
    }

    @Override
    public void handleActionDeleteEfsElemente() {
        efsTreeController.handleActionDeleteEfsElemente();
    }

    @Override
    public void handleActionCopyEfsElemente() {
        efsTreeController.handleActionCopyEfsElemente();
    }

    @Override
    public void handleActionCutEfsElemente() {
        efsTreeController.handleActionCutEfsElemente();
    }

    @Override
    public void handleActionPasteEfsElemente() {
        efsTreeController.handleActionPasteEfsElemente();
    }

    @Override
    public void handleActionShowHistorie() {
        efsViewTabPaneController.handleActionShowHistory(efsTreeController.getSelectedEfsElement());
    }

    @Override
    public void handleActionShowRevisionen() {
        efsViewTabPaneController.handleActionShowRevisions(getVehicleConfig(), true);
    }

    @Override
    public BooleanProperty toggleAenderungsansichtProperty() {
        return efsTreeController.toggleChangeViewProperty();
    }

    @Override
    public ObjectProperty<DisplayMode> selectedDisplayModeproperty() {
        return efsTreeController.selectedDisplayModeProperty();
    }

    @Override
    public ObjectProperty<List<DisplayMode>> availableDisplayModesProperty() {
        return efsTreeController.availableDisplayModesProperty();
    }

    @Override
    public ObjectProperty<PartListViewMode> viewModeEfsProperty() {
        return viewModeEfsProperty;
    }

    @Override
    public void handleActionShowSuche() {
        efsViewTabPaneController.handleActionShowSearch(efsTreeController);
    }

    @Override
    public void handleActionShowInspector() {
        handleActionShowInspector(null);
    }

    @Override
    public void handlerActionShowReplaceAggregat() {
        List<EfsElementDTO> aggregateElements = efsTreeController.getEfsElementTreeModel().getAggregateElements();

        AggregateTabController replaceController = new AggregateTabController(aggregateElements);
        Optional<List<Pair<RowAction, EfsElementDTO>>> data = replaceController.showAndWait();
        data.ifPresent(elementsToReplace -> {
            ServiceController<ListMultimap<EfsElementDTO, EfsElementDTO>> service = new ServiceController<>();
            service.setExecutionTime(20000);
            service.setOnFailed(e -> handleException(service.getException()));
            service.setOnSucceeded(e -> registerNewElements(service.getValue()));
            service.start(() -> createSubPartLists(elementsToReplace));
        });
    }

    public void handleActionShowInspector(EfsElementDTO root) {
        efsViewTabPaneController.handleActionShowInspector(getVehicleConfig(), root);
    }

    public final ObjectProperty<VehicleConfigDTO> vehicleConfigProperty() {
        return vehicleConfig;
    }

    @Override
    public void handleActionShowPartPropertiesView() {
        partPropertiesViewTabPaneController.handleActionShowPartPropertiesView(
                efsTreeController.getSelectedEfsElement());
    }

    public void handleActionShowInInspector(EfsElementDTO root) {
        Long selectedEfsElementId = efsTreeController.getSelectedEfsElement().getId();
        boolean isSelectedInInspector = inspectorItemCountProperty().get().getElementIdsInInspector()
                .contains(selectedEfsElementId);

        if (isSelectedInInspector) {
            handleActionShowInspector(root);
            EventBus.getInstance().post(new ShowElementInInspectorEvent(efsTreeController.getSelectedEfsElement()));
        }
    }

    public void handleActionReloadPartPropertiesView(EfsElementDTO efsElement) {
        partPropertiesViewTabPaneController.handleActionReloadPartPropertiesView(efsElement);
    }

    @Override
    public void handleActionNavigateBack() {
        this.efsTreeController.handleActionNavigateBack();
    }

    @Override
    public void handleActionNavigateForward() {
        this.efsTreeController.handleActionNavigateForward();
    }

    @Override
    public void handleActionCollapseTree() {
        this.efsTreeController.handleActionCollapseTree();
    }

    @Override
    public void handleActionCollapseAllTree() {
        this.efsTreeController.handleActionCollapseAllTree();
    }

    @Override
    public void handleActionExpandTree() {
        this.efsTreeController.handleActionExpandTree();
    }

    @Override
    public void handleActionExpandAllTree() {
        this.efsTreeController.handleActionExpandAllTree();
    }

    @Override
    public void handleActionClearFilters() {
        this.efsTreeController.handleActionClearFilters();
        this.efsTreeController.reloadEfsHeaderFilteredContent(0, 0);
    }

    @Override
    public void handleActionResetSorting() {
        this.efsTreeController.handleActionResetSorting();
    }

    @Override
    public void handleActionExcelExport() {
        this.efsTreeController.handleActionExcelExport();
    }

    @Override
    public void handleActionShowCompareDialog() {
        this.efsTreeController.handleActionShowCompareDialog();
    }

    @Override
    public BooleanProperty disablePropertyCompare() {
        return this.efsTreeController.disablePropertyCompare();
    }

    @Override
    public BooleanProperty disablePropertyNewEfsElement() {
        return efsTreeController.disablePropertyNewEfsElement();
    }

    @Override
    public BooleanProperty disablePropertyDeleteEfsElemente() {
        return efsTreeController.disablePropertyDeleteEfsElemente();
    }

    @Override
    public BooleanProperty disablePropertyCopyEfsElemente() {
        return efsTreeController.disablePropertyCopyEfsElemente();
    }

    @Override
    public BooleanProperty disablePropertyCutEfsElemente() {
        return efsTreeController.disablePropertyCutEfsElemente();
    }

    @Override
    public BooleanProperty disablePropertyPasteEfsElemente() {
        return efsTreeController.disablePropertyPasteEfsElemente();
    }

    @Override
    public BooleanProperty disablePropertyShowHistory() {
        return efsTreeController.disablePropertyShowHistory();
    }

    @Override
    public BooleanProperty disablePropertyShowRevision() {
        return efsTreeController.disablePropertyShowRevision();
    }

    @Override
    public BooleanProperty disablePropertyShowChanges() {
        return efsTreeController.disablePropertyShowChanges();
    }

    @Override
    public BooleanProperty disablePropertyShowSuche() {
        if (disablePropertyShowSearch == null) {
            disablePropertyShowSearch = new SimpleBooleanProperty(false);
        }

        return disablePropertyShowSearch;
    }

    @Override
    public BooleanProperty disablePropertyShowInspector() {
        if (disablePropertyShowInspector == null) {
            disablePropertyShowInspector = new SimpleBooleanProperty(false);
        }

        return disablePropertyShowInspector;
    }

    @Override
    public BooleanProperty disablePropertyShowPartProperties() {
        return efsTreeController.disablePropertyShowPartProperties();
    }

    @Override
    public BooleanProperty disablePropertyExcelExport() {
        return efsTreeController.disablePropertyExcelExport();
    }

    @Override
    public BooleanProperty disablePropertyNavigateBack() {
        return efsTreeController.disablePropertyNavigateBack();
    }

    @Override
    public BooleanProperty disablePropertyNavigateForward() {
        return efsTreeController.disablePropertyNavigateForward();
    }

    @Override
    public BooleanProperty disablePropertyClearFilters() {
        return efsTreeController.disablePropertyClearFilters();
    }

    @Override
    public BooleanProperty disablePropertyResetSorting() {
        return efsTreeController.disablePropertyResetSorting();
    }

    @Override
    public BooleanProperty disablePropertyCollapseTree() {
        return efsTreeController.disablePropertyCollapseTree();
    }

    @Override
    public BooleanProperty disablePropertyCollapseAllTree() {
        return efsTreeController.disablePropertyCollapseAllTree();
    }

    @Override
    public BooleanProperty disablePropertyExpandTree() {
        return efsTreeController.disablePropertyExpandTree();
    }

    @Override
    public BooleanProperty disablePropertyExpandAllTree() {
        return efsTreeController.disablePropertyExpandAllTree();
    }

    public final void setSelectTabAction(EventHandler<Event> handler, Runnable tabSelectedAction) {
        selectTabEventHandler().set(handler);

        efsTreeController.setActionSelectTab(tabSelectedAction);
    }

    public void setBindedTableList(Map<Class<?>, TableColumnHeaderChangeListener> tables) {
        this.efsTreeController.setBindedColumnHeaderListeners(tables);
    }

    public ObjectProperty<InspectorItemCounter> inspectorItemCountProperty() {
        return efsTreeController.getInspectorItemCountProperty();
    }

    public void saveSingleVehiclePartListControllerCurrentState() {
        efsTreeController.saveConfigOnClose();
    }

    @Subscribe
    private void handleInspectorEfsEdit(InspectorEditOfEfsElementSolutionEvent event) {
        if (!getVehicleConfig().getId().equals(event.getVehicleConfigId())) {
            return;
        }

        Collection<EfsElementDTO> changes = event.getChangedElements();
        for (EfsElementDTO efsElement : changes) {
            efsTreeController.getEfsElementTreeModel().updateNode(efsElement, true, true);
        }

        efsTreeController.getEfsTreeTableView().getSelectionModel().clearSelection();
    }

    @Subscribe
    private void handleRevertRevisionEvent(RevertRevisionEvent event) {
        for (EfsElementDTO efsElement : event.getResult()) {
            efsTreeController.getEfsElementTreeModel().updateNode(efsElement, true, true);
        }
    }

    @Subscribe
    private void onShowAggregateEvent(ShowAggregateEvent event) {
        Long vehiclePartListId = getVehicleConfig().getVehiclePartList().getId();
        if (vehiclePartListId.equals(event.getVehiclePartListId())) {
            handlerActionShowReplaceAggregat();
        }
    }

    private VehicleConfigDTO getVehicleConfig() {
        return vehicleConfig.get();
    }

    private void registerEventHandlers() {
        efsTreeController.setEfsSelectionAction(this::handleActionReloadHistory);
        efsViewTabPaneController.setEfsSelectionAction(getSelectEfsElementEventHandler());
        efsViewTabPaneController.setEfsSearchAction(getEfsSearchEventHandler());
        efsViewTabPaneController.setInspectorJumpToHandler(
                event -> efsTreeController.selectElementById(event.getElement().getId()));
    }

    private EventHandler<EfsElementSelectionEvent> getSelectEfsElementEventHandler() {
        if (selectEfsElementEventHandler == null) {
            selectEfsElementEventHandler = this::handleActionSelectEfsElement;
        }

        return selectEfsElementEventHandler;
    }

    private EventHandler<EfsElementSearchEvent> getEfsSearchEventHandler() {
        if (efsElementSearchEvent == null) {
            efsElementSearchEvent = this::handleActionSearchEfsElement;
        }

        return efsElementSearchEvent;
    }

    private void createScrollToFirstColumnListener() {
        KeyCombination keyCombo = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);
        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                efsTreeController.scrollToFirstColumn();
            }
        });
    }

    private void createScrollToLastColumnListener() {
        KeyCombination keyCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);
        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                efsTreeController.scrollToLastColumn();
            }
        });
    }

    private void createExpandTreeItemListener() {
        KeyCombination keyCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);
        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                efsTreeController.setSelectedItemExpanded();
            }
        });
    }

    private void createCollapseTreeItemListener() {
        KeyCombination keyCombo = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);
        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                efsTreeController.setSelectedItemCollapsed();
            }
        });
    }

    private void handleActionSearchEfsElement(EfsElementSearchEvent elementSearchEvent) {
        List<EfsElementDTO> searchEfsElements = efsTreeController.searchEfsElements(elementSearchEvent.getSearchTerm());
        efsViewTabPaneController.setSearchEfsElements(elementSearchEvent.getSearchTabController(), searchEfsElements);
    }

    private void loadEfsViewTabPane() {
        efsViewTabPaneController = load(EfsViewTabPaneController.class);
        efsViewTabPaneController.setViewTabAction(this::handleEfsViewTabChange);
        registerSubController(efsViewTabPaneController);
    }

    private void loadPartPropertiesViewTabPane() {
        partPropertiesViewTabPaneController = load(PartPropertiesViewTabPaneController.class);
        partPropertiesViewTabPaneController.setEfsPropertiesTabAction(this::handleEfsPropertiesTabChange);
        registerSubController(partPropertiesViewTabPaneController);
    }

    private void handleEfsViewTabChange(EfsViewTabEvent event) {
        if (event.getEventType().equals(EfsViewTabEvent.RESIZE_EFS_VIEW_TAB)) {
            splitPaneEfsView.resize(0, 0);
            return;
        }

        if (event.getTabCount() <= 0) {
            splitPaneEfsView.getItems().remove(efsViewTabPaneController.getControl());
        } else if (!splitPaneEfsView.getItems().contains(efsViewTabPaneController.getControl())) {
            splitPaneEfsView.getItems().add(efsViewTabPaneController.getControl());
            splitPaneEfsView.setDividerPositions(0.55);
        } else if (splitPaneEfsView.getDividerPositions()[0] > 0.55) {
            splitPaneEfsView.setDividerPositions(0.55);
        }
    }

    private void handleEfsPropertiesTabChange(EfsPropertiesTabEvent event) {
        if (event.getEventType().equals(EfsPropertiesTabEvent.RESIZE_PROPERTIES_TAB)) {
            splitPaneEfsProperties.resize(0, 0);
            return;
        }

        if (event.getTabCount() <= 0) {
            splitPaneEfsProperties.getItems().remove(partPropertiesViewTabPaneController.getControl());
        } else if (!splitPaneEfsProperties.getItems().contains(partPropertiesViewTabPaneController.getControl())) {
            splitPaneEfsProperties.getItems().add(partPropertiesViewTabPaneController.getControl());
            splitPaneEfsProperties.setDividerPositions(0.77);
        } else if (splitPaneEfsProperties.getDividerPositions()[0] > 0.77) {
            splitPaneEfsProperties.setDividerPositions(0.77);
        }
    }

    private ListMultimap<EfsElementDTO, EfsElementDTO> createSubPartLists(
            Collection<Pair<RowAction, EfsElementDTO>> elementsToReplace) {
        ListMultimap<EfsElementDTO, EfsElementDTO> topLevelElements = MultimapBuilder.hashKeys().arrayListValues()
                .build();
        for (Pair<RowAction, EfsElementDTO> pair : elementsToReplace) {
            RowAction action = pair.first();
            if (action.isRequestNew()) {
                continue;
            }

            EfsElementDTO element = pair.second();
            VehicleConfigDTO config = getVehicleConfig();

            Collection<EfsElementDTO> subPartList = PlsRestClientHolder.getInstance().createSubPartList(
                    new CreateSubPartListDTO(element.getId(), action.getProductData().getId(),
                            config.getPrNumberString(), config.getValidDate())).efsElementDTOS();

            element.setWeightControlFlag(null);
            topLevelElements.putAll(element, subPartList);
        }

        return topLevelElements;
    }

    private void registerNewElements(ListMultimap<EfsElementDTO, EfsElementDTO> elementsByParent) {
        for (EfsElementDTO key : elementsByParent.keySet()) {
            Collection<EfsElementDTO> efsElements = elementsByParent.get(key);
            Collection<EfsElementDTO> elementsToRegister = new ArrayList<>(efsElements.size() + 1);
            elementsToRegister.add(key);

            Collection<EfsElementDTO> efsElementDTOS = setVehicleConfigAndReturn(key, efsElements);
            elementsToRegister.addAll(efsElementDTOS);

            Map<Long, Double> newWeights = EfsElementResolver.registerElements(elementsToRegister);
            for (Map.Entry<Long, Double> entry : newWeights.entrySet()) {
                Long partListId = entry.getKey();
                Double weight = entry.getValue();
                EventBus.getInstance().post(new FzgStuecklisteGewichtEvent(partListId, weight));
            }
        }
    }

    private Collection<EfsElementDTO> setVehicleConfigAndReturn(EfsElementDTO parent,
            Collection<EfsElementDTO> children) {
        if (children == null) {
            return List.of();
        }

        Collection<EfsElementDTO> result = new ArrayList<>(children.size());
        for (EfsElementDTO element : children) {
            element.setParent(parent);
            element.setParentId(parent.getId());
            element.setVehiclePartListId(parent.getVehiclePartListId());
            result.add(element);

            Collection<EfsElementDTO> elements = setVehicleConfigAndReturn(element, element.getChildren());
            result.addAll(elements);
        }

        return result;
    }

    private ObjectProperty<EventHandler<Event>> selectTabEventHandler() { // NO_UCD (use private)
        return selectTabEventHandler;
    }

    public void setViewModeEfsProperty(ObjectProperty<PartListViewMode> viewModeEfsProperty) {
        this.viewModeEfsProperty = viewModeEfsProperty;
    }
}
