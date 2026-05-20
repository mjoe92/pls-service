package de.vw.paso.client.control.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.tablebase.TableColumnHeaderChangeListener;
import de.vw.paso.client.control.tablebase.filter.TableFilterUtils;
import de.vw.paso.client.control.tablebase.filter.panel.CustomFilterPanelFactory;
import de.vw.paso.client.control.tablebase.filter.panel.CustomTableFilterValue;
import de.vw.paso.client.stammdaten.FilteringUpdateEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.PasoPredicate;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.customfilter.CustomFilterUtil;
import de.vw.paso.client.util.customfilter.FilterPanelPredicateData;
import de.vw.paso.client.util.customfilter.PredicateData;

public class CustomTableView<S> extends TableView<S> implements TableColumnHeaderChangeListener {

    private final Map<TableColumnBase<S, ?>, PasoPredicate<S>> columnToPredicateMap;
    private final Map<TableColumnBase<S, ?>, PredicateData> columnToPredicateDataMap;
    private final Map<TableColumnBase<S, ?>, FilterPanelPredicateData> columnToFilterPanelPredicateDataMap;

    private FilteredList<? extends S> filteredList;
    private PasoPredicate<S> lastSetPredicate;

    public CustomTableView() {
        this.columnToPredicateMap = new HashMap<>();
        this.columnToPredicateDataMap = new HashMap<>();
        this.columnToFilterPanelPredicateDataMap = new HashMap<>();

        setFixedCellSize(24);
        setOnKeyPressed(this::handleKeyEvent);
        setPlaceholder(new Label(I18N.getString("no.data")));
    }

    public void makeHeaderWrappable() {
        for (TableColumn<S, ?> sTableColumn : getColumns()) {
            makeHeaderWrappable(sTableColumn);
        }
    }

    public void makeHeaderWrappable(TableColumn<S, ?> column) {
        if (column.getColumns().isEmpty()) {
            TableFilterUtils.makeHeaderWrappable(column);
            return;
        }

        for (TableColumn<S, ?> childColumn : column.getColumns()) {
            makeHeaderWrappable(childColumn);
        }
    }

    public void makeFilterable() {
        for (TableColumn<S, ?> sTableColumn : getColumns()) {
            makeFilterable(sTableColumn);
        }
    }

    public void makeFilterable(TableColumn<S, ?> col) {
        if (col.getColumns().isEmpty()) {
            TableFilterUtils.makeFilterable(col, this::openPopup);
            return;
        }

        for (TableColumn<S, ?> sTableColumn : col.getColumns()) {
            makeFilterable(sTableColumn);
        }
    }

    public void filter(TableColumnBase<S, ?> column) {
        openPopup(column);
    }

    public void filterContent(TableColumnBase<S, ?> column, String filterText,
            ObservableList<CustomTableFilterValue> backingList, ObservableList<CustomTableFilterValue> visibleItems) {
        lastSetPredicate = null;
        getSelectionModel().clearSelection();

        if (backingList == null) {
            columnToFilterPanelPredicateDataMap.remove(column);
        }

        if (filterText.isEmpty() && (CustomFilterUtil.noSelectedItems(backingList) || CustomFilterUtil.allItemsSelected(
                backingList, visibleItems))) {

            columnToPredicateMap.remove(column);
            columnToPredicateDataMap.remove(column);
            TableFilterUtils.removeFilter(column);

            if (columnToPredicateMap.isEmpty()) {
                filteredList.setPredicate(null);
            } else {
                TableColumnBase<S, ?> columnToPredicate = columnToPredicateMap.keySet().iterator().next();
                if (columnToPredicate != null) {
                    PasoPredicate<S> predicate = columnToPredicateMap.get(columnToPredicate);
                    filteredList.setPredicate(predicate.andAll(columnToPredicateMap));
                    lastSetPredicate = predicate.andAll(columnToPredicateMap);
                }
            }
        } else {
            updatePredicateMaps(column, filterText, backingList, visibleItems);
        }

        EventBus.getInstance().post(new FilteringUpdateEvent());
        refresh();
    }

