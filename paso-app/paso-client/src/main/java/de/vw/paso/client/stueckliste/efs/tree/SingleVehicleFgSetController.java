package de.vw.paso.client.stueckliste.efs.tree;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;

import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.RemoveSummaryHighlightStylingEvent;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.exception.ControllerException;
import de.vw.paso.client.explorer.vehicleconfig.event.ShowCompareTabEvent;
import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.efs.FgSetTabController;
import de.vw.paso.client.stueckliste.efs.display.strategy.AbstractDisplayStrategyForTrees;
import de.vw.paso.client.stueckliste.efs.display.strategy.FgSetDisplayStrategy;
import de.vw.paso.client.stueckliste.efs.export.fgset.FgSetExcelExporter;
import de.vw.paso.client.stueckliste.efs.tree.cell.AggregatedEfsCellFactory;
import de.vw.paso.client.stueckliste.efs.tree.model.FgSetTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.FgSetTreeItemPropertyNames;
import de.vw.paso.client.stueckliste.efs.tree.model.FgSetTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.FgSetTreeObject;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabPaneController;
import de.vw.paso.client.stueckliste.efs.views.compare.ComparePartListSelectionDialog;
import de.vw.paso.client.stueckliste.efs.views.compare.ComparePartListSelectionDialogResult;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.FgSetSummarisedTabController;
import de.vw.paso.client.stueckliste.event.FgSetTreeRefreshEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.TreeItemUtil;
import de.vw.paso.client.util.highlight.SelectionHighlightManager;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

@FXController(name = "fg-set-tree")
public class SingleVehicleFgSetController extends SingleVehicleBaseController<FgSetTreeObject> {

    private static final String FG_SET_ROW_SELECTION = "fg-set-highlight-row-selection";
    private static final String FG_SET_COLUMN_SELECTION = "fg-set-highlight-col-selection";

    private final AbstractDisplayStrategyForTrees<FgSetTreeObject> displayStrategy = FgSetDisplayStrategy.getStrategyWithoutDeletion();

    @FXML
    private CustomTreeTableView<FgSetTreeObject> fgSetTreeTableView;
    @FXML
    private TreeTableColumn<FgSetTreeObject, String> colSetKey;
    @FXML
    private TreeTableColumn<FgSetTreeObject, String> colDescription;
    @FXML
    private TreeTableColumn<FgSetTreeObject, Double> colPlatform;
    @FXML
    private TreeTableColumn<FgSetTreeObject, Double> colSystem;
    @FXML
    private TreeTableColumn<FgSetTreeObject, Double> colHut;
    @FXML
    private TreeTableColumn<FgSetTreeObject, Double> colWeightAll;

    private final TreeTableColumn<FgSetTreeObject, Double> colPlatWeight = new TreeTableColumn<>();
    private final TreeTableColumn<FgSetTreeObject, Integer> colPlatNum = new TreeTableColumn<>();
    private final TreeTableColumn<FgSetTreeObject, Double> colSystemWeight = new TreeTableColumn<>();
    private final TreeTableColumn<FgSetTreeObject, Integer> colSystemNum = new TreeTableColumn<>();
    private final TreeTableColumn<FgSetTreeObject, Double> colHutWeight = new TreeTableColumn<>();
    private final TreeTableColumn<FgSetTreeObject, Integer> colHutNum = new TreeTableColumn<>();
    private final TreeTableColumn<FgSetTreeObject, Double> colAllWeight = new TreeTableColumn<>();
    private final TreeTableColumn<FgSetTreeObject, Integer> colAllNum = new TreeTableColumn<>();

    private final SelectionHighlightManager<FgSetTreeObject> highlightManager = new SelectionHighlightManager<>();
    private final ObservableList<SetKeyDTO> setKeys = FXCollections.observableArrayList();

    private FgSetTreeModel fgSetTreeModel;
    private FgSetTabController parentController;

    private String lastSelectedColumnProperty;

    private SplitPane splitPaneEfsView;

