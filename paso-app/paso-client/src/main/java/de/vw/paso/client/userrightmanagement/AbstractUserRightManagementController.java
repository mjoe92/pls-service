package de.vw.paso.client.userrightmanagement;

import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.main.ribbonmenu.usermanagement.RibbonMenuUserManagementListener;
import de.vw.paso.client.stammdaten.FilteringUpdateEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.PasoWildCardPattern;

public abstract class AbstractUserRightManagementController<T> extends BaseController<GridPane>
        implements RibbonMenuUserManagementListener {

    @FXML
    private GridPane gridPaneTab;
    @FXML
    private TextField searchTextField;
    @FXML
    protected CustomTableView<T> tableView;

    private BooleanProperty disablePropertyRefresh;
    private BooleanProperty disablePropertyResetFilters;

    private Collection<T> items;
    private PasoWildCardPattern patternSearchTerm;

    protected abstract void doLoad(Consumer<List<T>> callback);

    protected abstract Comparator<? super T> getItemComparator();

    protected abstract boolean getFilterCriteria(T item, PasoWildCardPattern pattern);

    protected abstract void initializeView();

    protected abstract void initTableColumns();

    @Override
    public GridPane getControl() {
        return gridPaneTab;
    }

    @Override
    public Parent getStyleableParent() {
        return gridPaneTab;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initializeView();
        initTableColumns();

        EventBus.getInstance().register(this);
        tableView.sceneProperty().addListener(observable -> {
            if (tableView.sceneProperty().getValue() == null) {
                EventBus.getInstance().unregister(this);
            }
        });

        tableView.makeFilterable();
    }

    @Override
    public void start() {
        doLoad(this::setItems);
    }

    @Override
    public void handleActionRefresh() {
        doLoad(this::setItems);
    }

    @Override
    public void handleActionClearFilters() {
        disablePropertyClearFilters().set(true);
        tableView.clearFilters();
    }

    @Override
    public BooleanProperty disablePropertyRefresh() {
        if (disablePropertyRefresh == null) {
            disablePropertyRefresh = new SimpleBooleanProperty(false);
        }

        return disablePropertyRefresh;
    }

    @Override
    public BooleanProperty disablePropertyClearFilters() {
        if (disablePropertyResetFilters == null) {
            disablePropertyResetFilters = new SimpleBooleanProperty(true);
        }

        return disablePropertyResetFilters;
    }

    @FXML
    private void handleSearch() {
        setPatternSearchTerm(searchTextField.getText());
        if (items != null) {
            fillTable(items);
        }
    }

    private void setItems(List<T> items) {
        this.items = items;

        fillTable(items);
    }

    private void fillTable(Collection<T> items) {
        //todo: not good -> this can be changed when users on top removed or added -> better use interface with id return for T
        int prevSelectedItemIndex = tableView.getSelectionModel().getSelectedIndex();

        Collection<T> filteredItems = items.stream().filter(item -> getFilterCriteria(item, patternSearchTerm))
                .sorted(getItemComparator()).toList();
        tableView.setItems(FXCollections.observableArrayList(filteredItems));

        tableView.reapplyFilter(patternSearchTerm != null);

        tableView.getSelectionModel().select(prevSelectedItemIndex);
        tableView.scrollTo(prevSelectedItemIndex);
        tableView.requestFocus();
    }

    private void setPatternSearchTerm(String searchTerm) {
        try {
            patternSearchTerm = new PasoWildCardPattern(searchTerm);
        } catch (Exception exception) {
            handleException(exception);
        }
    }

    @Subscribe
    private void handleFilterUpdate(FilteringUpdateEvent event) {
        disablePropertyClearFilters().setValue(tableView.getColumnToPredicateDataMap().isEmpty());
    }
}