package de.vw.paso.client.smartfix;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;

import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.main.ribbonmenu.smartfix.RibbonMenuSmartFixEvent;
import de.vw.paso.client.main.ribbonmenu.smartfix.RibbonMenuSmartFixTabListener;
import de.vw.paso.client.main.tab.AbstractMainTabController;
import de.vw.paso.client.stammdaten.FilteringUpdateEvent;
import de.vw.paso.delegate.stueckliste.smartfix.SmartFixRestClientHolder;
import de.vw.paso.service.partlist.smartfix.SmartFixDTO;
import org.apache.commons.lang3.SerializationUtils;

@FXController(name = "smart-fix-tab")
public class SmartFixTabController extends AbstractMainTabController implements RibbonMenuSmartFixTabListener {

    @FXML
    private Tab smartFixTab;
    @FXML
    private CustomTableView<SmartFixDTO> treeView;
    @FXML
    private TableColumn<SmartFixDTO, String> nameColumn;
    @FXML
    private TableColumn<SmartFixDTO, String> activeColumn;

    private final BooleanProperty disablePropertyResetFilters;
    private final BooleanProperty disablePropertyAdd;
    private final BooleanProperty disablePropertyEdit;
    private final BooleanProperty disablePropertyDelete;
    private final BooleanProperty disablePropertySetActive;

    public SmartFixTabController() {
        disablePropertyResetFilters = new SimpleBooleanProperty(true);
        disablePropertyAdd = new SimpleBooleanProperty(false);
        disablePropertyEdit = new SimpleBooleanProperty(true);
        disablePropertyDelete = new SimpleBooleanProperty(true);
        disablePropertySetActive = new SimpleBooleanProperty(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        treeView.makeFilterable();
        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        nameColumn.setCellValueFactory(data -> {
            SmartFixDTO value = data.getValue();
            return value == null ? null : new SimpleStringProperty(value.getName());
        });

        activeColumn.setCellValueFactory(data -> {
            SmartFixDTO value = data.getValue();
            return value == null ? null : new SimpleStringProperty(toColumnValue(value.isActive()));
        });

        treeView.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldSelection, newSelection) -> buttonState());
        buttonState();
    }

    @Override
    public RibbonMenuSmartFixEvent getRibbonMenuEvent() {
        return new RibbonMenuSmartFixEvent(this);
    }

    @Override
    public void start() {
        load();
    }

    @Override
    public Tab getControl() {
        return smartFixTab;
    }

    @Override
    public Parent getStyleableParent() {
        return null;
    }

    @Override
    public void handleActionAdd() {
        SmartFixDTO smartFix = new SmartFixDTO();
        smartFix.setActive(true);

        showSmartFixDialog(smartFix, event -> addItem((SmartFixDTO) event.getSource().getValue()));
    }

    @Override
    public void handleActionEdit() {
        SmartFixDTO selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        SmartFixDTO editableClone = SerializationUtils.clone(selectedItem);
        showSmartFixDialog(editableClone, event -> updateItem((SmartFixDTO) event.getSource().getValue(), false));
    }

    @Override
    public void handleActionDelete() {
        SmartFixDTO selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        ServiceController<Void> task = new ServiceController<>();
        task.setOnFailed(e -> handleException(task.getException()));
        task.setOnSucceeded(e -> updateItem(selectedItem, true));
        task.start(() -> SmartFixRestClientHolder.getInstance().delete(selectedItem.getId()));
    }

    @Override
    public void handleActionSetActive() {
        SmartFixDTO selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        SmartFixDTO editableClone = SerializationUtils.clone(selectedItem);
        editableClone.setActive(!editableClone.isActive());

        ServiceController<SmartFixDTO> task = new ServiceController<>();
        task.setOnFailed(e -> handleException(task.getException()));
        task.setOnSucceeded(e -> updateItem(task.getValue(), false));
        task.start(() -> SmartFixRestClientHolder.getInstance().save(editableClone));
    }

    @Override
    public void handleActionRefresh() {
        load();
    }

    @Override
    public void handleActionClearFilters() {
        disablePropertyResetFilters.set(true);
        treeView.clearFilters();
    }

    @Override
    public BooleanProperty disablePropertyClearFilters() {
        return disablePropertyResetFilters;
    }

    @Override
    public ObservableValue<? extends Boolean> disablePropertyAdd() {
        return disablePropertyAdd;
    }

    @Override
    public ObservableValue<? extends Boolean> disablePropertyEdit() {
        return disablePropertyEdit;
    }

    @Override
    public ObservableValue<? extends Boolean> disablePropertyRemove() {
        return disablePropertyDelete;
    }

    @Override
    public ObservableValue<? extends Boolean> disablePropertySetActive() {
        return disablePropertyDelete;
    }

    @Subscribe
    private void handleFilterUpdate(FilteringUpdateEvent event) {
        disablePropertyResetFilters.setValue(treeView.getColumnToPredicateDataMap().isEmpty());
    }

    private void buttonState() {
        boolean notSelected = treeView.getSelectionModel().getSelectedItem() == null;

        disablePropertyDelete.set(notSelected);
        disablePropertyEdit.set(notSelected);
        disablePropertySetActive.set(notSelected);
    }

    private void load() {
        doAsync(() -> {
            Collection<SmartFixDTO> rules = SmartFixRestClientHolder.getInstance().loadAll().smartFixDTOList();
            return FXCollections.observableArrayList(rules);
        }, this::setItemsAndSelect);
    }

    private void addItem(SmartFixDTO fix) {
        //todo: remove after correct exception throwing and handling due to PASO-1225
        if (fix.getId() != null) {
            treeView.getItems().add(fix);
        }
    }

    private void showSmartFixDialog(SmartFixDTO smartFix, EventHandler<WorkerStateEvent> onSucceedHandler) {
        SmartFixEditDialog dialog = new SmartFixEditDialog(smartFix);
        dialog.showAndWait().ifPresent(result -> {
            ServiceController<SmartFixDTO> task = new ServiceController<>();
            task.setOnFailed(e -> dialog.handleException(task.getException()));
            task.setOnSucceeded(onSucceedHandler);
            task.start(() -> SmartFixRestClientHolder.getInstance().save(result));
        });
    }

    private void updateItem(SmartFixDTO fix, boolean delete) {
        if (fix.getId() == null) {
            return;
        }

        ObservableList<SmartFixDTO> items = treeView.getItems();
        for (int i = 0; i < items.size(); i++) {
            SmartFixDTO smartFix = items.get(i);
            if (smartFix.getId().equals(fix.getId())) {
                if (delete) {
                    items.remove(i);
                } else {
                    items.set(i, fix);
                }

                return;
            }
        }
    }

    private String toColumnValue(Boolean active) {
        if (active == null) {
            return null;
        }

        String messageKey = active ? "yes" : "no";
        return I18N.getString(messageKey);
    }

    private void setItemsAndSelect(ObservableList<SmartFixDTO> rows) {
        SmartFixDTO selectedItem = treeView.getSelectionModel().getSelectedItem();

        treeView.setItems(rows);

        selectTableItems(selectedItem);
    }

    private void selectTableItems(SmartFixDTO prevSelectedItem) {
        if (prevSelectedItem == null) {
            return;
        }

        List<SmartFixDTO> items = treeView.getItems();
        for (int i = 0; i < items.size(); i++) {
            SmartFixDTO item = items.get(i);
            if (item.getId().equals(prevSelectedItem.getId())) {
                treeView.getSelectionModel().select(i);
                treeView.scrollTo(i);
                treeView.requestFocus();

                return;
            }
        }
    }
}
