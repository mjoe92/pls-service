package de.vw.paso.client.stueckliste.efs.views.summarised;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.stueckliste.efs.converter.SeparatedPartNumberStringConverter;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItemPropertyNames;
import de.vw.paso.compare.CompareSummaryRow;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;

public class AbstractSummarisedCompareTabController extends BaseController<BorderPane> implements Initializable {

    @FXML
    private Label label;
    @FXML
    private BorderPane sumCompareBorderPane;
    @FXML
    private CustomTableView<CompareSummaryRow> sumCompareTableView;
    @FXML
    private SummarizedTableColumn<CompareSummaryRow, String> colPartNumber;
    @FXML
    private SummarizedTableColumn<CompareSummaryRow, String> colDescription;
    @FXML
    private SummarizedTableColumn<CompareSummaryRow, String> colDescription2;
    @FXML
    private SummarizedTableColumn<CompareSummaryRow, String> colAP;
    @FXML
    private SummarizedTableColumn<CompareSummaryRow, String> colSetKey;
    @FXML
    private SummarizedTableColumn<CompareSummaryRow, String> colCostGroup;
    @FXML
    private SummarizedTableColumn<CompareSummaryRow, String> colChangeState;
    @FXML
    private SummarizedTableColumn<CompareSummaryRow, Double> colDeltaWeight;

    private static final String HIGHLIGHT_CELL_STYLE = "change-cell";
    private static final String HIGHLIGHT_COLUMN_GROUP_FIRST = "summary-group-column1-first";
    private static final String HIGHLIGHT_COLUMN_GROUP_SECOND = "summary-group-column-second";

    private final Collection<CompareSummaryRow> elements;
    private Map<Long, List<EfsElementDTO>> efsElementsMap;

    private String columnHeaderName;
    private String itemName;
    private VehicleConfigDTO secondLastConfig;
    private VehicleConfigDTO lastConfig;

    public AbstractSummarisedCompareTabController() {
        elements = new ArrayList<>();
        efsElementsMap = new HashMap<>();
    }

    @Override
    public BorderPane getControl() {
        return sumCompareBorderPane;
    }

    @Override
    public Parent getStyleableParent() {
        return sumCompareBorderPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initColumns();

        sumCompareTableView.setPrefHeight(1024);
    }

    public void initEfsElementList(Map<Long, List<EfsElementDTO>> efsElementsMap, VehicleConfigDTO secondLast,
        VehicleConfigDTO last, String columnHeaderName, String itemName) {
        this.secondLastConfig = secondLast;
        this.lastConfig = last;
        this.efsElementsMap = efsElementsMap;
        this.columnHeaderName = columnHeaderName;
        this.itemName = itemName;

        initData();
        initColumnGroups();
        initTable();
    }

    private void initTable() {
        sumCompareTableView.makeHeaderWrappable();
        sumCompareTableView.getItems().setAll(elements);

        initLabel();
    }

