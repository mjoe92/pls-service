package de.vw.paso.client.stueckliste.efs.tree;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.base.AbstractController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.tablebase.TableColumnHeaderChangeListener;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.control.treetable.TreeFilteringUpdateEvent;
import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.column.alignment.ColumnAlignment;
import de.vw.paso.client.stueckliste.efs.display.strategy.Filter;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.ExpandCollapseUtil;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.IEfsElementResolverListener;
import lombok.Getter;
import lombok.Setter;

public abstract class SingleVehicleBaseController<TO> extends AbstractController
        implements IEfsElementResolverListener {

    protected static final String SUMMARY_MESSAGE = I18N.getString("table.row.summary");

    @Getter
    private final Map<TreeTableColumn<TO, ?>, Filter<TO>> columnToFilterMap = new HashMap<>();

    private final List<TreeItem<TO>> treeItems4Navigation = new ArrayList<>();

    private boolean isNavigateOverTreeItems = false;
    private int navigationIndex = -1;

    private BooleanProperty disablePropertyNavigateForward;
    private BooleanProperty disablePropertyNavigateBack;
    private BooleanProperty disablePropertyClearFilters;
    private BooleanProperty disablePropertyResetSorting;

    @Setter
    private Map<Class<?>, TableColumnHeaderChangeListener> bindedColumnHeaderListeners;

    protected boolean isColumnChanging = false;

    protected boolean isResettingSort = false;

    public abstract void selectElementById(Long id);

    protected abstract void initTreeTable();

    protected abstract void initTreeTableColumns();

    protected abstract CustomTreeTableView<TO> getTreeTableView();

    protected abstract AbstractTreeModel<? extends TreeItem<TO>, TO> getTreeModel();

    @SuppressWarnings("unchecked")
    public void addBindedColumnHeaderListener(TableColumnHeaderChangeListener listener) {
        bindedColumnHeaderListeners.put(getClass(), listener);

        notifyTableColumnChanged(((TreeTableView<TO>) bindedColumnHeaderListeners.get(
                SingleVehiclePartListController.class)).getVisibleLeafColumns().stream().map(TreeTableColumn::getText)
                .collect(Collectors.toList()));
    }

    public List<String> getSVPLColumnOrder() {
        List<String> cols = ((TreeTableView<TO>) bindedColumnHeaderListeners.get(
                SingleVehiclePartListController.class)).getColumns().stream().map(TreeTableColumn::getText)
                .collect(Collectors.toList());

        cols.remove(I18N.getString("treetablecolumn.gewichtknoten"));
        cols.remove(I18N.getString("treetablecolumn.cog"));
        cols.remove(I18N.getString("treetablecolumn.set"));

        return cols;
    }

    public void notifyTableColumnChanged(List<String> newColumnList) {
        isColumnChanging = true;
        bindedColumnHeaderListeners.values().forEach(c -> c.tableColumnHeaderChanged(newColumnList));
        isColumnChanging = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initTreeTable();
        initTreeTableColumns();

        EfsElementResolver.addListener(this);
        EventBus.getInstance().register(this);
    }

    @Override
    public void start() {
        super.start();

        setActionStates();
    }

    protected abstract void initSorting();

    @Override
    protected void stop() {
        super.stop();

        treeItems4Navigation.clear();
    }

    public void handleActionCollapseAllTree() {
        ExpandCollapseUtil.collapseAll(getTreeModel().getRoot());
        getTreeTableView().scrollTo(1);
    }

    public void handleActionExpandAllTree() {
        ExpandCollapseUtil.expandAll(getTreeModel().getRoot());
    }

    public void handleActionCollapseTree() {
        if (getSelectedTreeItem() == null) {
            ExpandCollapseUtil.setExpanded(getTreeTableView(), getTreeTableView().getRoot(), false, false);
        } else {
            ExpandCollapseUtil.setExpanded(getTreeTableView(), getSelectedTreeItem(), false, true);
        }
    }

    public void handleActionExpandTree() {
        if (getSelectedTreeItem() == null) {
            ExpandCollapseUtil.setExpanded(getTreeTableView(), getTreeTableView().getRoot(), true, false);
        } else {
            ExpandCollapseUtil.setExpanded(getTreeTableView(), getSelectedTreeItem(), true, true);
        }
    }

    public void setSelectedItemExpanded() {
        if (getSelectedTreeObject() != null) {
            ExpandCollapseUtil.setExpanded(getTreeTableView(), getSelectedTreeItem(), true, true);
        }
    }

    public void setSelectedItemCollapsed() {
        if (getSelectedTreeObject() != null) {
            ExpandCollapseUtil.setExpanded(getTreeTableView(), getSelectedTreeItem(), false, true);
        }
    }

    public void handleActionNavigateBack() {
        try {
            isNavigateOverTreeItems = true;

            --navigationIndex;

            setSelectedTreeItem(treeItems4Navigation.get(navigationIndex));
        } finally {
            isNavigateOverTreeItems = false;
        }
    }

    public void handleActionNavigateForward() {
        try {
            isNavigateOverTreeItems = true;

            ++navigationIndex;

            setSelectedTreeItem(treeItems4Navigation.get(navigationIndex));
        } finally {
            isNavigateOverTreeItems = false;
        }
    }

    public BooleanProperty disablePropertyNavigateBack() {
        if (disablePropertyNavigateBack == null) {
            disablePropertyNavigateBack = new SimpleBooleanProperty(true);
        }

        return disablePropertyNavigateBack;
    }

    public BooleanProperty disablePropertyNavigateForward() {
        if (disablePropertyNavigateForward == null) {
            disablePropertyNavigateForward = new SimpleBooleanProperty(true);
        }

        return disablePropertyNavigateForward;
    }

    public BooleanProperty disablePropertyClearFilters() {
        if (disablePropertyClearFilters == null) {
            disablePropertyClearFilters = new SimpleBooleanProperty(true);
        }

        return disablePropertyClearFilters;
    }

    public BooleanProperty disablePropertyResetSorting() {
        if (disablePropertyResetSorting == null) {
            disablePropertyResetSorting = new SimpleBooleanProperty(true);
        }

        return disablePropertyResetSorting;
    }

    protected <T> void initColumn(TreeTableColumn<TO, T> column,
            Callback<TreeTableColumn.CellDataFeatures<TO, T>, ObservableValue<T>> cellValueFactory,
            Callback<TreeTableColumn<TO, T>, TreeTableCell<TO, T>> cellFactory, Function<TO, Object> filterFunction,
            String columnIdentifier) {
        column.setCellValueFactory(cellValueFactory);

        if (cellFactory != null) {
            column.setCellFactory(cellFactory);
        }

        column.setEditable(false);
        column.setId(columnIdentifier);

        getColumnToFilterMap().put(column, new Filter<>(filterFunction));

        initColumnAlignment(column);
    }

    private <S, T> void initColumnAlignment(TreeTableColumn<S, T> column) {
        ColumnAlignment columnAlignment = ColumnAlignment.findByColumnName(column.getId());
        column.setStyle(columnAlignment.getAlignment());
    }

    protected void addDisabledEditKeys() {
        getTreeTableView().setOnKeyPressed(event -> {
            TreeTablePosition<TO, ?> position = getTreeTableView().getFocusModel().getFocusedCell();

            if (position != null) {
                if (!event.isControlDown() && !event.isAltDown() && !(event.getCode() == KeyCode.UP) && !(
                        event.getCode() == KeyCode.DOWN) && !(event.getCode() == KeyCode.RIGHT) && !(event.getCode()
                        == KeyCode.LEFT)) {
                    getTreeTableView().edit(position.getRow(), position.getTableColumn());
                }
            }
        });
    }

    protected void setActionStates() {
        setNavigationState();
    }

    protected void handleEfsSelected(TreeItem<TO> newValue) {
        if (newValue != null && newValue.getValue() != null) {
            addTreeItem4Navigation(newValue);
        }

        setActionStates();
    }

    protected void resetNavigation() {
        navigationIndex = -1;

        treeItems4Navigation.clear();

        setActionStates();
    }

    private TO getSelectedTreeObject() {
        if (getTreeTableView().getSelectionModel().getSelectedItem() == null) {
            return null;
        }

        return getTreeTableView().getSelectionModel().getSelectedItem().getValue();
    }

    private TreeItem<TO> getSelectedTreeItem() {
        if (getTreeTableView().getSelectionModel().isEmpty()) {
            return null;
        }

        return getTreeTableView().getSelectionModel().getSelectedItem();
    }

    private void setSelectedTreeItem(TreeItem<TO> treeItem) {
        TreeItem<TO> parent = treeItem.getParent();
        while (parent != null) {
            parent.setExpanded(true);
            parent = parent.getParent();
        }

        TreeTableView<TO> treeTableView = getTreeTableView();
        treeTableView.requestFocus();
        treeTableView.getSelectionModel().clearSelection();
        for (TreeTableColumn<TO, ?> column : treeTableView.getColumns()) {
            treeTableView.getSelectionModel().select(treeTableView.getRow(treeItem), column);
        }

        treeTableView.scrollTo(treeTableView.getRow(treeItem));
    }

    private void addTreeItem4Navigation(TreeItem<TO> newValue) {
        if (newValue == null || newValue.getValue() == null || isNavigateOverTreeItems) {
            return;
        }

        if (navigationIndex >= 0) {
            TreeItem<TO> treeItemAtCurrentIndex = treeItems4Navigation.get(navigationIndex);

            if (newValue.getValue().equals(treeItemAtCurrentIndex.getValue())) {
                return;
            }
        }

        // if the current index is not at the last position in the list
        if (treeItems4Navigation.size() != (navigationIndex + 1)) {
            Collection<TreeItem<TO>> tmpList = new ArrayList<>();

            for (int i = 0; i <= navigationIndex; i++) {
                tmpList.add(treeItems4Navigation.get(i));
            }

            treeItems4Navigation.clear();
            treeItems4Navigation.addAll(tmpList);
        }

        treeItems4Navigation.add(newValue);

        ++navigationIndex;
    }

    private void setNavigationState() {
        disablePropertyNavigateBack().set(navigationIndex <= 0);
        disablePropertyNavigateForward().set(navigationIndex + 1 == treeItems4Navigation.size());
    }

    public void handleActionClearFilters() {
        if (getTreeTableView() instanceof CustomTreeTableView<TO> treeTableView) {
            treeTableView.clearFilters();
            disablePropertyClearFilters().set(true);
        }
    }

    @Subscribe
    public void disableClearFilterButton(TreeFilteringUpdateEvent event) {
        if (getTreeTableView() instanceof CustomTreeTableView<TO> treeTableView) {
            disablePropertyClearFilters().set(!treeTableView.isFiltered());
        }
    }
}