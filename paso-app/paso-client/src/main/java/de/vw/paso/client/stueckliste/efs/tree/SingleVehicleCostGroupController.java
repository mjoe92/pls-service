package de.vw.paso.client.stueckliste.efs.tree;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
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
import de.vw.paso.client.stueckliste.efs.CostGroupTabController;
import de.vw.paso.client.stueckliste.efs.display.strategy.AbstractDisplayStrategyForTrees;
import de.vw.paso.client.stueckliste.efs.display.strategy.CostGroupDisplayStrategy;
import de.vw.paso.client.stueckliste.efs.export.costgroup.CostGroupExcelExporter;
import de.vw.paso.client.stueckliste.efs.tree.cell.AggregatedEfsCellFactory;
import de.vw.paso.client.stueckliste.efs.tree.model.CostGroupTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.CostGroupTreeItemPropertyNames;
import de.vw.paso.client.stueckliste.efs.tree.model.CostGroupTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.CostGroupTreeObject;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabPaneController;
import de.vw.paso.client.stueckliste.efs.views.compare.ComparePartListSelectionDialog;
import de.vw.paso.client.stueckliste.efs.views.compare.ComparePartListSelectionDialogResult;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.CostGroupSummarisedTabController;
import de.vw.paso.client.stueckliste.event.CostGroupTreeRefreshEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.TreeItemUtil;
import de.vw.paso.client.util.highlight.SelectionHighlightManager;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

@FXController(name = "cost-group-tree")
public class SingleVehicleCostGroupController extends SingleVehicleBaseController<CostGroupTreeObject> {

    private static final String COST_GROUP_ROW_SELECTION = "cost-group-highlight-row-selection";
    private static final String COST_GROUP_COLUMN_SELECTION = "cost-group-highlight-col-selection";

    private final TreeTableColumn<CostGroupTreeObject, Double> colPlatWeight;
    private final TreeTableColumn<CostGroupTreeObject, Integer> colPlatNum;
    private final TreeTableColumn<CostGroupTreeObject, Double> colSystemWeight;
    private final TreeTableColumn<CostGroupTreeObject, Integer> colSystemNum;
    private final TreeTableColumn<CostGroupTreeObject, Double> colHutWeight;
    private final TreeTableColumn<CostGroupTreeObject, Integer> colHutNum;
    private final TreeTableColumn<CostGroupTreeObject, Double> colAllWeight;
    private final TreeTableColumn<CostGroupTreeObject, Integer> colAllNum;

    private final SelectionHighlightManager<CostGroupTreeObject> highlightManager;
    private final Collection<CostGroupDTO> costGroups;
    private final AbstractDisplayStrategyForTrees<CostGroupTreeObject> displayStrategy;

    @FXML
    private CustomTreeTableView<CostGroupTreeObject> costGroupTreeTableView;
    @FXML
    private TreeTableColumn<CostGroupTreeObject, String> colCostGroup;
    @FXML
    private TreeTableColumn<CostGroupTreeObject, String> colDescription;
    @FXML
    private TreeTableColumn<CostGroupTreeObject, Double> colPlatform;
    @FXML
    private TreeTableColumn<CostGroupTreeObject, Double> colSystem;
    @FXML
    private TreeTableColumn<CostGroupTreeObject, Double> colHut;
    @FXML
    private TreeTableColumn<CostGroupTreeObject, Double> colWeightAll;

    private CostGroupTreeModel costGroupTreeModel;
    private CostGroupTabController parentController;

    private String lastSelectedColumnProperty;

    private BooleanProperty disablePropertyCloseSummary;
    private BooleanProperty disablePropertyCompare;
    private BooleanProperty disablePropertyExcelExport;
    private BooleanProperty toggleDisplayNumberOfPartsProperty;

