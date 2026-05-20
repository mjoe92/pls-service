package de.vw.paso.client.stueckliste.efs.views.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.TableCellFactory;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.control.tablebase.filter.panel.CustomFilterPanel;
import de.vw.paso.client.control.tablebase.filter.panel.CustomTableFilterValue;
import de.vw.paso.client.control.textfield.PasoCustomTextFieldClearable;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObj;
import de.vw.paso.client.explorer.vehicleconfig.converter.DateTimeStringConverter;
import de.vw.paso.client.stammdaten.FilteringUpdateEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.client.util.icon.FilterIcon;
import de.vw.paso.client.util.icon.StuecklisteIcon;
import de.vw.paso.exception.ServiceConsumer;
import de.vw.paso.pls.Status;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.DateUtil;
import de.vw.paso.utility.StringConstant;

//todo: refactor!
public class ComparePartListSelectionDialog extends BaseDialogController<ComparePartListSelectionDialogResult>
        implements ServiceConsumer {

    private static final DataFormat format = new DataFormat("application/x-java-serialized-object");
    public static final String DD_MM_YYYY = "dd.MM.yyyy";

    private final CustomTableView<ComparePartListItem> tableView;
    private final TableColumn<ComparePartListItem, Boolean> columnCheckBox;
    private final TableColumn<ComparePartListItem, String> columnBrand;
    private final TableColumn<ComparePartListItem, String> columnProductId;
    private final TableColumn<ComparePartListItem, String> columnVehicleName;
    private final TableColumn<ComparePartListItem, String> columnProject;
    private final TableColumn<ComparePartListItem, String> columnName;
    private final TableColumn<ComparePartListItem, Date> columnValidDate;
    private final TableColumn<ComparePartListItem, Double> columnWeight;
    private final TableColumn<ComparePartListItem, String> columnModelKey;
    private final TableColumn<ComparePartListItem, String> columnCountry;
    private final TableColumn<ComparePartListItem, Integer> columnYear;
    private final TableColumn<ComparePartListItem, String> columnDescription;
    private final TableColumn<ComparePartListItem, Date> columnBeginDate;
    private final TableColumn<ComparePartListItem, Date> columnEndDate;
    private final TableColumn<ComparePartListItem, String> columnStatus;
    private final TableColumn<ComparePartListItem, Date> columnCreated;
    private final TableColumn<ComparePartListItem, Date> columnLastChange;

    private final CustomTableView<VehicleConfigDTO> selectedConfigsTableView;
    private final TableColumn<VehicleConfigDTO, ImageView> colReference;
    private final TableColumn<VehicleConfigDTO, String> colVehicleConfigName;

    private final Button upButton;
    private final Button downButton;
    private final Button clearFilterButton;
    private final Button selectAllOpenPartListsButton;
    private final Button selectReferenceButton;

    private final GridPane gridPaneForSearchAndFilter;

    private final Collection<Long> openPartListIDs;
    private final Collection<VehicleConfigDTO> allVehicleConfig;
    private final List<ComparePartListItem> items;
    private final ObservableList<VehicleConfigDTO> selectedVehicleConfigs;
    private final Map<VehicleConfigDTO, Boolean> referencedVehicleConfig;
    private PasoWildCardPattern patternSearchTermTableComparePartList;
    private VehicleConfigDTO referenceVehicleConfig;
    private BooleanProperty disablePropertyClearFilters;
    private BooleanProperty disablePropertySelectAllOpenPartLists;

    public ComparePartListSelectionDialog(Collection<VehicleConfigDTO> preSelectedVehicleConfigs,
            TreeItem<VehicleConfigTreeObj> selectedFzgKonfigTreeItem, List<VehicleConfigDTO> allVehicleConfigs,
            Collection<Long> openPartListIDs) {
        columnBrand = new TableColumn<>(I18N.getString("column.brand"));
        columnProductId = new TableColumn<>(I18N.getString("column.product"));
        columnVehicleName = new TableColumn<>(I18N.getString("column.vehicleName"));
        columnProject = new TableColumn<>(I18N.getString("column.project"));
        columnName = new TableColumn<>(I18N.getString("bezeichnung"));
        columnValidDate = new TableColumn<>(I18N.getString("column.validDate"));
        columnWeight = new TableColumn<>(I18N.getString("column.weight"));
        columnModelKey = new TableColumn<>(I18N.getString("column.modelKey"));
        columnCountry = new TableColumn<>(I18N.getString("column.country"));
        columnYear = new TableColumn<>(I18N.getString("column.year"));
        columnDescription = new TableColumn<>(I18N.getString("column.description"));
        columnBeginDate = new TableColumn<>(I18N.getString("column.beginDate"));
        columnEndDate = new TableColumn<>(I18N.getString("column.endDate"));
        columnStatus = new TableColumn<>(I18N.getString("column.status"));
        columnCreated = new TableColumn<>(I18N.getString("column.created"));
        columnLastChange = new TableColumn<>(I18N.getString("column.lastChange"));
        selectedConfigsTableView = new CustomTableView<>();
        colReference = new TableColumn<>();
        colVehicleConfigName = new TableColumn<>(I18N.getString("selectedConfigsTable.col.config"));

        tableView = new CustomTableView<>();
        columnCheckBox = new TableColumn<>();

        upButton = new Button(null, new ImageView(ActionIcon.ARROW_UP_16x16.getImage()));
        downButton = new Button(null, new ImageView(ActionIcon.ARROW_DOWN_16x16.getImage()));
        clearFilterButton = new Button(I18N.getString("dialog.clearFilter"),
                new ImageView(FilterIcon.CLEARFILTERS_16X16.getImage()));
        selectAllOpenPartListsButton = new Button(I18N.getString("button.selectAllOpenPartLists"));
        selectReferenceButton = new Button(I18N.getString("dialog.reference"));
        gridPaneForSearchAndFilter = new GridPane();

        EventBus.getInstance().register(this);

        referencedVehicleConfig = new HashMap<>();
        selectedVehicleConfigs = FXCollections.observableArrayList();
        if (preSelectedVehicleConfigs != null) {
            Collection<VehicleConfigDTO> preSelectedVehicleConfigsWithStatusComplete = preSelectedVehicleConfigs.stream()
                    .filter(vehicleConfig -> Status.COMPLETE.equals(vehicleConfig.getStatus())).toList();

            selectedVehicleConfigs.addAll(preSelectedVehicleConfigsWithStatusComplete);
        }

        this.allVehicleConfig = allVehicleConfigs;

        items = convertVehicleConfigsToComparePartListItem(allVehicleConfigs);
        this.openPartListIDs = openPartListIDs;

        initialize(I18N.getString("dialog.compare.title"), () -> {
            setHeaderText(I18N.getString("dialog.compare.header"));

            initContent();
            initDoubleClickOnListView();

            if (selectedFzgKonfigTreeItem != null) {
                filterVehicleConfigs(selectedFzgKonfigTreeItem);
            }
            commitButton.setDisable(selectedVehicleConfigs.size() < 2);
        });
    }

    public void unregisterEventBus() {
        EventBus.getInstance().unregister(this);
    }

    public void handleException(Throwable throwable) {
        ExceptionHandler.instance().handleException(throwable, this);
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return null;
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return c -> {
            c.next();
            commitButton.setDisable(selectedVehicleConfigs == null || selectedVehicleConfigs.isEmpty()
                    || selectedVehicleConfigs.size() < 2);
        };
    }

    @Override
    protected boolean isInvalid() {
        return selectedVehicleConfigs == null || selectedVehicleConfigs.isEmpty() || selectedVehicleConfigs.size() < 2;
    }

    @Override
    protected ComparePartListSelectionDialogResult dialogResult() {
        ComparePartListSelectionDialogResult result = new ComparePartListSelectionDialogResult();
        result.setReferenceVehicleConfig(referenceVehicleConfig);
        result.setSelectedVehicleConfigs(selectedVehicleConfigs);

        return result;
    }

    @SuppressWarnings("unused")
    @Subscribe
    private void handleFilterUpdate(FilteringUpdateEvent event) {
        disablePropertyClearFilters().setValue(tableView.getColumnToPredicateDataMap().isEmpty());
    }

    private void initContent() {
        initTable();
        initColumns();
        initSelectedVehicleConfigsTable();
        initSelectedVehicleConfigsTableColumns();
        initButtons();

        tableView.getSortOrder().add(columnCreated);

        addValidationListenerToInputField(selectedVehicleConfigs);

        VBox content = new VBox(5);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        content.setPrefWidth(1200);
        content.setPrefHeight(650);

        Label labelSearch = new Label(I18N.getString("search"));
        PasoCustomTextFieldClearable searchArea = new PasoCustomTextFieldClearable();
        searchArea.textProperty().addListener((obs, ov, nv) -> setComparePartListSearchText(nv));

        gridPaneForSearchAndFilter.setHgap(5);
        gridPaneForSearchAndFilter.setVgap(5);
        gridPaneForSearchAndFilter.add(labelSearch, 0, 0);
        gridPaneForSearchAndFilter.add(searchArea, 1, 0);
        gridPaneForSearchAndFilter.add(clearFilterButton, 2, 0);
        gridPaneForSearchAndFilter.add(selectAllOpenPartListsButton, 3, 0);

        Label selectedConfigsSubTitle = new Label(I18N.getString("dialog.selected"));

        VBox navigationButtons = new VBox(5);
        navigationButtons.setAlignment(Pos.TOP_LEFT);
        navigationButtons.getChildren().addAll(upButton, downButton);

        VBox referenceButton = new VBox();
        VBox.setVgrow(selectReferenceButton, Priority.ALWAYS);
        referenceButton.setAlignment(Pos.BOTTOM_LEFT);
        referenceButton.getChildren().addAll(selectReferenceButton);

        VBox buttonsVBox = new VBox(5);
        VBox.setVgrow(navigationButtons, Priority.ALWAYS);
        buttonsVBox.getChildren().addAll(navigationButtons, referenceButton);

        HBox hBoxForSelectedVehicleConfigs = new HBox(5);
        HBox.setHgrow(selectedConfigsTableView, Priority.ALWAYS);
        hBoxForSelectedVehicleConfigs.getChildren().addAll(selectedConfigsTableView, buttonsVBox);

        content.getChildren()
                .addAll(gridPaneForSearchAndFilter, tableView, selectedConfigsSubTitle, hBoxForSelectedVehicleConfigs);

        getDialogPane().setContent(content);
    }

    private void initSelectedVehicleConfigsTable() {
        selectedConfigsTableView.getColumns().addAll(colReference, colVehicleConfigName);

        selectedConfigsTableView.setPrefHeight(250);
        selectedConfigsTableView.setItems(FXCollections.observableArrayList(selectedVehicleConfigs));
        selectedConfigsTableView.setRowFactory(tableView -> {
            TableRow<VehicleConfigDTO> row = new TableRow<>();
            row.setOnDragDetected(event -> {
                if (row.isEmpty()) {
                    return;
                }

                Integer index = row.getIndex();

                Dragboard dragboard = selectedConfigsTableView.startDragAndDrop(TransferMode.MOVE);

                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.put(format, index);

                dragboard.setContent(clipboardContent);
                event.consume();
            });

            setDragOverOnRow(row);
            setDragDropOnRow(row);

            return row;

        });
        selectedConfigsTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    boolean disable = newValue == null;

                    upButton.setDisable(disable);
                    downButton.setDisable(disable);
                    selectReferenceButton.setDisable(disable);
                });
        selectedConfigsTableView.makeFilterable();
    }

    private void setDragOverOnRow(TableRow<VehicleConfigDTO> row) {
        row.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(format) && row.getIndex() != (Integer) db.getContent(format)) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            }
        });
    }

    private void setDragDropOnRow(TableRow<VehicleConfigDTO> row) {
        row.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (!db.hasContent(format)) {
                return;
            }

            int draggedIndex = (Integer) db.getContent(format);
            selectedConfigsTableView.getSelectionModel().clearSelection();

            VehicleConfigDTO draggedVehicleConfig = selectedConfigsTableView.getItems().remove(draggedIndex);

            int dropIndex = row.isEmpty() ? selectedConfigsTableView.getItems().size() : row.getIndex();
            selectedConfigsTableView.getItems().add(dropIndex, draggedVehicleConfig);

            event.setDropCompleted(true);
            selectedConfigsTableView.getSelectionModel().select(dropIndex);
            event.consume();
        });
    }

    private void initSelectedVehicleConfigsTableColumns() {
        colReference.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                referencedVehicleConfig.get(cellData.getValue()) != null && referencedVehicleConfig.get(
                        cellData.getValue()) ?
                        new ImageView(StuecklisteIcon.REFERENCE_PART_LIST_FLAG_16x16.getImage()) : new ImageView()));

        colVehicleConfigName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    }

    private void initDoubleClickOnListView() {
        selectedConfigsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                referenceVehicleConfig = selectedConfigsTableView.getSelectionModel().getSelectedItem();

                referencedVehicleConfig.clear();
                referencedVehicleConfig.put(referenceVehicleConfig, true);

                selectedConfigsTableView.refresh();
            }
        });
    }

    private void initButtons() {
        upButton.setDisable(true);
        upButton.setOnAction(event -> {
            VehicleConfigDTO selectedConfig = selectedConfigsTableView.getSelectionModel().getSelectedItem();
            int selectedIndex = selectedConfigsTableView.getSelectionModel().getSelectedIndex();

            selectedConfigsTableView.getSelectionModel().clearSelection();

            if (selectedIndex > 0) {
                VehicleConfigDTO vehicleConfig = selectedConfigsTableView.getItems().remove(selectedIndex);

                selectedConfigsTableView.getItems().add(selectedIndex - 1, vehicleConfig);
                selectedConfigsTableView.getSelectionModel().select(selectedConfig);
            }
        });

        downButton.setDisable(true);
        downButton.setOnAction(event -> {
            VehicleConfigDTO selectedConfig = selectedConfigsTableView.getSelectionModel().getSelectedItem();
            int selectedIndex = selectedConfigsTableView.getSelectionModel().getSelectedIndex();

            selectedConfigsTableView.getSelectionModel().clearSelection();

            VehicleConfigDTO vehicleConfig = selectedConfigsTableView.getItems().remove(selectedIndex);

            if (selectedIndex < selectedConfigsTableView.getItems().size()) {
                selectedConfigsTableView.getItems().add(selectedIndex + 1, vehicleConfig);
                selectedConfigsTableView.getSelectionModel().select(selectedConfig);
            } else {
                selectedConfigsTableView.getItems().add(vehicleConfig);
                selectedConfigsTableView.getSelectionModel().select(selectedConfig);
            }
        });

        clearFilterButton.setOnAction(event -> handleActionClearFilter());
        clearFilterButton.disableProperty().bind(disablePropertyClearFilters());

        selectAllOpenPartListsButton.setOnAction(event -> handleActionSelectAllOpenPartLists());
        selectAllOpenPartListsButton.disableProperty().bind(disablePropertySelectAllOpenPartLists());
        selectAllOpenPartListsButton.setTooltip(new Tooltip(I18N.getString("button.selectAllOpenPartListsToolTip")));

        selectReferenceButton.setDisable(true);
        selectReferenceButton.setWrapText(true);
        selectReferenceButton.setTextAlignment(TextAlignment.CENTER);
        selectReferenceButton.setPrefWidth(80);
        selectReferenceButton.setOnAction(event -> {
            if (referenceVehicleConfig != null && referenceVehicleConfig.equals(
                    selectedConfigsTableView.getSelectionModel().getSelectedItem())) {
                referenceVehicleConfig = null;
                referencedVehicleConfig.clear();
            } else {
                referenceVehicleConfig = selectedConfigsTableView.getSelectionModel().getSelectedItem();
                referencedVehicleConfig.clear();
                referencedVehicleConfig.put(referenceVehicleConfig, true);
            }

            selectedConfigsTableView.refresh();
            selectedConfigsTableView.requestFocus();
        });
    }

    private void initTable() {
        tableView.getColumns()
                .addAll(columnCheckBox, columnBrand, columnProductId, columnVehicleName, columnProject, columnName,
                        columnValidDate, columnWeight, columnModelKey, columnCountry, columnYear, columnDescription,
                        columnBeginDate, columnEndDate, columnStatus, columnCreated, columnLastChange);

        tableView.setItems(FXCollections.observableArrayList(items));
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setEditable(true);
        tableView.makeFilterable();
    }

    private void initColumns() {
        columnCheckBox.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        columnCheckBox.setCellFactory(CheckBoxTableCell.forTableColumn(param -> {
            ComparePartListItem item = tableView.getItems().get(param);
            if (getSelectedVehicleConfigIds(selectedVehicleConfigs).contains(item.getVehicleConfig().getId())) {
                item.setSelected(true);
            }

            item.selectedProperty().addListener(l -> updateSelectedItems(item));

            return item.selectedProperty();
        }));
        columnCheckBox.setEditable(true);
        columnCheckBox.setSortable(false);

        columnBrand.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getVehicleConfig().getVehicleProject().getBrandCode().getBrandName()));

        columnProductId.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getVehicleConfig().getVehiclePartList().getProductKeyVehicle()));

        columnVehicleName.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getVehicleConfig().getVehicleProject().getDescription()));

        columnProject.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getVehicleConfig().getVehicleProject().getProjectName()));

        columnName.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getVehicleConfig().getName()));

        columnValidDate.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(cellData.getValue().getVehicleConfig().getValidDate()));
        columnValidDate.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));

        columnWeight.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().getVehicleConfig().getVehiclePartList().getWeight()));
        columnWeight.setCellFactory(new TableCellFactory<>(new DoubleStringConverter()));

        columnModelKey.setCellValueFactory(
                cellData -> new SimpleStringProperty(getModelKey(cellData.getValue().getVehicleConfig())));

        columnCountry.setCellValueFactory(
                cellData -> new SimpleStringProperty(getModelCountry(cellData.getValue().getVehicleConfig())));

        columnYear.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(getModelYear(cellData.getValue().getVehicleConfig())));

        columnDescription.setCellValueFactory(
                cellData -> new SimpleStringProperty(getModelDescription(cellData.getValue().getVehicleConfig())));

        columnBeginDate.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(getModelBeginDate(cellData.getValue().getVehicleConfig())));
        columnBeginDate.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));

        columnEndDate.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(getModelEndDate(cellData.getValue().getVehicleConfig())));
        columnEndDate.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));

        columnStatus.setCellValueFactory(
                cellData -> new SimpleStringProperty(getDatenstandStatus(cellData.getValue().getVehicleConfig())));

        columnCreated.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(cellData.getValue().getVehicleConfig().getTimestampCreate()));
        columnCreated.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));

        columnLastChange.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(cellData.getValue().getVehicleConfig().getTimestampChange()));
        columnLastChange.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));
    }

    private void updateSelectedItems(ComparePartListItem item) {
        if (!item.isSelected()) {
            selectedVehicleConfigs.remove(item.getVehicleConfig());
            selectedConfigsTableView.setItems(selectedVehicleConfigs);
        } else if (!getSelectedVehicleConfigIds(selectedVehicleConfigs).contains(item.getVehicleConfig().getId())) {
            selectedVehicleConfigs.add(item.getVehicleConfig());
            selectedConfigsTableView.setItems(selectedVehicleConfigs);
        }
    }

    private void filterVehicleConfigs(TreeItem<VehicleConfigTreeObj> selectedFzgKonfigTreeItem) {
        VehicleConfigTreeObj selectedVehicleConfigTreeObject = selectedFzgKonfigTreeItem.getValue();

        if (selectedVehicleConfigTreeObject.isBrand()) {
            filterTable(selectedVehicleConfigTreeObject.getBrand().getBrandName(), columnBrand);
        } else if (selectedVehicleConfigTreeObject.isProductKey()) {
            filterTable(selectedVehicleConfigTreeObject.getProductKey(), columnProductId);
        } else if (selectedVehicleConfigTreeObject.isFavoritesGroup()) {
            List<String> filterTexts = selectedFzgKonfigTreeItem.getChildren().stream()
                    .map(treeItem -> treeItem.getValue().getVehicleProject().getProjectName()).toList();

            filterTable(filterTexts, columnProject);
        } else if (selectedVehicleConfigTreeObject.isFavorite() || selectedVehicleConfigTreeObject.isVehicleProject()) {
            filterTable(selectedVehicleConfigTreeObject.getVehicleProject().getProjectName(), columnProject);
        } else if (selectedVehicleConfigTreeObject.isRecentlyUsedGroup()) {
            if (selectedVehicleConfigTreeObject.getVehicleConfig() == null) {
                List<String> filterTexts = selectedFzgKonfigTreeItem.getChildren().stream()
                        .map(treeItem -> treeItem.getValue().getVehicleConfig().getName()).toList();

                filterTable(filterTexts, columnName);
            } else {
                filterTable(selectedVehicleConfigTreeObject.getVehicleConfig().getName(), columnName);
            }
        }
    }

    private void filterTable(String filterText, TableColumn<ComparePartListItem, ?> column) {
        filterTable(List.of(filterText), column);
    }

    private void filterTable(List<String> filterTexts, TableColumn<ComparePartListItem, ?> column) {
        tableView.filter(column);

        ObservableList<CustomTableFilterValue> list = ((CustomFilterPanel) ((CustomMenuItem) column.getContextMenu()
                .getItems().getFirst()).contentProperty().get()).getCheckListView().getItems();

        ObservableList<CustomTableFilterValue> filteredList = FXCollections.observableArrayList(
                list.stream().filter(e -> filterTexts.contains(e.getUnformattedLabelText())).toList());

        boolean isFiltered = list.equals(filteredList);
        if (!isFiltered) {
            tableView.filterContent(column, filterTexts.getFirst(), list, filteredList);
            displayFilteredText();
        }

        disablePropertyClearFilters().set(isFiltered);
    }

    private String getModelKey(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null || vehicleConfig.getModel() == null ? null :
                vehicleConfig.getModel().getModelKey();
    }

    private String getModelCountry(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null || vehicleConfig.getModel() == null ? null :
                vehicleConfig.getModel().getModelImport().getSalesRegion().id();
    }

    private Integer getModelYear(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null || vehicleConfig.getModel() == null
                || vehicleConfig.getModel().getModelImport().getModelYear() == null ? null :
                vehicleConfig.getModel().getModelImport().getModelYear();
    }

    private String getModelDescription(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null || vehicleConfig.getModel() == null ? null :
                vehicleConfig.getModel().getDescription();
    }

    private Date getModelBeginDate(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null || vehicleConfig.getModel() == null ? null :
                vehicleConfig.getModel().getBeginDate();
    }

    private Date getModelEndDate(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null || vehicleConfig.getModel() == null ? null : vehicleConfig.getModel().getEndDate();
    }

    private String getDatenstandStatus(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null || vehicleConfig.getModel() == null ? null : vehicleConfig.getModel().getStatus();
    }

    private void setComparePartListSearchText(String searchTerm) {
        try {
            patternSearchTermTableComparePartList = new PasoWildCardPattern(searchTerm);
        } catch (Exception exception) {
            handleException(exception);
        }

        fillTableComparePartLists();
    }

    private void fillTableComparePartLists() {
        Collection<Long> selectedPartListIDs = new ArrayList<>();
        for (ComparePartListItem item : items) {
            if (item.isSelected()) {
                selectedPartListIDs.add(item.getVehicleConfig().getId());
            }
        }

        items.clear();

        if (allVehicleConfig == null) {
            return;
        }

        for (VehicleConfigDTO vehicleConfig : allVehicleConfig) {
            if (matchVehicleConfigTableComparePartList(vehicleConfig)) {
                items.add(convertVehicleConfigsToComparePartListItem(List.of(vehicleConfig)).getFirst());
            }

            if (selectedPartListIDs.contains(vehicleConfig.getId()) && !items.isEmpty()) {
                items.getLast().setSelected(true);
            }
        }

        tableView.setItems(FXCollections.observableArrayList(items));
        tableView.sort();
    }

    private List<ComparePartListItem> convertVehicleConfigsToComparePartListItem(
            List<VehicleConfigDTO> vehicleConfigs) {
        List<ComparePartListItem> result = new ArrayList<>();
        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            ComparePartListItem item = new ComparePartListItem();
            item.setVehicleConfig(vehicleConfig);

            boolean select = selectedVehicleConfigs != null && !selectedVehicleConfigs.isEmpty()
                    && selectedVehicleConfigs.stream()
                    .anyMatch(selectedVehicleConfig -> selectedVehicleConfig.getId().equals(vehicleConfig.getId()));
            item.setSelected(select);

            result.add(item);
        }

        return result;
    }

    private void displayFilteredText() {
        Label label = new Label(StringConstant.PIPE_DOUBLE_SPACE + I18N.getString("dialog.filterNotification"));

        gridPaneForSearchAndFilter.add(label, 7, 0);
    }

    private boolean matchVehicleConfigTableComparePartList(VehicleConfigDTO vehicleConfig) {
        if (patternSearchTermTableComparePartList == null) {
            return true;
        }

        for (TableColumn<ComparePartListItem, ?> column : tableView.getColumns()) {
            ObservableValue<?> cellObservableValue = column.getCellObservableValue(
                    new ComparePartListItem(vehicleConfig));
            Object value = cellObservableValue.getValue();
            if (value != null && patternSearchTermTableComparePartList.matches(value.toString()) != null) {
                return true;
            }
        }

        //todo: refactor
        return patternSearchTermTableComparePartList == null || (vehicleConfig.getVehicleProject() != null
                && patternSearchTermTableComparePartList.matches(vehicleConfig.getVehicleProject().getProjectName())
                != null) || (vehicleConfig.getVehicleProject() != null
                && patternSearchTermTableComparePartList.matches(vehicleConfig.getVehicleProject().getProductKey())
                != null) || (vehicleConfig.getVehicleProject() != null
                && patternSearchTermTableComparePartList.matches(vehicleConfig.getVehicleProject().getDescription())
                != null) || patternSearchTermTableComparePartList.matches(vehicleConfig.getName()) != null || (
                vehicleConfig.getValidDate() != null && patternSearchTermTableComparePartList.matches(
                        DateUtil.formatDate(vehicleConfig.getValidDate(), DD_MM_YYYY)) != null) || (
                vehicleConfig.getVehiclePartList().getWeight() != null && patternSearchTermTableComparePartList.matches(
                        vehicleConfig.getVehiclePartList().getWeight().toString()) != null) || (
                vehicleConfig.getModel() != null && (
                        patternSearchTermTableComparePartList.matches(vehicleConfig.getModel().getModelKey()) != null
                                || (vehicleConfig.getModel().getModelImport().getSalesRegion() != null &&
                                patternSearchTermTableComparePartList.matches(
                                        vehicleConfig.getModel().getModelImport().getSalesRegion().id()) != null) || (
                                vehicleConfig.getModel().getModelImport().getModelYear() != null &&
                                        patternSearchTermTableComparePartList.matches(
                                                vehicleConfig.getModel().getModelImport().getModelYear().toString())
                                                != null) ||
                                patternSearchTermTableComparePartList.matches(vehicleConfig.getModel().getDescription())
                                        != null || patternSearchTermTableComparePartList.matches(
                                DateUtil.formatDate(vehicleConfig.getModel().getBeginDate(), DD_MM_YYYY)) != null ||
                                patternSearchTermTableComparePartList.matches(
                                        DateUtil.formatDate(vehicleConfig.getModel().getEndDate(), DD_MM_YYYY))
                                        != null)) || patternSearchTermTableComparePartList.matches(
                DateUtil.formatDate(vehicleConfig.getTimestampCreate(), DD_MM_YYYY)) != null || (
                vehicleConfig.getTimestampChange() != null && patternSearchTermTableComparePartList.matches(
                        DateUtil.formatDate(vehicleConfig.getTimestampChange(), DD_MM_YYYY)) != null);
    }

    private void handleActionClearFilter() {
        tableView.clearFilters();

        if (gridPaneForSearchAndFilter.getChildren().size() > 3) {
            gridPaneForSearchAndFilter.getChildren().removeLast();
        }

        disablePropertyClearFilters().set(true);
    }

    private void handleActionSelectAllOpenPartLists() {
        List<Long> filteredIDs = new ArrayList<>();
        if (tableView.getFilteredList() != null) {
            for (ComparePartListItem filteredItem : tableView.getFilteredList()) {
                filteredIDs.add(filteredItem.getVehicleConfig().getId());
            }
        }

        for (Long openPartListID : openPartListIDs) {
            getFilteredIDs(filteredIDs, openPartListID);
        }

        selectedConfigsTableView.setItems(selectedVehicleConfigs);
    }

    private void getFilteredIDs(List<Long> filteredIDs, Long openPartListID) {
        for (ComparePartListItem item : items) {
            if (item.getVehicleConfig().getId().equals(openPartListID) && (filteredIDs.isEmpty()
                    || filteredIDs.contains(item.getVehicleConfig().getId()))) {
                item.setSelected(true);
                if (!getSelectedVehicleConfigIds(selectedVehicleConfigs).contains(item.getVehicleConfig().getId())) {
                    selectedVehicleConfigs.add(item.getVehicleConfig());
                }
            }
        }
    }

    private BooleanProperty disablePropertyClearFilters() {
        if (disablePropertyClearFilters == null) {
            disablePropertyClearFilters = new SimpleBooleanProperty(true);
        }

        return disablePropertyClearFilters;
    }

    private BooleanProperty disablePropertySelectAllOpenPartLists() {
        if (disablePropertySelectAllOpenPartLists == null) {
            disablePropertySelectAllOpenPartLists = new SimpleBooleanProperty(false);
        }

        return disablePropertySelectAllOpenPartLists;
    }

    private List<Long> getSelectedVehicleConfigIds(List<VehicleConfigDTO> selectedVehicleConfigDTOS) {
        return selectedVehicleConfigDTOS.stream().map(VehicleConfigDTO::getId).collect(Collectors.toList());
    }
}
