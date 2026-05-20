package de.vw.paso.client.stueckliste.efs.views.suche;

import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DateStringConverter;

import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.control.cell.CogCoordinates;
import de.vw.paso.client.control.cell.CogTreeTableCell;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.control.textfield.PasoCustomTextField;
import de.vw.paso.client.stueckliste.efs.converter.SeparatedPartNumberStringConverter;
import de.vw.paso.client.stueckliste.efs.tree.SingleVehiclePartListController;
import de.vw.paso.client.stueckliste.efs.tree.VisibleColumnsChangedEvent;
import de.vw.paso.client.stueckliste.efs.views.AbstractEfsViewTabController;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabType;
import de.vw.paso.client.stueckliste.efs.views.historie.event.EfsElementSearchEvent;
import de.vw.paso.client.stueckliste.efs.views.historie.event.EfsElementSelectionEvent;
import de.vw.paso.client.stueckliste.efs.views.suche.cell.EfsSucheCellFactory;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import de.vw.paso.client.util.converter.LongStringConverter;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementDTO;

@FXController(name = "search-tab")
public class SearchTabController extends AbstractEfsViewTabController {

    @FXML
    private Tab tabSearch;
    @FXML
    private PasoCustomTextField<String> searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private CustomTableView<AbstractEfsElementDTO> searchEfsTableView;
    @FXML
    private TableColumn<EfsElementDTO, String> colPartNumber;
    @FXML
    private TableColumn<EfsElementDTO, String> colPartNumberVornummer;
    @FXML
    private TableColumn<EfsElementDTO, String> colPartNumberMittelgruppe;
    @FXML
    private TableColumn<EfsElementDTO, String> colPartNumberEndNumber;
    @FXML
    private TableColumn<EfsElementDTO, String> colPartNumberIndex;
    @FXML
    private TableColumn<EfsElementDTO, String> colDescription1;
    @FXML
    private TableColumn<EfsElementDTO, String> colDescription2;
    @FXML
    private TableColumn<EfsElementDTO, Integer> colBomNumber;
    @FXML
    private TableColumn<EfsElementDTO, String> colProduct;
    @FXML
    private TableColumn<EfsElementDTO, String> colPartType;
    @FXML
    private TableColumn<EfsElementDTO, Integer> colNodeLevel;
    @FXML
    private TableColumn<EfsElementDTO, String> colNodeType;
    @FXML
    private TableColumn<EfsElementDTO, String> colNodeLabel;
    @FXML
    private TableColumn<EfsElementDTO, String> colAp;
    @FXML
    private TableColumn<EfsElementDTO, String> colSetKey;
    @FXML
    private TableColumn<EfsElementDTO, String> colCostGroup;
    @FXML
    private TableColumn<EfsElementDTO, String> colConstructionsGroup;
    @FXML
    private TableColumn<EfsElementDTO, String> colProductStructure;
    @FXML
    private TableColumn<EfsElementDTO, Integer> colQuantity;
    @FXML
    private TableColumn<EfsElementDTO, String> colQuantityUnit;
    @FXML
    private TableColumn<EfsElementDTO, String> colQuantityUnitExtended;
    @FXML
    private TableColumn<EfsElementDTO, String> colWeightControlFlag;
    @FXML
    private TableColumn<EfsElementDTO, Double> colWeightNode;
    @FXML
    private TableColumn<EfsElementDTO, Double> colWeightAll;
    @FXML
    private TableColumn<EfsElementDTO, Double> colWeightPrio;
    @FXML
    private TableColumn<EfsElementDTO, Double> colWeightWeightedTe;
    @FXML
    private TableColumn<EfsElementDTO, Date> colWeightWeightedTeDate;
    @FXML
    private TableColumn<EfsElementDTO, Double> colWeightCalculatedTe;
    @FXML
    private TableColumn<EfsElementDTO, Date> colWeightCalculatedTeDate;
    @FXML
    private TableColumn<EfsElementDTO, Double> colWeightEstimatedTe;
    @FXML
    private TableColumn<EfsElementDTO, Date> colWeightEstimatedTeDate;
    @FXML
    private TableColumn<EfsElementDTO, Double> colWeightWeightedProd;
    @FXML
    private TableColumn<EfsElementDTO, Date> colWeightWeightedProdDate;
    @FXML
    private TableColumn<EfsElementDTO, String> colBeginDateKey;
    @FXML
    private TableColumn<EfsElementDTO, Date> colDrawingDate;
    @FXML
    private TableColumn<EfsElementDTO, String> colDrawingStatus;
    @FXML
    private TableColumn<EfsElementDTO, Date> colBeginDate;
    @FXML
    private TableColumn<EfsElementDTO, String> colEndDateKey;
    @FXML
    private TableColumn<EfsElementDTO, Date> colEndDate;
    @FXML
    private TableColumn<EfsElementDTO, String> colAssemblyIndicator;
    @FXML
    private TableColumn<EfsElementDTO, String> colConstructionsState;
    @FXML
    private TableColumn<EfsElementDTO, String> colQuality;
    @FXML
    private TableColumn<EfsElementDTO, Double> colMatThickness;
    @FXML
    private TableColumn<EfsElementDTO, String> colSeeDrawing;
    @FXML
    private TableColumn<EfsElementDTO, String> colRespConstr1;
    @FXML
    private TableColumn<EfsElementDTO, String> colRespConstr2;
    @FXML
    private TableColumn<EfsElementDTO, String> colBuildSampleApproval;
    @FXML
    private TableColumn<EfsElementDTO, Date> colBuildSampleApprovalDate;
    @FXML
    private TableColumn<EfsElementDTO, String> colTechnicallyOkay;
    @FXML
    private TableColumn<EfsElementDTO, Date> colRelDateSoll;
    @FXML
    private TableColumn<EfsElementDTO, String> colDesignerName;
    @FXML
    private TableColumn<EfsElementDTO, String> colDesignerCostGroup;
    @FXML
    private TableColumn<EfsElementDTO, String> colDesignerPhone;
    @FXML
    private TableColumn<EfsElementDTO, Date> colKStandRelDate;
    @FXML
    private TableColumn<EfsElementDTO, Date> colTioFreiRelDate;
    @FXML
    private TableColumn<EfsElementDTO, String> colMFPStatus;
    @FXML
    private TableColumn<EfsElementDTO, Double> colMFPThickness;
    @FXML
    private TableColumn<EfsElementDTO, String> colKseKz;
    @FXML
    private TableColumn<EfsElementDTO, String> colWeightAcceptedFromEpis;
    @FXML
    private TableColumn<EfsElementDTO, Integer> colBaukastenFlag;
    @FXML
    private TableColumn<EfsElementDTO, String> colBaukastenStatus;
    @FXML
    private TableColumn<EfsElementDTO, String> colBaukastenNodeId;
    @FXML
    private TableColumn<EfsElementDTO, String> colDmuRelevant;
    @FXML
    private TableColumn<EfsElementDTO, String> colProcessStatus;
    @FXML
    private TableColumn<EfsElementDTO, String> colMaterialType;
    @FXML
    private TableColumn<EfsElementDTO, Date> colEarliestPvs;
    @FXML
    private TableColumn<EfsElementDTO, Date> colEarliestNs;
    @FXML
    private TableColumn<EfsElementDTO, Date> colEarliestSop;
    @FXML
    private TableColumn<EfsElementDTO, Date> colPActivationDate;
    @FXML
    private TableColumn<EfsElementDTO, Date> colConstructureDate;
    @FXML
    private TableColumn<EfsElementDTO, String> colAvonStatus;
    @FXML
    private TableColumn<EfsElementDTO, String> colPrNumberRule;
    @FXML
    private TableColumn<EfsElementDTO, Long> colTisSort;
    @FXML
    private TableColumn<EfsElementDTO, CogCoordinates> cog;
    @FXML
    private TableColumn<EfsElementDTO, String> colNodeId;
    @FXML
    private TableColumn<EfsElementDTO, String> colWahlweiseFall;
    @FXML
    private TableColumn<EfsElementDTO, Integer> colWahlweiseNr;