    private void initColumns() {
        colPartNumber.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getFirstEfsElement() != null ? new SeparatedPartNumberStringConverter().toString(
                cellData.getValue().getFirstEfsElement().getPartNumber())
                : new SeparatedPartNumberStringConverter().toString(
                    cellData.getValue().getSecondEfsElement().getPartNumber())));
        colPartNumber.setConverter(new SeparatedPartNumberStringConverter());
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getFirstEfsElement() != null ? cellData.getValue().getFirstEfsElement()
                .getDescription1() : cellData.getValue().getSecondEfsElement().getDescription1()));

        colDescription2.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getFirstEfsElement() != null ? cellData.getValue().getFirstEfsElement()
                .getDescription2() : cellData.getValue().getSecondEfsElement().getDescription2()));

        colAP.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getFirstEfsElement() != null ? cellData.getValue().getFirstEfsElement().getAp()
                : cellData.getValue().getSecondEfsElement().getAp()));

        colSetKey.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getFirstEfsElement() != null ? cellData.getValue().getFirstEfsElement().getSetKey()
                : cellData.getValue().getSecondEfsElement().getSetKey()));

        colCostGroup.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getFirstEfsElement() != null ? cellData.getValue().getFirstEfsElement().getCostGroup()
                : cellData.getValue().getSecondEfsElement().getCostGroup()));

        colChangeState.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChangeState()));

        colDeltaWeight.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getDeltaWeight()));
    }

    private void initColumnGroups() {
        TableColumn<CompareSummaryRow, ?> group = new TableColumn<>(secondLastConfig.getName());

        TableColumn<CompareSummaryRow, Integer> colQuantity = new TableColumn<>(I18N.getString("tablecolumn.quantity"));
        colQuantity.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getFirstEfsElement() == null ? null
                : cellData.getValue().getFirstEfsElement().getQuantity()));
        colQuantity.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.QUANTITY));

        TableColumn<CompareSummaryRow, String> colQuantityUnit = new TableColumn<>(
            I18N.getString("tablecolumn.quantityUnit"));
        colQuantityUnit.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getFirstEfsElement() == null ? StringConstant.EMPTY
                : cellData.getValue().getFirstEfsElement().getQuantityUnit()));
        colQuantityUnit.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.QUANTITY_UNIT));

        TableColumn<CompareSummaryRow, String> colWeightControlFlag = new TableColumn<>(
            I18N.getString("tablecolumn.weightControlFlag"));
        colWeightControlFlag.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getFirstEfsElement() == null ? StringConstant.EMPTY
                : cellData.getValue().getFirstEfsElement().getWeightControlFlag() == null ? null
                    : cellData.getValue().getFirstEfsElement().getWeightControlFlag().getValue()));
        colWeightControlFlag.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_CONTROL_FLAG));

        TableColumn<CompareSummaryRow, Double> colWeightAll = new TableColumn<>(I18N.getString("tablecolumn.weight"));
        colWeightAll.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getFirstEfsElement() == null ? null
                : cellData.getValue().getFirstEfsElement().getWeight()));
        colWeightAll.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_ALL));

        TableColumn<CompareSummaryRow, Double> colWeightPrio = new TableColumn<>(
            I18N.getString("tablecolumn.weightPrio"));
        colWeightPrio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getFirstEfsElement() == null ? null
                : cellData.getValue().getFirstEfsElement().getEfsElementMara().getPrioritizedWeight()));
        colWeightPrio.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_PRIO));

        TableColumn<CompareSummaryRow, Double> colWeightWeightedTe = new TableColumn<>(
            I18N.getString("tablecolumn.weightWeightedTe"));
        colWeightWeightedTe.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getFirstEfsElement() == null ? null
                : cellData.getValue().getFirstEfsElement().getEfsElementMara().getWeightWeightedTe()));
        colWeightWeightedTe.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_TE));

        TableColumn<CompareSummaryRow, Double> colCalculatedWeight = new TableColumn<>(
            I18N.getString("tablecolumn.weightCalculated"));
        colCalculatedWeight.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getFirstEfsElement() == null ? null
                : cellData.getValue().getFirstEfsElement().getEfsElementMara().getWeightCalculatedTe()));
        colCalculatedWeight.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_CALCULATED_TE));

        TableColumn<CompareSummaryRow, Double> colEstimatedWeight = new TableColumn<>(
            I18N.getString("tablecolumn.weightEstimated"));
        colEstimatedWeight.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getFirstEfsElement() == null ? null
                : cellData.getValue().getFirstEfsElement().getEfsElementMara().getWeightEstimatedTe()));
        colEstimatedWeight.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_ESTIMATED_TE));

        TableColumn<CompareSummaryRow, Double> colWeightWeightedProd = new TableColumn<>(
            I18N.getString("tablecolumn.weightWeightedProd"));
        colWeightWeightedProd.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getFirstEfsElement() == null ? null
                : cellData.getValue().getFirstEfsElement().getEfsElementMara().getWeightWeightedProd()));
        colWeightWeightedProd.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_PROD));

        group.getColumns().addAll(colQuantity, colQuantityUnit, colWeightControlFlag, colWeightAll, colWeightPrio,
            colWeightWeightedTe, colCalculatedWeight, colEstimatedWeight, colWeightWeightedProd);

        sumCompareTableView.getColumns().add(group);
        group.getColumns().forEach(col -> col.getStyleClass().add(HIGHLIGHT_COLUMN_GROUP_FIRST));

        TableColumn<CompareSummaryRow, Object> groupLastConfig = new TableColumn<>(lastConfig.getName());

        TableColumn<CompareSummaryRow, Integer> colQuantity2 = new TableColumn<>(
            I18N.getString("tablecolumn.quantity"));
        colQuantity2.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getSecondEfsElement() == null ? null
                : cellData.getValue().getSecondEfsElement().getQuantity()));
        colQuantity2.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.QUANTITY));

        TableColumn<CompareSummaryRow, String> colQuantityUnit2 = new TableColumn<>(
            I18N.getString("tablecolumn.quantityUnit"));
        colQuantityUnit2.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getSecondEfsElement() == null ? StringConstant.EMPTY
                : cellData.getValue().getSecondEfsElement().getQuantityUnit()));
        colQuantityUnit2.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.QUANTITY_UNIT));

        TableColumn<CompareSummaryRow, String> colWeightControlFlag2 = new TableColumn<>(
            I18N.getString("tablecolumn.weightControlFlag"));
        colWeightControlFlag2.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getSecondEfsElement() == null ? StringConstant.EMPTY
                : cellData.getValue().getSecondEfsElement().getWeightControlFlag() == null ? null
                    : cellData.getValue().getSecondEfsElement().getWeightControlFlag().getValue()));
        colWeightControlFlag2.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_CONTROL_FLAG));

        TableColumn<CompareSummaryRow, Double> colWeightAll2 = new TableColumn<>(I18N.getString("tablecolumn.weight"));
        colWeightAll2.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getSecondEfsElement() == null ? null
                : cellData.getValue().getSecondEfsElement().getWeight()));
        colWeightAll2.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_ALL));

        TableColumn<CompareSummaryRow, Double> colWeightPrio2 = new TableColumn<>(
            I18N.getString("tablecolumn.weightPrio"));
        colWeightPrio2.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getSecondEfsElement() == null ? null
                : cellData.getValue().getSecondEfsElement().getEfsElementMara().getPrioritizedWeight()));
        colWeightPrio2.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_PRIO));

        TableColumn<CompareSummaryRow, Double> colWeightWeightedTe2 = new TableColumn<>(
            I18N.getString("tablecolumn.weightWeightedTe"));
        colWeightWeightedTe2.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getSecondEfsElement() == null ? null
                : cellData.getValue().getSecondEfsElement().getEfsElementMara().getWeightWeightedTe()));
        colWeightWeightedTe2.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_TE));

        TableColumn<CompareSummaryRow, Double> colCalculatedWeight2 = new TableColumn<>(
            I18N.getString("tablecolumn.weightCalculated"));
        colCalculatedWeight2.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getSecondEfsElement() == null ? null
                : cellData.getValue().getSecondEfsElement().getEfsElementMara().getWeightCalculatedTe()));
        colCalculatedWeight2.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_CALCULATED_TE));

        TableColumn<CompareSummaryRow, Double> colEstimatedWeight2 = new TableColumn<>(
            I18N.getString("tablecolumn.weightEstimated"));
        colEstimatedWeight2.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getSecondEfsElement() == null ? null
                : cellData.getValue().getSecondEfsElement().getEfsElementMara().getWeightEstimatedTe()));
        colEstimatedWeight2.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_ESTIMATED_TE));

        TableColumn<CompareSummaryRow, Double> colWeightWeightedProd2 = new TableColumn<>(
            I18N.getString("tablecolumn.weightWeightedProd"));
        colWeightWeightedProd2.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getSecondEfsElement() == null ? null
                : cellData.getValue().getSecondEfsElement().getEfsElementMara().getWeightWeightedProd()));
        colWeightWeightedProd2.setCellFactory(createCellFactory(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_PROD));

        groupLastConfig.getColumns()
            .addAll(colQuantity2, colQuantityUnit2, colWeightControlFlag2, colWeightAll2, colWeightPrio2,
                colWeightWeightedTe2, colCalculatedWeight2, colEstimatedWeight2, colWeightWeightedProd2);
        for (TableColumn<CompareSummaryRow, ?> col : groupLastConfig.getColumns()) {
            col.getStyleClass().add(HIGHLIGHT_COLUMN_GROUP_SECOND);
        }

        sumCompareTableView.getColumns().add(groupLastConfig);
    }

    private <T> Callback<TableColumn<CompareSummaryRow, T>, TableCell<CompareSummaryRow, T>> createCellFactory(
        String propertyName) {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().remove(HIGHLIGHT_CELL_STYLE);

                if (item == null || empty) {
                    setText(null);
                    return;
                }

                CompareSummaryRow rowItem = getTableRow().getTableView().getItems().get(getIndex());

                setText(item.toString());

                if (rowItem.isChange() && rowItem.getChangedColumnNames().contains(propertyName)) {
                    getStyleClass().add(HIGHLIGHT_CELL_STYLE);
                }
            }
        };
    }

    private void initLabel() {
        if (itemName.isEmpty()) {
            itemName = I18N.getString("summary.unknown");
        }

        label.setText(
            I18N.getString("tab.summary.title") + StringConstant.SPACE + itemName + StringConstant.SPACE_DASH_SPACE
                + columnHeaderName);
        label.setPadding(new Insets(0, 0, 0, 10));
    }

    private void initData() {
        List<EfsElementDTO> efsElementsFirstConfig = efsElementsMap.get(secondLastConfig.getId()).stream()
            .filter(e -> !e.isDeleted()).collect(Collectors.toList());
        Collection<EfsElementDTO> efsElementsSecondConfig = efsElementsMap.get(lastConfig.getId()).stream()
            .filter(e -> !e.isDeleted()).collect(Collectors.toList());

        for (int index = efsElementsFirstConfig.size() - 1; index >= 0; index--) {
            EfsElementDTO efsElementInFirst = efsElementsFirstConfig.get(index);

            EfsElementDTO efsElementInSecond = efsElementsSecondConfig.stream().filter(
                value -> value.getWeight().equals(efsElementInFirst.getWeight()) && value.getPartNumber()
                    .equals(efsElementInFirst.getPartNumber())).findFirst().orElse(null);

            if (efsElementInSecond != null) {
                efsElementsFirstConfig.remove(index);
                efsElementsSecondConfig.remove(efsElementInSecond);
            }
        }

        ListMultimap<String, EfsElementDTO> mapOfFirst = MultimapBuilder.ListMultimapBuilder.hashKeys()
            .arrayListValues().build();
        ListMultimap<String, EfsElementDTO> mapOfSecond = MultimapBuilder.ListMultimapBuilder.hashKeys()
            .arrayListValues().build();

        for (EfsElementDTO element : efsElementsFirstConfig) {
            mapOfFirst.put(element.getPartNumber(), element);
        }

        for (EfsElementDTO element : efsElementsSecondConfig) {
            mapOfSecond.put(element.getPartNumber(), element);
        }

        List<String> partNumbersOfSecond = efsElementsSecondConfig.stream().map(AbstractEfsElementDTO::getPartNumber)
            .collect(Collectors.toList());
        List<String> partNumbersOfFirst = efsElementsFirstConfig.stream().map(AbstractEfsElementDTO::getPartNumber)
            .collect(Collectors.toList());

        List<String> completedPartNumbers = new ArrayList<>();
        checkEfsElementsChangeInPartLists(partNumbersOfFirst, completedPartNumbers, mapOfFirst, mapOfSecond);
        checkEfsElementsChangeInPartLists(partNumbersOfSecond, completedPartNumbers, mapOfFirst, mapOfSecond);
    }

    private void checkEfsElementsChangeInPartLists(List<String> partNumbers, List<String> completedPartNumbers,
        ListMultimap<String, EfsElementDTO> mapOfFirst, ListMultimap<String, EfsElementDTO> mapOfSecond) {
        for (String partNumber : partNumbers) {
            if (completedPartNumbers.contains(partNumber)) {
                continue;
            }

            List<EfsElementDTO> efsElementsFromFirst = mapOfFirst.get(partNumber);
            List<EfsElementDTO> efsElementsFromSecond = mapOfSecond.get(partNumber);

            if (efsElementsFromFirst.isEmpty()) {
                efsElementsFromSecond.forEach(efsElement -> createFgSetSummaryRow(null, efsElement,
                    I18N.getString("tablecolumn.changeState.new")));
            } else if (efsElementsFromFirst.size() == efsElementsFromSecond.size()) {
                for (int index = 0; index < efsElementsFromFirst.size(); index++) {
                    createFgSetSummaryRow(efsElementsFromFirst.get(index), efsElementsFromSecond.get(index),
                        I18N.getString("tablecolumn.changeState.changed"));
                }
            } else if (efsElementsFromFirst.size() > efsElementsFromSecond.size()) {
                int diff = efsElementsFromFirst.size() - efsElementsFromSecond.size();

                for (int index = diff - 1; index >= 0; index--) {
                    createFgSetSummaryRow(efsElementsFromFirst.get(index), null,
                        I18N.getString("tablecolumn.changeState.removed"));

                    efsElementsFromFirst.remove(efsElementsFromFirst.get(index));
                }

                for (int index = 0; index < efsElementsFromSecond.size(); index++) {
                    createFgSetSummaryRow(efsElementsFromFirst.get(index), efsElementsFromSecond.get(index),
                        I18N.getString("tablecolumn.changeState.changed"));
                }
            } else {
                int diff = efsElementsFromSecond.size() - efsElementsFromFirst.size();

                for (int index = diff - 1; index >= 0; index--) {
                    createFgSetSummaryRow(null, efsElementsFromSecond.get(index),
                        I18N.getString("tablecolumn.changeState.new"));

                    efsElementsFromSecond.remove(efsElementsFromSecond.get(index));
                }

                for (int index = 0; index < efsElementsFromSecond.size(); index++) {
                    createFgSetSummaryRow(efsElementsFromFirst.get(index), efsElementsFromSecond.get(index),
                        I18N.getString("tablecolumn.changeState.changed"));
                }
            }

            completedPartNumbers.add(partNumber);
        }
    }

    private void createFgSetSummaryRow(EfsElementDTO firstElement, EfsElementDTO secondElement, String changeState) {
        CompareSummaryRow summaryRow = new CompareSummaryRow();
        summaryRow.setFirstEfsElement(firstElement);
        summaryRow.setSecondEfsElement(secondElement);
        summaryRow.setChangeState(changeState);

        if (firstElement == null) {
            summaryRow.setDeltaWeight(secondElement.getWeight());
        } else if (secondElement == null) {
            summaryRow.setDeltaWeight(-firstElement.getWeight());
        } else {
            summaryRow.setDeltaWeight(secondElement.getWeight() - firstElement.getWeight());
            summaryRow.setChange(true);
            summaryRow.setChangedColumnNames(checkWhichColumnIsChanged(firstElement, secondElement));
        }

        elements.add(summaryRow);
    }

    private List<String> checkWhichColumnIsChanged(EfsElementDTO first, EfsElementDTO second) {
        List<String> result = new ArrayList<>(8);

        if (!first.getQuantity().equals(second.getQuantity())) {
            result.add(EfsElementTreeItemPropertyNames.QUANTITY);
        }

        if (!first.getQuantityUnit().equals(second.getQuantityUnit())) {
            result.add(EfsElementTreeItemPropertyNames.QUANTITY_UNIT);
        }

        if (!first.getWeight().equals(second.getWeight())) {
            result.add(EfsElementTreeItemPropertyNames.WEIGHT_ALL);
        }

        if (!first.getEfsElementMara().getPrioritizedWeight()
            .equals(second.getEfsElementMara().getPrioritizedWeight())) {
            result.add(EfsElementTreeItemPropertyNames.WEIGHT_PRIO);
        }

        if (!first.getEfsElementMara().getWeightWeightedTe().equals(second.getEfsElementMara().getWeightWeightedTe())) {
            result.add(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_TE);
        }

        if (!first.getEfsElementMara().getWeightCalculatedTe()
            .equals(second.getEfsElementMara().getWeightCalculatedTe())) {
            result.add(EfsElementTreeItemPropertyNames.WEIGHT_CALCULATED_TE);
        }

        if (!first.getEfsElementMara().getWeightEstimatedTe()
            .equals(second.getEfsElementMara().getWeightEstimatedTe())) {
            result.add(EfsElementTreeItemPropertyNames.WEIGHT_ESTIMATED_TE);
        }

        if (!first.getEfsElementMara().getWeightWeightedProd()
            .equals(second.getEfsElementMara().getWeightWeightedProd())) {
            result.add(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_PROD);
        }

        return result;
    }
}
