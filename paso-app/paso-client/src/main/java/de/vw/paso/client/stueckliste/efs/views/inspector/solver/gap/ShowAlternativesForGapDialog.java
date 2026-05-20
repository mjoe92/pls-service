package de.vw.paso.client.stueckliste.efs.views.inspector.solver.gap;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.converter.DateStringConverter;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.PrNumberRuleTableCell;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolutionDialog;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.util.TableCellFactory;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class ShowAlternativesForGapDialog extends AbstractSolutionDialog<Collection<EfsElementDTO>> {

    private final EfsElementDTO selectedEfsElement;
    private final Map<EfsElementDTO, String> alternativeParts;
    private final VehicleConfigDTO vehicleConfig;

    private CustomTableView<EfsElementDTO> tableView;

    ShowAlternativesForGapDialog(Collection<TreeItem<InspectorTreeItemObject>> selectedItems,
        Map<EfsElementDTO, String> efsElements, VehicleConfigDTO vehicleConfig) {
        super(selectedItems);

        this.alternativeParts = efsElements;
        this.selectedEfsElement = selectedItems.iterator().next().getValue().getEntry().getElement();
        this.vehicleConfig = vehicleConfig;

        initialize(
            String.format(I18N.getString("alternativesForRisse.dialog.title"), selectedEfsElement.getDescription1()),
            () -> {
                this.setHeaderText(I18N.getString("alternativesForRisse.dialog.headerText"));
                initContent();
                addStylesheet();
            });
    }

    private void initContent() {
        VBox content = new VBox();

        Label label = new Label(String.format(I18N.getString("alternativesForRisse.dialog.text"),
            new DateStringConverter("dd.MM.yyyy").toString(vehicleConfig.getValidDate())));
        label.setPadding(new Insets(0, 0, 10, 0));

        tableView = createTable();
        tableView.makeFilterable();
        tableView.setItems(FXCollections.observableArrayList(alternativeParts.keySet()));

        addValidationListenerToInputField(tableView);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        content.setPrefWidth(1200);
        content.getChildren().addAll(label, tableView);

        getDialogPane().setContent(content);
    }

    private CustomTableView<EfsElementDTO> createTable() {
        CustomTableView<EfsElementDTO> tableView = new CustomTableView<>();

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.teilenummer",
            cellData -> new SimpleStringProperty(cellData.getValue().getPartNumber()),
            TableCellFactory.forColumnTeilenummer());

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.benennung",
            (CellDataFeatures<EfsElementDTO, String> cellData) -> new SimpleStringProperty(
                cellData.getValue().getDescription1()), null);

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.zusatzbenennung",
            (CellDataFeatures<EfsElementDTO, String> cellData) -> new SimpleStringProperty(
                cellData.getValue().getDescription2()), null);

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.reason",
            cellData -> new SimpleStringProperty(I18N.getString(alternativeParts.get(cellData.getValue()))),
            TableCellFactory.forColumnReason(alternativeParts));

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.einsatzserie",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getBeginDate()),
            TableCellFactory.forDateColumn());

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.entfallserie",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndDate()), TableCellFactory.forDateColumn());

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.prnummern",
            (CellDataFeatures<EfsElementDTO, String> cellData) -> new SimpleObjectProperty<>(
                cellData.getValue().getPrNumberRule()), param -> new PrNumberRuleTableCell(vehicleConfig));

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.ap",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getAp()), TableCellFactory.forColumnAp());

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.setkey",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getSetKey()), null);

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.costgroup",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getCostGroup()), null);

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.menge",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantity()),
            TableCellFactory.forIntegerColumn());

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.einheit",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantityUnit()), null);

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.gws",
            cellData -> new SimpleObjectProperty<>(getWeightControlFlag(cellData)), null);

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.gewichtprio",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getPrioritizedWeight()),
            TableCellFactory.forDoubleColumnReadOnly(true));

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.konstgewogen",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getWeightWeightedTe()),
            TableCellFactory.forDoubleColumnReadOnly(true));

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.konsterrechnet",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getWeightCalculatedTe()),
            TableCellFactory.forDoubleColumnReadOnly(true));

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.konstgeschaetzt",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getWeightEstimatedTe()),
            TableCellFactory.forDoubleColumnReadOnly(true));

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.prodgewogen",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getWeightWeightedProd()),
            TableCellFactory.forDoubleColumnReadOnly(true));

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.einsatzschluessel",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getBeginDateKey()), null);

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.entfallschluessel",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndDateKey()), null);

        createColumn(tableView.getColumns(), "alternativesForRisse.tablecolumn.processstatus",
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getProcessStatus()), null);

        return tableView;
    }

    private String getWeightControlFlag(CellDataFeatures<EfsElementDTO, Object> cellData) {
        WeightControlFlag weightControlFlag = cellData.getValue().getWeightControlFlag();
        return weightControlFlag == null ? null : weightControlFlag.getValue();
    }

    private <T> void createColumn(Collection<TableColumn<EfsElementDTO, ?>> columns, String titleKey,
        Callback<CellDataFeatures<EfsElementDTO, T>, ObservableValue<T>> reader,
        Callback<TableColumn<EfsElementDTO, T>, TableCell<EfsElementDTO, T>> cellCreator) {
        TableColumn<EfsElementDTO, T> column = new TableColumn<>(I18N.getString(titleKey));
        column.setCellValueFactory(reader);
        if (cellCreator != null) {
            column.setCellFactory(cellCreator);
        }

        columns.add(column);
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(
            tableView.getSelectionModel().getSelectedItem() == null);
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected boolean isInvalid() {
        return tableView.getSelectionModel().getSelectedItem() == null;
    }

    @Override
    protected Collection<EfsElementDTO> dialogResult() {
        EfsElementDTO newEfsElement = tableView.getSelectionModel().getSelectedItem();
        newEfsElement.setId(selectedEfsElement.getId());
        newEfsElement.setParentId(selectedEfsElement.getParentId());
        newEfsElement.setParent(selectedEfsElement.getParent());
        newEfsElement.setChildren(selectedEfsElement.getChildren());
        newEfsElement.setVehiclePartListId(selectedEfsElement.getVehiclePartListId());
        newEfsElement.setChange(UserProperties.getUserId());

        return Collections.singletonList(newEfsElement);
    }
}
