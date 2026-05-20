package de.vw.paso.client.stueckliste.compare;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.main.ribbonmenu.compare.costgroup.RibbonMenuCompareCostGroupListener;
import de.vw.paso.client.stueckliste.efs.export.costgroup.CostGroupCompareExcelExporter;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedCompareTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.CostGroupCompareSummarisedTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.CostGroupSummarisedTabController;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.ExpandCollapseUtil;
import de.vw.paso.client.util.highlight.SelectionHighlightManager;
import de.vw.paso.compare.AbstractCompareResult;
import de.vw.paso.compare.costgroup.CostGroupCompareResult;
import de.vw.paso.compare.costgroup.CostGroupCompareRow;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;

@FXController(name = "cost-group-compare-tab")
public class CostGroupCompareTabController extends AbstractCompareTabController<CostGroupCompareRow>
        implements RibbonMenuCompareCostGroupListener {

    private static final String COL_STYLE = "highlight-col-selection";
    private static final String ROW_STYLE = "highlight-row-selection";

    @FXML
    private Tab tabCostGroup;
    @FXML
    private SplitPane costGroupCompareSplitPane;
    @FXML
    private CustomTreeTableView<CostGroupCompareRow> costGroupCompareTreeView;
    @FXML
    private TreeTableColumn<CostGroupCompareRow, String> costGroupTreeColumn;
    @FXML
    private TreeTableColumn<CostGroupCompareRow, String> description;

    private final SelectionHighlightManager<CostGroupCompareRow> highlightManager;
    private final Map<String, CostGroupDTO> costGroupMap;

    private boolean isResettingSort;

    private CostGroupCompareResult costGroupCompareResult;
    private BooleanProperty disablePropertyExcelExport;
    private BooleanProperty toggleDisplayNumberOfPartsProperty;
    private BooleanProperty toggleDisplayDeltaColumnsProperty;

    public CostGroupCompareTabController() {
        highlightManager = new SelectionHighlightManager<>();
        costGroupMap = new HashMap<>();
        isResettingSort = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        loadCostGroups();

        initColumns();
        initTable();

        highlightManager.initTable(costGroupCompareTreeView, ROW_STYLE, COL_STYLE);
    }

    @Override
    public void stop() {
        super.stop();
        highlightManager.removeFromTable();
    }

    @Override
    public void handleActionExcelExport() {
        StringBuilder title = new StringBuilder("CostGroupCompareExport");

        for (VehicleConfigDTO vehicleConfig : costGroupCompareResult.getVehicleConfigs()) {
            title.append(StringConstant.UNDERLINE).append(vehicleConfig.getName());
        }

        try {
            new CostGroupCompareExcelExporter(title.toString(), costGroupCompareResult.getVehicleConfigs(),
                    costGroupCompareTreeView.getRoot(), costGroupMap, getMapOfDeltaWeightsOfPartLists()).export(
                    I18N.getString("excel.default.sheet.name"));
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public BooleanProperty disablePropertyExcelExport() {
        if (disablePropertyExcelExport == null) {
            disablePropertyExcelExport = new SimpleBooleanProperty(false);
        }

        return disablePropertyExcelExport;
    }

    @Override
    public void handleActionCollapseTree() {
        expandOrCollapseTree(false);
    }

    @Override
    public void handleActionCollapseAllTree() {
        ExpandCollapseUtil.collapseAll(costGroupCompareTreeView.getRoot());
    }

    @Override
    public void handleActionExpandTree() {
        expandOrCollapseTree(true);
    }

    @Override
    public void handleActionExpandAllTree() {
        ExpandCollapseUtil.expandAll(costGroupCompareTreeView.getRoot());
    }

    @Override
    public void handleActionResetSorting() {
        isResettingSort = true;

        costGroupTreeColumn.setSortType(TreeTableColumn.SortType.ASCENDING);

        costGroupCompareTreeView.getSortOrder().setAll(costGroupTreeColumn);
        costGroupCompareTreeView.sort();

        TreeItem<CostGroupCompareRow> summary = costGroupCompareTreeView.getRoot().getChildren().removeFirst();
        TreeItem<CostGroupCompareRow> unknown = costGroupCompareTreeView.getRoot().getChildren().removeFirst();

        if (summary.getValue().isSum()) {
            costGroupCompareTreeView.getRoot().getChildren().addLast(unknown);
            costGroupCompareTreeView.getRoot().getChildren().addLast(summary);
        } else {
            costGroupCompareTreeView.getRoot().getChildren().addLast(summary);
            costGroupCompareTreeView.getRoot().getChildren().addLast(unknown);
        }

        costGroupCompareTreeView.getSortOrder().clear();

        disablePropertyResetSorting().set(true);
        isResettingSort = false;
    }

    @Override
    public BooleanProperty toggleDisplayNumberOfPartsProperty() {
        if (toggleDisplayNumberOfPartsProperty == null) {
            toggleDisplayNumberOfPartsProperty = new SimpleBooleanProperty(false);

            toggleDisplayNumberOfPartsProperty.addListener((obs, oldVal, newVal) -> handleDisplayNumberOfParts(newVal));
        }

        return toggleDisplayNumberOfPartsProperty;
    }

    @Override
    public BooleanProperty toggleDisplayDeltaColumnsProperty() {
        if (toggleDisplayDeltaColumnsProperty == null) {
            toggleDisplayDeltaColumnsProperty = new SimpleBooleanProperty(true);

            toggleDisplayDeltaColumnsProperty.addListener((obs, oldVal, newVal) -> handleDisplayDeltaColumns(newVal));
        }

        return toggleDisplayDeltaColumnsProperty;
    }

    @Override
    public void handleCompareViewModeChange(ApCompareGroup selectedAp, Boolean newVal) {
        if (newVal) {
            for (TreeTableColumn<CostGroupCompareRow, String> col : getMapOfApColumns().get(selectedAp).values()) {
                col.setVisible(true);
            }

            return;
        }

        for (TreeTableColumn<CostGroupCompareRow, String> col : getMapOfApColumns().get(selectedAp).values()) {
            col.setVisible(false);
        }
    }

    @Override
    public void actionReopenCompareTabs() {
        ReopenCompareTabsEvent reopenEvent = new ReopenCompareTabsEvent(costGroupCompareResult.getVehicleConfigs());
        EventBus.getInstance().post(reopenEvent);
    }

    @Override
    protected CustomTreeTableView<CostGroupCompareRow> getTreeTableView() {
        return costGroupCompareTreeView;
    }

    @Override
    protected SplitPane getSplitPane() {
        return costGroupCompareSplitPane;
    }

    @Override
    protected Tab getTab() {
        return tabCostGroup;
    }

    @Override
    protected AbstractCompareResult getResult() {
        return costGroupCompareResult;
    }

    @Override
    protected Class<? extends AbstractSummarisedTabController> getSummarisedTabControllerClass() {
        return CostGroupSummarisedTabController.class;
    }

    @Override
    protected SplitPane getCompareSplitPane() {
        return costGroupCompareSplitPane;
    }

    @Override
    protected Class<? extends AbstractSummarisedCompareTabController> getSummarisedCompareTabControllerClass() {
        return CostGroupCompareSummarisedTabController.class;
    }

    protected void setCostGroupResult(CostGroupCompareResult result) {
        super.setResult(result);

        this.costGroupCompareResult = result;

        Map<String, TreeItem<CostGroupCompareRow>> treeItemMap = new LinkedHashMap<>();
        TreeItem<CostGroupCompareRow> treeItem = createTreeItems(result.getRoot(), treeItemMap);
        fillMissingTreeItems(treeItemMap);
        sortTreeItems(treeItem);

        TreeItem<CostGroupCompareRow> treeItemSum = new TreeItem<>();
        CostGroupCompareRow sumRow = new CostGroupCompareRow();
        sumRow.setSum(true);
        treeItemSum.setValue(sumRow);

        treeItem.getChildren().add(treeItemSum);
        costGroupCompareTreeView.setRoot(treeItem);
        handleActionResetSorting();

        highlightManager.setStyleToTreeTableColumnGroups(costGroupCompareTreeView, true);
    }

    private void initTable() {
        costGroupCompareTreeView.setShowRoot(false);
        costGroupCompareTreeView.makeHeaderWrappable(costGroupTreeColumn);
        costGroupCompareTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleSelection(newValue));
        costGroupCompareTreeView.getSortOrder()
                .addListener((ListChangeListener<TreeTableColumn<CostGroupCompareRow, ?>>) change -> {
                    if (!isResettingSort) {
                        disablePropertyResetSorting().set(false);
                    }
                });

        for (TreeTableColumn<CostGroupCompareRow, ?> column : costGroupCompareTreeView.getColumns()) {
            column.sortTypeProperty().addListener((observableValue, sortType, t1) -> {
                if (!isResettingSort) {
                    disablePropertyResetSorting().set(false);
                }
            });
        }

        for (TreeTableColumn<CostGroupCompareRow, ?> column : costGroupCompareTreeView.getColumns()) {
            initColumnAlignment(column);
        }
    }

    private void initColumns() {
        costGroupTreeColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getValue().getCostGroupStr()));
        costGroupTreeColumn.setCellFactory(createCellFactory(String.class, null, null));

        description.setCellValueFactory(cellData -> {
            CostGroupDTO costGroup = costGroupMap.get(cellData.getValue().getValue().getCostGroupStr());
            if (costGroup != null) {
                return new SimpleStringProperty(costGroup.getDescription());
            }

            if (cellData.getValue().getValue().isSum()) {
                return new SimpleStringProperty(SUMMARY_MESSAGE);
            }

            return new SimpleStringProperty(UNKNOWN_MESSAGE);
        });

        description.setCellFactory(createCellFactory(String.class, null, null));
    }

    private void loadCostGroups() {
        Collection<CostGroupDTO> costGroups = CacheManager.getCostGroups();
        for (CostGroupDTO costGroup : costGroups) {
            costGroupMap.put(costGroup.getCostGroupName(), costGroup);
        }
    }

    private void sortTreeItems(TreeItem<CostGroupCompareRow> treeItem) {
        treeItem.getChildren().sort((t1, t2) -> {
            String value1 = t1.getValue().getCostGroupStr();
            String value2 = t2.getValue().getCostGroupStr();

            if (value1 == null) {
                return 1;
            }

            return value2 == null ? -1 : value1.compareTo(value2);
        });

        for (TreeItem<CostGroupCompareRow> child : treeItem.getChildren()) {
            sortTreeItems(child);
        }
    }

    private void fillMissingTreeItems(Map<String, TreeItem<CostGroupCompareRow>> treeItemMap) {
        for (CostGroupDTO costGroup : costGroupMap.values()) {
            TreeItem<CostGroupCompareRow> treeItem = treeItemMap.get(costGroup.getCostGroupName());
            if (treeItem == null) {
                createTreeItemForCostGroup(treeItemMap, costGroup);
            }
        }
    }

    private TreeItem<CostGroupCompareRow> createTreeItemForCostGroup(
            Map<String, TreeItem<CostGroupCompareRow>> treeItemMap, CostGroupDTO costGroup) {
        CostGroupCompareRow costGroupCompareRow = new CostGroupCompareRow(costGroup.getCostGroupName());
        TreeItem<CostGroupCompareRow> treeItem = new TreeItem<>(costGroupCompareRow);
        TreeItem<CostGroupCompareRow> parent = treeItemMap.get(costGroup.getParentCostGroupName());
        if (parent == null) {
            parent = createTreeItemForCostGroup(treeItemMap,
                    CacheManager.getCostGroupByIdAndName(costGroup.getVersion(), costGroup.getParentCostGroupName()));
        }

        parent.getChildren().add(treeItem);
        treeItemMap.put(costGroup.getCostGroupName(), treeItem);
        return treeItem;
    }

    private TreeItem<CostGroupCompareRow> createTreeItems(CostGroupCompareRow root,
            Map<String, TreeItem<CostGroupCompareRow>> treeItemMap) {
        TreeItem<CostGroupCompareRow> rootItem = new TreeItem<>(root);
        treeItemMap.put(root.getCostGroupStr(), rootItem);
        for (CostGroupCompareRow child : root.getChildren()) {
            TreeItem<CostGroupCompareRow> childItem = createTreeItems(child, treeItemMap);
            rootItem.getChildren().add(childItem);
        }

        return rootItem;
    }

    private void expandOrCollapseTree(boolean isExpand) {
        boolean exists = getSelectedTreeItem() == null;
        TreeItem<CostGroupCompareRow> selected = exists ? costGroupCompareTreeView.getRoot() : getSelectedTreeItem();

        ExpandCollapseUtil.setExpanded(costGroupCompareTreeView, selected, isExpand, !exists);
    }
}
