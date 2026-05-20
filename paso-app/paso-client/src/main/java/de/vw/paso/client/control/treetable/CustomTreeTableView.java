package de.vw.paso.client.control.treetable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.TreeTableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.tablebase.TableColumnHeaderChangeListener;
import de.vw.paso.client.control.tablebase.filter.TableFilterUtils;
import de.vw.paso.client.control.tablebase.filter.panel.CustomFilterPanelFactory;
import de.vw.paso.client.control.tablebase.filter.panel.CustomTableFilterValue;
import de.vw.paso.client.control.tablebase.tableconfig.TableColumnSelectionUtils;
import de.vw.paso.client.model.tree.AbstractFlatTreeItem;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.PasoPredicate;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.ExpandCollapseUtil;
import de.vw.paso.client.util.customfilter.CustomFilterUtil;
import de.vw.paso.client.util.customfilter.CustomTreeTableUtil;
import de.vw.paso.client.util.customfilter.FilterPanelPredicateData;
import de.vw.paso.client.util.customfilter.PredicateData;
import de.vw.paso.service.tableconfig.TableConfigDTO;

public class CustomTreeTableView<S> extends TreeTableView<S> implements TableColumnHeaderChangeListener {

    private final Map<TableColumnBase<TreeItem<S>, ?>, PasoPredicate<TreeItem<S>>> columnToPredicateMap;
    private final Map<TableColumnBase<TreeItem<S>, ?>, PredicateData> columnPredicateDataMap;
    private final Map<TableColumnBase<TreeItem<S>, ?>, FilterPanelPredicateData> columnToFilterPanelPredicateDataMap;
    private final Collection<Runnable> filterChangeListeners;

    private Collection<TreeItem<S>> treeItems;
    private PasoPredicate<TreeItem<S>> lastSetPredicate;
    private PasoPredicate<TreeItem<S>> customPredicate;

    public CustomTreeTableView() {
        columnToPredicateMap = new HashMap<>();
        columnPredicateDataMap = new HashMap<>();
        columnToFilterPanelPredicateDataMap = new HashMap<>();
        filterChangeListeners = new ArrayList<>();

        setFixedCellSize(24);
        setOnKeyPressed(this::handleKeyEvent);
        setPlaceholder(new Label(I18N.getString("no.data")));
    }

    public Map<TableColumnBase<TreeItem<S>, ?>, PasoPredicate<TreeItem<S>>> getColumnToPredicateMap() {
        return columnToPredicateMap;
    }

    public TableConfigDTO getColumnConfig() {
        return (TableConfigDTO) getProperties().get(TableColumnSelectionUtils.COLUMN_CONFIG);
    }

    public boolean isFiltered() {
        return !columnToPredicateMap.isEmpty();
    }

    public void addFilterChangeListener(Runnable listener) {
        filterChangeListeners.add(listener);
    }

    public void makeHeaderWrappable() {
        for (TreeTableColumn<S, ?> column : getColumns()) {
            makeHeaderWrappable(column);
        }
    }

    public void makeHeaderWrappable(TreeTableColumn<S, ?> col) {
        if (col.getColumns().isEmpty()) {
            TableFilterUtils.makeHeaderWrappable(col);
            return;
        }

        for (TreeTableColumn<S, ?> sTreeTableColumn : col.getColumns()) {
            makeHeaderWrappable(sTreeTableColumn);
        }
    }

    public void setHeaderHeight(double height) {
        for (TreeTableColumn<S, ?> column : getColumns()) {
            setHeaderHeight(column, height);
        }
    }

    public void makeFilterable() {
        for (TreeTableColumn<S, ?> column : getColumns()) {
            makeFilterable(column);
        }
    }

    public void makeFilterable(TreeTableColumn<S, ?> column) {
        if (column.getColumns().isEmpty()) {
            TableFilterUtils.makeFilterable(column, this::openPopup);
            return;
        }

        for (TreeTableColumn<S, ?> childColumn : column.getColumns()) {
            makeFilterable(childColumn);
        }
    }

    public void filter(TableColumnBase<TreeItem<S>, ?> column) {
        openPopup(column);
    }

    public void setColGraphicWidthBindingFor2NestedColumns(TreeTableColumn<?, ?> column) {
        if (column.getGraphic() == null) {
            return;
        }

        if (column.getGraphic() instanceof Region region) {
            if (column.getColumns() == null || column.getColumns().isEmpty()) {
                region.prefWidthProperty().bind(column.widthProperty().subtract(5));
                return;
            }

            region.prefWidthProperty().bind(column.getColumns().getFirst().widthProperty()
                    .add(column.getColumns().get(1).widthProperty()));
        }
    }

