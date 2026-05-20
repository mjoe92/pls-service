package de.vw.paso.client.stueckliste.efs.views.aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import de.vw.paso.client.base.AbstractDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.service.PollingServiceController;
import de.vw.paso.client.control.cell.CellUtils;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.delegate.pls.PlsRestClientHolder;
import de.vw.paso.partlist.dto.EfsElementAggregateMappingDTO;
import de.vw.paso.pls.EfsAggregateInformationDTO;
import de.vw.paso.pls.PartListStatus;
import de.vw.paso.pls.ProductDataDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.pls.SubPartListRequestDTO;
import de.vw.paso.utility.DateUtil;
import de.vw.paso.utility.EfsElementUtil;
import de.vw.paso.utility.Pair;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregateTabController extends AbstractDialogController<List<Pair<RowAction, EfsElementDTO>>> {

    private static final Logger LOG = LoggerFactory.getLogger(AggregateTabController.class);

    private static final int DIALOG_WITH = 850;

    private final ObservableList<AggregateRowObject> rows;
    private final Node replaceButton;
    private final Node requestButton;
    private final CustomTableView<AggregateRowObject> customTableView;

    private final BooleanProperty statusDataLoaded;

    private final Map<String, ObservableList<RowAction>> actionsByAggregate;
    private final Map<String, RowAction> actionByProductDataId;
    private final Map<Long, EfsElementAggregateMappingDTO> efsAggregateMappingsById;

    private ButtonType replaceType;

    private PollingServiceController<?> statusUpdater;

    private Map<String, Integer> positionByAggregateMap;

    private boolean preselect;
    private boolean updateItem;

    public AggregateTabController(List<EfsElementDTO> aggregateElements) {
        statusDataLoaded = new SimpleBooleanProperty(false);
        actionsByAggregate = new HashMap<>();
        actionByProductDataId = new HashMap<>();
        efsAggregateMappingsById = new HashMap<>();
        positionByAggregateMap = new HashMap<>();

        setTitle(I18N.getString("aggregate"));
        getDialogPane().setHeader(createHeader());
        setResultConverter(param -> replaceType.equals(param) ? getElementsToReplace() : null);

        customTableView = createTable();
        rows = FXCollections.observableArrayList(aggregateElements.stream().map(element -> {
            AggregateRowObject row = new AggregateRowObject(element);
            row.rowActionProperty().addListener((e, o, n) -> validateAndRefresh());
            return row;
        }).toList());
        customTableView.setItems(rows);

        replaceType = new ButtonType(I18N.getString("replace"), ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().add(replaceType);
        replaceButton = getDialogPane().lookupButton(replaceType);
        replaceButton.setDisable(true);

        ButtonType requestButtonType = new ButtonType(I18N.getString("request"), ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().add(requestButtonType);
        requestButton = getDialogPane().lookupButton(requestButtonType);
        requestButton.setDisable(true);
        requestButton.addEventFilter(ActionEvent.ACTION, this::requestPartList);

        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        getDialogPane().setContent(customTableView);

        setWidth(DIALOG_WITH);
        setHeight(350);

        statusDataLoaded.addListener((observable, oldValue, newValue) -> {
            if (!oldValue && newValue) {
                startUpdateTask();
            }
        });

        loadAvailableData(rows);

        Window window = getDialogPane().getScene().getWindow();
        window.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> stopUpdateTask());

        Node cancelButton = getDialogPane().lookupButton(ButtonType.CLOSE);
        cancelButton.addEventFilter(ActionEvent.ACTION, event -> stopUpdateTask());
        replaceButton.addEventFilter(ActionEvent.ACTION, event -> stopUpdateTask());
    }

    private <T extends Event> void requestPartList(T t) {
        t.consume();

        doAsync(() -> {
            for (AggregateRowObject row : rows) {
                RowAction rowAction = row.rowActionProperty().get();
                if (rowAction == null || !rowAction.isRequestNew()) {
                    continue;
                }

                EfsElementDTO efsElement = row.getElement();
                ProductDataDTO dto = new ProductDataDTO();
                try {
                    LOG.info("Request data for {}", efsElement.getAggregate());

                    dto = PlsRestClientHolder.getInstance().requestSubPartList(
                            new SubPartListRequestDTO(efsElement.getId(), efsElement.getAggregate()));

                    efsAggregateMappingsById.put(efsElement.getId(),
                            new EfsElementAggregateMappingDTO(efsElement.getId(), dto.getId(), null, null));

                    LOG.info("product data id is: {}", dto.getId());
                } catch (Exception e) {
                    dto.setStatus(PartListStatus.ERROR);
                    handleException(e);
                }
                RowAction newAction = new RowAction(dto, I18N.getString("aggregate.column.action.description"));
                addAction(efsElement.getAggregate(), newAction);
                row.rowActionProperty().set(newAction);
            }
        }, customTableView::refresh);
    }

    private Node createHeader() {
        Label headerLabel = new Label();
        headerLabel.setText(I18N.getString("aggregate.header"));
        BorderPane.setAlignment(headerLabel, Pos.CENTER_LEFT);

        BorderPane headerPane = new BorderPane();
        headerPane.getStyleClass().add("header-panel");
        headerPane.setPrefSize(DIALOG_WITH, 80);
        headerPane.setCenter(headerLabel);
        return headerPane;
    }

    private CustomTableView<AggregateRowObject> createTable() {
        CustomTableView<AggregateRowObject> customTableView = new CustomTableView<>();
        customTableView.setEditable(true);
        customTableView.setPadding(new Insets(0));

        TableColumn<AggregateRowObject, String> aggregateColumn = new TableColumn<>(
                I18N.getString("aggregate.column.aggregate"));
        aggregateColumn.setCellValueFactory(e -> new SimpleObjectProperty<>(e.getValue().getElement().getAggregate()));
        customTableView.getColumns().add(aggregateColumn);

        TableColumn<AggregateRowObject, String> partNumberColumn = new TableColumn<>(
                I18N.getString("aggregate.column.partnumber"));
        partNumberColumn.setCellValueFactory(e -> new SimpleObjectProperty<>(
                EfsElementUtil.convertPartNumberString(e.getValue().getElement().getEfsElementMara().getPartNumber())));
        customTableView.getColumns().add(partNumberColumn);

        TableColumn<AggregateRowObject, String> descriptionColumn = new TableColumn<>(
                I18N.getString("aggregate.column.description"));
        descriptionColumn.setCellValueFactory(
                e -> new SimpleObjectProperty<>(e.getValue().getElement().getDescription1()));
        customTableView.getColumns().add(descriptionColumn);

        TableColumn<AggregateRowObject, String> description2Column = new TableColumn<>(
                I18N.getString("aggregate.column.description2"));
        description2Column.setCellValueFactory(
                e -> new SimpleObjectProperty<>(e.getValue().getElement().getEfsElementMara().getDescription2De()));
        customTableView.getColumns().add(description2Column);

        TableColumn<AggregateRowObject, RowAction> replaceColumn = new TableColumn<>(
                I18N.getString("aggregate.column.selected"));
        replaceColumn.editableProperty().set(true);
        replaceColumn.setCellValueFactory(e -> e.getValue().rowActionProperty());
        replaceColumn.setCellFactory(param -> {
            ComboBox<RowAction> combo = new ComboBox<>();
            CellUtils.setSize(combo);

            TableCell<AggregateRowObject, RowAction> cell = new TableCell<>() {

                @Override
                protected void updateItem(RowAction action, boolean empty) {
                    updateItem = true;
                    super.updateItem(action, empty);
                    if (empty) {
                        setGraphic(null);
                        updateItem = false;
                        return;
                    }

                    combo.setValue(action);
                    setGraphic(combo);
                    TableRow<AggregateRowObject> tableRow = getTableRow();
                    if (tableRow == null) {
                        return;
                    }

                    AggregateRowObject rowItem = tableRow.getItem();
                    if (rowItem == null) {
                        return;
                    }

                    ObservableList<RowAction> possibleActions = actionsByAggregate.get(
                            rowItem.getElement().getAggregate());
                    if (possibleActions != null) {
                        combo.getItems().setAll(possibleActions);
                    }

                    EfsElementAggregateMappingDTO aggMapping = efsAggregateMappingsById.get(
                            rowItem.getElement().getId());
                    if (aggMapping != null && aggMapping.importDate() != null) {
                        setEditable(false);
                        combo.setValue(
                                new RowAction(DateUtil.formatDate(aggMapping.importDate(), "dd.MM.YYYY"), false));
                        combo.setDisable(true);
                    } else if (!isValidProductKey(rowItem.getElement().getAggregate())) {
                        setEditable(false);
                        combo.setDisable(true);
                    } else {
                        setEditable(true);
                        combo.setDisable(false);
                    }

                    updateItem = false;
                }
            };

            combo.setOnShown(event -> stopUpdateTask());
            combo.setOnHidden(event -> startUpdateTask());
            combo.setOnAction(e -> {
                if (!updateItem) {
                    customTableView.getItems().get(cell.getIndex()).rowActionProperty().set(combo.getValue());
                }
            });

            cell.getStyleClass().add("combo-box-table-cell");

            return cell;
        });
        customTableView.getColumns().add(replaceColumn);

        TableColumn<AggregateRowObject, String> availableColumn = new TableColumn<>(
                I18N.getString("aggregate.column.available"));
        availableColumn.setCellValueFactory(getStatusCellFactory());
        customTableView.getColumns().add(availableColumn);

        TableColumn<AggregateRowObject, String> positionColumn = new TableColumn<>(
                I18N.getString("aggregate.column.position"));
        positionColumn.setCellValueFactory(getPositionCellFactory());
        customTableView.getColumns().add(positionColumn);

        return customTableView;
    }

    private void validateAndRefresh() {
        boolean disableReplace = true;
        boolean disableRequest = true;
        for (AggregateRowObject row : rows) {
            RowAction rowAction = row.rowActionProperty().get();
            if (rowAction == null) {
                continue;
            }

            if (rowAction.isRequestNew()) {
                disableRequest = false;
            }

            EfsElementAggregateMappingDTO mapping = efsAggregateMappingsById.get(row.getElement().getId());
            if ((mapping == null || mapping.importDate() == null) && rowAction.isUseExising()
                    && PartListStatus.READY == rowAction.getProductData().getStatus()) {
                disableReplace = false;
            }
        }

        replaceButton.setDisable(disableReplace);
        requestButton.setDisable(disableRequest);

        customTableView.refresh();
    }

    private void loadAvailableData(List<AggregateRowObject> rows) {
        Map<String, AggregateRowObject> rowByAggregate = new HashMap<>(rows.size());
        List<String> productKeysToLoad = new ArrayList<>(rows.size());
        List<String> efsElementIds = new ArrayList<>(rows.size());
        List<String> validProductKeys = new ArrayList<>(rows.size());
        for (AggregateRowObject row : rows) {
            Long id = row.getElement().getId();
            String aggregate = row.getElement().getAggregate();

            efsElementIds.add(id.toString());
            rowByAggregate.put(aggregate, row);
            EfsElementAggregateMappingDTO aggMapping = efsAggregateMappingsById.get(id);
            if (aggMapping == null || aggMapping.importDate() == null) {
                productKeysToLoad.add(aggregate);
                if (isValidProductKey(aggregate)) {
                    validProductKeys.add(aggregate);
                }
            }
        }

        doAsync(() -> PlsRestClientHolder.getInstance().getAggregateInformation(efsElementIds, validProductKeys),
                result -> addAggregates(result, productKeysToLoad, rowByAggregate));
    }

    private void addAggregates(EfsAggregateInformationDTO result, List<String> productKeysToLoad,
            Map<String, AggregateRowObject> rowByAggregate) {
        Collection<ProductDataDTO> productData = result.productData();
        positionByAggregateMap = result.requestPositions();
        for (EfsElementAggregateMappingDTO element : result.aggregateMappings()) {
            efsAggregateMappingsById.put(element.efsElementId(), element);
        }

        for (String productKey : productKeysToLoad) {
            actionsByAggregate.computeIfAbsent(productKey, k -> {
                ObservableList<RowAction> actionList = FXCollections.observableArrayList();
                actionList.add(new RowAction(I18N.getString("aggregate.column.action.no.selected"), false));
                actionList.add(new RowAction(I18N.getString("aggregate.column.action.request"), isValidProductKey(k)));
                return actionList;
            });
        }

        /*
         * Preselect 'Request new' only the first time, after the dialog opens.
         */
        if (!preselect) {
            preselect = true;

            Collection<String> ids = new HashSet<>(productData.size());
            for (ProductDataDTO product : productData) {
                ids.add(product.getProductId());
            }

            for (String key : rowByAggregate.keySet()) {
                if (ids.contains(key)) {
                    continue;
                }

                EfsElementDTO element = rowByAggregate.get(key).getElement();
                EfsElementAggregateMappingDTO aggMapping = efsAggregateMappingsById.get(element.getId());
                if (aggMapping == null || aggMapping.importDate() == null) {
                    rowByAggregate.get(key).rowActionProperty().set(actionsByAggregate.get(key).get(1));
                }
            }
        }

        for (ProductDataDTO dto : productData) {
            RowAction existingAction = actionByProductDataId.get(dto.getId());
            if (existingAction == null) {
                RowAction rowAction = new RowAction(dto, I18N.getString("aggregate.column.action.description"));
                addAction(dto.getProductId(), rowAction);

                /*
                 * If there is data for today, it does not make sense to request it again.
                 * Preselect today's request if there is data, or preselect "request new" if not
                 * Therefore we remove 'request new' if there is data for today
                 */
                AggregateRowObject row = rowByAggregate.get(dto.getProductId());
                ObservableList<RowAction> actionsList = actionsByAggregate.get(dto.getProductId());

                if (checkToday(dto.getImportDate())) {
                    row.rowActionProperty().set(rowAction);

                    actionsList.removeIf(RowAction::isRequestNew);
                } else {
                    row.rowActionProperty().set(actionsByAggregate.get(row.getElement().getAggregate()).get(1));
                }

            } else {
                /*
                 * Only the status will change. So instead of changing the complete dto instance, we just update the status
                 */
                existingAction.getProductData().setStatus(dto.getStatus());
            }
        }

        statusDataLoaded.set(true);
        validateAndRefresh();
    }

    private boolean isValidProductKey(String productKey) {
        return productKey != null && !productKey.isBlank() && productKey.length() >= 3 && productKey.length() <= 4;
    }

    private boolean checkToday(Date d) {
        return DateUtils.isSameDay(new Date(), d);
    }

    private Callback<CellDataFeatures<AggregateRowObject, String>, ObservableValue<String>> getPositionCellFactory() {
        return p -> {
            AggregateRowObject row = p.getValue();
            EfsElementDTO element = row.getElement();
            RowAction rowAction = row.rowActionProperty().get();
            EfsElementAggregateMappingDTO aggMapping = efsAggregateMappingsById.get(element.getId());
            if (aggMapping != null && aggMapping.importDate() != null) {
                return new SimpleStringProperty();
            }

            if (rowAction != null && rowAction.isUseExising() && PartListStatus.PENDING == rowAction.getProductData()
                    .getStatus()) {
                if (positionByAggregateMap != null) {
                    Integer position = positionByAggregateMap.get(element.getAggregate());
                    if (position != null) {
                        return new SimpleStringProperty(position.toString());
                    }
                } else if (statusDataLoaded.get()) {
                    return new SimpleStringProperty(I18N.getString("aggregate.column.available.unknown"));
                }
            }

            return new SimpleStringProperty();
        };
    }

    private Callback<CellDataFeatures<AggregateRowObject, String>, ObservableValue<String>> getStatusCellFactory() {
        return p -> new SimpleStringProperty(getStatusText(p));
    }

    private String getStatusText(CellDataFeatures<AggregateRowObject, String> p) {
        AggregateRowObject row = p.getValue();
        EfsElementDTO element = row.getElement();
        RowAction rowAction = row.rowActionProperty().get();
        EfsElementAggregateMappingDTO aggMapping = efsAggregateMappingsById.get(element.getId());

        String statusTxt;
        if (!isValidProductKey(element.getAggregate())) {
            statusTxt = I18N.getString("aggregate.column.invalid");
        } else if (aggMapping != null && aggMapping.importDate() != null) {
            statusTxt = I18N.getString("aggregate.column.available.already.replaced");
        } else if (rowAction != null && rowAction.isUseExising()) {
            PartListStatus status = rowAction.getProductData().getStatus();
            statusTxt = switch (status) {
                case READY -> I18N.getString("aggregate.column.available.available");
                case TIMEOUT -> I18N.getString("aggregate.column.available.timeout");
                case PENDING -> I18N.getString("aggregate.column.available.pending");
                case ERROR -> I18N.getString("aggregate.column.available.not.available");
                case UNKNOWN -> I18N.getString("aggregate.column.available.unknown");
            };
        } else {
            statusTxt = StringConstant.EMPTY;
        }

        return statusTxt;
    }

    private void addAction(String productId, RowAction action) {
        if (action.getProductData() != null) {
            actionByProductDataId.put(action.getProductData().getId(), action);
        }

        ObservableList<RowAction> actionsForAggregate = actionsByAggregate.get(productId);
        actionsForAggregate.add(action);
        sortActions(actionsForAggregate);
    }

    private void sortActions(List<RowAction> actions) {
        actions.sort((e1, e2) -> {
            if (e1.isDoNothing()) {
                return -1;
            }
            if (e2.isDoNothing()) {
                return 1;
            }
            if (e1.isRequestNew()) {
                return -1;
            }
            if (e2.isRequestNew()) {
                return 1;
            }

            return e2.getProductData().getImportDate().compareTo(e1.getProductData().getImportDate());
        });
    }

    private void startUpdateTask() {
        statusUpdater = new PollingServiceController<>();
        statusUpdater.setPoll(true);
        statusUpdater.setExecutionTime(5000);
        statusUpdater.start(() -> {
            try {
                loadAvailableData(rows);
            } catch (Exception e) {
                LOG.warn("Could not update status. PLS not available?", e);
            }
        });
    }

    private void stopUpdateTask() {
        if (statusUpdater != null) {
            statusUpdater.setPoll(false);
            statusUpdater.cancel();
            statusUpdater = null;
        }
    }

    private List<Pair<RowAction, EfsElementDTO>> getElementsToReplace() {
        List<Pair<RowAction, EfsElementDTO>> result = new ArrayList<>(rows.size());

        for (AggregateRowObject row : rows) {
            RowAction action = row.rowActionProperty().get();
            if (action == null) {
                continue;
            }

            EfsElementAggregateMappingDTO mapping = efsAggregateMappingsById.get(row.getElement().getId());
            if ((mapping == null || mapping.importDate() == null) && action.isUseExising()
                    && PartListStatus.READY == action.getProductData().getStatus()) {
                result.add(new Pair<>(action, row.getElement()));
            }
        }

        return result;
    }
}
