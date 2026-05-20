package de.vw.paso.client.stueckliste.efs.views.summarised;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.breadcrumb.PasoBreadCrumbBar;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.stueckliste.compare.VisibleColumnsCompareChangeEvent;
import de.vw.paso.client.stueckliste.efs.converter.SeparatedPartNumberStringConverter;
import de.vw.paso.client.stueckliste.efs.tree.ColumnSequenceChangeEvent;
import de.vw.paso.client.stueckliste.event.SelectEfsElementOnEfsTabEvent;
import de.vw.paso.client.stueckliste.teilenummer.PartNumberTableCell;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.TableCellFactory;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;

public abstract class AbstractSummarisedTabController extends BaseController<BorderPane> implements Initializable {

    @FXML
    private Label label;
    @FXML
    private PasoBreadCrumbBar<EfsElementDTO> breadCrumbBar;
    @FXML
    private BorderPane borderPaneTab;
    @FXML
    private CustomTableView<EfsElementDTO> tableView;
    @FXML
    private Button clearFilterAndSelect;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colPartNumber;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colPartNumberVornummer;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colPartNumberMittelgruppe;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colPartNumberEndNumber;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colPartNumberIndex;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colDescription1;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colDescription2;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Integer> colBomNumber;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colProduct;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colPartType;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colNodeId;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Integer> colNodeLevel;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colNodeType;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colNodeLabel;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colNodeValueParent;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colNodeValue;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colAp;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colSetKey;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colCostGroup;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colConstructionsGroup;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colProductStructure;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colPositionVariant;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colDeletionFlag;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Integer> colQuantity;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colQuantityUnit;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colQuantityUnitExtended;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colWeightControlFlag;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Double> colWeightAll;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Double> colWeightPrio;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Double> colWeightWeightedTe;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colWeightWeightedTeDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Double> colWeightCalculatedTe;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colWeightCalculatedTeDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Double> colWeightEstimatedTe;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colWeightEstimatedTeDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Double> colWeightWeightedProd;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colWeightWeightedProdDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colBeginDateKey;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colDrawingDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colDrawingStatus;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colBeginDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colEndDateKey;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colEndDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colAssemblyIndicator;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colConstructionsState;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colQuality;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Double> colMatThickness;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colSeeDrawing;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colRespConstr1;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colRespConstr2;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colBuildSampleApproval;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colBuildSampleApprovalDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colTechnicallyOkay;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colRelDateSoll;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colDesignerName;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colDesignerCostGroup;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colDesignerPhone;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colKStandRelDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colTioFreiRelDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colMFPStatus;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Double> colMFPThickness;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colKseKz;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colWeightAcceptedFromEpis;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Integer> colBaukastenFlag;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colBaukastenStatus;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colBaukastenNodeId;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colDmuRelevant;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colProcessStatus;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colMaterialType;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colEarliestPvs;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colEarliestNs;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colEarliestSop;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colPActivationDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Date> colKonstructureDate;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colAvonStatus;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colPrNumberRule;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Integer> colWahlweiseNr;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, String> colWahlweiseFall;
    @FXML
    private SummarizedTableColumn<EfsElementDTO, Long> colTisSort;

    private final ContextMenu contextMenu;

    private Collection<EfsElementDTO> efsElements;
    private boolean isCrumbAction;
    private String columnHeaderName;
    private String itemName;
    private String partListName;
    private VehicleConfigDTO vehicleConfig;
    private boolean isManualChange;
    private boolean isCompare;

    private ListChangeListener<? super TableColumn<EfsElementDTO, ?>> colSequenceChangeListener;
    private ListChangeListener<? super TableColumn<EfsElementDTO, ?>> colVisibilityChangeListener;

    public AbstractSummarisedTabController() {
        contextMenu = new ContextMenu();
        efsElements = new ArrayList<>();
    }

    @Override
    public BorderPane getControl() {
        return borderPaneTab;
    }