    public void setHeaderHeight(TreeTableColumn<S, ?> column, double height) {
        if (column.getGraphic() == null) {
            Label label = new Label(column.getText());
            label.setStyle("-fx-padding: 4px;");
            label.setAlignment(Pos.CENTER);
            label.setTextAlignment(TextAlignment.CENTER);
            label.setPrefWidth(column.getPrefWidth());
            label.prefWidthProperty().bind(column.widthProperty());
            label.setPrefHeight(height);

            column.setGraphic(label);
            return;
        }

        if (column.getGraphic() instanceof Region region) {
            region.setPrefHeight(height);
        }
    }

    public void filterContent(TableColumnBase<TreeItem<S>, ?> column, String filterText,
            ObservableList<CustomTableFilterValue> backingList, ObservableList<CustomTableFilterValue> visibleItems) {

        getSelectionModel().clearSelection();
        if (backingList == null) {
            lastSetPredicate = null;
            columnToFilterPanelPredicateDataMap.remove(column);
        }

        TreeItem<S> root = getRoot();
        if (filterText.isEmpty() && (CustomFilterUtil.noSelectedItems(backingList) || CustomFilterUtil.allItemsSelected(
                backingList, visibleItems))) {
            updatePredicateMaps(column, root);
        } else {
            updatePredicateMaps(column, filterText, backingList, visibleItems, root);
        }
        expandOrCollapseAllIfColumnToPredicateNotEmpty(root);

        EventBus.getInstance().post(new TreeFilteringUpdateEvent());
        treeItems.clear();
        refresh();

        for (Runnable filterChangeListener : filterChangeListeners) {
            filterChangeListener.run();
        }
    }

    public void clearLastStoredFilter() {
        if (columnToPredicateMap.isEmpty()) {
            lastSetPredicate = null;
        }
    }

    public void clearFilters() {
        lastSetPredicate = null;
        columnToPredicateMap.clear();
        columnPredicateDataMap.clear();
        columnToFilterPanelPredicateDataMap.clear();
        removeFilters();

        TreeItem<S> root = getRoot();
        if (root instanceof FilterableTreeItem<S> filterableTreeItemRoot) {
            filterableTreeItemRoot.predicateProperty().set(combine(null, customPredicate));
        }

        ExpandCollapseUtil.collapseAll(root);
        ExpandCollapseUtil.setExpanded(this, this.getRoot(), true, false);
    }

    public void removeFilter(TreeTableColumn<S, ?> col) {
        if (col.getColumns().isEmpty()) {
            TableFilterUtils.removeFilter(col);
            return;
        }

        for (TreeTableColumn<S, ?> sTreeTableColumn : col.getColumns()) {
            removeFilter(sTreeTableColumn);
        }
    }

    public void reapplyFilters() {
        FilterableTreeItem<S> root = (FilterableTreeItem<S>) getRoot();
        root.predicateProperty().setValue(combine(lastSetPredicate, customPredicate));
        if (root instanceof AbstractFlatTreeItem<S>) {
            specialCaseHandling(root);
            ExpandCollapseUtil.expandAll(root);
        } else if (root.getValue() instanceof InspectorTreeItemObject) {
            specialCaseHandling(root);
        }
    }

    public void scrollToCenter(int rowIndex) {
        TreeTableViewSkin<?> ts = (TreeTableViewSkin<?>) getSkin();
        VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(1);
        if (vf.getFirstVisibleCell() != null && vf.getLastVisibleCell() != null) {
            int start = vf.getFirstVisibleCell().getIndex();
            int end = vf.getLastVisibleCell().getIndex();
            int visibleRows = end - start;
            int scrollTo = rowIndex - visibleRows / 2;

            scrollTo(scrollTo);
        }
    }

    public void scrollToCenter(TreeItem<S> treeItem) {
        scrollToCenter(getRow(treeItem));
    }

    @Override
    public void tableColumnHeaderChanged(List<String> newColumnList) {
        for (TreeTableColumn<S, ?> column : getColumns()) {
            column.setVisible(newColumnList.contains(column.getText()));
        }
    }

    public void setCustomPredicate(PasoPredicate<TreeItem<S>> customPredicate) {
        this.customPredicate = customPredicate;
        reapplyFilters();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CustomTreeTableViewSkin<>(this);
    }

    private void openPopup(TableColumnBase<TreeItem<S>, ?> column) {
        if (treeItems == null || treeItems.isEmpty()) {
            List<TreeItem<S>> abstractTreeItemList = new ArrayList<>();
            CustomTreeTableUtil.collectTreeItemsFromRoot((FilterableTreeItem<S>) getRoot(), abstractTreeItemList,
                    columnToPredicateMap.isEmpty(), columnPredicateDataMap, column);

            treeItems = abstractTreeItemList;
        }

        CustomFilterPanelFactory.createMenuItem(column, treeItems, this, columnPredicateDataMap,
                columnToFilterPanelPredicateDataMap);
    }