    private final ObservableList<AbstractEfsElementDTO> searchData;
    private final ObjectProperty<EventHandler<EfsElementSearchEvent>> efsSearchProperty;

    private SingleVehiclePartListController parentController;

    private boolean isColumnChanging;

    public SearchTabController() {
        searchData = FXCollections.observableArrayList();
        efsSearchProperty = new SimpleObjectProperty<>(this, "EfsElement");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        tabSearch.setGraphic(new ImageView(ActionIcon.SEARCH_16X16.getImage()));

        initTable();
        initTableColumns();

        initListeners();

        searchEfsTableView.makeFilterable();
        // runLater() to request the textfield whn this controller is visible.
        Platform.runLater(() -> searchTextField.requestFocus());

        EventBus.getInstance().register(this);
    }

    @Subscribe
    public void onVisibleColumnsChangedEvent(VisibleColumnsChangedEvent event) {
        setVisibleColumns(event.getColumns());
    }

    @Override
    public Tab getControl() {
        return tabSearch;
    }

    @Override
    public Parent getStyleableParent() {
        return searchEfsTableView;
    }

    public final ObjectProperty<EventHandler<EfsElementSearchEvent>> efsSearchProperty() { // NO_UCD (use private)
        return efsSearchProperty;
    }

    public final void setEfsSearchAction(EventHandler<EfsElementSearchEvent> eventHandler) { // NO_UCD (use private)
        efsSearchProperty().set(eventHandler);
    }

