package de.vw.paso.client.tiwh;

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;

import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.control.cell.TableCellFactory;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.explorer.vehicleconfig.converter.DateTimeStringConverter;
import de.vw.paso.client.main.ribbonmenu.tiwhrequestqueue.RibbonMenuTiWhRequestQueueEvent;
import de.vw.paso.client.main.ribbonmenu.tiwhrequestqueue.RibbonMenuTiWhRequestQueueListener;
import de.vw.paso.client.main.tab.AbstractMainTabController;
import de.vw.paso.client.stammdaten.FilteringUpdateEvent;
import de.vw.paso.client.util.converter.BooleanStringConverter;
import de.vw.paso.client.util.converter.SetStringConverter;
import de.vw.paso.client.util.icon.AdminAreaIcon;
import de.vw.paso.client.valueobject.TiWhRequestQueueVMO;
import de.vw.paso.delegate.pls.PlsRestClientHolder;

@FXController(name = "tiwh-request-queue-tab")
public class TiWhRequestQueueTabController extends AbstractMainTabController
    implements Initializable, RibbonMenuTiWhRequestQueueListener {

    @FXML
    private Tab tiWhRequestQueueTab;
    @FXML
    private CustomTableView<TiWhRequestQueueVMO> tableView;
    @FXML
    private TableColumn<TiWhRequestQueueVMO, String> colProductId;
    @FXML
    private TableColumn<TiWhRequestQueueVMO, Date> colRequestSequence;
    @FXML
    private TableColumn<TiWhRequestQueueVMO, Set<String>> colRequesterIds;
    @FXML
    private TableColumn<TiWhRequestQueueVMO, Boolean> colRequested;
    @FXML
    private TableColumn<TiWhRequestQueueVMO, Boolean> colProcessing;

    private final ObservableList<TiWhRequestQueueVMO> queue;

    private BooleanProperty disablePropertyClearFilters;

    public TiWhRequestQueueTabController() {
        queue = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        tiWhRequestQueueTab.setGraphic(new ImageView(AdminAreaIcon.TI_WH_REQUEST_QUEUE_ICON_16x16.getImage()));

        initTableColumns();
        tableView.setItems(queue);
        tableView.makeFilterable();

        loadQueue();
    }

    @Override
    public RibbonMenuTiWhRequestQueueEvent getRibbonMenuEvent() {
        return new RibbonMenuTiWhRequestQueueEvent(this);
    }

    @Override
    public Tab getControl() {
        return tiWhRequestQueueTab;
    }

    @Override
    public Parent getStyleableParent() {
        return tableView;
    }

    @Override
    public void handleActionRefresh() {
        handleActionClearFilters();
        loadQueue();
    }

    @Override
    public void handleActionClearFilters() {
        tableView.clearFilters();

        disablePropertyClearFilters().set(true);
    }

    @Override
    public BooleanProperty disablePropertyClearFilters() {
        if (disablePropertyClearFilters == null) {
            disablePropertyClearFilters = new SimpleBooleanProperty(true);
        }

        return disablePropertyClearFilters;
    }

    @Subscribe
    private void handleFilterUpdate(FilteringUpdateEvent event) {
        disablePropertyClearFilters().setValue(tableView.getColumnToPredicateDataMap().isEmpty());
    }

    private void initTableColumns() {
        colProductId.setCellValueFactory(cellData -> cellData.getValue().productIdProperty());

        colRequestSequence.setCellValueFactory(cellData -> cellData.getValue().requestSequenceProperty());
        colRequestSequence.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));

        colRequesterIds.setCellValueFactory(cellData -> cellData.getValue().requesterIdsProperty());
        colRequesterIds.setCellFactory(new TableCellFactory<>(new SetStringConverter()));

        colRequested.setCellValueFactory(cellData -> cellData.getValue().requestedProperty());
        colRequested.setCellFactory(new TableCellFactory<>(new BooleanStringConverter()));

        colProcessing.setCellValueFactory(cellData -> cellData.getValue().processingProperty());
        colProcessing.setCellFactory(new TableCellFactory<>(new BooleanStringConverter()));
    }

    private void loadQueue() {
        doAsync(() -> TiWhRequestQueueVMO.toVMOs(
            PlsRestClientHolder.getInstance().getTiWhRequestQueue().tiWhRequestQueueDTOList()), this::fillTable);
    }

    private void fillTable(Collection<TiWhRequestQueueVMO> queues) {
        String[] prevSelectedItemIds = tableView.getSelectionModel().getSelectedItems().stream()
            .map(request -> request.productIdProperty().get()).toArray(String[]::new);

        this.queue.setAll(queues);

        selectTableItems(prevSelectedItemIds);
    }

    private void selectTableItems(String[] prevSelectedItemIds) {
        if (prevSelectedItemIds.length == 0) {
            return;
        }

        int[] selectedIndices = new int[prevSelectedItemIds.length];
        List<String> configIds = tableView.getItems().stream().map(request -> request.productIdProperty().get())
            .toList();
        for (int i = 0; i < prevSelectedItemIds.length; i++) {
            selectedIndices[i] = configIds.indexOf(prevSelectedItemIds[i]);
        }

        int firstItemIndex = selectedIndices[0];
        tableView.getSelectionModel().selectIndices(firstItemIndex, selectedIndices);
        tableView.scrollTo(firstItemIndex);
        tableView.requestFocus();
    }
}