    private void handleKeyEvent(KeyEvent keyEvent) {
        if (!isEditable()) {
            return;
        }

        KeyCode keyCode = keyEvent.getCode();
        if (!getSelectionModel().isEmpty() && (keyCode.isDigitKey() || keyCode.isLetterKey()
                || keyCode.isWhitespaceKey())) {
            TreeTablePosition<S, ?> pos = getSelectionModel().getSelectedCells().getFirst();
            edit(pos.getRow(), pos.getTableColumn());
        }
    }

    private void updatePredicateMaps(TableColumnBase<TreeItem<S>, ?> column, String filterText,
            ObservableList<CustomTableFilterValue> backingList, ObservableList<CustomTableFilterValue> visibleItems,
            TreeItem<S> root) {
        if (CustomFilterUtil.allItemsSelected(backingList, visibleItems)) {
            for (CustomTableFilterValue filterValueObject : backingList) {
                if (!filterText.isEmpty() && !visibleItems.contains(filterValueObject)) {
                    filterValueObject.selectedProperty().set(false);
                }
            }
        }

        PasoPredicate<TreeItem<S>> predicate = CustomFilterUtil.createPredicate(column, backingList,
                columnPredicateDataMap);
        columnToFilterPanelPredicateDataMap.remove(column);
        columnToPredicateMap.remove(column);

        PasoPredicate<TreeItem<S>> pasoPredicate = combine(predicate.andAll(columnToPredicateMap), customPredicate);
        FilterableTreeItem<S> filterableRoot = (FilterableTreeItem<S>) root;
        filterableRoot.predicateProperty().setValue(pasoPredicate);

        lastSetPredicate = predicate.andAll(columnToPredicateMap);
        columnToPredicateMap.put(column, predicate);

        TableFilterUtils.setFilter(column);

        if (filterableRoot instanceof AbstractFlatTreeItem<S> || root.getValue() instanceof InspectorTreeItemObject) {
            specialCaseHandling(filterableRoot);
        }
    }

    private void expandOrCollapseAllIfColumnToPredicateNotEmpty(TreeItem<S> root) {
        if (!columnToPredicateMap.isEmpty()) {
            ExpandCollapseUtil.expandAll(root);
            return;
        }

        ExpandCollapseUtil.collapseAll(root);
        if (root.getValue() instanceof InspectorTreeItemObject) {
            return;
        }

        ExpandCollapseUtil.setExpanded(this, root, true, false);
    }

    private void updatePredicateMaps(TableColumnBase<TreeItem<S>, ?> column, TreeItem<S> root) {
        columnToPredicateMap.remove(column);
        columnPredicateDataMap.remove(column);
        columnToFilterPanelPredicateDataMap.remove(column);
        TableFilterUtils.removeFilter(column);

        FilterableTreeItem<S> filterableRoot = (FilterableTreeItem<S>) root;
        if (columnToPredicateMap.isEmpty()) {
            filterableRoot.predicateProperty().setValue(combine(null, customPredicate));
            return;
        }

        TableColumnBase<TreeItem<S>, ?> item = columnToPredicateMap.keySet().iterator().next();
        if (item == null) {
            return;
        }

        PasoPredicate<TreeItem<S>> pasoPredicate = columnToPredicateMap.get(item);
        filterableRoot.predicateProperty()
                .setValue(combine(pasoPredicate.andAll(columnToPredicateMap), customPredicate));
        lastSetPredicate = pasoPredicate.andAll(columnToPredicateMap);

        if (filterableRoot instanceof AbstractFlatTreeItem<S>) {
            specialCaseHandling(filterableRoot);
        }

        if (filterableRoot.getValue() instanceof InspectorTreeItemObject) {
            specialCaseHandling(filterableRoot);
        }
    }

    private void specialCaseHandling(FilterableTreeItem<S> root) {
        if (root.getValue() instanceof InspectorTreeItemObject) {
            for (TreeItem<S> child : root.getChildren()) {
                FilterableTreeItem<S> filterableChild = (FilterableTreeItem<S>) child;
                if (filterableChild.getFilteredChildren().isEmpty()) {
                    filterableChild.predicateProperty().setValue(combine(treeItem -> true, customPredicate));
                    continue;
                }

                specialCaseHandling(filterableChild);
            }

            return;
        }

        for (TreeItem<S> child : root.getChildren()) {
            FilterableTreeItem<S> filterableChild = (FilterableTreeItem<S>) child;
            if (filterableChild.getFilteredChildren().isEmpty()) {
                filterableChild.predicateProperty().setValue(combine(treeItem -> true, customPredicate));
            }
        }
    }

    private void removeFilters() {
        for (TreeTableColumn<S, ?> column : getColumns()) {
            removeFilter(column);
        }
    }

    private PasoPredicate<TreeItem<S>> combine(PasoPredicate<TreeItem<S>> first, PasoPredicate<TreeItem<S>> second) {
        if (first == null && second == null) {
            return null;
        }

        if (first == null) {
            return second;
        }

        if (second == null) {
            return first;
        }

        return o -> first.test(o) && second.test(o);
    }
}