    public void setSearchEfsElements(List<EfsElementDTO> searchEfsElements) {
        searchData.clear();
        searchData.addAll(searchEfsElements);
        searchEfsTableView.setItems(searchData);
    }

    public void setVisibleColumns(List<String> visibleColumns) {
        if (!isColumnChanging) {
            isColumnChanging = true;
            searchEfsTableView.getColumns().forEach(e -> e.setVisible(visibleColumns.contains(e.getText())));
            searchEfsTableView.getColumns().sort(Comparator.comparingInt(o -> visibleColumns.indexOf(o.getText())));
            isColumnChanging = false;
        }
    }

    @Override
    protected EfsViewTabType getType() {
        return EfsViewTabType.SEARCH;
    }

    @FXML
    private void handleSearch() {
        handleSearchKeyPressed(
            new KeyEvent(KeyEvent.KEY_PRESSED, null, null, KeyCode.ENTER, false, false, false, false));
    }

    @FXML
    private void handleSearchKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER) && searchTextField.textProperty().get() != null
            && !searchTextField.textProperty().get().isEmpty()) {
            efsSearchProperty.get().handle(
                new EfsElementSearchEvent(this, EfsElementSearchEvent.EFS_ELEMENT_SUCHE, this,
                    searchTextField.textProperty().get()));
        }
    }

    private void initTable() {
        searchEfsTableView.makeHeaderWrappable();
        searchEfsTableView.getVisibleLeafColumns()
            .addListener((ListChangeListener<TableColumn<AbstractEfsElementDTO, ?>>) c -> {
                if (!isColumnChanging) {
                    isColumnChanging = true;
                    EventBus.getInstance().post(
                        new VisibleColumnsChangedEvent(parentController.vehicleConfigProperty().get(),
                            VisibleColumnsChangedEvent.getColumnNames(searchEfsTableView.getVisibleLeafColumns())));
                    isColumnChanging = false;
                }
            });
    }

    private void initTableColumns() {
        DateStringConverter dateStringConverter = new DateStringConverter("dd.MM.yyyy");
        IntegerStringConverter integerStringConverter = new IntegerStringConverter();
        DoubleStringConverter doubleStringConverter = new DoubleStringConverter();

        initColumn(colPartNumber, EfsElementDTO::getPartNumber, new SeparatedPartNumberStringConverter());
        initColumn(colPartNumberVornummer, e -> e.getEfsElementMara().getPartNumberVornummer());
        initColumn(colPartNumberMittelgruppe, e -> e.getEfsElementMara().getPartNumberMittelgruppe());
        initColumn(colPartNumberEndNumber, e -> e.getEfsElementMara().getPartNumberMittelgruppe());
        initColumn(colPartNumberIndex, e -> e.getEfsElementMara().getPartNumberIndex());

        initColumn(colDescription1, AbstractEfsElementDTO::getDescription1);
        initColumn(colDescription2, AbstractEfsElementDTO::getDescription2);

        initColumn(colBomNumber, AbstractEfsElementDTO::getBomNumber, integerStringConverter);
        initColumn(colProduct, AbstractEfsElementDTO::getProduct);
        initColumn(colPartType, AbstractEfsElementDTO::getPartType);
        initColumn(colNodeId, AbstractEfsElementDTO::getNodeId);
        initColumn(colNodeLevel, AbstractEfsElementDTO::getNodeLevel);
        initColumn(colNodeType, AbstractEfsElementDTO::getNodeType);
        initColumn(colNodeLabel, AbstractEfsElementDTO::getNodeLabel);

        initColumn(colAp, AbstractEfsElementDTO::getAp);

        initColumn(colSetKey, AbstractEfsElementDTO::getSetKey);
        initColumn(colCostGroup, AbstractEfsElementDTO::getCostGroup);
        initColumn(colConstructionsGroup, AbstractEfsElementDTO::getConstructionsGroup);
        initColumn(colProductStructure, AbstractEfsElementDTO::getProductStructure);

        initColumn(colQuantity, AbstractEfsElementDTO::getQuantity, integerStringConverter);
        initColumn(colQuantityUnit, AbstractEfsElementDTO::getQuantityUnit);
        initColumn(colQuantityUnitExtended, AbstractEfsElementDTO::getQuantityUnitExtended);

        initColumn(colWeightControlFlag, element -> getWeightControlFlagValue(element.getWeightControlFlag()));

        initColumn(colWeightNode, EfsElementDTO::getNodeWeight, doubleStringConverter);
        initColumn(colWeightAll, AbstractEfsElementDTO::getTotalWeight, doubleStringConverter);
        initColumn(colWeightPrio, e -> e.getEfsElementMara().getPrioritizedWeight(), doubleStringConverter);
        initColumn(colWeightWeightedTe, e -> e.getEfsElementMara().getWeightWeightedTe(), doubleStringConverter);
        initColumn(colWeightWeightedTeDate, e -> e.getEfsElementMara().getWeightWeightedTeDate(), dateStringConverter);
        initColumn(colWeightCalculatedTe, e -> e.getEfsElementMara().getWeightCalculatedTe(), doubleStringConverter);
        initColumn(colWeightCalculatedTeDate, e -> e.getEfsElementMara().getWeightCalculatedTeDate(),
            dateStringConverter);
        initColumn(colWeightEstimatedTe, e -> e.getEfsElementMara().getWeightEstimatedTe(), doubleStringConverter);
        initColumn(colWeightEstimatedTeDate, e -> e.getEfsElementMara().getWeightEstimatedTeDate(),
            dateStringConverter);
        initColumn(colWeightWeightedProd, e -> e.getEfsElementMara().getWeightWeightedProd(), doubleStringConverter);
        initColumn(colWeightWeightedProdDate, e -> e.getEfsElementMara().getWeightWeightedProdDate(),
            dateStringConverter);

        initColumn(colBeginDateKey, AbstractEfsElementDTO::getBeginDateKey);
        initColumn(colDrawingDate, e -> e.getEfsElementMara().getDrawingDate(), dateStringConverter);
        initColumn(colDrawingStatus, e -> e.getEfsElementMara().getDrawingStatus());
        initColumn(colBeginDate, AbstractEfsElementDTO::getBeginDate, dateStringConverter);
        initColumn(colEndDateKey, AbstractEfsElementDTO::getEndDateKey);
        initColumn(colEndDate, AbstractEfsElementDTO::getEndDate, dateStringConverter);

        initColumn(colAssemblyIndicator, e -> e.getEfsElementMara().getAssemblyIndicator());
        initColumn(colConstructionsState, e -> e.getEfsElementMara().getConstructionsState());
        initColumn(colQuality, e -> e.getEfsElementMara().getQuality());
        initColumn(colMatThickness, e -> e.getEfsElementMara().getMaterialThickness(), doubleStringConverter);
        initColumn(colSeeDrawing, e -> e.getEfsElementMara().getSeeDrawing());
        initColumn(colRespConstr1, e -> e.getEfsElementMara().getResponsibleConstr1());
        initColumn(colRespConstr2, e -> e.getEfsElementMara().getResponsibleConstr2());
        initColumn(colBuildSampleApproval, e -> e.getEfsElementMara().getBuildSampleApproval());
        initColumn(colBuildSampleApprovalDate, e -> e.getEfsElementMara().getBuildSampleApprovalTargetDate(),
            dateStringConverter);
        initColumn(colTechnicallyOkay, e -> e.getEfsElementMara().getTechnicallyOkay());
        initColumn(colRelDateSoll, e -> e.getEfsElementMara().getReleaseDateSoll(), dateStringConverter);
        initColumn(colDesignerName, e -> e.getEfsElementMara().getDesignerName());
        initColumn(colDesignerCostGroup, e -> e.getEfsElementMara().getDesignerCostGroup());
        initColumn(colDesignerPhone, e -> e.getEfsElementMara().getDesignerPhoneNumber());
        initColumn(colKStandRelDate, e -> e.getEfsElementMara().getKStandReleaseDate(), dateStringConverter);
        initColumn(colTioFreiRelDate, e -> e.getEfsElementMara().getTioFreiReleaseDate(), dateStringConverter);
        initColumn(colMFPStatus, e -> e.getEfsElementMara().getMfpStatus());
        initColumn(colMFPThickness, e -> e.getEfsElementMara().getMfpThickness(), doubleStringConverter);
        initColumn(colKseKz, e -> e.getEfsElementMara().getKseKz());
        initColumn(colWeightAcceptedFromEpis, e -> e.getEfsElementMara().getWeightAcceptedFromEPIS());
        initColumn(colBaukastenFlag, AbstractEfsElementDTO::getBaukasten, integerStringConverter);
        initColumn(colBaukastenStatus, AbstractEfsElementDTO::getBaukastenStatus);
        initColumn(colBaukastenNodeId, AbstractEfsElementDTO::getBaukastenNodeId);
        initColumn(colDmuRelevant, AbstractEfsElementDTO::getDmuRelevant);
        initColumn(colProcessStatus, AbstractEfsElementDTO::getProcessStatus);
        initColumn(colMaterialType, AbstractEfsElementDTO::getMaterialType);
        initColumn(colEarliestPvs, AbstractEfsElementDTO::getEarliestPvs, dateStringConverter);
        initColumn(colEarliestNs, AbstractEfsElementDTO::getEarliestNs, dateStringConverter);
        initColumn(colEarliestSop, AbstractEfsElementDTO::getEarliestSop, dateStringConverter);
        initColumn(colPActivationDate, AbstractEfsElementDTO::getPActivationDate, dateStringConverter);
        initColumn(colConstructureDate, AbstractEfsElementDTO::getKonstructureDate, dateStringConverter);
        initColumn(colAvonStatus, AbstractEfsElementDTO::getAvonStatus);

        initColumn(colPrNumberRule, AbstractEfsElementDTO::getPrNumberRule);

        initColumn(colTisSort, AbstractEfsElementDTO::getTisSort, new LongStringConverter());
        initColumn(cog, e -> new CogCoordinates(e.getCogX(), e.getCogY(), e.getCogZ()),
            new CogTreeTableCell.CogConverter());
        initColumn(colWahlweiseFall, AbstractEfsElementDTO::getWahlweiseFall);
        initColumn(colWahlweiseNr, AbstractEfsElementDTO::getWahlweiseNr, integerStringConverter);
    }

    private String getWeightControlFlagValue(WeightControlFlag gws) {
        return gws == null ? null : gws.getValue();
    }

    private <S extends AbstractEfsElementDTO, T> void initColumn(TableColumn<S, T> column,
        Function<S, T> propertyGetter) {
        initColumn(column, propertyGetter, null);
    }

    private <S extends AbstractEfsElementDTO, T> void initColumn(TableColumn<S, T> column,
        Function<S, T> propertyGetter, StringConverter<T> converter) {
        initColumnImpl(column, e -> new SimpleObjectProperty<>(propertyGetter.apply(e.getValue())), converter);
    }

    private <S extends AbstractEfsElementDTO, T> void initColumnImpl(TableColumn<S, T> column,
        Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> cellValueFactory,
        StringConverter<T> converter) {
        EfsSucheCellFactory<S, T> cellFactory = new EfsSucheCellFactory<>(converter);
        column.setCellValueFactory(cellValueFactory);
        column.setCellFactory(cellFactory);
        column.setEditable(false);
    }

    private void initListeners() {
        searchButton.disableProperty().set(true);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> setButtonVisibility());

        searchEfsTableView.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> selectEfsElement());
    }

    private void setButtonVisibility() {
        String textFieldText = searchTextField.textProperty().get();

        searchButton.disableProperty().set(textFieldText == null || textFieldText.isEmpty());
    }

    private void selectEfsElement() {
        AbstractEfsElementDTO selectedItem = getSelectedItem();
        if (selectedItem != null) {
            efsSelectionProperty().get().handle(
                new EfsElementSelectionEvent(this, EfsElementSelectionEvent.SELECT_EFS_ELEMENT_IN_TREE,
                    selectedItem.getId()));
        }
    }

    private AbstractEfsElementDTO getSelectedItem() {
        var selectionModel = searchEfsTableView.getSelectionModel();
        return selectionModel.isEmpty() ? null : selectionModel.getSelectedItem();
    }

    public void setParentController(SingleVehiclePartListController parentController) {
        this.parentController = parentController;
    }
}
