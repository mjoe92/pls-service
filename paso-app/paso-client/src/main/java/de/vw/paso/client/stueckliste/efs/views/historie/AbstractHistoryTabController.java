package de.vw.paso.client.stueckliste.efs.views.historie;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DateStringConverter;

import de.vw.paso.client.control.cell.CogCoordinates;
import de.vw.paso.client.control.cell.CogTreeTableCell;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.stueckliste.column.alignment.ColumnAlignment;
import de.vw.paso.client.stueckliste.converter.CostGroupStringConverter;
import de.vw.paso.client.stueckliste.converter.QuantityUnitStringConverter;
import de.vw.paso.client.stueckliste.converter.SetKeyStringConverter;
import de.vw.paso.client.stueckliste.efs.control.EfsCellUtil;
import de.vw.paso.client.stueckliste.efs.converter.SeparatedPartNumberStringConverter;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementHistoryTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItemPropertyNames;
import de.vw.paso.client.stueckliste.efs.views.AbstractEfsViewTabController;
import de.vw.paso.client.stueckliste.efs.views.historie.cell.AenderungsartCellFactory;
import de.vw.paso.client.stueckliste.efs.views.historie.cell.EfsHistorieCellFactory;
import de.vw.paso.client.stueckliste.efs.views.historie.model.EfsElementHistoryTreeModel;
import de.vw.paso.client.util.QuantityUnit;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import de.vw.paso.client.util.converter.LongStringConverter;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import de.vw.paso.service.partlist.efselementhistory.EfsElementDTOWrapper;

public abstract class AbstractHistoryTabController extends AbstractEfsViewTabController { // NO_UCD (use default)

    @FXML
    private GridPane gridPane;
    @FXML
    private Tab efsHistoryTab;
    @FXML
    protected CustomTreeTableView<IEfsElementForDTO> efsHistoryTreeTableView;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Long> revision;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> typeChange;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Date> timestampChange;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> userChange;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> partNumber;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> description1;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> description2;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> ap;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> setKey;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> costGroup;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Integer> quantity;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, QuantityUnit> quantityUnit;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> weightControlFlag;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Double> weightAll;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Double> weightPrio;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Double> weightWeightedTe;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Double> weightCalculatedTe;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Double> weightEstimatedTe;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Double> weightWeightedProd;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> beginDateKey;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Date> drawingDate;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> drawingStatus;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Date> beginDate;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> endDateKey;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Date> endDate;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, String> prNumberRule;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, Long> tisSort;
    @FXML
    private TreeTableColumn<IEfsElementForDTO, CogCoordinates> cog;

    protected EfsElementHistoryTreeModel efsElementHistoryTreeModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initTreeTable();
        initTreeTableColumns();

