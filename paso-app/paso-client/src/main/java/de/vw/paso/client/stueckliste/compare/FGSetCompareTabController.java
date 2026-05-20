package de.vw.paso.client.stueckliste.compare;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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
import de.vw.paso.client.main.ribbonmenu.compare.fgset.RibbonMenuCompareFgSetListener;
import de.vw.paso.client.stueckliste.efs.export.fgset.FgSetCompareExcelExporter;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedCompareTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.FgSetCompareSummarisedTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.FgSetSummarisedTabController;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.ExpandCollapseUtil;
import de.vw.paso.client.util.highlight.SelectionHighlightManager;
import de.vw.paso.compare.AbstractCompareResult;
import de.vw.paso.compare.fgset.FGSetCompareRow;
import de.vw.paso.compare.fgset.FgSetCompareResult;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;

@FXController(name = "fg-set-compare-tab")
public class FGSetCompareTabController extends AbstractCompareTabController<FGSetCompareRow>
        implements RibbonMenuCompareFgSetListener {

    private static final String COL_STYLE = "highlight-col-selection";
    private static final String ROW_STYLE = "highlight-row-selection";

    @FXML
    private Tab tabFgSet;
    @FXML
    private SplitPane fgsetCompareSplitPane;
    @FXML
    private CustomTreeTableView<FGSetCompareRow> fgSetCompareTreeView;
    @FXML
    private TreeTableColumn<FGSetCompareRow, String> setKeyTreeColumn;
    @FXML
    private TreeTableColumn<FGSetCompareRow, String> description;

    private final Map<String, SetKeyDTO> setKeyMap;
    private final SelectionHighlightManager<FGSetCompareRow> highlightManager;

    private boolean isResettingSort;

    private FgSetCompareResult fgSetCompareResult;

    private BooleanProperty disablePropertyExcelExport;
    private BooleanProperty toggleDisplayNumberOfPartsProperty;
    private BooleanProperty toggleDisplayDeltaColumnsProperty;

    public static List<VehicleConfigDTO> vehicleConfigs;

    public FGSetCompareTabController() {
        setKeyMap = new HashMap<>();
        highlightManager = new SelectionHighlightManager<>();
        isResettingSort = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        loadSetKeys();

        initColumns();
        initTable();

        highlightManager.initTable(fgSetCompareTreeView, ROW_STYLE, COL_STYLE);
    }

    @Override
    public void stop() {
        super.stop();
        highlightManager.removeFromTable();
    }

    @Override
    public void handleActionExcelExport() {
        StringBuilder title = new StringBuilder("FgSetCompareExport");

        for (VehicleConfigDTO vehicleConfig : fgSetCompareResult.getVehicleConfigs()) {
            title.append(StringConstant.UNDERLINE).append(vehicleConfig.getName());
        }

        try {
            new FgSetCompareExcelExporter(title.toString(), fgSetCompareResult.getVehicleConfigs(),
                    fgSetCompareTreeView.getRoot(), setKeyMap, getMapOfDeltaWeightsOfPartLists()).export(
                    I18N.getString("excel.default.sheet.name"));
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void handleActionCollapseTree() {
        expandOrCollapseTree(false);
    }

    @Override
    public void handleActionCollapseAllTree() {
        ExpandCollapseUtil.collapseAll(fgSetCompareTreeView.getRoot());
    }

    @Override
    public void handleActionExpandTree() {
        expandOrCollapseTree(true);
    }

    @Override
    public void handleActionExpandAllTree() {
        ExpandCollapseUtil.expandAll(fgSetCompareTreeView.getRoot());
    }

    @Override
    public void handleActionResetSorting() {
        isResettingSort = true;

        setKeyTreeColumn.setSortType(TreeTableColumn.SortType.ASCENDING);

        fgSetCompareTreeView.getSortOrder().setAll(setKeyTreeColumn);
        fgSetCompareTreeView.sort();

        TreeItem<FGSetCompareRow> summary = fgSetCompareTreeView.getRoot().getChildren().removeFirst();
        TreeItem<FGSetCompareRow> unknown = fgSetCompareTreeView.getRoot().getChildren().removeFirst();

        if (summary.getValue().isSum()) {
            fgSetCompareTreeView.getRoot().getChildren().addLast(unknown);
            fgSetCompareTreeView.getRoot().getChildren().addLast(summary);
        } else {
            fgSetCompareTreeView.getRoot().getChildren().addLast(summary);
            fgSetCompareTreeView.getRoot().getChildren().addLast(unknown);
        }

        fgSetCompareTreeView.getSortOrder().clear();

        disablePropertyResetSorting().set(true);
        isResettingSort = false;
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
            for (TreeTableColumn<FGSetCompareRow, String> col : getMapOfApColumns().get(selectedAp).values()) {
                col.setVisible(true);
            }
        } else {
            for (TreeTableColumn<FGSetCompareRow, String> col : getMapOfApColumns().get(selectedAp).values()) {
                col.setVisible(false);
            }
        }
    }

    @Override
    public void actionReopenCompareTabs() {
        EventBus.getInstance().post(new ReopenCompareTabsEvent(fgSetCompareResult.getVehicleConfigs()));
    }

    @Override
    protected Class<? extends AbstractSummarisedTabController> getSummarisedTabControllerClass() {
        return FgSetSummarisedTabController.class;
    }

    @Override
    protected SplitPane getCompareSplitPane() {
        return fgsetCompareSplitPane;
    }

    @Override
    protected Class<? extends AbstractSummarisedCompareTabController> getSummarisedCompareTabControllerClass() {
        return FgSetCompareSummarisedTabController.class;
    }

    @Override
    protected CustomTreeTableView<FGSetCompareRow> getTreeTableView() {
        return fgSetCompareTreeView;
    }

    @Override
    protected SplitPane getSplitPane() {
        return fgsetCompareSplitPane;
    }

    @Override
    protected Tab getTab() {
        return tabFgSet;
    }

    @Override
    protected AbstractCompareResult getResult() {
        return fgSetCompareResult;
    }

    protected void setFgSetResult(FgSetCompareResult result) {
        this.fgSetCompareResult = result;
        super.setResult(result);

        Map<String, TreeItem<FGSetCompareRow>> treeItemMap = new LinkedHashMap<>();
        TreeItem<FGSetCompareRow> treeItem = createTreeItems(result.getRoot(), treeItemMap);
        fillMissingTreeItems(treeItemMap);
        sortTreeItems(treeItem);

        TreeItem<FGSetCompareRow> treeItemSum = new TreeItem<>();
        FGSetCompareRow sumRow = new FGSetCompareRow();
        sumRow.setSum(true);
        treeItemSum.setValue(sumRow);

        treeItem.getChildren().add(treeItemSum);
        fgSetCompareTreeView.setRoot(treeItem);
        handleActionResetSorting();

        highlightManager.setStyleToTreeTableColumnGroups(fgSetCompareTreeView, true);
    }

    private void initTable() {
        fgSetCompareTreeView.setShowRoot(false);
        fgSetCompareTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleSelection(newValue));
        fgSetCompareTreeView.getSortOrder()
                .addListener((ListChangeListener<TreeTableColumn<FGSetCompareRow, ?>>) change -> {
                    if (!isResettingSort) {
                        disablePropertyResetSorting().set(false);
                    }
                });

        for (TreeTableColumn<FGSetCompareRow, ?> column : fgSetCompareTreeView.getColumns()) {
            column.sortTypeProperty().addListener((observableValue, sortType, t1) -> {
                if (!isResettingSort) {
                    disablePropertyResetSorting().set(false);
                }
            });
        }

        for (TreeTableColumn<FGSetCompareRow, ?> fgSetCompareRowTreeTableColumn : fgSetCompareTreeView.getColumns()) {
            initColumnAlignment(fgSetCompareRowTreeTableColumn);
        }
    }

    private void initColumns() {
        setKeyTreeColumn.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getValue().getSetKeyStr()));
        setKeyTreeColumn.setCellFactory(createCellFactory(String.class, null, null));

        description.setCellValueFactory(param -> {
            SetKeyDTO setKeyDTO = setKeyMap.get(param.getValue().getValue().getSetKeyStr());
            if (setKeyDTO != null) {
                return new SimpleStringProperty(setKeyDTO.getDescription());
            }

            String text = param.getValue().getValue().isSum() ? SUMMARY_MESSAGE : UNKNOWN_MESSAGE;
            return new SimpleStringProperty(text);
        });

        description.setCellFactory(createCellFactory(String.class, null, null));
    }

    private void loadSetKeys() {
        Collection<Long> ids = new HashSet<>(vehicleConfigs.size());
        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            Long id = vehicleConfig.getSetVersion().getId();
            if (!ids.add(id)) {
                continue;
            }

            Collection<SetKeyDTO> setKeys = CacheManager.getSetKeys(id);
            for (SetKeyDTO key : setKeys) {
                setKeyMap.put(key.getSetKeyName(), key);
            }
        }
    }

    private void sortTreeItems(TreeItem<FGSetCompareRow> treeItem) {
        treeItem.getChildren().sort((t1, t2) -> {
            String v1 = t1.getValue().getSetKeyStr();
            String v2 = t2.getValue().getSetKeyStr();

            if (v1 == null) {
                return 1;
            }

            if (v2 == null) {
                return -1;
            }

            return v1.compareTo(v2);
        });

        for (TreeItem<FGSetCompareRow> child : treeItem.getChildren()) {
            sortTreeItems(child);
        }
    }

    private void fillMissingTreeItems(Map<String, TreeItem<FGSetCompareRow>> treeItemMap) {
        for (SetKeyDTO setKeyDTO : setKeyMap.values()) {
            TreeItem<FGSetCompareRow> treeItem = treeItemMap.get(setKeyDTO.getSetKeyName());
            if (treeItem == null) {
                createTreeItemForSetKey(treeItemMap, setKeyDTO);
            }
        }
    }

    private TreeItem<FGSetCompareRow> createTreeItemForSetKey(Map<String, TreeItem<FGSetCompareRow>> treeItemMap,
            SetKeyDTO setKey) {
        TreeItem<FGSetCompareRow> treeItem = new TreeItem<>(new FGSetCompareRow(setKey.getSetKeyName()));
        TreeItem<FGSetCompareRow> parent = treeItemMap.get(setKey.getParentSetKey());
        if (parent == null) {
            parent = createTreeItemForSetKey(treeItemMap, setKey.getParentSetKey());
        }

        parent.getChildren().add(treeItem);
        treeItemMap.put(setKey.getSetKeyName(), treeItem);
        return treeItem;
    }

    private TreeItem<FGSetCompareRow> createTreeItems(FGSetCompareRow root,
            Map<String, TreeItem<FGSetCompareRow>> treeItemMap) {
        TreeItem<FGSetCompareRow> ti = new TreeItem<>(root);
        treeItemMap.put(root.getSetKeyStr(), ti);
        for (FGSetCompareRow child : root.getChildren()) {
            TreeItem<FGSetCompareRow> childItem = createTreeItems(child, treeItemMap);
            ti.getChildren().add(childItem);
        }

        return ti;
    }

    private void expandOrCollapseTree(boolean isExpand) {
        boolean exists = getSelectedTreeItem() == null;
        TreeItem<FGSetCompareRow> selected = exists ? fgSetCompareTreeView.getRoot() : getSelectedTreeItem();

        ExpandCollapseUtil.setExpanded(fgSetCompareTreeView, selected, isExpand, !exists);
    }
}