    public SingleVehicleCostGroupController() {
        displayStrategy = CostGroupDisplayStrategy.getStrategyWithoutDeletion();
        costGroups = new ArrayList<>();
        highlightManager = new SelectionHighlightManager<>();

        colPlatWeight = new TreeTableColumn<>();
        colPlatNum = new TreeTableColumn<>();
        colSystemWeight = new TreeTableColumn<>();
        colSystemNum = new TreeTableColumn<>();
        colHutWeight = new TreeTableColumn<>();
        colHutNum = new TreeTableColumn<>();
        colAllWeight = new TreeTableColumn<>();
        colAllNum = new TreeTableColumn<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        highlightManager.initTable(costGroupTreeTableView, COST_GROUP_ROW_SELECTION, COST_GROUP_COLUMN_SELECTION);

        initSubColumns();
        costGroupTreeTableView.getSortOrder()
                .addListener((ListChangeListener<TreeTableColumn<CostGroupTreeObject, ?>>) change -> {
                    if (!isResettingSort) {
                        disablePropertyResetSorting().set(false);
                    }
                });

        for (TreeTableColumn<CostGroupTreeObject, ?> column : costGroupTreeTableView.getColumns()) {
            column.sortTypeProperty().addListener((observableValue, sortType, t1) -> {
                if (!isResettingSort) {
                    disablePropertyResetSorting().set(false);
                }
            });
        }
    }

    @Override
    protected void stop() {
        super.stop();

        getCostGroupTreeModel().removeAllElements();

        EfsElementResolver.removeListener(this);

        highlightManager.removeFromTable();
    }

    @Override
    protected void initTreeTable() {
        costGroupTreeTableView.showRootProperty().set(false);
        costGroupTreeTableView.setRoot(getCostGroupTreeModel().getRoot());
        costGroupTreeTableView.setEditable(false);
        costGroupTreeTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        costGroupTreeTableView.getSelectionModel().setCellSelectionEnabled(true);
        costGroupTreeTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleEfsSelected(newValue));
        costGroupTreeTableView.makeHeaderWrappable();
        costGroupTreeTableView.makeFilterable();
        costGroupTreeTableView.setHeaderHeight(48);
        costGroupTreeTableView.getSortOrder().addListener(
                (ListChangeListener<? super TreeTableColumn<CostGroupTreeObject, ?>>) change -> disablePropertyResetSorting().set(
                        false));

