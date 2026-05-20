package de.vw.paso.client.stueckliste.compare;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.PartGroupTreeTableCell;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.main.ribbonmenu.compare.partgroup.RibbonMenuComparePartGroupListener;
import de.vw.paso.client.stueckliste.efs.control.CommonCellUtil;
import de.vw.paso.client.stueckliste.efs.export.partgroup.PartGroupCompareExcelExporter;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedCompareTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.PartGroupCompareSummarisedTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.PartGroupSummarisedTabController;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.ExpandCollapseUtil;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import de.vw.paso.client.util.highlight.SelectionHighlightManager;
import de.vw.paso.client.valueobject.PartGroupVMO;
import de.vw.paso.compare.AbstractCompareResult;
import de.vw.paso.compare.partgroup.PartGroupCompareResult;
import de.vw.paso.compare.partgroup.PartGroupCompareRow;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.SpecPartGroupCategory;
import de.vw.paso.utility.StringConstant;

@FXController(name = "part-group-compare-tab")
public class PartGroupCompareTabController extends AbstractCompareTabController<PartGroupCompareRow>
        implements RibbonMenuComparePartGroupListener {

    private static final String COL_STYLE = "highlight-col-selection";
    private static final String ROW_STYLE = "highlight-row-selection";

    @FXML
    private Tab tabPartGroup;
    @FXML
    private SplitPane partGroupCompareSplitPane;
    @FXML
    private CustomTreeTableView<PartGroupCompareRow> partGroupCompareTreeView;
    @FXML
    private TreeTableColumn<PartGroupCompareRow, Integer> partGroupTreeColumn;
    @FXML
    private TreeTableColumn<PartGroupCompareRow, String> description;

    private final SelectionHighlightManager<PartGroupCompareRow> highlightManager;

    private Map<String, PartGroupDTO> partGroupMap;

    private PartGroupCompareResult partGroupCompareResult;
    private BooleanProperty disablePropertyExcelExport;
    private BooleanProperty toggleDisplayNumberOfPartsProperty;
    private BooleanProperty toggleDisplayDeltaColumnsProperty;

    private boolean isResetSort;

    public PartGroupCompareTabController() {
        highlightManager = new SelectionHighlightManager<>();
        partGroupMap = new HashMap<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initColumns();
        initTable();

        highlightManager.initTable(partGroupCompareTreeView, ROW_STYLE, COL_STYLE);
    }

    @Override
    public void stop() {
        super.stop();
        highlightManager.removeFromTable();
    }

    @Override
    public void handleActionExcelExport() {
        StringBuilder title = new StringBuilder("PartGroupCompareExport");

        for (VehicleConfigDTO vehicleConfig : partGroupCompareResult.getVehicleConfigs()) {
            title.append(StringConstant.UNDERLINE).append(vehicleConfig.getName());
        }

        try {
            new PartGroupCompareExcelExporter(title.toString(), partGroupCompareResult.getVehicleConfigs(),
                    partGroupCompareTreeView.getRoot(), partGroupMap, getMapOfDeltaWeightsOfPartLists()).export(
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
            for (TreeTableColumn<PartGroupCompareRow, String> col : getMapOfApColumns().get(selectedAp).values()) {
                col.setVisible(true);
            }

            return;
        }

        for (TreeTableColumn<PartGroupCompareRow, String> column : getMapOfApColumns().get(selectedAp).values()) {
            column.setVisible(false);
        }
    }

    @Override
    public void handleActionCollapseTree() {
        expandOrCollapseTree(false);
    }

    @Override
    public void handleActionCollapseAllTree() {
        ExpandCollapseUtil.collapseAll(partGroupCompareTreeView.getRoot());
    }

    @Override
    public void handleActionExpandTree() {
        expandOrCollapseTree(true);
    }

    @Override
    public void handleActionExpandAllTree() {
        ExpandCollapseUtil.expandAll(partGroupCompareTreeView.getRoot());
    }

    @Override
    public void handleActionResetSorting() {
        isResetSort = true;

        partGroupTreeColumn.setSortType(TreeTableColumn.SortType.ASCENDING);

        partGroupCompareTreeView.getSortOrder().setAll(partGroupTreeColumn);
        partGroupCompareTreeView.sort();

        TreeItem<PartGroupCompareRow> summary = partGroupCompareTreeView.getRoot().getChildren().removeFirst();
        TreeItem<PartGroupCompareRow> unknown = partGroupCompareTreeView.getRoot().getChildren().removeFirst();

        if (summary.getValue().isSum()) {
            partGroupCompareTreeView.getRoot().getChildren().addLast(unknown);
            partGroupCompareTreeView.getRoot().getChildren().addLast(summary);
        } else {
            partGroupCompareTreeView.getRoot().getChildren().addLast(summary);
            partGroupCompareTreeView.getRoot().getChildren().addLast(unknown);
        }

        partGroupCompareTreeView.getSortOrder().clear();
        disablePropertyResetSorting().set(true);
        isResetSort = false;
    }

    @Override
    public void actionReopenCompareTabs() {
        EventBus.getInstance().post(new ReopenCompareTabsEvent(partGroupCompareResult.getVehicleConfigs()));
    }

    @Override
    protected CustomTreeTableView<PartGroupCompareRow> getTreeTableView() {
        return partGroupCompareTreeView;
    }

    @Override
    protected SplitPane getSplitPane() {
        return partGroupCompareSplitPane;
    }

    @Override
    protected Tab getTab() {
        return tabPartGroup;
    }

    @Override
    protected AbstractCompareResult getResult() {
        return partGroupCompareResult;
    }

    @Override
    protected Class<? extends AbstractSummarisedTabController> getSummarisedTabControllerClass() {
        return PartGroupSummarisedTabController.class;
    }

    @Override
    protected SplitPane getCompareSplitPane() {
        return partGroupCompareSplitPane;
    }

    @Override
    protected Class<? extends AbstractSummarisedCompareTabController> getSummarisedCompareTabControllerClass() {
        return PartGroupCompareSummarisedTabController.class;
    }

    @Override
    protected String getItemNameForSummaryView(TreeItem<PartGroupCompareRow> treeItem) {
        PartGroupDTO partGroup = treeItem.getValue().getPartGroup();

        if (partGroup == null || partGroup.getCategory() == null) {
            return treeItem.getValue().getPartGroupStr();
        }

        if (partGroup.getCategory() < 100) {
            return treeItem.getValue().getPartGroupStr();
        }

        if (partGroup.isCategory()) {
            return SpecPartGroupCategory.getStringForCategory(partGroup.getCategory());
        }

        String text = treeItem.getValue().getPartGroupStr();
        return SpecPartGroupCategory.getStringForCategory(partGroup.getCategory()) + text.substring(3);
    }

    protected void setPartGroups(Map<String, PartGroupDTO> partGroups) {
        this.partGroupMap = partGroups;
    }

    protected void setPartGroupResult(PartGroupCompareResult result) {
        this.partGroupCompareResult = result;
        super.setResult(result);

        Map<String, TreeItem<PartGroupCompareRow>> treeItemMap = new LinkedHashMap<>();
        TreeItem<PartGroupCompareRow> treeItem = createTreeItems(result.getRoot(), treeItemMap);
        for (TreeItem<PartGroupCompareRow> partGroupCompareRowTreeItem : treeItem.getChildren()) {
            sortTreeItems(partGroupCompareRowTreeItem);
        }

        PartGroupCompareRow sumRow = new PartGroupCompareRow();
        sumRow.setSum(true);

        TreeItem<PartGroupCompareRow> treeItemSum = new TreeItem<>();
        treeItemSum.setValue(sumRow);

        treeItem.getChildren().add(treeItemSum);
        partGroupCompareTreeView.setRoot(treeItem);
        handleActionResetSorting();

        highlightManager.setStyleToTreeTableColumnGroups(partGroupCompareTreeView, true);
    }

    private void initTable() {
        partGroupCompareTreeView.setShowRoot(false);
        partGroupCompareTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleSelection(newValue));
        partGroupCompareTreeView.getSortOrder()
                .addListener((ListChangeListener<TreeTableColumn<PartGroupCompareRow, ?>>) change -> {
                    if (!isResetSort) {
                        disablePropertyResetSorting().set(false);
                    }
                });

        for (TreeTableColumn<PartGroupCompareRow, ?> column : partGroupCompareTreeView.getColumns()) {
            column.sortTypeProperty().addListener((observableValue, sortType, t1) -> {
                if (!isResetSort) {
                    disablePropertyResetSorting().set(false);
                }
            });
        }

        for (TreeTableColumn<PartGroupCompareRow, ?> partGroupCompareRowTreeTableColumn : partGroupCompareTreeView.getColumns()) {
            initColumnAlignment(partGroupCompareRowTreeTableColumn);
        }
    }

    private void initColumns() {
        partGroupTreeColumn.setCellValueFactory(param -> {
            PartGroupDTO partGroup = param.getValue().getValue().getPartGroup();
            if (partGroup == null) {
                return new SimpleObjectProperty<>();
            }

            PartGroupVMO partGroupVMO = PartGroupVMO.toVMO(partGroup);
            if (partGroupVMO.isCategory()) {
                return partGroupVMO.categoryProperty();
            }

            if (partGroupVMO.isMgr()) {
                return partGroupVMO.mgrProperty();
            }

            return partGroupVMO.ugrProperty();
        });

        partGroupTreeColumn.setCellFactory(createCellFactoryInt());
        description.setCellValueFactory(param -> {
            PartGroupCompareRow row = param.getValue().getValue();
            if (row.isSum()) {
                return new SimpleStringProperty(SUMMARY_MESSAGE);
            }

            PartGroupDTO partGroup = row.getPartGroup();
            if (partGroup == null) {
                return new SimpleStringProperty(UNKNOWN_MESSAGE);
            }

            return PartGroupVMO.toVMO(partGroup).descriptionProperty();
        });
        description.setCellFactory(createCellFactory(String.class, null, null));
    }

    private void sortTreeItems(TreeItem<PartGroupCompareRow> treeItem) {
        treeItem.getChildren().sort((t1, t2) -> {
            String v1 = t1.getValue().getPartGroupStr();
            String v2 = t2.getValue().getPartGroupStr();

            if (v1 == null) {
                return 1;
            }

            if (v2 == null) {
                return -1;
            }

            return v1.compareTo(v2);
        });

        for (TreeItem<PartGroupCompareRow> child : treeItem.getChildren()) {
            sortTreeItems(child);
        }
    }

    private TreeItem<PartGroupCompareRow> createTreeItems(PartGroupCompareRow root,
            Map<String, TreeItem<PartGroupCompareRow>> treeItemMap) {
        TreeItem<PartGroupCompareRow> ti = new TreeItem<>(root);
        treeItemMap.put(root.getPartGroupStr(), ti);

        for (PartGroupCompareRow child : root.getChildren()) {
            TreeItem<PartGroupCompareRow> childItem = createTreeItems(child, treeItemMap);
            ti.getChildren().add(childItem);
        }

        return ti;
    }

    private Callback<TreeTableColumn<PartGroupCompareRow, Integer>, TreeTableCell<PartGroupCompareRow, Integer>> createCellFactoryInt() {
        return param -> {
            PartGroupTreeTableCell<PartGroupCompareRow> cell = new PartGroupTreeTableCell<>(Integer.class);
            cell.setConverter(new IntegerStringConverter());

            CommonCellUtil.formatCell(cell);

            return cell;
        };
    }

    private void expandOrCollapseTree(boolean isExpand) {
        boolean exists = getSelectedTreeItem() == null;
        TreeItem<PartGroupCompareRow> selected = exists ? partGroupCompareTreeView.getRoot() : getSelectedTreeItem();

        ExpandCollapseUtil.setExpanded(partGroupCompareTreeView, selected, isExpand, !exists);
    }
}
