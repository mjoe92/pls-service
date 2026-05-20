package de.vw.paso.client.stueckliste.efs.views.properties;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.util.converter.DateStringConverter;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.stueckliste.efs.converter.SeparatedPartNumberStringConverter;
import de.vw.paso.client.util.TableCellFactory;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import de.vw.paso.client.util.converter.LongStringConverter;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.partlist.domain.PartProperty;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.stage.Stage;
import de.vw.paso.utility.StringConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FXController(name = "part-properties-tab")
public class PartPropertiesTabController extends BaseController<Tab> implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(PartPropertiesTabController.class);

    private static final String SEPARATOR = StringConstant.SPACE_DASH_SPACE;

    @FXML
    private Tab partPropertiesTab;
    @FXML
    private CustomTableView<PartProperty> partPropertiesTable;
    @FXML
    private TableColumn<PartProperty, String> colPropertyName;
    @FXML
    private TableColumn<PartProperty, String> colProperty;

    private final ObservableList<PartProperty> partProperties = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initTableColumns();
        initTable();
    }

    @Override
    public Tab getControl() {
        return partPropertiesTab;
    }

    @Override
    public Parent getStyleableParent() {
        return partPropertiesTable;
    }

    private void initTable() {
        partPropertiesTable.setItems(partProperties);
    }

    private void initTableColumns() {
        colPropertyName.setCellValueFactory(
                cellData -> new SimpleStringProperty(I18N.getString(cellData.getValue().getPropertyNameKey())));
        colProperty.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPropertyValue()));
        colProperty.setCellFactory(TableCellFactory.forPartPropertyValueColumn());
    }

    private void fillTable(Collection<PartProperty> items) {
        this.partProperties.clear();
        this.partProperties.addAll(items);
    }

    public void setEfsElementProperties(EfsElementDTO efsElement) {
        DateStringConverter dateStringConverter = new DateStringConverter("dd.MM.yyyy");
        IntegerStringConverter integerStringConverter = new IntegerStringConverter();
        DoubleStringConverter doubleStringConverter = new DoubleStringConverter();

        Collection<PartProperty> propertyList = new ArrayList<>(78);
        if (Stage.LOCAL == PasoClientProperties.get().getStage()) {
            createProperty("property.id", efsElement.getId().toString(), propertyList);
        }

        createProperty("property.nodeId", efsElement.getNodeId(), propertyList);
        createProperty("property.teilenummer",
                new SeparatedPartNumberStringConverter().toString(efsElement.getPartNumber()), propertyList);
        createProperty("property.teilenummer.vornummer", efsElement.getEfsElementMara().getPartNumberVornummer(),
                propertyList);
        createProperty("property.teilenummer.mittelgruppe", efsElement.getEfsElementMara().getPartNumberMittelgruppe(),
                propertyList);
        createProperty("property.teilenummer.endnumber", efsElement.getEfsElementMara().getPartNumberEndNumber(),
                propertyList);
        createProperty("property.teilenummer.index", efsElement.getEfsElementMara().getPartNumberIndex(), propertyList);
        createProperty("property.benennung", efsElement.getDescription1(), propertyList);
        createProperty("property.zusatzbenennung", efsElement.getDescription2(), propertyList);
        createProperty("property.benennung", efsElement.getProduct(), propertyList);
        createProperty("property.bomnumber", integerStringConverter.toString(efsElement.getBomNumber()), propertyList);
        createProperty("property.parttype", efsElement.getPartType(), propertyList);
        createProperty("property.nodeLevel", integerStringConverter.toString(efsElement.getNodeLevel()), propertyList);
        createProperty("property.nodeType", efsElement.getNodeType(), propertyList);
        createProperty("property.nodeLabel", efsElement.getNodeLabel(), propertyList);
        createProperty("node.value.parent", efsElement.getNodeValueParent(), propertyList);
        createProperty("property.nodeValue", efsElement.getNodeValue(), propertyList);
        createProperty("property.menge", integerStringConverter.toString(efsElement.getQuantity()), propertyList);
        createProperty("property.gws", readWeightControlFlag(efsElement), propertyList);
        createProperty("property.gewichtknoten", doubleStringConverter.toString(efsElement.getNodeWeight()),
                propertyList);
        createProperty("property.gewichtgesamt", doubleStringConverter.toString(efsElement.getWeight()), propertyList);
        createProperty("property.gewichtprio",
                doubleStringConverter.toString(efsElement.getEfsElementMara().getPrioritizedWeight()), propertyList);
        createProperty("property.konstgewogen",
                doubleStringConverter.toString(efsElement.getEfsElementMara().getWeightWeightedTe()), propertyList);
        createProperty("property.konstgewogendate",
                dateStringConverter.toString(efsElement.getEfsElementMara().getWeightWeightedTeDate()), propertyList);
        createProperty("property.konsterrechnet",
                doubleStringConverter.toString(efsElement.getEfsElementMara().getWeightCalculatedTe()), propertyList);
        createProperty("property.konsterrechnetdate",
                dateStringConverter.toString(efsElement.getEfsElementMara().getWeightCalculatedTeDate()), propertyList);
        createProperty("property.konstgeschaetzt",
                doubleStringConverter.toString(efsElement.getEfsElementMara().getWeightEstimatedTe()), propertyList);
        createProperty("property.konstgeschaetztdate",
                dateStringConverter.toString(efsElement.getEfsElementMara().getWeightEstimatedTeDate()), propertyList);
        createProperty("property.prodgewogen",
                doubleStringConverter.toString(efsElement.getEfsElementMara().getWeightWeightedProd()), propertyList);
        createProperty("property.prodgewogendate",
                dateStringConverter.toString(efsElement.getEfsElementMara().getWeightWeightedProdDate()), propertyList);
        createProperty("property.ap", getWorkPackageNumberString(efsElement.getAp()), propertyList);
        createProperty("property.setkey", efsElement.getSetKey(), propertyList);
        createProperty("property.costgroup", efsElement.getCostGroup(), propertyList);
        createProperty("property.constructionsGroup", efsElement.getConstructionsGroup(), propertyList);
        createProperty("property.productStructure", efsElement.getProductStructure(), propertyList);
        createProperty("property.positionVariant", efsElement.getPositionVariant(), propertyList);
        createProperty("property.deletionFlag", efsElement.getDeletionFlag(), propertyList);
        createProperty("property.einheit", efsElement.getQuantityUnit(), propertyList);
        createProperty("property.einheitErweitert", efsElement.getQuantityUnitExtended(), propertyList);
        createProperty("property.einsatzschluessel", efsElement.getBeginDateKey(), propertyList);
        createProperty("property.zeichnungdatum",
                dateStringConverter.toString(efsElement.getEfsElementMara().getDrawingDate()), propertyList);
        createProperty("property.zeichnungstand", getDrawingStatus(efsElement.getEfsElementMara().getDrawingStatus()),
                propertyList);
        createProperty("property.einsatzserie", dateStringConverter.toString(efsElement.getBeginDate()), propertyList);
        createProperty("property.entfallschluessel", efsElement.getEndDateKey(), propertyList);
        createProperty("property.entfallserie", dateStringConverter.toString(efsElement.getEndDate()), propertyList);

        PartProperty assemblyIndicator = createProperty("property.assemblyindicator",
                getAssemblyIndicator(efsElement.getEfsElementMara().getAssemblyIndicator()), propertyList);
        String desc = getAssemblyIndicatorAdditionalDesc(efsElement.getEfsElementMara().getAssemblyIndicator());
        assemblyIndicator.setPropertyValueDescription(desc);

        createProperty("property.constructionsState", efsElement.getEfsElementMara().getConstructionsState(),
                propertyList);
        createProperty("property.quality", efsElement.getEfsElementMara().getQuality(), propertyList);
        createProperty("property.materialThickness",
                doubleStringConverter.toString(efsElement.getEfsElementMara().getMaterialThickness()), propertyList);
        createProperty("property.seedrawing", efsElement.getEfsElementMara().getSeeDrawing(), propertyList);
        createProperty("property.respConstr1",
                getResponsibleConst(efsElement.getEfsElementMara().getResponsibleConstr1()), propertyList);
        createProperty("property.respConstr2",
                getResponsibleConst(efsElement.getEfsElementMara().getResponsibleConstr2()), propertyList);
        createProperty("property.buildsampleapproval", efsElement.getEfsElementMara().getBuildSampleApproval(),
                propertyList);
        createProperty("property.buildsampleapprovalDate",
                dateStringConverter.toString(efsElement.getEfsElementMara().getBuildSampleApprovalTargetDate()),
                propertyList);
        createProperty("property.technicallyOk", efsElement.getEfsElementMara().getTechnicallyOkay(), propertyList);
        createProperty("property.relDateSoll",
                dateStringConverter.toString(efsElement.getEfsElementMara().getReleaseDateSoll()), propertyList);
        createProperty("property.designerName", efsElement.getEfsElementMara().getDesignerName(), propertyList);
        createProperty("property.designerCostGroup", efsElement.getEfsElementMara().getDesignerCostGroup(),
                propertyList);
        createProperty("property.designerPhoneNum", efsElement.getEfsElementMara().getDesignerPhoneNumber(),
                propertyList);
        createProperty("property.kStandRelDate",
                dateStringConverter.toString(efsElement.getEfsElementMara().getKStandReleaseDate()), propertyList);
        createProperty("property.tioFreiRelDate",
                dateStringConverter.toString(efsElement.getEfsElementMara().getTioFreiReleaseDate()), propertyList);
        createProperty("property.MFPStatus", getMfpStatus(efsElement.getEfsElementMara().getMfpStatus()), propertyList);
        createProperty("property.MFPThickness",
                doubleStringConverter.toString(efsElement.getEfsElementMara().getMfpThickness()), propertyList);
        createProperty("property.kseKz", efsElement.getEfsElementMara().getKseKz(), propertyList);
        createProperty("property.weightAcceptedFromEpis", efsElement.getEfsElementMara().getWeightAcceptedFromEPIS(),
                propertyList);
        createProperty("property.prnummern", efsElement.getPrNumberRule(), propertyList);
        createProperty("property.sortierung", new LongStringConverter().toString(efsElement.getTisSort()),
                propertyList);
        createProperty("property.baukastenFlag", getBaukastenFlag(efsElement.getBaukasten()), propertyList);
        createProperty("property.baukastenStatus", efsElement.getBaukastenStatus(), propertyList);
        createProperty("property.baukastenNodeId", efsElement.getBaukastenNodeId(), propertyList);
        createProperty("property.processStatus", efsElement.getProcessStatus(), propertyList);
        createProperty("property.dmuRelevant", efsElement.getDmuRelevant(), propertyList);
        createProperty("property.materialtType", efsElement.getMaterialType(), propertyList);
        createProperty("property.earliestPvs", dateStringConverter.toString(efsElement.getEarliestPvs()), propertyList);
        createProperty("property.earliestNs", dateStringConverter.toString(efsElement.getEarliestNs()), propertyList);
        createProperty("property.earliestSop", dateStringConverter.toString(efsElement.getEarliestSop()), propertyList);
        createProperty("property.pActivationDate", dateStringConverter.toString(efsElement.getPActivationDate()),
                propertyList);
        createProperty("property.konstructureDate", dateStringConverter.toString(efsElement.getKonstructureDate()),
                propertyList);
        createProperty("property.avonStatus", getAvonStatus(efsElement.getAvonStatus()), propertyList);
        createProperty("property.wahlweiseFall", efsElement.getWahlweiseFall(), propertyList);
        createProperty("property.wahlweiseNr", numberToString(efsElement.getWahlweiseNr()), propertyList);

        fillTable(propertyList);
    }

    private String readWeightControlFlag(EfsElementDTO efsElement) {
        WeightControlFlag weightControlFlag = efsElement.getWeightControlFlag();
        return weightControlFlag == null ? null : weightControlFlag.getValue();
    }

    private PartProperty createProperty(String propertyNameKey, String efsElement,
            Collection<PartProperty> propertyList) {
        PartProperty idProperty = new PartProperty();
        idProperty.setPropertyNameKey(propertyNameKey);
        idProperty.setPropertyValue(efsElement);
        propertyList.add(idProperty);

        return idProperty;
    }

    private String getDrawingStatus(String drawingStatus) {
        return drawingStatus == null || drawingStatus.isEmpty() ? StringConstant.EMPTY :
                drawingStatus + SEPARATOR + getTranslation("property.zeichnungstand" + drawingStatus);
    }

    private String getAssemblyIndicator(String assemblyIndicator) {
        return assemblyIndicator == null || assemblyIndicator.isEmpty() ?
                getTranslation("property.assemblyindicatorEmpty") :
                assemblyIndicator + SEPARATOR + getTranslation("property.assemblyindicator" + assemblyIndicator);
    }

    private String getAssemblyIndicatorAdditionalDesc(String assemblyIndicator) {
        String messageKey =
                assemblyIndicator == null || assemblyIndicator.isEmpty() ? "property.assemblyindicatorDescrEmpty" :
                        "property.assemblyindicatorDescr" + assemblyIndicator;
        return getTranslation(messageKey);
    }

    private String getResponsibleConst(String respConst) {
        return respConst == null || respConst.isEmpty() ? StringConstant.EMPTY :
                respConst + SEPARATOR + getTranslation("property.respConstr" + respConst);
    }

    private String getWorkPackageNumberString(String workPackageNumber) {
        String key = workPackageNumber == null || workPackageNumber.isEmpty() ? "property.workPackageNumberH" :
                "property.workPackageNumber" + workPackageNumber.substring(1);
        return workPackageNumber + SEPARATOR + getTranslation(key);
    }

    private String getMfpStatus(String mfpStatus) {
        if (mfpStatus == null || mfpStatus.isEmpty()) {
            return StringConstant.EMPTY;
        }

        String mfpStatusIndex = mfpStatus.substring(0, mfpStatus.length() - 1);
        return mfpStatusIndex + SEPARATOR + getTranslation("property.MFPStatus" + mfpStatusIndex);
    }

    private String getBaukastenFlag(Integer baukastenNum) {
        return baukastenNum == null ? StringConstant.EMPTY :
                baukastenNum + SEPARATOR + getTranslation("property.baukastenFlag" + baukastenNum);
    }

    private String getAvonStatus(String avonStatus) {
        return avonStatus == null || avonStatus.isEmpty() ? StringConstant.EMPTY :
                avonStatus + SEPARATOR + getTranslation("property.avonStatus" + avonStatus);
    }

    private String getTranslation(String str) {
        try {
            return I18N.getString(str);
        } catch (Exception e) {
            LOG.warn("No translation found for {}", str);
        }

        return StringConstant.EMPTY;
    }

    private String numberToString(Number number) {
        return number == null ? StringConstant.EMPTY : number.toString();
    }
}
