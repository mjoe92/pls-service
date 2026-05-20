package de.vw.paso.client.stueckliste.efs.tree.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import de.vw.paso.client.control.cell.CogCoordinates;
import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.client.stueckliste.efs.views.historie.converter.QuantityConverter;
import de.vw.paso.client.util.QuantityUnit;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementMaraDTO;

public abstract class AbstractEfsElementTreeItem<EFS extends IEfsElementForDTO> extends AbstractTreeItem<EFS> {

    private static final Collection<String> PROPERTY_NAMES_COMPARE = new ArrayList<>(72);

    static {
        // REVISION, AENDERUNGSART, GEAENDERT_DATUM and GEAENDERT_VON won't be compared
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.PART_NUMBER);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.PART_NUMBER_VORNUMMER);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.PART_NUMBER_MITTELGRUPPE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.PART_NUMBER_END_NUMBER);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.PART_NUMBER_INDEX);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.DESCRIPTION1);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.DESCRIPTION2);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.BOM_NUMBER);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.PRODUCT);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.PART_TYPE);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.NODE_TYPE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.NODE_LEVEL);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.NODE_LABEL);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.AP);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.SET_KEY);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.COST_GROUP);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.CONSTRUCTIONS_GROUP);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.PRODUCT_STRUCTURE);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.QUANTITY);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.WEIGHT_CONTROL_FLAG);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.WEIGHT_ALL);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.WEIGHT_NODE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.WEIGHT_PRIO);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_TE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_TE_DATE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.WEIGHT_CALCULATED_TE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.WEIGHT_CALCULATED_TE_DATE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.WEIGHT_ESTIMATED_TE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_PROD);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.QUANTITY_UNIT);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.QUANTITY_UNIT_EXTENDED);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.BEGIN_DATE_KEY);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.DRAWING_DATE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.DRAWING_STATUS);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.BEGIN_DATE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.END_DATE_KEY);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.END_DATE);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.ASSEMBLY_INDICATOR);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.CONSTRUCTIONS_STATE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.QUALITY);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.MATERIAL_THICKNESS);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.SEE_DRAWING);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.RESPONSIBLE_CONSTR_1);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.RESPONSIBLE_CONSTR_2);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.BUILD_SAMPLE_APPROVAL);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.BUILD_SAMPLE_APPROVAL_DATE);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.TECHNICALLY_OKAY);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.RELEASE_DATE_SOLL);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.DESIGNER_NAME);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.DESIGNER_COST_GROUP);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.DESIGNER_PHONE_NUMBER);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.K_STAND_RELEASE_DATE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.TIO_FREI_RELEASE_DATE);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.MFP_STATUS);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.MFP_THICKNESS);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.KSE_KZ);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.WEIGHT_ACCEPTED_FROM_EPIS);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.BAUKASTEN_FLAG);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.BAUKASTEN_STATUS);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.BAUKASTEN_NODE_ID);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.DMU_RELEVANT);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.PROCESS_STATUS);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.MATERIAL_TYPE);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.EARLIEST_PVS);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.EARLIEST_NS);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.EARLIEST_SOP);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.P_ACTIVATION_DATE);
        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.KONSTRUCTURE_DATE);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.AVON_STATUS);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.PR_NUMBER_RULE);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.TIS_SORT);

        PROPERTY_NAMES_COMPARE.add(EfsElementTreeItemPropertyNames.COG);
    }

    public static Collection<String> getPropertyNamesCompare() {
        return new ArrayList<>(PROPERTY_NAMES_COMPARE);
    }

    private final StringProperty propertyPartNumber;
    private final StringProperty propertyPartNumberVornummer;
    private final StringProperty propertyPartNumberMittelgruppe;
    private final StringProperty propertyPartNumberEndNumber;
    private final StringProperty propertyPartNumberIndex;
    private final StringProperty propertyDescription1;
    private final StringProperty propertyDescription2;
    private final ObjectProperty<Integer> propertyBomNumber;
    private final StringProperty propertyProduct;
    private final StringProperty propertyPartType;
    private final StringProperty propertyNodeType;
    private final ObjectProperty<Integer> propertyNodeLevel;
    private final StringProperty propertyNodeLabel;
    private final StringProperty propertyNodeValueParent;
    private final StringProperty propertyNodeValue;
    private final StringProperty propertyBeginDateKey;
    private final StringProperty propertyEndDateKey;
    private final ObjectProperty<Date> propertyDrawingDate;
    private final StringProperty propertyDrawingStatus;
    private final ObjectProperty<Date> propertyBeginDate;
    private final ObjectProperty<Date> propertyEndDate;
    private final StringProperty propertyConstructionsGroup;
    private final StringProperty propertyProductStructure;
    private final StringProperty propertyPositionVariant;
    private final StringProperty propertyDeletionFlag;
    private final StringProperty propertyConstructionsState;
    private final StringProperty propertyQuantityUnitExtended;
    private final StringProperty propertyAssemblyIndicator;
    private final StringProperty propertyQuality;
    private final ObjectProperty<Double> propertyMaterialThickness;
    private final StringProperty propertySeeDrawing;
    private final StringProperty propertyRespConstr1;
    private final StringProperty propertyRespConstr2;
    private final StringProperty propertyBuildSampleApproval;
    private final ObjectProperty<Date> propertyBuildSampleApprovalDate;
    private final StringProperty propertyTechnicallyOkay;
    private final ObjectProperty<Date> propertyRelDateSoll;
    private final StringProperty propertyDesignerName;
    private final StringProperty propertyDesignerCostGroup;
    private final StringProperty propertyDesignerPhoneNumber;
    private final ObjectProperty<Date> propertyKStandRelDate;
    private final ObjectProperty<Date> propertyTioFreiRelDate;
    private final StringProperty propertyMfpStatus;
    private final ObjectProperty<Double> propertyMfpThickness;
    private final StringProperty propertyKseKz;
    private final StringProperty propertyWeightAcceptedFromEpis;
    private final ObjectProperty<Integer> propertyBaukastenFlag;
    private final StringProperty propertyBaukastenStatus;
    private final StringProperty propertyBaukastenNodeId;
    private final StringProperty propertyDmuRelevant;
    private final StringProperty propertyProcessStatus;
    private final StringProperty propertyMaterialType;
    private final ObjectProperty<Date> propertyEarliestPvs;
    private final ObjectProperty<Date> propertyEarliestNs;
    private final ObjectProperty<Date> propertyEarliestSop;
    private final ObjectProperty<Date> propertyPActivationDate;
    private final ObjectProperty<Date> propertyConstructureDate;
    private final StringProperty propertyAvonStatus;
    private final StringProperty propertyPrNumberRule;
    private final ObjectProperty<Long> propertyTisSort;
    private final ObjectProperty<CogCoordinates> propertyCog;
    private final StringProperty propertyNodeId;
    private final StringProperty propertyWahlweiseFall;
    private final ObjectProperty<Integer> propertyWahlweiseNr;

    private final StringProperty propertyGroupString;

    ObjectProperty<AbstractEfsElementMaraDTO> propertyEfsElementMara;

    public AbstractEfsElementTreeItem(EFS element,
        ObjectProperty<AbstractEfsElementMaraDTO> propertyEfsElementMara) { // NO_UCD (use default)
        super(element);
        this.propertyEfsElementMara = propertyEfsElementMara;

        initialize();
        initValues();
        initCalculatedWeights();

        propertyPartNumber = new SimpleStringProperty(propertyEfsElementMara.get().getPartNumber());
        propertyPartNumberVornummer = new SimpleStringProperty(propertyEfsElementMara.get().getPartNumberVornummer());
        propertyPartNumberMittelgruppe = new SimpleStringProperty(
            propertyEfsElementMara.get().getPartNumberMittelgruppe());
        propertyPartNumberEndNumber = new SimpleStringProperty(propertyEfsElementMara.get().getPartNumberEndNumber());
        propertyPartNumberIndex = new SimpleStringProperty(propertyEfsElementMara.get().getPartNumberIndex());
        if (IEfsElementForDTO.GAP_FLAG.equals(element.getGap())) {
            propertyDescription1 = new SimpleStringProperty(element.getNodeLabel());
        } else {
            propertyDescription1 = new SimpleStringProperty(propertyEfsElementMara.get().getDescription1De());
        }
        propertyDescription2 = new SimpleStringProperty(propertyEfsElementMara.get().getDescription2De());
        propertyBomNumber = new SimpleObjectProperty<>(element.getBomNumber());
        propertyProduct = new SimpleStringProperty(element.getProduct());
        propertyPartType = new SimpleStringProperty(element.getPartType());
        propertyNodeLabel = new SimpleStringProperty(element.getNodeLabel());
        propertyNodeLevel = new SimpleObjectProperty<>(element.getNodeLevel());
        propertyNodeType = new SimpleStringProperty(element.getNodeType());
        propertyNodeValueParent = new SimpleStringProperty(element.getNodeValueParent());
        propertyNodeValue = new SimpleStringProperty(element.getNodeValue());
        propertyAp = new SimpleObjectProperty<>(element.getAp());
        propertySetKey = new SimpleStringProperty(element.getSetKey());
        propertyCostGroup = new SimpleStringProperty(element.getCostGroup());
        propertyConstructionsGroup = new SimpleStringProperty(element.getConstructionsGroup());
        propertyProductStructure = new SimpleStringProperty(element.getProductStructure());
        propertyPositionVariant = new SimpleStringProperty(element.getPositionVariant());
        propertyDeletionFlag = new SimpleStringProperty(element.getDeletionFlag());
        propertyBeginDateKey = new SimpleStringProperty(element.getBeginDateKey());
        propertyEndDateKey = new SimpleStringProperty(element.getEndDateKey());
        propertyDrawingDate = new SimpleObjectProperty<>(propertyEfsElementMara.get().getDrawingDate());
        propertyDrawingStatus = new SimpleStringProperty(propertyEfsElementMara.get().getDrawingStatus());
        propertyBeginDate = new SimpleObjectProperty<>(element.getBeginDate());
        propertyEndDate = new SimpleObjectProperty<>(element.getEndDate());
        propertyQuantityUnitExtended = new SimpleStringProperty(element.getQuantityUnitExtended());
        propertyAssemblyIndicator = new SimpleStringProperty(propertyEfsElementMara.get().getAssemblyIndicator());
        propertyConstructionsState = new SimpleStringProperty(propertyEfsElementMara.get().getConstructionsState());
        propertyQuality = new SimpleStringProperty(propertyEfsElementMara.get().getQuality());
        propertyMaterialThickness = new SimpleObjectProperty<>(propertyEfsElementMara.get().getMaterialThickness());
        propertySeeDrawing = new SimpleStringProperty(propertyEfsElementMara.get().getSeeDrawing());
        propertyRespConstr1 = new SimpleStringProperty(propertyEfsElementMara.get().getResponsibleConstr1());
        propertyRespConstr2 = new SimpleStringProperty(propertyEfsElementMara.get().getResponsibleConstr2());
        propertyBuildSampleApproval = new SimpleStringProperty(propertyEfsElementMara.get().getBuildSampleApproval());
        propertyBuildSampleApprovalDate = new SimpleObjectProperty<>(
            propertyEfsElementMara.get().getBuildSampleApprovalTargetDate());
        propertyTechnicallyOkay = new SimpleStringProperty(propertyEfsElementMara.get().getTechnicallyOkay());
        propertyRelDateSoll = new SimpleObjectProperty<>(propertyEfsElementMara.get().getReleaseDateSoll());
        propertyDesignerName = new SimpleStringProperty(propertyEfsElementMara.get().getDesignerName());
        propertyDesignerCostGroup = new SimpleStringProperty(propertyEfsElementMara.get().getDesignerCostGroup());
        propertyDesignerPhoneNumber = new SimpleStringProperty(propertyEfsElementMara.get().getDesignerPhoneNumber());
        propertyKStandRelDate = new SimpleObjectProperty<>(propertyEfsElementMara.get().getKStandReleaseDate());
        propertyTioFreiRelDate = new SimpleObjectProperty<>(propertyEfsElementMara.get().getTioFreiReleaseDate());
        propertyMfpStatus = new SimpleStringProperty(propertyEfsElementMara.get().getMfpStatus());
        propertyMfpThickness = new SimpleObjectProperty<>(propertyEfsElementMara.get().getMfpThickness());
        propertyKseKz = new SimpleStringProperty(propertyEfsElementMara.get().getKseKz());
        propertyWeightAcceptedFromEpis = new SimpleStringProperty(
            propertyEfsElementMara.get().getWeightAcceptedFromEPIS());
        propertyBaukastenFlag = new SimpleObjectProperty<>(element.getBaukasten());
        propertyBaukastenStatus = new SimpleStringProperty(element.getBaukastenStatus());
        propertyBaukastenNodeId = new SimpleStringProperty(element.getBaukastenNodeId());
        propertyDmuRelevant = new SimpleStringProperty(element.getDmuRelevant());
        propertyProcessStatus = new SimpleStringProperty(element.getProcessStatus());
        propertyMaterialType = new SimpleStringProperty(element.getMaterialType());
        propertyEarliestPvs = new SimpleObjectProperty<>(element.getEarliestPvs());
        propertyEarliestNs = new SimpleObjectProperty<>(element.getEarliestNs());
        propertyEarliestSop = new SimpleObjectProperty<>(element.getEarliestSop());
        propertyPActivationDate = new SimpleObjectProperty<>(element.getPActivationDate());
        propertyConstructureDate = new SimpleObjectProperty<>(element.getKonstructureDate());
        propertyAvonStatus = new SimpleStringProperty(element.getAvonStatus());
        propertyPrNumberRule = new SimpleStringProperty(element.getPrNumberRule());
        propertyTisSort = new SimpleObjectProperty<>(element.getTisSort());
        propertyCog = new SimpleObjectProperty<>(new CogCoordinates(element));
        propertyNodeId = new SimpleStringProperty(element.getNodeId());
        propertyWahlweiseFall = new SimpleStringProperty(element.getWahlweiseFall());
        propertyWahlweiseNr = new SimpleObjectProperty<>(element.getWahlweiseNr());

        propertyGroupString = new SimpleStringProperty();

        valueProperty().addListener((observable, oldValue, newValue) -> updateValues());

    }

    @Override
    public void setUserObject(EFS efs) {
        super.setUserObject(efs);
        updateValues();
    }

    private void updateValues() {
        initValues();
        updatePropertiesAfterUserObjectChanged();
        initCalculatedWeights();
    }

    protected abstract void initCalculatedWeights();

    private void initialize() {
        PROPERTY_NAMES_COMPARE.forEach(propertyName -> {
            createChangePropertyByName(propertyName);
            createTooltipPropertyByName(propertyName);
        });
    }

    private void createChangePropertyByName(String propertyName) {
        propertyNameToChangePropertyMap.put(propertyName, new SimpleBooleanProperty(false));
    }

    private void createTooltipPropertyByName(String propertyName) {
        propertyNameToTooltipMap.put(propertyName, new SimpleObjectProperty<>());
    }

    private void initValues() {
        Double weightWeightedTe = getEfsElementMara().getWeightWeightedTe();
        setWeightWeightedTe(weightWeightedTe != null ? weightWeightedTe : 0.0);

        Date weightWeightedTeDate = getEfsElementMara().getWeightWeightedTeDate();
        setWeightWeightedTeDate(weightWeightedTeDate);

        Double weightCalculatedTe = getEfsElementMara().getWeightCalculatedTe();
        setWeightCalculatedTe(weightCalculatedTe != null ? weightCalculatedTe : 0.0);

        Date weightCalculatedTeDate = getEfsElementMara().getWeightCalculatedTeDate();
        setWeightCalculatedTeDate(weightCalculatedTeDate);

        Double weightEstimatedTe = getEfsElementMara().getWeightEstimatedTe();
        setWeightEstimatedTe(weightEstimatedTe != null ? weightEstimatedTe : 0.0);

        Date weightEstimatedTeDate = getEfsElementMara().getWeightEstimatedTeDate();
        setWeightEstimatedTeDate(weightEstimatedTeDate);

        Double weightWeightedProd = getEfsElementMara().getWeightWeightedProd();
        setWeightWeightedProd(weightWeightedProd != null ? weightWeightedProd : 0.0);

        Date weightWeightedProdDate = getEfsElementMara().getWeightWeightedProdDate();
        setWeightWeightedProdDate(weightWeightedProdDate);

        Integer quantity = getUserObject().getQuantity();
        setQuantity(quantity);

        QuantityUnit quantityUnit = QuantityConverter.getEinheitForKuerzel(getUserObject().getQuantityUnit());
        setQuantityUnit(quantityUnit != null ? quantityUnit : QuantityUnit.UNKNOWN);

        WeightControlFlag weightControlFlag = getUserObject().getWeightControlFlag();
        setWeightControlFlag(weightControlFlag);
    }

    private void updatePropertiesAfterUserObjectChanged() {
        String partNumber = getUserObject().getPartNumber();
        setPartNumber(partNumber);

        String costGroup = getUserObject().getCostGroup();
        setCostGroup(costGroup);

        String setKey = getUserObject().getSetKey();
        setSetKey(setKey);

        Date beginDate = getUserObject().getBeginDate();
        setBeginDate(beginDate);

        String beginDateKey = getUserObject().getBeginDateKey();
        setBeginDateKey(beginDateKey);

        Date endDate = getUserObject().getEndDate();
        setEndDate(endDate);

        String endDateKey = getUserObject().getEndDateKey();
        setEndDateKey(endDateKey);

        String prNumberRule = getUserObject().getPrNumberRule();
        setPrNumberRule(prNumberRule);

        Long sort = getUserObject().getTisSort();
        setTisSort(sort);

        setAp(getUserObject().getAp());
    }

    @Override
    protected Object getKey() {
        return getUserObject().getId();
    }

    @Override
    protected Object getParentKey() {
        return getUserObject().getParent() != null ? getUserObject().getParent().getId() : null;
    }

    private final ObjectProperty<Double> propertyWeightWeightedTe = new SimpleObjectProperty<>(-1d);

    public ObjectProperty<Double> propertyWeightWeightedTe() {
        return propertyWeightWeightedTe;
    } // NO_UCD (use default)

    public void setWeightWeightedTe(Double weightWeightedTe) {
        getEfsElementMara().setWeightWeightedTe(weightWeightedTe);
        propertyWeightWeightedTe().set(weightWeightedTe);
    }

    private final ObjectProperty<Date> propertyWeightWeightedTeDate = new SimpleObjectProperty<>();

    public ObjectProperty<Date> propertyWeightWeightedTeDate() {
        return propertyWeightWeightedTeDate;
    } // NO_UCD (use default)

    public void setWeightWeightedTeDate(Date weightWeightedTeDate) {
        getEfsElementMara().setWeightWeightedTeDate(weightWeightedTeDate);
        propertyWeightWeightedTeDate().set(weightWeightedTeDate);
    }

    private final ObjectProperty<Double> propertyWeightCalculatedTe = new SimpleObjectProperty<>(-1d);

    public ObjectProperty<Double> propertyWeightCalculatedTe() {
        return propertyWeightCalculatedTe;
    } // NO_UCD (use default)

    public void setWeightCalculatedTe(Double weightCalculatedTe) {
        getEfsElementMara().setWeightCalculatedTe(weightCalculatedTe);
        propertyWeightCalculatedTe().set(weightCalculatedTe);
    }

    private final ObjectProperty<Date> propertyWeightCalculatedTeDate = new SimpleObjectProperty<>();

    public ObjectProperty<Date> propertyWeightCalculatedTeDate() {
        return propertyWeightCalculatedTeDate;
    } // NO_UCD (use default)

    public void setWeightCalculatedTeDate(Date weightCalculatedTeDate) {
        getEfsElementMara().setWeightCalculatedTeDate(weightCalculatedTeDate);
        propertyWeightCalculatedTeDate().set(weightCalculatedTeDate);
    }

    private final ObjectProperty<Double> propertyWeightEstimatedTe = new SimpleObjectProperty<>(-1d);

    public ObjectProperty<Double> propertyWeightEstimatedTe() {
        return propertyWeightEstimatedTe;
    } // NO_UCD (use default)

    public void setWeightEstimatedTe(Double weightEstimatedTe) {
        getEfsElementMara().setWeightEstimatedTe(weightEstimatedTe);
        propertyWeightEstimatedTe().set(weightEstimatedTe);
    }

    private final ObjectProperty<Date> propertyWeightEstimatedTeDate = new SimpleObjectProperty<>();

    public ObjectProperty<Date> propertyWeightEstimatedTeDate() {
        return propertyWeightEstimatedTeDate;
    } // NO_UCD (use default)

    public void setWeightEstimatedTeDate(Date weightEstimatedTe) {
        getEfsElementMara().setWeightEstimatedTeDate(weightEstimatedTe);
        propertyWeightEstimatedTeDate().set(weightEstimatedTe);
    }

    private final ObjectProperty<Double> propertyWeightWeightedProd = new SimpleObjectProperty<>(-1d);

    public ObjectProperty<Double> propertyWeightWeightedProd() {
        return propertyWeightWeightedProd;
    } // NO_UCD (use default)

    public void setWeightWeightedProd(Double weightWeightedProd) {
        getEfsElementMara().setWeightWeightedProd(weightWeightedProd);
        propertyWeightWeightedProd().set(weightWeightedProd);
    }

    private final ObjectProperty<Date> propertyWeightWeightedProdDate = new SimpleObjectProperty<>();

    public ObjectProperty<Date> propertyWeightWeightedProdDate() {
        return propertyWeightWeightedProdDate;
    } // NO_UCD (use default)

    public void setWeightWeightedProdDate(Date weightWeightedProd) {
        getEfsElementMara().setWeightWeightedProdDate(weightWeightedProd);
        propertyWeightWeightedProdDate().set(weightWeightedProd);
    }

    private ObjectProperty<String> propertyAp = new SimpleObjectProperty<>();

    public ObjectProperty<String> propertyAp() {
        return propertyAp;
    } // NO_UCD (use default)

    public String getAp() {
        return propertyAp().get();
    }

    public void setAp(String ap) {
        getUserObject().setAp(ap);
        propertyAp().set(ap);
    }

    private StringProperty propertySetKey = new SimpleStringProperty();

    public StringProperty propertySetKey() {
        return propertySetKey;
    } // NO_UCD (use default)

    public String getSetKey() {
        return propertySetKey().get();
    }

    public void setSetKey(final String setKey) {
        getUserObject().setSetKey(setKey);
        propertySetKey().set(setKey);
    }

    private StringProperty propertyCostGroup = new SimpleStringProperty();

    public StringProperty propertyCostGroup() {
        return propertyCostGroup;
    } // NO_UCD (use default)

    public String getCostGroup() {
        return propertyCostGroup().get();
    }

    public void setCostGroup(final String costGroup) {
        getUserObject().setCostGroup(costGroup);
        propertyCostGroup().set(costGroup);
    }

    private final ObjectProperty<Integer> propertyQuantity = new SimpleObjectProperty<>();

    public ObjectProperty<Integer> propertyQuantity() {
        return propertyQuantity;
    } // NO_UCD (use default)

    public Number getQuantity() {
        return propertyQuantity().get();
    }

    public void setQuantity(Integer quantity) {
        getUserObject().setQuantity(quantity);
        propertyQuantity().set(quantity);
    }

    private final ObjectProperty<QuantityUnit> propertyQuantityUnit = new SimpleObjectProperty<>();

    public ObjectProperty<QuantityUnit> propertyQuantityUnit() {
        return propertyQuantityUnit;
    } // NO_UCD (use default)

    public QuantityUnit getQuantityUnit() {
        return propertyQuantityUnit().get();
    }

    public void setQuantityUnit(QuantityUnit quantityUnit) {
        getUserObject().setQuantityUnit(quantityUnit.getShortName());
        propertyQuantityUnit().set(quantityUnit);
    }

    private final StringProperty propertyWeightControlFlag = new SimpleStringProperty();

    public StringProperty propertyWeightControlFlag() {
        return propertyWeightControlFlag;
    } // NO_UCD (use default)

    public String getWeightControlFlag() {
        return propertyWeightControlFlag().get();
    }

    public void setWeightControlFlag(WeightControlFlag weightControlFlag) {
        getUserObject().setWeightControlFlag(weightControlFlag);
        String weightControlFlagValue = weightControlFlag == null ? null : weightControlFlag.getValue();
        propertyWeightControlFlag().set(weightControlFlagValue);
    }

    public ObservableValue<CogCoordinates> propertyCog() {
        return propertyCog;
    }

    public void setCogCoordinates(CogCoordinates cog) {
        getUserObject().setCogX(cog.getCogX());
        getUserObject().setCogY(cog.getCogY());
        getUserObject().setCogZ(cog.getCogZ());
        propertyCog.set(cog);
    }

    public CogCoordinates getCog() {
        return propertyCog.get();
    }

    /** Gewicht Properties */
    private final ObjectProperty<Double> propertyWeightPrio = new SimpleObjectProperty<>();

    public ObjectProperty<Double> propertyWeightPrio() { // NO_UCD (use default)
        return propertyWeightPrio;
    }

    public Double getWeightPrio() {
        return propertyWeightPrio.get();
    }

    public void setWeightPrio(Double weight) {
        propertyWeightPrio.set(weight);
    }

    private ObjectProperty<Double> propertyWeightNode;

    public ObjectProperty<Double> propertyWeightNode() { // NO_UCD (use default)
        if (propertyWeightNode == null) {
            propertyWeightNode = new SimpleObjectProperty<>(0.0);
        }

        return propertyWeightNode;
    }

    public void setWeightNode(Double weight) {
        propertyWeightNode().set(weight);
    }

    private final ObjectProperty<Double> propertyWeightAll = new SimpleObjectProperty<>(-1.0);

    public ObjectProperty<Double> propertyWeightAll() { // NO_UCD (use default)
        return propertyWeightAll;
    }

    public Double getWeightAll() {
        return propertyWeightAll.get();
    }

    public void setWeightAll(Double weight) {
        propertyWeightAll.set(weight);
    }

    /** EFS-Properties */
    public StringProperty propertyPartNumber() {
        return propertyPartNumber;
    }

    public StringProperty propertyPartNumberVornummer() {
        return propertyPartNumberVornummer;
    }

    public StringProperty propertyPartNumberMittelgruppe() {
        return propertyPartNumberMittelgruppe;
    }

    public StringProperty propertyPartNumberEndNumber() {
        return propertyPartNumberEndNumber;
    }

    public StringProperty propertyPartNumberIndex() {
        return propertyPartNumberIndex;
    }

    public StringProperty propertyDescription1() {
        return propertyDescription1;
    }

    public StringProperty propertyDescription2() {
        return propertyDescription2;
    }

    public ObjectProperty<Integer> propertyBomNumber() {
        return propertyBomNumber;
    }

    public StringProperty propertyProduct() {
        return propertyProduct;
    }

    public StringProperty propertyPartType() {
        return propertyPartType;
    }

    public StringProperty propertyNodeType() {
        return propertyNodeType;
    }

    public StringProperty propertyNodeLabel() {
        return propertyNodeLabel;
    }

    public StringProperty propertyNodeValueParent() {
        return propertyNodeValueParent;
    }

    public StringProperty propertyNodeValue() {
        return propertyNodeValue;
    }

    public ObjectProperty<Integer> propertyNodeLevel() {
        return propertyNodeLevel;
    }

    public StringProperty propertyBeginDateKey() {
        return propertyBeginDateKey;
    }

    public StringProperty propertyEndDateKey() {
        return propertyEndDateKey;
    }

    public ObjectProperty<Date> propertyDrawingDate() {
        return propertyDrawingDate;
    }

    public StringProperty propertyDrawingStatus() {
        return propertyDrawingStatus;
    }

    public ObjectProperty<Date> propertyBeginDate() {
        return propertyBeginDate;
    }

    public ObjectProperty<Date> propertyEndDate() {
        return propertyEndDate;
    }

    public StringProperty propertyConstructionsGroup() {
        return propertyConstructionsGroup;
    }

    public StringProperty propertyProductStructure() {
        return propertyProductStructure;
    }

    public StringProperty propertyPositionVariantProperty() {
        return propertyPositionVariant;
    }

    public StringProperty propertyDeletionFlag() {
        return propertyDeletionFlag;
    }

    public StringProperty propertyQuantityUnitExtended() {
        return propertyQuantityUnitExtended;
    }

    public StringProperty propertyAssemblyIndicator() {
        return propertyAssemblyIndicator;
    }

    public StringProperty propertyConstructionsState() {
        return propertyConstructionsState;
    }

    public StringProperty propertyQuality() {
        return propertyQuality;
    }

    public ObjectProperty<Double> propertyMaterialThickness() {
        return propertyMaterialThickness;
    }

    public StringProperty propertySeeDrawing() {
        return propertySeeDrawing;
    }

    public StringProperty propertyRespConstr1() {
        return propertyRespConstr1;
    }

    public StringProperty propertyRespConstr2() {
        return propertyRespConstr2;
    }

    public StringProperty propertyBuildSampleApproval() {
        return propertyBuildSampleApproval;
    }

    public ObjectProperty<Date> propertyBuildSampleApprovalDate() {
        return propertyBuildSampleApprovalDate;
    }

    public StringProperty propertyTechnicallyOkay() {
        return propertyTechnicallyOkay;
    }

    public ObjectProperty<Date> propertyRelDateSoll() {
        return propertyRelDateSoll;
    }

    public StringProperty propertyDesignerName() {
        return propertyDesignerName;
    }

    public StringProperty propertyDesignerCostGroup() {
        return propertyDesignerCostGroup;
    }

    public StringProperty propertyDesignerPhoneNumber() {
        return propertyDesignerPhoneNumber;
    }

    public ObjectProperty<Date> propertyKStandRelDate() {
        return propertyKStandRelDate;
    }

    public ObjectProperty<Date> propertyTioFreiRelDate() {
        return propertyTioFreiRelDate;
    }

    public StringProperty propertyMfpStatus() {
        return propertyMfpStatus;
    }

    public ObjectProperty<Double> propertyMfpThickness() {
        return propertyMfpThickness;
    }

    public StringProperty propertyKseKz() {
        return propertyKseKz;
    }

    public StringProperty propertyWeightAcceptedFromEpis() {
        return propertyWeightAcceptedFromEpis;
    }

    public ObjectProperty<Integer> propertyBaukastenFlag() {
        return propertyBaukastenFlag;
    }

    public Integer getBaukastenFlag() {
        return propertyBaukastenFlag.get();
    }

    public StringProperty propertyBaukastenStatus() {
        return propertyBaukastenStatus;
    }

    public StringProperty propertyBaukastenNodeId() {
        return propertyBaukastenNodeId;
    }

    public StringProperty propertyProcessStatus() {
        return propertyProcessStatus;
    }

    public StringProperty propertyDmuRelevant() {
        return propertyDmuRelevant;
    }

    public StringProperty propertyMaterialType() {
        return propertyMaterialType;
    }

    public ObjectProperty<Date> propertyEarliestPvs() {
        return propertyEarliestPvs;
    }

    public ObjectProperty<Date> propertyEarliestNs() {
        return propertyEarliestNs;
    }

    public ObjectProperty<Date> propertyEarliestSop() {
        return propertyEarliestSop;
    }

    public ObjectProperty<Date> propertyPActivationDate() {
        return propertyPActivationDate;
    }

    public ObjectProperty<Date> propertyConstructureDate() {
        return propertyConstructureDate;
    }

    public StringProperty propertyAvonStatus() {
        return propertyAvonStatus;
    }

    public StringProperty propertyPrNumberRule() {
        return propertyPrNumberRule;
    }

    public ObjectProperty<Long> propertyTisSort() {
        return propertyTisSort;
    }

    /** Delegate methods */
    public AbstractEfsElementMaraDTO getEfsElementMara() {
        return propertyEfsElementMara.get();
    }

    public String getPartNumber() {
        return propertyPartNumber().get();
    }

    public void setPartNumber(String partNumber) {
        propertyPartNumber().set(partNumber);
        //		getEfsElementMara().setPartNumber(partNumber);
        //		// Die Teilenummer wird im EfsElement mit gepflegt
        //		getUserObject().setPartNumber(partNumber);
        //		// Außerdem muss das UserObject aktualisiert werden,
        //		// da getEfsElementMara() nur auf die Map zeigt und nicht auf die Eigenschaft der Entity
        //		getUserObject().getEfsElementMara().setPartNumber(partNumber);
    }

    public void setPartNumberVornummer(String partNumberVornummer) {
        propertyPartNumberVornummer().set(partNumberVornummer);
        //	  getEfsElementMara().setPartNumberVornummer(partNumberVornummer);
    }

    public void setPartNumberMittelgruppe(String partNumberMittelgruppe) {
        propertyPartNumberMittelgruppe().set(partNumberMittelgruppe);
        //    getEfsElementMara().setPartNumberMittelgruppe(partNumberMittelgruppe);
    }

    public void setPartNumberEndNumber(String partNumberEndNumber) {
        propertyPartNumberEndNumber().set(partNumberEndNumber);
        //    getEfsElementMara().setPartNumberEndNumber(partNumberEndNumber);
    }

    public void setPartNumberIndex(String partNumberIndex) {
        propertyPartNumberIndex().set(partNumberIndex);
        //    getEfsElementMara().setPartNumberIndex(partNumberIndex);
    }

    public String getDescription1() {
        return propertyDescription1().get();
    }

    public void setDescription1(String description1) {
        propertyDescription1().set(description1);
        getEfsElementMara().setDescription1De(description1);
    }

    public String getDescription2() {
        return propertyDescription2().get();
    }

    public void setDescription2(String description2) {
        propertyDescription2().set(description2);
        getEfsElementMara().setDescription2De(description2);
    }

    public void setBomNumber(Integer bomNumber) {
        propertyBomNumber().set(bomNumber);
    }

    public String getProduct() {
        return propertyProduct().get();
    }

    public void setProduct(String product) {
        propertyProduct().set(product);
    }

    public Integer getNodeLevel() {
        return propertyNodeLevel().get();
    }

    public void setNodeLevel(Integer nodeLevel) {
        propertyNodeLevel().set(nodeLevel);
    }

    public String getNodeType() {
        return propertyNodeType().get();
    }

    public void setNodeType(String nodeType) {
        propertyNodeType().set(nodeType);
    }

    public String getNodeLabel() {
        return propertyNodeLabel().get();
    }

    public void setNodeLabel(String nodeLabel) {
        propertyNodeLabel().set(nodeLabel);
    }

    public void setBeginDateKey(String beginDateKey) {
        propertyBeginDateKey().set(beginDateKey);
        getUserObject().setBeginDateKey(beginDateKey);
    }

    public void setEndDateKey(String endDateKey) {
        propertyEndDateKey().set(endDateKey);
        getUserObject().setEndDateKey(endDateKey);
    }

    public void setDrawingDate(Date drawingDate) {
        propertyDrawingDate().set(drawingDate);
        getEfsElementMara().setDrawingDate(drawingDate);
    }

    public void setDrawingStatus(String drawingStatus) {
        propertyDrawingStatus().set(drawingStatus);
        getEfsElementMara().setDrawingStatus(drawingStatus);
    }

    public Date getBeginDate() {
        return propertyBeginDate().get();
    }

    public void setBeginDate(Date beginDate) {
        propertyBeginDate().set(beginDate);
        getUserObject().setBeginDate(beginDate);
    }

    public Date getEndDate() {
        return propertyEndDate().get();
    }

    public void setEndDate(Date endDate) {
        propertyEndDate().set(endDate);
        getUserObject().setEndDate(endDate);
    }

    public String getConstructionsGroup() {
        return propertyConstructionsGroup().get();
    }

    public void setConstructionsGroup(String constructionsGroup) {
        propertyConstructionsGroup().set(constructionsGroup);
    }

    public String getProductStructure() {
        return propertyProductStructure().get();
    }

    public void setProductStructure(String productStructure) {
        propertyProductStructure().set(productStructure);
    }

    public void setQuantityUnitExtended(String quantityUnitExtended) {
        propertyQuantityUnitExtended().set(quantityUnitExtended);
    }

    public void setAssemblyIndicator(String assemblyIndicator) {
        propertyAssemblyIndicator().set(assemblyIndicator);
        getEfsElementMara().setAssemblyIndicator(assemblyIndicator);
    }

    public String getConstructionsState() {
        return propertyConstructionsState().get();
    }

    public void setConstructionsState(String constructionsState) {
        propertyConstructionsState().set(constructionsState);
    }

    public String getQuality() {
        return propertyQuality().get();
    }

    public void setQuality(String quality) {
        propertyQuality().set(quality);
        getEfsElementMara().setQuality(quality);
    }

    public Double getMaterialThickness() {
        return propertyMaterialThickness().get();
    }

    public void setMaterialThickness(Double materialThickness) {
        propertyMaterialThickness().set(materialThickness);
        getEfsElementMara().setMaterialThickness(materialThickness);
    }

    public void setSeeDrawing(String seeDrawing) {
        propertySeeDrawing().set(seeDrawing);
        getEfsElementMara().setSeeDrawing(seeDrawing);
    }

    public String getRespConstr1() {
        return propertyRespConstr1().get();
    }

    public void setRespConstr1(String respConstr1) {
        propertyRespConstr1().set(respConstr1);
        getEfsElementMara().setResponsibleConstr1(respConstr1);
    }

    public String getRespConstr2() {
        return propertyRespConstr2().get();
    }

    public void setRespConstr2(String respConstr2) {
        propertyRespConstr2().set(respConstr2);
        getEfsElementMara().setResponsibleConstr2(respConstr2);
    }

    public void setBuildSampleApproval(String buildSampleApproval) {
        propertyBuildSampleApproval().set(buildSampleApproval);
        getEfsElementMara().setBuildSampleApproval(buildSampleApproval);
    }

    public void setBuildSampleApprovalDate(Date buildSampleApprovalDate) {
        propertyBuildSampleApprovalDate().set(buildSampleApprovalDate);
        getEfsElementMara().setBuildSampleApprovalTargetDate(buildSampleApprovalDate);
    }

    public void setTechnicallyOkay(String technicallyOkay) {
        propertyTechnicallyOkay().set(technicallyOkay);
        getEfsElementMara().setTechnicallyOkay(technicallyOkay);
    }

    public Date getRelDateSoll() {
        return propertyRelDateSoll().get();
    }

    public void setRelDateSoll(Date relDateSoll) {
        propertyRelDateSoll().set(relDateSoll);
        getEfsElementMara().setReleaseDateSoll(relDateSoll);
    }

    public String getDesignerName() {
        return propertyDesignerName().get();
    }

    public void setDesignerName(String designerName) {
        propertyDesignerName().set(designerName);
        getEfsElementMara().setDesignerName(designerName);
    }

    public String getDesignerCostGroup() {
        return propertyDesignerCostGroup().get();
    }

    public void setDesignerCostGroup(String designerCostGroup) {
        propertyDesignerCostGroup().set(designerCostGroup);
        getEfsElementMara().setDesignerCostGroup(designerCostGroup);
    }

    public void setDesignerPhoneNumber(String designerPhoneNumber) {
        propertyDesignerPhoneNumber().set(designerPhoneNumber);
        getEfsElementMara().setDesignerPhoneNumber(designerPhoneNumber);
    }

    public void setKStandRelDate(Date kStandRelDate) {
        propertyKStandRelDate().set(kStandRelDate);
        getEfsElementMara().setKStandReleaseDate(kStandRelDate);
    }

    public Date getTioFreiRelDate() {
        return propertyTioFreiRelDate().get();
    }

    public void setTioFreiRelDate(Date tioFreiRelDate) {
        propertyTioFreiRelDate().set(tioFreiRelDate);
        getEfsElementMara().setTioFreiReleaseDate(tioFreiRelDate);
    }

    public void setMfpStatus(String mfpStatus) {
        propertyMfpStatus().set(mfpStatus);
        getEfsElementMara().setMfpStatus(mfpStatus);
    }

    public void setMfpThickness(Double mfpThickness) {
        propertyMaterialThickness().set(mfpThickness);
        getEfsElementMara().setMfpThickness(mfpThickness);
    }

    public String getKseKz() {
        return propertyKseKz().get();
    }

    public void setKseKz(String kseKz) {
        propertyKseKz().set(kseKz);
        getEfsElementMara().setKseKz(kseKz);
    }

    public String getWeightAcceptedFromEpis() {
        return propertyWeightAcceptedFromEpis().get();
    }

    public void setWeightAcceptedFromEpis(String weightAcceptedFromEpis) {
        propertyWeightAcceptedFromEpis().set(weightAcceptedFromEpis);
        getEfsElementMara().setWeightAcceptedFromEPIS(weightAcceptedFromEpis);
    }

    public String getBaukastenStatus() {
        return propertyBaukastenStatus().get();
    }

    public void setBaukastenStatus(String baukastenStatus) {
        propertyBaukastenStatus().set(baukastenStatus);
    }

    public String getBaukastenNodeId() {
        return propertyBaukastenNodeId().get();
    }

    public void setBaukastenNodeId(String baukastenNodeId) {
        propertyBaukastenNodeId().set(baukastenNodeId);
    }

    public String getProcessStatus() {
        return propertyProcessStatus().get();
    }

    public void setProcessStatus(String processStatus) {
        propertyProcessStatus().set(processStatus);
    }

    public String getDmuRelevant() {
        return propertyDmuRelevant().get();
    }

    public void setDmuRelevant(String dmuRelevant) {
        propertyDmuRelevant().set(dmuRelevant);
    }

    public void setMaterialType(String materialType) {
        propertyMaterialType().set(materialType);
    }

    public Date getEarliestPvs() {
        return propertyEarliestPvs().get();
    }

    public void setEarliestPvs(Date earliestPvs) {
        propertyEarliestPvs().set(earliestPvs);
    }

    public Date getEarliestNs() {
        return propertyEarliestNs().get();
    }

    public void setEarliestNs(Date earliestNs) {
        propertyEarliestNs().set(earliestNs);
    }

    public Date getEarliestSop() {
        return propertyEarliestSop().get();
    }

    public void setEarliestSop(Date earliestSop) {
        propertyEarliestSop().set(earliestSop);
    }

    public void setPActivationDate(Date pActivationDate) {
        propertyPActivationDate().set(pActivationDate);
    }

    public Date getKonstructureDate() {
        return propertyConstructureDate().get();
    }

    public void setConstructureDate(Date constructureDate) {
        propertyConstructureDate().set(constructureDate);
    }

    public String getAvonStatus() {
        return propertyAvonStatus().get();
    }

    public void setAvonStatus(String avonStatus) {
        propertyAvonStatus().set(avonStatus);
    }

    public String getPrNumberRule() {
        return propertyPrNumberRule().get();
    }

    public void setPrNumberRule(String prNumberRule) {
        propertyPrNumberRule().set(prNumberRule);
        getUserObject().setPrNumberRule(prNumberRule);
    }

    public Long getTisSort() {
        return propertyTisSort().get();
    }

    public void setTisSort(Long tisSort) {
        propertyTisSort().set(tisSort);
        getUserObject().setTisSort(tisSort);
    }

    public Long getId() {
        return getUserObject().getId();
    }

    public void setId(Long id) {
        getUserObject().setId(id);
    }

    public Integer getDeleted() {
        return getUserObject().getDeleted();
    }

    @Override
    public boolean isDeleted() {
        return getUserObject().isDeleted() != null && getUserObject().isDeleted();
    }

    public void setDeleted(Integer deleted) {
        getUserObject().setDeleted(deleted);
    }

    public String getAggregate() {
        return getUserObject().getAggregate();
    }

    public void setAggregate(String aggregate) {
        getUserObject().setAggregate(aggregate);
    }

    public StringProperty propertyGroupString() {
        return propertyGroupString;
    }

    public void setPropertyGroupString(String string) {
        propertyGroupString().set(string);
    }

    public StringProperty propertyNodeId() {
        return propertyNodeId;
    }

    public void setNodeId(String nodeId) {
        propertyNodeId.set(nodeId);
    }

    public StringProperty propertyWahlweiseFall() {
        return propertyWahlweiseFall;
    }

    public void setWahlweiseFall(String wahlweiseFall) {
        propertyWahlweiseFall.set(wahlweiseFall);
    }

    public ObjectProperty<Integer> propertyWahlweiseNr() {
        return propertyWahlweiseNr;
    }

    public void setWahlweiseNr(Integer nr) {
        propertyWahlweiseNr.set(nr);
    }
}