    @Override
    public Parent getStyleableParent() {
        return borderPaneTab;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initColumns();
        initBreadCrumbBar();
        initClearFilterAndSelectButton();

        tableView.setPrefHeight(1024);
        tableView.setOnMouseClicked(event -> {
            refreshTreeItemInCrumbBar();
            selectEfsElement(event);
            showContextMenu(event);
        });
        tableView.makeFilterable();

        MenuItem clearFilterAndSelect = new MenuItem(I18N.getString("clearfilter.select"));

        clearFilterAndSelect.setOnAction(e -> clearFilterAndSelectEfsElement());
        contextMenu.getItems().add(clearFilterAndSelect);

        colSequenceChangeListener = (ListChangeListener.Change<? extends TableColumn<EfsElementDTO, ?>> c) -> {
            if (isManualChange) {
                return;
            }

            c.next();
            if (c.wasRemoved() && !c.getRemoved().equals(c.getList())) {
                EventBus.getInstance().post(new ColumnSequenceChangeEvent(vehicleConfig,
                    tableView.getColumns().stream().map(TableColumnBase::getText).collect(Collectors.toList()),
                    getClass()));
            }
        };

        tableView.getColumns().addListener(colSequenceChangeListener);
    }

    @Override
    public void stop() {
        super.stop();

        tableView.getVisibleLeafColumns().removeListener(colVisibilityChangeListener);
        tableView.getColumns().removeListener(colSequenceChangeListener);
    }

    protected void initTable() {
        tableView.getItems().clear();
        tableView.getItems().addAll(efsElements);

        initLabel();
        initBreadCrumbBar();
    }