        addDisabledEditKeys();
    }

    @Override
    protected void initTreeTableColumns() {
        initColumn(colCostGroup, costGroupCellValueFactory(),
                AggregatedEfsCellFactory.forReadOnlyStringColumn(CostGroupTreeItemPropertyNames.COST_GROUP),
                e -> e.getAggregationObject().getCostGroupName(), CostGroupTreeItemPropertyNames.COST_GROUP);
        initColumn(colDescription, cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyDescription(),
                AggregatedEfsCellFactory.forReadOnlyStringColumn(CostGroupTreeItemPropertyNames.DESCRIPTION),
                e -> e.getAggregationObject().getDescription(), CostGroupTreeItemPropertyNames.DESCRIPTION);
        initColumn(colPlatform, cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyWeightPlatform(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(CostGroupTreeItemPropertyNames.PLATFORM,
                        this::handleClickEvent), CostGroupTreeObject::getPlatform,
                CostGroupTreeItemPropertyNames.PLATFORM);
        initColumn(colSystem, cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyWeightSystem(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(CostGroupTreeItemPropertyNames.SYSTEM,
                        this::handleClickEvent), CostGroupTreeObject::getSystem, CostGroupTreeItemPropertyNames.SYSTEM);
        initColumn(colHut, cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyWeightHut(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(CostGroupTreeItemPropertyNames.HUT,
                        this::handleClickEvent), CostGroupTreeObject::getHut, CostGroupTreeItemPropertyNames.HUT);
        initColumn(colWeightAll, cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyWeightAll(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(CostGroupTreeItemPropertyNames.WEIGHT_ALL,
                        this::handleClickEvent), CostGroupTreeObject::getWeightAll,
                CostGroupTreeItemPropertyNames.WEIGHT_ALL);
    }

    private <TO, T> Callback<CellDataFeatures<TO, T>, ObservableValue<T>> costGroupCellValueFactory() {
        return cellData -> {
            CostGroupTreeItem costGroupTreeItem = (CostGroupTreeItem) cellData.getValue();
            StringProperty result =
                    costGroupTreeItem.propertySummaryRow().get() ? new SimpleStringProperty(StringConstant.EMPTY) :
                            costGroupTreeItem.propertyCostGroup();

            return (ObservableValue<T>) result;
        };
    }

    private void initSubColumns() {
        String textNumber = I18N.getString("treetablecolumn.num");
        String textWeight = I18N.getString("treetablecolumn.weight");

        colPlatWeight.setText(textWeight);
        initColumn(colPlatWeight, cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyWeightPlatform(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(CostGroupTreeItemPropertyNames.PLATFORM,
                        this::handleClickEvent), CostGroupTreeObject::getPlatform,
                CostGroupTreeItemPropertyNames.PLATFORM);

        colPlatNum.setText(textNumber);
        colPlatNum.setCellValueFactory(cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyNumPlatform());
        colPlatNum.setCellFactory(
                AggregatedEfsCellFactory.forReadOnlyIntegerColumn(CostGroupTreeItemPropertyNames.PLATFORM,
                        this::handleClickEvent));

        colSystemWeight.setText(textWeight);
        initColumn(colSystemWeight, cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyWeightSystem(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(CostGroupTreeItemPropertyNames.SYSTEM,
                        this::handleClickEvent), CostGroupTreeObject::getSystem, CostGroupTreeItemPropertyNames.SYSTEM);

        colSystemNum.setText(textNumber);
        colSystemNum.setCellValueFactory(cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyNumSystem());
        colSystemNum.setCellFactory(
                AggregatedEfsCellFactory.forReadOnlyIntegerColumn(CostGroupTreeItemPropertyNames.SYSTEM,
                        this::handleClickEvent));

        colHutWeight.setText(textWeight);
        initColumn(colHutWeight, cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyWeightHut(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(CostGroupTreeItemPropertyNames.HUT,
                        this::handleClickEvent), CostGroupTreeObject::getHut, CostGroupTreeItemPropertyNames.HUT);

        colHutNum.setText(textNumber);
        colHutNum.setCellValueFactory(cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyNumHut());
        colHutNum.setCellFactory(AggregatedEfsCellFactory.forReadOnlyIntegerColumn(CostGroupTreeItemPropertyNames.HUT,
                this::handleClickEvent));

        colAllWeight.setText(textWeight);
        initColumn(colAllWeight, cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyWeightAll(),
                AggregatedEfsCellFactory.forReadOnlyDoubleColumn(CostGroupTreeItemPropertyNames.WEIGHT_ALL,
                        this::handleClickEvent), CostGroupTreeObject::getWeightAll,
                CostGroupTreeItemPropertyNames.WEIGHT_ALL);

        colAllNum.setText(textNumber);
        colAllNum.setCellValueFactory(cellData -> ((CostGroupTreeItem) cellData.getValue()).propertyNumAll());
        colAllNum.setCellFactory(
                AggregatedEfsCellFactory.forReadOnlyIntegerColumn(CostGroupTreeItemPropertyNames.WEIGHT_ALL,
                        this::handleClickEvent));
    }

    @Override
    protected CustomTreeTableView<CostGroupTreeObject> getTreeTableView() {
        return costGroupTreeTableView;
    }

    @Override
    protected AbstractTreeModel<CostGroupTreeItem, CostGroupTreeObject> getTreeModel() {
        return costGroupTreeModel;
    }

    public void setParentController(CostGroupTabController parentController) {
        this.parentController = parentController;
    }

    public void setEfsElemente(Collection<EfsElementDTO> efsElements) {
        Collection<CostGroupTreeObject> costGroupTreeObjects = new ArrayList<>();
        Map<String, List<EfsElementDTO>> efsElementCollector = new HashMap<>();

        List<EfsElementDTO> efsElementsWithEmptyCostGroupKey = new ArrayList<>();
        String emptyCostGroupName = I18N.getString("costgroupkey.empty.name");

        for (EfsElementDTO efsElement : efsElements) {
            String costGroup = efsElement.getCostGroup();

            if (StringUtils.isEmpty(costGroup)) {
                efsElementsWithEmptyCostGroupKey.add(efsElement);
                continue;
            }

            if (!efsElementCollector.containsKey(costGroup)) {
                efsElementCollector.put(costGroup, new ArrayList<>());
            }

            efsElementCollector.get(costGroup).add(efsElement);
        }

        for (CostGroupDTO costGroup : costGroups) {
            if (efsElementCollector.containsKey(costGroup.getCostGroupName())) {
                CostGroupTreeObject costGroupTreeObject = new CostGroupTreeObject(costGroup,
                        efsElementCollector.remove(costGroup.getCostGroupName()));
                costGroupTreeObjects.add(costGroupTreeObject);
                continue;
            }

            costGroupTreeObjects.add(new CostGroupTreeObject(costGroup));
        }

        for (Entry<String, List<EfsElementDTO>> entry : efsElementCollector.entrySet()) {
            String key = entry.getKey();
            CostGroupDTO costGroup = new CostGroupDTO(key, CostGroupDTO.UNKNOWN_COST_GROUP_VERSION);

            List<EfsElementDTO> value = entry.getValue();
            CostGroupTreeObject costGroupTreeObject = new CostGroupTreeObject(costGroup, value);
            costGroupTreeObjects.add(costGroupTreeObject);
        }

        CostGroupDTO costGroupKeyForEmptyElements = new CostGroupDTO(emptyCostGroupName,
                CostGroupDTO.EMPTY_COST_GROUP_KEY_VERSION);
        costGroupTreeObjects.add(
                new CostGroupTreeObject(costGroupKeyForEmptyElements, efsElementsWithEmptyCostGroupKey));

        costGroupTreeModel = (CostGroupTreeModel) displayStrategy.createDisplayModel(costGroupTreeObjects);

        costGroupTreeTableView.setRoot(costGroupTreeModel.getRoot());
        costGroupTreeTableView.getRoot().setExpanded(true);
    }

    public void setCostGroups(List<CostGroupDTO> costGroups) {
        this.costGroups.clear();
        this.costGroups.addAll(costGroups);
    }

    private void refreshCostGroup(EfsElementDTO efsElement) {
        if (getCostGroupTreeModel().getTreeItems().size() > 1) {
            displayStrategy.updateNode(getCostGroupTreeModel(), efsElement);
        }
    }

    public void scrollToFirstColumn() {
        costGroupTreeTableView.scrollToColumnIndex(0);
    }

    public void scrollToLastColumn() {
        int size = costGroupTreeTableView.getColumns().size();

        costGroupTreeTableView.scrollToColumnIndex(size - 1);
    }

    @Override
    public void selectElementById(Long itemId) {
        if (itemId != null) {
            selectCostGroup(itemId);
        }
    }

    private void selectCostGroup(Long costGroupId) {
        CostGroupTreeItem treeItem = getCostGroupTreeModel().getTreeItem(costGroupId);
        if (treeItem == null) {
            return;
        }

        TreeItem<CostGroupTreeObject> parent = treeItem.getParent();
        while (parent != null) {
            parent.setExpanded(true);
            parent = parent.getParent();
        }

        costGroupTreeTableView.requestFocus();
        costGroupTreeTableView.getSelectionModel().clearSelection();
        costGroupTreeTableView.getColumns().forEach(column -> costGroupTreeTableView.getSelectionModel()
                .select(costGroupTreeTableView.getRow(treeItem), column));
        costGroupTreeTableView.scrollToCenter(treeItem);
    }

    private CostGroupTreeModel getCostGroupTreeModel() {
        if (costGroupTreeModel == null) {
            costGroupTreeModel = new CostGroupTreeModel();
        }

        return costGroupTreeModel;
    }

    @Override
    public void onEfsElementUpdate(Collection<EfsElementDTO> efsElements) {
        if (efsElements.isEmpty()) {
            return;
        }

        EfsElementDTO firstElement = efsElements.iterator().next();
        if (!parentController.getVehiclePartListId().equals(firstElement.getVehiclePartListId())) {
            return;
        }

        for (EfsElementDTO element : efsElements) {
            refreshCostGroup(element);
        }

        costGroupTreeTableView.requestFocus();

        if (lastSelectedColumnProperty != null) {
            handleClickEvent(true, lastSelectedColumnProperty);
        }
    }

    @Subscribe
    private void clearOldCostGroupNode(CostGroupTreeRefreshEvent event) {
        if (event.isTreeRefresh() && event.getVehiclePartListId().equals(parentController.getVehiclePartListId())) {
            getCostGroupTreeModel().updateNode(event.getEfsElement(), false, true);
        }

        if (lastSelectedColumnProperty != null) {
            handleClickEvent(true, lastSelectedColumnProperty);
        }
    }

    private void handleClickEvent(boolean isDoubleClick, String propertyName) {
        EfsViewTabPaneController efsViewTabPaneController = parentController.getEfsViewTabPaneController();
        String columnHeaderName;

        SplitPane splitPaneEfsView = parentController.getSplitPaneEfsView();

        if (isDoubleClick) {
            TreeItem<CostGroupTreeObject> item = costGroupTreeTableView.getSelectionModel().getSelectedItem();
            List<EfsElementDTO> efsElementList = new ArrayList<>();
            lastSelectedColumnProperty = propertyName;

            switch (propertyName) {
                case CostGroupTreeItemPropertyNames.WEIGHT_ALL -> {
                    efsElementList = TreeItemUtil.getAggregatedChildren(item);
                    columnHeaderName = I18N.getString("treetablecolumn.weightall");
                }
                case CostGroupTreeItemPropertyNames.HUT -> {
                    efsElementList.addAll(TreeItemUtil.getAggregatedChildren(item).stream()
                            .filter(e -> ApCompareGroup.HUT.containsAp(e.getAp())).toList());
                    columnHeaderName = I18N.getString("treetablecolumn.hut");
                }
                case CostGroupTreeItemPropertyNames.SYSTEM -> {
                    efsElementList.addAll(TreeItemUtil.getAggregatedChildren(item).stream()
                            .filter(e -> ApCompareGroup.SYSTEM.containsAp(e.getAp())).toList());
                    columnHeaderName = I18N.getString("treetablecolumn.system");
                }
                default -> {
                    efsElementList.addAll(TreeItemUtil.getAggregatedChildren(item).stream()
                            .filter(e -> ApCompareGroup.PLATFORM.containsAp(e.getAp())).toList());
                    columnHeaderName = I18N.getString("treetablecolumn.platform");
                }
            }

            if (!splitPaneEfsView.getItems().contains(efsViewTabPaneController.getControl())
                    && !efsElementList.isEmpty()) {
                splitPaneEfsView.setDividerPositions(0.65);

                AbstractSummarisedTabController controller = handleActionShowSummarisedView(
                        CostGroupSummarisedTabController.class, efsElementList, columnHeaderName,
                        item.getValue().getId(), splitPaneEfsView);

                addBindedColumnHeaderListener(controller.getTableView());

                ListChangeListener<? super TableColumn<EfsElementDTO, ?>> listChangeListener = (ListChangeListener.Change<? extends TableColumn<EfsElementDTO, ?>> c) -> {
                    if (c.next()) {
                        notifyTableColumnChanged(
                                controller.getTableView().getVisibleLeafColumns().stream().map(TableColumnBase::getText)
                                        .collect(Collectors.toList()));
                    }
                };

                controller.initVisibleColumnListener(listChangeListener);
                return;
            }

            handleActionReloadSummarisedView(CostGroupSummarisedTabController.class, efsElementList, columnHeaderName,
                    item.getValue().getId(), splitPaneEfsView);

            if (splitPaneEfsView.getDividerPositions()[0] > 0.65) {
                splitPaneEfsView.setDividerPositions(0.65);
            }
        }
    }

    private AbstractSummarisedTabController handleActionShowSummarisedView(
            Class<? extends AbstractSummarisedTabController> controllerClass, List<EfsElementDTO> efsElements,
            String propertyName, String itemName, SplitPane splitPaneEfsView) {
        AbstractSummarisedTabController controller = BaseController.load(controllerClass);

        if (controller.getSelectedEfsElement() == null) {
            controller.initEfsElementList(efsElements, propertyName, itemName,
                    parentController.getVehiclePartList().getVehicleConfig(), null, false);
        } else {
            controller.initEfsElementList(efsElements, controller.getSelectedEfsElement());
        }

        if (splitPaneEfsView.getItems().size() > 1) {
            splitPaneEfsView.getItems().remove(1);
        }

        splitPaneEfsView.getItems().add(controller.getControl());

        disablePropertyCloseSummary.set(false);

        ColumnSequenceChangeEvent changeEvent = new ColumnSequenceChangeEvent(
                parentController.getVehiclePartList().getVehicleConfig(), getSVPLColumnOrder(),
                SingleVehicleCostGroupController.class);
        EventBus.getInstance().post(changeEvent);

        return controller;
    }

    private void handleActionReloadSummarisedView(Class<? extends AbstractSummarisedTabController> controllerClass,
            List<EfsElementDTO> efsElements, String propertyName, String itemName, SplitPane splitPane)
            throws ControllerException {
        if (controllerClass == null) {
            return;
        }

        handleActionShowSummarisedView(controllerClass, efsElements, propertyName, itemName, splitPane);
    }

    private void handleDisplayNumberOfParts(boolean newVal) {
        if (newVal) {
            addCountColumns();
        } else {
            removeCountColumns();
        }
    }

    private void addCountColumns() {
        costGroupTreeTableView.setHeaderHeight(24);

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
        costGroupTreeTableView.setHeaderHeight(48);
    }

    private void setColumnHeaderBinding() {
        costGroupTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colPlatform);
        costGroupTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colSystem);
        costGroupTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colHut);
        costGroupTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colWeightAll);
    }

    public void handleActionShowCompareDialog() {
        VehicleConfigDTO vehicleConfig = parentController.getVehiclePartList().getVehicleConfig();
        List<VehicleConfigDTO> vehicleConfigs = VehicleConfigRestClientHolder.getInstance()
                .loadNonDeletedVehicleConfigs().vehicleConfigDTOList();

        List<VehicleConfigDTO> finalVehicleConfigs = vehicleConfigs.stream().filter(e -> e.getVehiclePartList() != null)
                .toList();

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

        costGroupTreeTableView.getSortOrder().clear();

        colCostGroup.setSortType(TreeTableColumn.SortType.ASCENDING);

        costGroupTreeTableView.getSortOrder().setAll(colCostGroup);
        costGroupTreeTableView.sort();

        TreeItem<CostGroupTreeObject> unknown = costGroupTreeTableView.getRoot().getChildren().removeFirst();
        TreeItem<CostGroupTreeObject> summary = costGroupTreeTableView.getRoot().getChildren().removeFirst();

        boolean isSummary = summary.getValue().getAggregationObject().getDescription().equals(SUMMARY_MESSAGE);
        if (isSummary) {
            costGroupTreeTableView.getRoot().getChildren().addLast(unknown);
            costGroupTreeTableView.getRoot().getChildren().addLast(summary);
        } else {
            costGroupTreeTableView.getRoot().getChildren().addLast(summary);
            costGroupTreeTableView.getRoot().getChildren().addLast(unknown);
        }

        disablePropertyResetSorting().set(true);
        isResettingSort = false;
    }

    @Override
    protected void initSorting() {
        handleActionResetSorting();
    }

    public void handleActionExcelExport() {
        VehicleConfigDTO vehicleConfig = parentController.getVehiclePartList().getVehicleConfig();

        String fileName =
                "CostGroupExport_" + vehicleConfig.getVehicleProject().getProjectName() + StringConstant.UNDERLINE
                        + vehicleConfig.getName();

        fileName = fileName.replaceAll("/+", StringConstant.UNDERLINE);

        try {
            new CostGroupExcelExporter(fileName, List.of(vehicleConfig), costGroupTreeModel.getRoot()).export(
                    I18N.getString("excel.default.sheet.name"));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void handleActionCloseSummary() {
        parentController.getSplitPaneEfsView().getItems().remove(1);

        RemoveSummaryHighlightStylingEvent<CostGroupTreeObject> highlightStylingEvent = new RemoveSummaryHighlightStylingEvent<>(
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
