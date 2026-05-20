package de.vw.paso.client.stammdaten;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.PasoWildCardPattern;

public abstract class AbstractMasterDataTableViewController<T> extends AbstractMasterDataController<GridPane>
    implements Initializable {

    protected static final String DIALOG_TITLE_ADD = "dialog.region.add.title";
    protected static final String DIALOG_TITLE_EDIT = "dialog.region.edit.title";

    @FXML
    protected CustomTableView<T> productTableView;

    @FXML
    private GridPane gridPaneTab;
    @FXML
    private TextField searchTextField;

    private Collection<T> items;
    private PasoWildCardPattern patternSearchTerm;

    protected abstract void doLoad(Consumer<List<T>> callback);

    protected abstract Comparator<? super T> getItemComparator();

    protected abstract boolean getFilterCriteria(T item, PasoWildCardPattern pattern);

    protected abstract void initializeView();

    protected abstract void initTableColumns();

    protected void openAddDialog(T selectedItem, Consumer<Optional<T>> callback) {
        /* This code block is only an empty stub. */
    }

    protected void openEditDialog(T selectedItem, Consumer<Optional<T>> callback) {
        /* This code block is only an empty stub. */
    }

    protected void doAdd(T newItem, Consumer<T> callback) {
        /* This code block is only an empty stub. */
    }

    protected void doEdit(T selectedItem, T newItem, Consumer<T> callback) {
        /* This code block is only an empty stub. */
    }

    protected void doDelete(T selectedItem, Runnable callback) {
        /* This code block is only an empty stub. */
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initializeView();
        initTable();

        EventBus.getInstance().register(this);
        productTableView.sceneProperty().addListener(observable -> {
            if (productTableView.sceneProperty().getValue() == null) {
                EventBus.getInstance().unregister(this);
            }
        });

        productTableView.makeFilterable();
    }

    @Override
    public void start() {
        doLoad(this::setItems);
    }

    @Override
    public GridPane getControl() {
        return gridPaneTab;
    }

    @Override
    public Parent getStyleableParent() {
        return gridPaneTab;
    }

    @Override
    public void handleActionAdd() {
        openAddDialog(getSelectedItem(), result -> result.ifPresent(newItem -> doAdd(newItem, this::handleLoadItems)));
    }

    @Override
    public void handleActionEdit() {
        openEditDialog(getSelectedItem(),
            result -> result.ifPresent(newItem -> doEdit(getSelectedItem(), newItem, this::handleLoadItems)));
    }

    @Override
    public void handleActionDelete() {
        doDelete(getSelectedItem(), () -> doLoad(this::setItems));
        productTableView.getSelectionModel().clearSelection();
    }

    @Override
    public void handleActionRefresh() {
        doLoad(this::setItems);
    }

    @Override
    public void handleActionResetFilters() {
        disablePropertyResetFilters().set(true);
        productTableView.clearFilters();
    }

    protected final List<T> getImmutableItems() {
        return new ArrayList<>(items);
    }

    protected void refreshTable() {
        refreshTable(false);
    }

    protected void refreshTable(Boolean resetSelection) {
        if (items == null) {
            return;
        }

        fillTable();

        if (resetSelection) {
            productTableView.getSelectionModel().select(null);
        }
    }

    protected T getSelectedItem() {
        return productTableView.getSelectionModel().getSelectedItem();
    }

    protected void setItems(List<T> items) {
        this.items = items;

        fillTable();
    }

    @FXML
    private void handleSearch() {
        setPatternSearchTerm(searchTextField.getText());

        refreshTable(true);
    }

    @Subscribe
    private void handleFilterUpdate(FilteringUpdateEvent event) {
        disablePropertyResetFilters().setValue(productTableView.getColumnToPredicateDataMap().isEmpty());
    }

    private void initTable() {
        initTableColumns();

        if (productTableView.isEditable()) {
            productTableView.getSelectionModel().selectedItemProperty().addListener(this::handleTableSelection);

            if (productTableView.getSelectionModel().getSelectionMode().equals(SelectionMode.MULTIPLE)) {
                productTableView.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> disablePropertyEdit().setValue(
                        productTableView.getSelectionModel().getSelectedItems().size() > 1));
            }
        }

    }

    private void fillTable() {
        ObservableList<T> filteredSortedItems = items.stream()
            .filter(item -> getFilterCriteria(item, patternSearchTerm))
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        productTableView.setItems(filteredSortedItems);
        productTableView.reapplyFilter(patternSearchTerm != null);
    }

    private void handleLoadItems(T item) {
        doLoad(this::setItems);
    }

    private void setPatternSearchTerm(String searchTerm) {
        try {
            patternSearchTerm = new PasoWildCardPattern(searchTerm);
        } catch (Exception exception) {
            handleException(exception);
        }
    }
}