    private void initBreadCrumbBar() {
        breadCrumbBar.setPrefHeight(30);
        breadCrumbBar.setReadonly(true);

        if (getSelectedEfsElement() == null) {
            breadCrumbBar.setSelectedCrumb(null);
        } else {
            tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectTreeItemInCrumbBar(
                    tableView.getSelectionModel().getSelectedItem()));
            breadCrumbBar.setOnCrumbAction(this::handleCrumbAction);
        }
    }

    private void handleCrumbAction(EfsElementDTO element) {
        try {
            isCrumbAction = true;
            breadCrumbBar.setSelectedCrumb(element);
        } finally {
            isCrumbAction = false;
        }
    }

    private void selectTreeItemInCrumbBar(EfsElementDTO newValue) {
        if (isCrumbAction) {
            return;
        }

        if (newValue != null) {
            breadCrumbBar.setSelectedCrumb(getSelectedEfsElement());
            return;
        }

        if (getSelectedEfsElement() != null) {
            breadCrumbBar.setSelectedCrumb(getSelectedEfsElement().getParent());
        }
    }

    private void refreshTreeItemInCrumbBar() {
        if (getSelectedEfsElement() != null) {
            breadCrumbBar.setSelectedCrumb(getSelectedEfsElement().getParent());
            breadCrumbBar.setSelectedCrumb(getSelectedEfsElement());
        }
    }

    private void initColumns() {
        colPartNumber.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPartNumber()));
        colPartNumber.setCellFactory(TableCellFactory.forColumnTeilenummer());
        colPartNumber.setConverter(new SeparatedPartNumberStringConverter());

        colPartNumberVornummer.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getPartNumberVornummer()));

        colPartNumberMittelgruppe.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getPartNumberMittelgruppe()));

        colPartNumberEndNumber.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getPartNumberEndNumber()));

        colPartNumberIndex.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getPartNumberIndex()));

        colDescription1.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getDescription1()));

        colDescription2.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getDescription2()));

        colBomNumber.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBomNumber()));
        colBomNumber.setCellFactory(TableCellFactory.forIntegerColumn());

        colProduct.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduct()));

        colPartType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPartType()));

        colNodeId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNodeId()));

        colNodeLevel.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getNodeLevel()));
        colNodeLevel.setCellFactory(TableCellFactory.forIntegerColumn());

        colNodeLabel.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNodeLabel()));

        colNodeValueParent.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getNodeValueParent()));
        colNodeValue.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNodeValue()));

        colNodeType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNodeType()));

        colAp.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAp()));
        colAp.setCellFactory(TableCellFactory.forColumnAp());

        colSetKey.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSetKey()));

        colCostGroup.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCostGroup()));

        colConstructionsGroup.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getConstructionsGroup()));

        colProductStructure.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getProductStructure()));

        colPositionVariant.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getPositionVariant()));
        colDeletionFlag.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getDeletionFlag()));

        colQuantity.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        colQuantity.setCellFactory(TableCellFactory.forIntegerColumn());

        colQuantityUnit.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getQuantityUnit()));

        colQuantityUnitExtended.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getQuantityUnitExtended()));

        colWeightControlFlag.setCellValueFactory(
            cellData -> new SimpleStringProperty(
                cellData.getValue().getWeightControlFlag() == null ? null : cellData.getValue().getWeightControlFlag().getValue()
            ));

        colWeightAll.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getWeight()));
        colWeightAll.setCellFactory(TableCellFactory.forDoubleColumnReadOnly(true));

        colWeightPrio.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getPrioritizedWeight()));
        colWeightPrio.setCellFactory(TableCellFactory.forDoubleColumnReadOnly(true));

        colWeightWeightedTe.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getWeightWeightedTe()));
        colWeightWeightedTe.setCellFactory(TableCellFactory.forDoubleColumnReadOnly(true));

        colWeightWeightedTeDate.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getWeightWeightedTeDate()));
        colWeightWeightedTeDate.setCellFactory(TableCellFactory.forDateColumn());

        colWeightCalculatedTe.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getWeightCalculatedTe()));
        colWeightCalculatedTe.setCellFactory(TableCellFactory.forDoubleColumnReadOnly(true));

        colWeightCalculatedTeDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getEfsElementMara().getWeightCalculatedTeDate()));
        colWeightCalculatedTeDate.setCellFactory(TableCellFactory.forDateColumn());

        colWeightEstimatedTe.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getWeightEstimatedTe()));
        colWeightEstimatedTe.setCellFactory(TableCellFactory.forDoubleColumnReadOnly(true));

        colWeightEstimatedTeDate.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getWeightEstimatedTeDate()));
        colWeightEstimatedTeDate.setCellFactory(TableCellFactory.forDateColumn());

        colWeightWeightedProd.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getWeightWeightedProd()));
        colWeightWeightedProd.setCellFactory(TableCellFactory.forDoubleColumnReadOnly(true));

        colWeightWeightedProdDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getEfsElementMara().getWeightWeightedProdDate()));
        colWeightWeightedProdDate.setCellFactory(TableCellFactory.forDateColumn());

        colBeginDateKey.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getBeginDateKey()));

        colDrawingDate.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getDrawingDate()));
        colDrawingDate.setCellFactory(TableCellFactory.forDateColumn());

        colDrawingStatus.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getDrawingStatus()));

        colBeginDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBeginDate()));
        colBeginDate.setCellFactory(TableCellFactory.forDateColumn());

        colEndDateKey.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndDateKey()));

        colEndDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndDate()));
        colEndDate.setCellFactory(TableCellFactory.forDateColumn());

        colAssemblyIndicator.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getAssemblyIndicator()));

        colConstructionsState.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getConstructionsState()));

        colQuality.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getQuality()));

        colMatThickness.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getMaterialThickness()));
        colMatThickness.setCellFactory(TableCellFactory.forDoubleColumnReadOnly(true));

        colSeeDrawing.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getSeeDrawing()));

        colRespConstr1.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getResponsibleConstr1()));

        colRespConstr2.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getResponsibleConstr2()));

        colBuildSampleApproval.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getBuildSampleApproval()));

        colBuildSampleApprovalDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            cellData.getValue().getEfsElementMara().getBuildSampleApprovalTargetDate()));
        colBuildSampleApprovalDate.setCellFactory(TableCellFactory.forDateColumn());

        colTechnicallyOkay.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getTechnicallyOkay()));

        colRelDateSoll.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getReleaseDateSoll()));
        colRelDateSoll.setCellFactory(TableCellFactory.forDateColumn());

        colDesignerName.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getDesignerName()));
        colDesignerCostGroup.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getDesignerCostGroup()));
        colDesignerPhone.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getDesignerPhoneNumber()));

        colKStandRelDate.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getKStandReleaseDate()));
        colKStandRelDate.setCellFactory(TableCellFactory.forDateColumn());

        colTioFreiRelDate.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getTioFreiReleaseDate()));
        colTioFreiRelDate.setCellFactory(TableCellFactory.forDateColumn());

        colMFPStatus.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getMfpStatus()));

        colMFPThickness.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEfsElementMara().getMfpThickness()));
        colMFPThickness.setCellFactory(TableCellFactory.forDoubleColumnReadOnly(true));

        colKseKz.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getKseKz()));

        colWeightAcceptedFromEpis.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getEfsElementMara().getWeightAcceptedFromEPIS()));

        colBaukastenFlag.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getBaukasten()));
        colBaukastenFlag.setCellFactory(TableCellFactory.forIntegerColumn());

        colBaukastenStatus.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getBaukastenStatus()));

        colBaukastenNodeId.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getBaukastenNodeId()));

        colDmuRelevant.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDmuRelevant()));

        colProcessStatus.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getProcessStatus()));

        colMaterialType.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getMaterialType()));

        colEarliestPvs.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEarliestPvs()));
        colEarliestPvs.setCellFactory(TableCellFactory.forDateColumn());

        colEarliestNs.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEarliestNs()));
        colEarliestNs.setCellFactory(TableCellFactory.forDateColumn());

        colEarliestSop.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getEarliestSop()));
        colEarliestSop.setCellFactory(TableCellFactory.forDateColumn());

        colPActivationDate.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getPActivationDate()));
        colPActivationDate.setCellFactory(TableCellFactory.forDateColumn());

        colKonstructureDate.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getKonstructureDate()));
        colKonstructureDate.setCellFactory(TableCellFactory.forDateColumn());

        colAvonStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAvonStatus()));

        colPrNumberRule.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getPrNumberRule()));

        colWahlweiseFall.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getWahlweiseFall()));
        colWahlweiseNr.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(cellData.getValue().getWahlweiseNr()));

        colTisSort.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTisSort()));
        colTisSort.setCellFactory(TableCellFactory.forLongColumn());
    }

    private void initLabel() {
        if (itemName.isEmpty()) {
            itemName = I18N.getString("summary.unknown");
        }

        String text = partListName == null ? I18N.getString("tab.title") + StringConstant.SPACE + itemName
            + StringConstant.SPACE_DASH_SPACE + columnHeaderName
            : partListName + StringConstant.SPACE_DASH_SPACE + I18N.getString("tab.title") + StringConstant.SPACE
                + itemName + StringConstant.SPACE_DASH_SPACE + columnHeaderName;

        label.setText(text);
        label.setPadding(new Insets(0, 0, 0, 10));
    }

    public EfsElementDTO getSelectedEfsElement() {
        return tableView.getSelectionModel().getSelectedItem() == null ? null
            : tableView.getSelectionModel().getSelectedItem();
    }

    private void selectEfsElement(MouseEvent event) {
        if (MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() == 1
            && getSelectedEfsElement() != null) {
            EventBus.getInstance()
                .post(new SelectEfsElementOnEfsTabEvent(false, vehicleConfig.getId(), getSelectedEfsElement().getId()));
        }
    }

    private void showContextMenu(MouseEvent event) {
        if (MouseButton.SECONDARY.equals(event.getButton()) && event.getClickCount() == 1
            && tableView.getSelectionModel().getSelectedItem() != null) {
            contextMenu.hide();

            if (event.getPickResult().getIntersectedNode() instanceof PartNumberTableCell
                && tableView.getSelectionModel().getSelectedItem().getPartNumber()
                .equals(((PartNumberTableCell) event.getPickResult().getIntersectedNode()).getItem())) {
                contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
            } else if (event.getPickResult().getIntersectedNode() instanceof Text) {
                String partNumFromCell = ((Text) event.getPickResult().getIntersectedNode()).getText();
                partNumFromCell = partNumFromCell.replace(StringConstant.DOT, StringConstant.EMPTY);

                if (partNumFromCell.equals(tableView.getSelectionModel().getSelectedItem().getPartNumber())) {
                    contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
                }
            }
        } else {
            contextMenu.hide();
        }
    }

    private void initClearFilterAndSelectButton() {
        clearFilterAndSelect.setOnAction(event -> clearFilterAndSelectEfsElement());
    }

    public void clearFilterAndSelectEfsElement() {
        EventBus.getInstance()
            .post(new SelectEfsElementOnEfsTabEvent(true, vehicleConfig.getId(), getSelectedEfsElement().getId()));
    }

    public void initEfsElementList(List<EfsElementDTO> efsElements, EfsElementDTO lastSelectedEfsElement) {
        this.efsElements = efsElements.stream().filter(e -> !e.isDeleted()).collect(Collectors.toList());

        initTable();

        if (efsElements.contains(lastSelectedEfsElement)) {
            tableView.getSelectionModel().select(lastSelectedEfsElement);
            tableView.scrollTo(lastSelectedEfsElement);
        }
    }

    public void initEfsElementList(List<EfsElementDTO> efsElements, String columnHeaderName, String itemName,
        VehicleConfigDTO vehicleConfig, String partListName, boolean isCompare) {
        this.efsElements = efsElements.stream().filter(e -> !e.isDeleted()).collect(Collectors.toList());
        this.columnHeaderName = columnHeaderName;
        this.itemName = itemName;
        this.vehicleConfig = vehicleConfig;
        this.isCompare = isCompare;

        if (partListName != null) {
            this.partListName = partListName;
        }

        initTable();

        if (isCompare) {
            addVisibleColumnChangeListener();
        }
    }

    private void addVisibleColumnChangeListener() {
        tableView.getVisibleLeafColumns()
            .addListener((ListChangeListener.Change<? extends TableColumn<EfsElementDTO, ?>> c) -> {
                if (c.next() && !isManualChange) {
                    EventBus.getInstance().post(new VisibleColumnsCompareChangeEvent(
                        tableView.getVisibleLeafColumns().stream().map(TableColumnBase::getText)
                            .collect(Collectors.toList()), getClass()));
                }
            });
    }

    @Subscribe
    public void handleColumnSequenceChanged(ColumnSequenceChangeEvent event) {
        if (event.getSenderClass().equals(getClass()) || isCompare && event.getVehicleConfig() != null
            || !isCompare && event.getVehicleConfig() == null || !Objects.equals(vehicleConfig,
            event.getVehicleConfig())) {
            return;
        }

        isManualChange = true;

        if (colVisibilityChangeListener != null) {
            tableView.getVisibleLeafColumns().removeListener(colVisibilityChangeListener);
        }

        Map<String, ? extends TableColumn<EfsElementDTO, ?>> tableColumnsByName = tableView.getColumns().stream()
            .collect(Collectors.toMap(TableColumnBase::getText, Function.identity()));

        Collection<? extends TableColumn<EfsElementDTO, ?>> columnsInChangedOrder = event.getColumns().stream()
            .map(tableColumnsByName::get).toList();

        tableView.getColumns().clear();
        tableView.getColumns().addAll(columnsInChangedOrder);

        if (colVisibilityChangeListener != null) {
            tableView.getVisibleLeafColumns().addListener(colVisibilityChangeListener);
        }

        isManualChange = false;
    }

    public void initVisibleColumnListener(
        ListChangeListener<? super TableColumn<EfsElementDTO, ?>> listChangeListener) {
        colVisibilityChangeListener = listChangeListener;

        tableView.getVisibleLeafColumns().addListener(listChangeListener);
    }

    @Subscribe
    public void columnVisibilityChangedOnCompare(VisibleColumnsCompareChangeEvent event) {
        if (isCompare && !getClass().equals(event.getSenderClass())) {
            isManualChange = true;

            for (TableColumn<EfsElementDTO, ?> column : tableView.getColumns()) {
                column.setVisible(event.getColumns().contains(column.getText()));
            }

            isManualChange = false;
        }
    }

    public CustomTableView<EfsElementDTO> getTableView() {
        return tableView;
    }
}