    public void reapplyFilter(boolean hasWildCardApplied) {
        if (filteredList == null) {
            return;
        }

        ObservableList<? extends S> source = hasWildCardApplied ? getItems() : filteredList.getSource();
        filteredList = new FilteredList<>(source);
        filteredList.setPredicate(lastSetPredicate);

        SortedList<S> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(comparatorProperty());
        setItems(sortedList);
    }

    public void clearFilters() {
        if (filteredList == null) {
            return;
        }

        lastSetPredicate = null;
        columnToPredicateMap.clear();
        columnToPredicateDataMap.clear();
        columnToFilterPanelPredicateDataMap.clear();
        removeFilters();

        filteredList = new FilteredList<>(filteredList.getSource());
        filteredList.setPredicate(null);

        SortedList<S> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(comparatorProperty());
        setItems(sortedList);
    }

    public void removeFilter(TableColumn<S, ?> column) {
        if (column.getColumns().isEmpty()) {
            TableFilterUtils.removeFilter(column);
            return;
        }

        for (TableColumn<S, ?> childColumn : column.getColumns()) {
            removeFilter(childColumn);
        }
    }

    public FilteredList<? extends S> getFilteredList() {
        return filteredList;
    }

    public Map<TableColumnBase<S, ?>, PredicateData> getColumnToPredicateDataMap() {
        return columnToPredicateDataMap;
    }

    @Override
    public void tableColumnHeaderChanged(final List<String> newColumnList) {
        for (TableColumn<S, ?> column : getColumns()) {
            column.setVisible(newColumnList.contains(column.getText()));
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CustomTableViewSkin<>(this);
    }

    private void handleKeyEvent(KeyEvent keyEvent) {
        if (!isEditable()) {
            return;
        }

        KeyCode keyCode = keyEvent.getCode();
        if (!getSelectionModel().isEmpty() && (keyCode.isDigitKey() || keyCode.isLetterKey()
                || keyCode.isWhitespaceKey())) {
            TablePosition<S, ?> pos = getSelectionModel().getSelectedCells().getFirst();
            edit(pos.getRow(), pos.getTableColumn());
        }
    }

    private void openPopup(TableColumnBase<S, ?> column) {
        if (filteredList == null || filteredList.isEmpty()) {
            filteredList = new FilteredList<>(getItems());
            SortedList<S> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(comparatorProperty());
            setItems(sortedList);
        }

        CustomFilterPanelFactory.createMenuItem(column, getItems(), this, columnToPredicateDataMap,
                columnToFilterPanelPredicateDataMap);
    }

    private void updatePredicateMaps(TableColumnBase<S, ?> column, String filterText,
            ObservableList<CustomTableFilterValue> backingList, ObservableList<CustomTableFilterValue> visibleItems) {
        if (CustomFilterUtil.allItemsSelected(backingList, visibleItems)) {
            for (CustomTableFilterValue filterValueObject : backingList) {
                if (!filterText.isEmpty() && !visibleItems.contains(filterValueObject)) {
                    filterValueObject.selectedProperty().set(false);
                }
            }
        }

        PasoPredicate<S> predicate = CustomFilterUtil.createPredicate(column, backingList, columnToPredicateDataMap);
        columnToPredicateMap.remove(column);
        columnToFilterPanelPredicateDataMap.remove(column);
        filteredList.setPredicate(predicate.andAll(columnToPredicateMap));
        lastSetPredicate = predicate.andAll(columnToPredicateMap);
        columnToPredicateMap.put(column, predicate);

        TableFilterUtils.setFilter(column);
    }

    private void removeFilters() {
        for (TableColumn<S, ?> column : getColumns()) {
            removeFilter(column);
        }
    }
}