        efsHistoryTreeTableView.setPrefHeight(1024);
    }

    @Override
    public Parent getStyleableParent() {
        return efsHistoryTreeTableView;
    }

    protected void initTreeTable() {
        efsHistoryTreeTableView.showRootProperty().set(false);
        efsHistoryTreeTableView.setRoot(getEfsElementHistoryTreeModel().getRoot());
        efsHistoryTreeTableView.setEditable(true);

        efsHistoryTreeTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        efsHistoryTreeTableView.getSelectionModel().setCellSelectionEnabled(true);

        efsHistoryTreeTableView.makeHeaderWrappable();
        efsHistoryTreeTableView.makeFilterable();
    }

    protected void initTreeTableColumns() {
        initSimpleColumns();
        initColumn(userChange);
    }

    protected void initSimpleColumns() {
        String dateFormat = "dd.MM.yyyy";

        initColumn(revision, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyRevision(),
            EfsElementTreeItemPropertyNames.REVISION, new LongStringConverter());
        initTypeChangeColumn(typeChange,
            cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyTypeChange());
        initColumn(timestampChange, EfsElementTreeItemPropertyNames.TIMESTAMP_CHANGE,
            new DateStringConverter(dateFormat));
        initColumn(partNumber, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyPartNumber(),
            EfsElementTreeItemPropertyNames.PART_NUMBER, new SeparatedPartNumberStringConverter());
        initColumn(description1, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyDescription1(),
            EfsElementTreeItemPropertyNames.DESCRIPTION1);
        initColumn(description2, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyDescription2(),
            EfsElementTreeItemPropertyNames.DESCRIPTION2);
        initColumn(ap, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyAp(),
            EfsElementTreeItemPropertyNames.AP);
        initColumn(setKey, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertySetKey(),
            EfsElementTreeItemPropertyNames.SET_KEY, new SetKeyStringConverter(true));
        initColumn(costGroup, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyCostGroup(),
            EfsElementTreeItemPropertyNames.COST_GROUP, new CostGroupStringConverter(true));
        initColumn(quantity, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyQuantity(),
            EfsElementTreeItemPropertyNames.QUANTITY, new IntegerStringConverter());
        initColumn(quantityUnit, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyQuantityUnit(),
            EfsElementTreeItemPropertyNames.QUANTITY_UNIT, new QuantityUnitStringConverter());
        initColumn(weightControlFlag,
            cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyWeightControlFlag(),
            EfsElementTreeItemPropertyNames.WEIGHT_CONTROL_FLAG);
        initColumn(weightAll, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyWeightAll(),
            EfsElementTreeItemPropertyNames.WEIGHT_ALL, new DoubleStringConverter());
        initColumn(weightPrio, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyWeightPrio(),
            EfsElementTreeItemPropertyNames.WEIGHT_PRIO, new DoubleStringConverter());
        initColumn(weightWeightedTe,
            cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyWeightWeightedTe(),
            EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_TE, new DoubleStringConverter());
        initColumn(weightCalculatedTe,
            cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyWeightCalculatedTe(),
            EfsElementTreeItemPropertyNames.WEIGHT_CALCULATED_TE, new DoubleStringConverter());
        initColumn(weightEstimatedTe,
            cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyWeightEstimatedTe(),
            EfsElementTreeItemPropertyNames.WEIGHT_ESTIMATED_TE, new DoubleStringConverter());
        initColumn(weightWeightedProd,
            cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyWeightWeightedProd(),
            EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_PROD, new DoubleStringConverter());
        initColumn(beginDateKey, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyBeginDateKey(),
            EfsElementTreeItemPropertyNames.BEGIN_DATE_KEY);
        initColumn(drawingDate, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyDrawingDate(),
            EfsElementTreeItemPropertyNames.DRAWING_DATE, new DateStringConverter(dateFormat));
        initColumn(drawingStatus, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyDrawingStatus(),
            EfsElementTreeItemPropertyNames.DRAWING_STATUS);
        initColumn(beginDate, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyBeginDate(),
            EfsElementTreeItemPropertyNames.BEGIN_DATE, new DateStringConverter(dateFormat));
        initColumn(endDateKey, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyEndDateKey(),
            EfsElementTreeItemPropertyNames.END_DATE_KEY);
        initColumn(endDate, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyEndDate(),
            EfsElementTreeItemPropertyNames.END_DATE, new DateStringConverter(dateFormat));
        initColumn(prNumberRule, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyPrNumberRule(),
            EfsElementTreeItemPropertyNames.PR_NUMBER_RULE);
        initColumn(tisSort, cellData -> ((EfsElementHistoryTreeItem) cellData.getValue()).propertyTisSort(),
            EfsElementTreeItemPropertyNames.TIS_SORT, new LongStringConverter());

        initCogColumn(cog);
    }

    private <S extends IEfsElementForDTO, T> void initColumn(TreeTableColumn<S, T> column) {
        initColumn(column, EfsElementTreeItemPropertyNames.USER_CHANGE, null);
    }

    private <S extends IEfsElementForDTO, T> void initColumn(TreeTableColumn<S, T> column, String propertyName,
        StringConverter<T> converter) {
        TreeItemPropertyValueFactory<S, T> cellValueFactory = new TreeItemPropertyValueFactory<>(propertyName);
        initColumn(column, cellValueFactory, propertyName, converter);
    }

    private <S extends IEfsElementForDTO, T> void initColumn(TreeTableColumn<S, T> column,
        Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> cellValueFactory, String propertyName) {
        initColumn(column, cellValueFactory, propertyName, null);
    }

    private <S extends IEfsElementForDTO, T> void initColumn(TreeTableColumn<S, T> column,
        Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> cellValueFactory, String propertyName,
        StringConverter<T> converter) {
        column.setCellValueFactory(cellValueFactory);
        column.setCellFactory(new EfsHistorieCellFactory<>(propertyName, converter));
        column.setEditable(false);

        initColumnAlignment(column);
    }

    private <S, T> void initColumnAlignment(TreeTableColumn<S, T> column) {
        ColumnAlignment columnAlignment = ColumnAlignment.findByColumnName(column.getId());
        column.setStyle(columnAlignment.getAlignment());
    }

    private <S extends IEfsElementForDTO, T> void initTypeChangeColumn(TreeTableColumn<S, T> column,
        Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> cellValueFactory) {
        column.setCellFactory(new AenderungsartCellFactory<>(EfsElementTreeItemPropertyNames.TYPE_CHANGE));
        column.setCellValueFactory(cellValueFactory);
        column.setEditable(false);
    }

    private void initCogColumn(TreeTableColumn<IEfsElementForDTO, CogCoordinates> column) {
        column.setCellFactory(param -> {
            CogTreeTableCell<IEfsElementForDTO> cell = new CogTreeTableCell<>();
            cell.setPropertyName(EfsElementTreeItemPropertyNames.COG);
            EfsCellUtil.formatCell(cell);
            return cell;
        });

        column.setCellValueFactory(cellData -> {
            TreeItem<IEfsElementForDTO> value = cellData.getValue();
            return new SimpleObjectProperty<>(new CogCoordinates(value.getValue()));
        });

        column.setEditable(false);
    }

    protected EfsElementHistoryTreeModel getEfsElementHistoryTreeModel() {
        if (efsElementHistoryTreeModel == null) {
            // Dummy als Root
            // FIXME We should remove the root node and every root EFSELement should be "root node"
            EfsElementDTO efs = PartListFactory.createEfsElement();
            EfsElementMaraDTO efsMara = PartListFactory.createEfsElementMara();

            efs.setEfsElementMara(efsMara);

            EfsElementDTOWrapper efsElementWrapper = new EfsElementDTOWrapper(efs, efsMara);

            efsElementHistoryTreeModel = new EfsElementHistoryTreeModel(efsElementWrapper);
        }

        return efsElementHistoryTreeModel;
    }
}