    private BooleanProperty disablePropertyCloseSummary;
    private BooleanProperty disablePropertyCompare;
    private BooleanProperty disablePropertyExcelExport;
    private BooleanProperty toggleDisplayNumberOfPartsProperty;
    private VehicleConfigDTO vehicleConfig;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        highlightManager.initTable(fgSetTreeTableView, FG_SET_ROW_SELECTION, FG_SET_COLUMN_SELECTION);

        initSubColumns();

        fgSetTreeTableView.getSortOrder()
                .addListener((ListChangeListener<TreeTableColumn<FgSetTreeObject, ?>>) change -> {
                    if (!isResettingSort) {
                        disablePropertyResetSorting().set(false);
                    }
                });

        for (TreeTableColumn<FgSetTreeObject, ?> column : fgSetTreeTableView.getColumns()) {
            column.sortTypeProperty().addListener((observableValue, sortType, t1) -> {
                if (!isResettingSort) {
                    disablePropertyResetSorting().set(false);
                }
            });
        }
    }

    @Override
    protected CustomTreeTableView<FgSetTreeObject> getTreeTableView() {
        return fgSetTreeTableView;
    }

    @Override
    protected AbstractTreeModel<FgSetTreeItem, FgSetTreeObject> getTreeModel() {
        return fgSetTreeModel;
    }

    @Override
    protected void stop() {
        super.stop();

        getFgSetTreeModel().removeAllElements();

        EfsElementResolver.removeListener(this);
        highlightManager.removeFromTable();
    }

    @Override
    public void initTreeTable() {
        fgSetTreeTableView.showRootProperty().set(false);
        fgSetTreeTableView.setRoot(getFgSetTreeModel().getRoot());
        fgSetTreeTableView.setEditable(false);
        fgSetTreeTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fgSetTreeTableView.getSelectionModel().setCellSelectionEnabled(true);
        fgSetTreeTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleEfsSelected(newValue));
        fgSetTreeTableView.makeHeaderWrappable();
        fgSetTreeTableView.makeFilterable();
        fgSetTreeTableView.setHeaderHeight(48);
        fgSetTreeTableView.getSortOrder().addListener(
                (ListChangeListener<? super TreeTableColumn<FgSetTreeObject, ?>>) change -> disablePropertyResetSorting().set(
                        false));

        addDisabledEditKeys();
    }

    @Override
    public void initTreeTableColumns() {
        initColumn(colSetKey, setKeyCellValueFactory(),
                AggregatedEfsCellFactory.forReadOnlyStringColumn(FgSetTreeItemPropertyNames.SET_KEY),
                e -> e.getAggregationObject().getSetKeyName(), FgSetTreeItemPropertyNames.SET_KEY);
        initColumn(colDescription, cellData -> ((FgSetTreeItem) cellData.getValue()).propertyDescription(),
                AggregatedEfsCellFactory.forReadOnlyStringColumn(FgSetTreeItemPropertyNames.DESCRIPTION),
                e -> e.getAggregationObject().getDescription(), FgSetTreeItemPropertyNames.DESCRIPTION);
        initColumn(colPlatform, cellData -> ((FgSetTreeItem) cellData.getValue()).propertyWeightPlatform(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(FgSetTreeItemPropertyNames.PLATFORM,
                        this::handleClickEvent), FgSetTreeObject::getPlatform, FgSetTreeItemPropertyNames.PLATFORM);
        initColumn(colSystem, cellData -> ((FgSetTreeItem) cellData.getValue()).propertyWeightSystem(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(FgSetTreeItemPropertyNames.SYSTEM,
                        this::handleClickEvent), FgSetTreeObject::getSystem, FgSetTreeItemPropertyNames.SYSTEM);
        initColumn(colHut, cellData -> ((FgSetTreeItem) cellData.getValue()).propertyWeightHut(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(FgSetTreeItemPropertyNames.HUT,
                        this::handleClickEvent), FgSetTreeObject::getHut, FgSetTreeItemPropertyNames.HUT);
        initColumn(colWeightAll, cellData -> ((FgSetTreeItem) cellData.getValue()).propertyWeightAll(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(FgSetTreeItemPropertyNames.WEIGHT_ALL,
                        this::handleClickEvent), FgSetTreeObject::getWeightAll, FgSetTreeItemPropertyNames.WEIGHT_ALL);
    }

    private <TO, T> Callback<CellDataFeatures<TO, T>, ObservableValue<T>> setKeyCellValueFactory() {
        return cellData -> {
            FgSetTreeItem fgSetTreeItem = (FgSetTreeItem) cellData.getValue();
            if (fgSetTreeItem.propertySummaryRow().get()) {
                return (ObservableValue<T>) new SimpleStringProperty(StringConstant.EMPTY);
            }

            return (ObservableValue<T>) fgSetTreeItem.propertySetKey();
        };
    }

    private void initSubColumns() {
        String textNumber = I18N.getString("treetablecolumn.num");
        String textWeight = I18N.getString("treetablecolumn.weight");

        colPlatWeight.setText(textWeight);
        initColumn(colPlatWeight, cellData -> ((FgSetTreeItem) cellData.getValue()).propertyWeightPlatform(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(FgSetTreeItemPropertyNames.PLATFORM,
                        this::handleClickEvent), FgSetTreeObject::getPlatform, FgSetTreeItemPropertyNames.PLATFORM);

        colPlatNum.setText(textNumber);
        colPlatNum.setCellValueFactory(cellData -> ((FgSetTreeItem) cellData.getValue()).propertyNumPlatform());
        colPlatNum.setCellFactory(AggregatedEfsCellFactory.forReadOnlyIntegerColumn(FgSetTreeItemPropertyNames.PLATFORM,
                this::handleClickEvent));

        colSystemWeight.setText(textWeight);
        initColumn(colSystemWeight, cellData -> ((FgSetTreeItem) cellData.getValue()).propertyWeightSystem(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(FgSetTreeItemPropertyNames.SYSTEM,
                        this::handleClickEvent), FgSetTreeObject::getSystem, FgSetTreeItemPropertyNames.SYSTEM);

        colSystemNum.setText(textNumber);
        colSystemNum.setCellValueFactory(cellData -> ((FgSetTreeItem) cellData.getValue()).propertyNumSystem());
        colSystemNum.setCellFactory(AggregatedEfsCellFactory.forReadOnlyIntegerColumn(FgSetTreeItemPropertyNames.SYSTEM,
                this::handleClickEvent));

        colHutWeight.setText(textWeight);
        initColumn(colHutWeight, cellData -> ((FgSetTreeItem) cellData.getValue()).propertyWeightHut(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(FgSetTreeItemPropertyNames.HUT,
                        this::handleClickEvent), FgSetTreeObject::getHut, FgSetTreeItemPropertyNames.HUT);

        colHutNum.setText(textNumber);
        colHutNum.setCellValueFactory(cellData -> ((FgSetTreeItem) cellData.getValue()).propertyNumHut());
        colHutNum.setCellFactory(AggregatedEfsCellFactory.forReadOnlyIntegerColumn(FgSetTreeItemPropertyNames.HUT,
                this::handleClickEvent));

        colAllWeight.setText(textWeight);
        initColumn(colAllWeight, cellData -> ((FgSetTreeItem) cellData.getValue()).propertyWeightAll(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(FgSetTreeItemPropertyNames.WEIGHT_ALL,
                        this::handleClickEvent), FgSetTreeObject::getWeightAll, FgSetTreeItemPropertyNames.WEIGHT_ALL);

        colAllNum.setText(textNumber);
        colAllNum.setCellValueFactory(cellData -> ((FgSetTreeItem) cellData.getValue()).propertyNumAll());
        colAllNum.setCellFactory(
                AggregatedEfsCellFactory.forReadOnlyIntegerColumn(FgSetTreeItemPropertyNames.WEIGHT_ALL,
                        this::handleClickEvent));
    }

    public void setParentController(FgSetTabController parentController) {
        this.parentController = parentController;
    }

    public void setEfsElemente(VehicleConfigDTO vehicleConfigDTO, Collection<EfsElementDTO> efsElements) {
        this.vehicleConfig = vehicleConfigDTO;

        Collection<FgSetTreeObject> fgSetTreeObjects = new ArrayList<>();
        Map<String, List<EfsElementDTO>> efsElementCollector = new HashMap<>();
        List<EfsElementDTO> efsElementsWithEmptySetKey = new ArrayList<>();
        String emptySetKeyName = I18N.getString("setkey.empty.name");

        for (EfsElementDTO efsElement : efsElements) {
            String setKey = efsElement.getSetKey();

            if (StringUtils.isEmpty(setKey)) {
                efsElementsWithEmptySetKey.add(efsElement);
                continue;
            }

            if (SetKeyDTO.NOT_RELEVANT_SET_KEY.equals(setKey)) {
                continue;
            }

            if (!efsElementCollector.containsKey(setKey)) {
                efsElementCollector.put(setKey, new ArrayList<>());
            }

            efsElementCollector.get(setKey).add(efsElement);
        }

        for (SetKeyDTO setKey : setKeys) {
            if (efsElementCollector.containsKey(setKey.getSetKeyName())) {
                fgSetTreeObjects.add(new FgSetTreeObject(setKey, efsElementCollector.remove(setKey.getSetKeyName())));
                continue;
            }

            fgSetTreeObjects.add(new FgSetTreeObject(setKey));
        }

        for (Map.Entry<String, List<EfsElementDTO>> entry : efsElementCollector.entrySet()) {
            String key = entry.getKey();
            SetKeyDTO setKeyDTO = new SetKeyDTO(key, null, null, SetKeyDTO.UNKNOWN_SET_KEY_VERSION);

            List<EfsElementDTO> value = entry.getValue();
            fgSetTreeObjects.add(new FgSetTreeObject(setKeyDTO, value));
        }

        SetKeyDTO setKeyForEmptyElements = new SetKeyDTO(emptySetKeyName, null, null, SetKeyDTO.EMPTY_SET_KEY_VERSION);
        fgSetTreeObjects.add(new FgSetTreeObject(setKeyForEmptyElements, efsElementsWithEmptySetKey));

        fgSetTreeModel = (FgSetTreeModel) displayStrategy.createDisplayModel(fgSetTreeObjects);

        fgSetTreeTableView.setRoot(fgSetTreeModel.getRoot());
        fgSetTreeTableView.getRoot().setExpanded(true);

        handleActionResetSorting();
    }

    public void setSetKeys(List<SetKeyDTO> setKeysDTOs) {
        this.setKeys.setAll(setKeysDTOs);
    }

    private void refreshFgSet(EfsElementDTO efsElement) {
        if (getFgSetTreeModel().getTreeItems().size() > 1) {
            displayStrategy.updateNode(getFgSetTreeModel(), efsElement);
        }
    }

    public void scrollToFirstColumn() {
        fgSetTreeTableView.scrollToColumnIndex(0);
    }

    public void scrollToLastColumn() {
        int size = fgSetTreeTableView.getColumns().size();
        fgSetTreeTableView.scrollToColumnIndex(size - 1);
    }

    @Override
    public void selectElementById(Long itemId) {
        if (itemId != null) {
            selectFgSet(itemId);
        }
    }

    private void selectFgSet(Long fgSetId) {
        FgSetTreeItem treeItem = getFgSetTreeModel().getTreeItem(fgSetId);
        if (treeItem == null) {
            return;
        }

        TreeItem<FgSetTreeObject> parent = treeItem.getParent();
        while (parent != null) {
            parent.setExpanded(true);
            parent = parent.getParent();
        }

        fgSetTreeTableView.requestFocus();
        fgSetTreeTableView.getSelectionModel().clearSelection();
        for (TreeTableColumn<FgSetTreeObject, ?> column : fgSetTreeTableView.getColumns()) {
            fgSetTreeTableView.getSelectionModel().select(fgSetTreeTableView.getRow(treeItem), column);
        }

        fgSetTreeTableView.scrollToCenter(treeItem);
    }

    private FgSetTreeModel getFgSetTreeModel() {
        if (fgSetTreeModel == null) {
            fgSetTreeModel = new FgSetTreeModel();
        }

        return fgSetTreeModel;
    }

    @Override
    public void onEfsElementUpdate(Collection<EfsElementDTO> efsElements) {
        if (efsElements.isEmpty()) {
            return;
        }

        EfsElementDTO efsElement = efsElements.iterator().next();
        if (!parentController.getVehiclePartListId().equals(efsElement.getVehiclePartListId())) {
            return;
        }

        for (EfsElementDTO efsElementDTO : efsElements) {
            refreshFgSet(efsElementDTO);
        }

        fgSetTreeTableView.requestFocus();

        if (lastSelectedColumnProperty != null) {
            handleClickEvent(true, lastSelectedColumnProperty);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void clearOldFgSetNode(FgSetTreeRefreshEvent event) {
        if (event.isTreeRefresh() && event.getVehiclePartListId().equals(parentController.getVehiclePartListId())) {
            getFgSetTreeModel().updateNode(event.getEfsElement(), false, true);
        }

        if (lastSelectedColumnProperty != null) {
            handleClickEvent(true, lastSelectedColumnProperty);
        }
    }

    private void handleClickEvent(boolean isDoubleClick, String propertyName) {
        splitPaneEfsView = parentController.getSplitPaneEfsView();
        EfsViewTabPaneController efsViewTabPaneController = parentController.getEfsViewTabPaneController();

        if (!isDoubleClick) {
            return;
        }

        TreeItem<FgSetTreeObject> item = fgSetTreeTableView.getSelectionModel().getSelectedItem();

        lastSelectedColumnProperty = propertyName;

        List<EfsElementDTO> efsElementList = new ArrayList<>();
        String columnHeaderName = switch (propertyName) {
            case FgSetTreeItemPropertyNames.WEIGHT_ALL -> {
                efsElementList = TreeItemUtil.getAggregatedChildren(item);
                yield I18N.getString("treetablecolumn.weightall");
            }
            case FgSetTreeItemPropertyNames.HUT -> {
                efsElementList.addAll(TreeItemUtil.getAggregatedChildren(item).stream()
                        .filter(e -> ApCompareGroup.HUT.containsAp(e.getAp())).toList());
                yield I18N.getString("treetablecolumn.hut");
            }
            case FgSetTreeItemPropertyNames.SYSTEM -> {
                efsElementList.addAll(TreeItemUtil.getAggregatedChildren(item).stream()
                        .filter(e -> ApCompareGroup.SYSTEM.containsAp(e.getAp())).toList());
                yield I18N.getString("treetablecolumn.system");
            }
            default -> {
                efsElementList.addAll(TreeItemUtil.getAggregatedChildren(item).stream()
                        .filter(e -> ApCompareGroup.PLATFORM.containsAp(e.getAp())).toList());
                yield I18N.getString("treetablecolumn.platform");
            }
        };

        if (splitPaneEfsView.getItems().contains(efsViewTabPaneController.getControl())) {
            handleActionReloadSummarisedView(FgSetSummarisedTabController.class, efsElementList, columnHeaderName,
                    item.getValue().getId(), splitPaneEfsView);

            if (splitPaneEfsView.getDividerPositions()[0] > 0.65) {
                splitPaneEfsView.setDividerPositions(0.65);
            }

            return;
        }

        if (efsElementList.isEmpty()) {
            return;
        }

        splitPaneEfsView.setDividerPositions(0.65);

        AbstractSummarisedTabController controller = handleActionShowSummarisedView(FgSetSummarisedTabController.class,
                efsElementList, columnHeaderName, item.getValue().getId(), splitPaneEfsView);

        addBindedColumnHeaderListener(controller.getTableView());

        ListChangeListener<? super TableColumn<EfsElementDTO, ?>> listChangeListener = (ListChangeListener.Change<? extends TableColumn<EfsElementDTO, ?>> c) -> {
            if (c.next()) {
                notifyTableColumnChanged(
                        controller.getTableView().getVisibleLeafColumns().stream().map(TableColumnBase::getText)
                                .collect(Collectors.toList()));
            }
        };

        controller.initVisibleColumnListener(listChangeListener);
    }

    private AbstractSummarisedTabController handleActionShowSummarisedView(
            Class<? extends AbstractSummarisedTabController> controllerClass, List<EfsElementDTO> efsElements,
            String propertyName, String itemName, SplitPane splitPaneEfsView) {
        AbstractSummarisedTabController controller = BaseController.load(controllerClass);

        if (controller.getSelectedEfsElement() != null) {
            controller.initEfsElementList(efsElements, controller.getSelectedEfsElement());
        } else {
            controller.initEfsElementList(efsElements, propertyName, itemName,
                    parentController.getVehiclePartList().getVehicleConfig(), null, false);
        }

        if (splitPaneEfsView.getItems().size() > 1) {
            splitPaneEfsView.getItems().remove(1);
        }

        splitPaneEfsView.getItems().add(controller.getControl());

        disablePropertyCloseSummary.set(false);

        EventBus.getInstance()
                .post(new ColumnSequenceChangeEvent(parentController.getVehiclePartList().getVehicleConfig(),
                        getSVPLColumnOrder(), SingleVehicleFgSetController.class));

        return controller;
    }

    private void handleActionReloadSummarisedView(Class<? extends AbstractSummarisedTabController> controllerClass,
            List<EfsElementDTO> efsElements, String propertyName, String itemName, SplitPane splitPaneEfsView)
            throws ControllerException {
        if (controllerClass == null) {
            return;
        }

        handleActionShowSummarisedView(controllerClass, efsElements, propertyName, itemName, splitPaneEfsView);
    }

    private void handleDisplayNumberOfParts(Boolean newVal) {
        if (newVal) {
            addCountColumns();
        } else {
            removeCountColumns();
        }
    }

    private void addCountColumns() {
        fgSetTreeTableView.setHeaderHeight(24);

        colPlatform.getColumns().addAll(colPlatNum, colPlatWeight);
        colSystem.getColumns().addAll(colSystemNum, colSystemWeight);
        colHut.getColumns().addAll(colHutNum, colHutWeight);
        colWeightAll.getColumns().addAll(colAllNum, colAllWeight);

        setColumnHeaderBinding();
    }

    private void removeCountColumns() {
        colPlatform.getColumns().removeAll(colPlatNum, colPlatWeight);
        colSystem.getColumns().removeAll(colSystemNum, colSystemWeight);
        colHut.getColumns().removeAll(colHutNum, colHutWeight);
        colWeightAll.getColumns().removeAll(colAllNum, colAllWeight);

        setColumnHeaderBinding();
        fgSetTreeTableView.setHeaderHeight(48);
    }

    private void setColumnHeaderBinding() {
        fgSetTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colPlatform);
        fgSetTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colSystem);
        fgSetTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colHut);
        fgSetTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colWeightAll);
    }

    public void handleActionShowCompareDialog() {
        VehicleConfigDTO vehicleConfig = parentController.getVehiclePartList().getVehicleConfig();
        Collection<VehicleConfigDTO> vehicleConfigs = VehicleConfigRestClientHolder.getInstance()
                .loadNonDeletedVehicleConfigs().vehicleConfigDTOList();

        List<VehicleConfigDTO> finalVehicleConfigs = vehicleConfigs.stream().filter(e -> e.getVehiclePartList() != null)
                .collect(Collectors.toList());

        List<VehicleConfigDTO> selectedConfigsList = new ArrayList<>();
        selectedConfigsList.add(vehicleConfig);

        ComparePartListSelectionDialog dialog = new ComparePartListSelectionDialog(selectedConfigsList, null,
                finalVehicleConfigs, parentController.getMainTabPaneController().getOpenPartListIDs());

        Optional<ComparePartListSelectionDialogResult> result = dialog.showAndWait();
        dialog.unregisterEventBus();
        result.ifPresent(comparePartListSelectionDialogResult -> EventBus.getInstance()
                .post(new ShowCompareTabEvent(comparePartListSelectionDialogResult.getSelectedVehicleConfigs(),
                        comparePartListSelectionDialogResult.getReferenceVehicleConfig())));
    }

    public void handleActionResetSorting() {
        isResettingSort = true;

        colSetKey.setSortType(TreeTableColumn.SortType.ASCENDING);

        fgSetTreeTableView.getSortOrder().setAll(colSetKey);
        fgSetTreeTableView.sort();

        TreeItem<FgSetTreeObject> summary = fgSetTreeTableView.getRoot().getChildren().removeFirst();
        TreeItem<FgSetTreeObject> unknown = fgSetTreeTableView.getRoot().getChildren().removeFirst();

        boolean isSummary = summary.getValue().getAggregationObject().getDescription().equals(SUMMARY_MESSAGE);
        if (isSummary) {
            fgSetTreeTableView.getRoot().getChildren().addLast(unknown);
            fgSetTreeTableView.getRoot().getChildren().addLast(summary);
        } else {
            fgSetTreeTableView.getRoot().getChildren().addLast(summary);
            fgSetTreeTableView.getRoot().getChildren().addLast(unknown);
        }

        fgSetTreeTableView.getSortOrder().clear();

        disablePropertyResetSorting().set(true);
        isResettingSort = false;
    }

    @Override
    protected void initSorting() {
        handleActionResetSorting();
    }

    public void handleActionExcelExport() {
        String fileName = "FgSetExport_" + vehicleConfig.getVehicleProject().getProjectName() + StringConstant.UNDERLINE
                + vehicleConfig.getName();

        fileName = fileName.replaceAll("/+", StringConstant.UNDERLINE);

        try {
            new FgSetExcelExporter(fileName, List.of(vehicleConfig), fgSetTreeModel.getRoot()).export(
                    I18N.getString("excel.default.sheet.name"));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void handleActionCloseSummary() {
        splitPaneEfsView.getItems().remove(1);

        RemoveSummaryHighlightStylingEvent<FgSetTreeObject> highlightStylingEvent = new RemoveSummaryHighlightStylingEvent<>(
                getTreeTableView().getRoot().getValue());
        EventBus.getInstance().post(highlightStylingEvent);

        disablePropertyCloseSummary.set(true);
    }

    public BooleanProperty toggleDisplayNumberOfPartsProperty() {
        if (toggleDisplayNumberOfPartsProperty == null) {
            toggleDisplayNumberOfPartsProperty = new SimpleBooleanProperty(false);

            toggleDisplayNumberOfPartsProperty.addListener((obs, oldVal, newVal) -> handleDisplayNumberOfParts(newVal));
        }

        return toggleDisplayNumberOfPartsProperty;
    }

    public BooleanProperty disablePropertyCloseSummary() {
        if (disablePropertyCloseSummary == null) {
            disablePropertyCloseSummary = new SimpleBooleanProperty(true);
        }

        return disablePropertyCloseSummary;
    }

    public BooleanProperty disablePropertyCompare() {
        if (disablePropertyCompare == null) {
            disablePropertyCompare = new SimpleBooleanProperty(false);
        }

        return disablePropertyCompare;
    }

    public BooleanProperty disablePropertyExcelExport() {
        if (disablePropertyExcelExport == null) {
            disablePropertyExcelExport = new SimpleBooleanProperty(false);
        }

        return disablePropertyExcelExport;
    }
}
