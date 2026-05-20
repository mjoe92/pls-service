package de.vw.paso.pll.preprocessing.formats.ppf.field;

import static de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField.toDate;
import static de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField.toDouble;
import static de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField.toInteger;
import static de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField.toQuantityUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import de.vw.paso.pll.model.PlsEfsElement;
import de.vw.paso.pll.model.WeightControlFlag;
import de.vw.paso.pll.preprocessing.formats.ppf.EbomPPF;
import de.vw.paso.pll.preprocessing.formats.raw.MaraWrapper;

public enum EbomFields implements PPFField<EbomPPF> {

    PART_NUMBER(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getPartnumber).orElse(null)),
    PART_NUMBER_VORNUMMER(
        ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getPartNumberVornummer).orElse(null)),
    PART_NUMBER_MITTELGRUPPE(
        ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getPartNumberMittelGruppe).orElse(null)),
    PART_NUMBER_ENDNUMBER(
        ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getPartNumberEndNumber).orElse(null)),
    PART_NUMBER_INDEX(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getPartNumberIndex).orElse(null)),
    WEIGHT_ESTIMATED(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getWeightEstimatedFE).orElse(null)),
    WEIGHT_CALCULATED(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getWeightCalculatedFE).orElse(null)),
    WEIGHT_MEASURED(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getWeightMeasuredFE).orElse(null)),
    WEIGHT_MEASURED_PROD(
        ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getWeightWeightedProd).orElse(null)),
    ZSB(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getZsbKz).orElse(null)),
    K_STAND(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getConstructionsState).orElse(null)),
    DESCRIPTION2(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getDescription2).orElse(null)),
    DRAWING_STATUS(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getDrawingStatus).orElse(null)),
    DRAWING_DATE(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getDrawingDate).orElse(null)),
    BASIC_MATERIAL(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getBasicMaterial).orElse(null)),
    QUALITY(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getQuality).orElse(null)),
    SEE_DRAWING(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getSeeDrawing).orElse(null)),
    RESPONSIBLE_CONSTR_1(
        ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getResponsibleConstr1).orElse(null)),
    RESPONSIBLE_CONSTR_2(
        ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getResponsibleConstr2).orElse(null)),
    BUILD_SAMPLE_APPROVAL(
        ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getBuildSampleApproval).orElse(null)),
    BUILD_SAMPLE_APPROVAL_TARGET_DATE(
        ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getBuildSampleApprovalTargetDate).orElse(null)),
    TECHNICALLY_OKAY(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getTechnicallyOkay).orElse(null)),
    RELEASE_DATE_SOLL(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getReleaseDateSoll).orElse(null)),
    DESIGNER_NAME_2(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getDesignerName).orElse(null)),
    K_STAND_RELEASE_DATE(
        ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getkStandReleaseDate).orElse(null)),
    TIO_FREI_RELEASE_DATE(
        ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getTioFreiReleaseDate).orElse(null)),
    MFP_STATUS(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getMFPStatus).orElse(null)),
    MFP_THICKNESS(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getMFPThickness).orElse(null)),
    KSE_KZ(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getKseKz).orElse(null)),
    WEIGHT_ACCEPTED_FROM_EPIS(
        ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getWeightAcceptedFromEPIS).orElse(null)),
    TRANSLATION_GER(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getTranslationGer).orElse(null)),
    TRANSLATION_EN(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getTranslationEn).orElse(null)),
    TRANSLATION2_EN(ebom -> Optional.ofNullable(ebom.getMara()).map(MaraWrapper::getTranslationEn).orElse(null)),

    RULE_ID(EbomPPF::getRuleID),
    BOM_NUMBER(ebom -> ebom.getEbom().getVWS()),
    PRODUCT(ebom -> ebom.getEbom().getProduct()),
    COSTGROUP(ebom -> ebom.getEbom().getCostGroup()),
    CONSTRUCTION_GROUP(ebom -> ebom.getEbom().getConstructionGroup()),
    PRODUCT_STRUCTURE(ebom -> ebom.getEbom().getProductStructure()),
    GWS(ebom -> ebom.getEbom().getGWS()),
    SET(ebom -> ebom.getEbom().getSetKZ()),
    POSITION_VARIANT(ebom -> ebom.getEbom().getPositionVariant()),
    DELETION_FLAG(ebom -> ebom.getEbom().getDeletionFlag()),
    QUANTITY(ebom -> ebom.getEbom().getQuantity()),
    QUANTITY_UNIT(ebom -> ebom.getEbom().getQuantityUnit()),
    QUANTITY_UNIT_EXTENDED(ebom -> ebom.getEbom().getQuantityUnitExtended()),
    BAUKASTEN_KZ(ebom -> ebom.getEbom().getBaukastenKz()),
    BAUKASTEN_STATUS(ebom -> ebom.getEbom().getBaukastenSt()),
    BAUKASTEN_NODE_ID(ebom -> ebom.getEbom().getBaukastenNodeId()),
    EINSATZ(ebom -> ebom.getEbom().getEinsatzSchl()),
    EINSATZ_DATE(ebom -> ebom.getEbom().getEinsatzDate()),
    ENTFALL(ebom -> ebom.getEbom().getEntfallSchl()),
    ENTFALL_DATE(ebom -> ebom.getEbom().getEntfallDate()),
    WAHLWEISE_FALL(ebom -> ebom.getEbom().getWahlweiseFall()),
    WAHLWEISE_NR(ebom -> ebom.getEbom().getWahlweiseNr()),
    SORT(ebom -> ebom.getEbom().getSort()),
    AGGREGAT(ebom -> ebom.getEbom().getAggregat()),
    PART_TYPE(ebom -> ebom.getEbom().getPartType()),
    WORK_PACKAGE_NUMBER(ebom -> ebom.getEbom().getWorkPackageNumber()),
    PROCESS_STATUS(ebom -> ebom.getEbom().getProcessStatus()),
    DMU_RELEVANT(ebom -> ebom.getEbom().getDMURelevant()),
    MATERIAL_TYPE(ebom -> ebom.getEbom().getMaterialType()),
    WEIGHT_ESTIMATED_DATE(ebom -> ebom.getEbom().getWeightEstimatedFEDate()),
    WEIGHT_CALCULATED_DATE(ebom -> ebom.getEbom().getWeightCalculatedDate()),
    WEIGHT_MEASURED_FE_DATE(ebom -> ebom.getEbom().getWeightWeightedFEDate()),
    WEIGHT_MEASURED_PROD_DATE(ebom -> ebom.getEbom().getWeightWeightedDate()),
    MATERIAL_THICKNESS(ebom -> ebom.getEbom().getMaterialThickness()),
    EARLIEST_PVS(ebom -> ebom.getEbom().getEarliestPVS()),
    EARLIEST_NS(ebom -> ebom.getEbom().getEarliestNS()),
    EARLIEST_SOP(ebom -> ebom.getEbom().getEarliestSOP()),
    DESIGNER_NAME(ebom -> ebom.getEbom().getDesignerName()),
    DESIGNER_COSTGROUP(ebom -> ebom.getEbom().getDesignerCostGroup()),
    DESIGNER_PHONE_NUMBER(ebom -> ebom.getEbom().getDesignerPhoneNumber()),
    P_ACTIVATION_DATE(ebom -> ebom.getEbom().getPActivationDate()),
    KONSTRUCTURE_DATE(ebom -> ebom.getEbom().getKonstructureDate()),
    AVON_STATUS(ebom -> ebom.getEbom().getAvonStatus());

    /**
     * Defines how to set the fields in the efs element.
     * Could be added to the enum definition above, but is really hard to read.
     */
    static final Map<EbomFields, FieldSetter> setter = new HashMap<>();

    static {
        setter.put(PART_NUMBER, PlsEfsElement::setPartNumber);
        setter.put(PART_NUMBER_VORNUMMER, PlsEfsElement::setPartNumberVornummer);
        setter.put(PART_NUMBER_MITTELGRUPPE, PlsEfsElement::setPartNumberMittelGruppe);
        setter.put(PART_NUMBER_ENDNUMBER, PlsEfsElement::setPartNumberEndNumber);
        setter.put(PART_NUMBER_INDEX, PlsEfsElement::setPartNumberIndex);
        setter.put(WEIGHT_ESTIMATED, (element, value) -> element.setWeightEstimatedTe(PPFField.toDouble(value, 0D)));
        setter.put(WEIGHT_CALCULATED, (element, value) -> element.setWeightCalculatedTe(toDouble(value, 0D)));
        setter.put(WEIGHT_MEASURED, (element, value) -> element.setWeightWeightedProd(toDouble(value, 0D)));
        setter.put(WEIGHT_MEASURED_PROD, (element, value) -> element.setWeightWeightedProd(toDouble(value, 0D)));
        setter.put(ZSB, PlsEfsElement::setAssemblyIndicator);//TODO DSt - ZSB?
        setter.put(K_STAND, PlsEfsElement::setConstructionsState);
        setter.put(DESCRIPTION2, PlsEfsElement::setDescription2De);
        setter.put(DRAWING_DATE, (element, value) -> element.setDrawingDate(toDate(value)));
        setter.put(DRAWING_STATUS, PlsEfsElement::setDrawingStatus);
        setter.put(BASIC_MATERIAL, (element, value) -> { });
        setter.put(QUALITY, PlsEfsElement::setQuality);
        setter.put(SEE_DRAWING, PlsEfsElement::setSeeDrawing);
        setter.put(RESPONSIBLE_CONSTR_1, PlsEfsElement::setResponsibleConstr1);
        setter.put(RESPONSIBLE_CONSTR_2, PlsEfsElement::setResponsibleConstr2);
        setter.put(BUILD_SAMPLE_APPROVAL, PlsEfsElement::setBuildSampleApproval);
        setter.put(BUILD_SAMPLE_APPROVAL_TARGET_DATE,
            (element, value) -> element.setBuildSampleApprovalTargetDate(toDate(value)));
        setter.put(TECHNICALLY_OKAY, PlsEfsElement::setTechnicallyOkay);
        setter.put(RELEASE_DATE_SOLL, (element, value) -> element.setReleaseDateSoll(toDate(value)));
        setter.put(DESIGNER_NAME_2, (element, value) -> { });
        setter.put(K_STAND_RELEASE_DATE, (element, value) -> element.setkStandReleaseDate(toDate(value)));
        setter.put(TIO_FREI_RELEASE_DATE, (element, value) -> element.setTioFreiReleaseDate(toDate(value)));
        setter.put(MFP_STATUS, PlsEfsElement::setMFPStatus);
        setter.put(MFP_THICKNESS, (element, value) -> element.setMFPThickness(toDouble(value, 0.0)));
        setter.put(KSE_KZ, PlsEfsElement::setKseKz);
        setter.put(WEIGHT_ACCEPTED_FROM_EPIS, PlsEfsElement::setWeightAcceptedFromEPIS);
        setter.put(TRANSLATION_GER, PlsEfsElement::setDescription1De);
        setter.put(TRANSLATION_EN, PlsEfsElement::setDescription1En);
        setter.put(TRANSLATION2_EN, PlsEfsElement::setDescription2En);

        setter.put(RULE_ID, (element, value) -> { });
        setter.put(BOM_NUMBER, ((element, value) -> element.setBomNumber(toInteger(value))));
        setter.put(PRODUCT, PlsEfsElement::setProduct);
        setter.put(COSTGROUP, PlsEfsElement::setCostGroup);
        setter.put(CONSTRUCTION_GROUP, PlsEfsElement::setConstructionsGroup);
        setter.put(PRODUCT_STRUCTURE, PlsEfsElement::setProductStructure);
        setter.put(GWS, (plsEfsElement, weightControlFlag) -> plsEfsElement.setWeightControlFlag(
            WeightControlFlag.getType(weightControlFlag)));
        setter.put(SET, PlsEfsElement::setSetKey);
        setter.put(POSITION_VARIANT, (PlsEfsElement::setPositionVariant));
        setter.put(DELETION_FLAG, (PlsEfsElement::setDeletionFlag));
        setter.put(QUANTITY, (element, value) -> element.setQuantity(toInteger(value, 0)));
        setter.put(QUANTITY_UNIT, (element, value) -> element.setQuantityUnit(toQuantityUnit(value)));
        setter.put(QUANTITY_UNIT_EXTENDED, PlsEfsElement::setQuantityUnitExtended);
        setter.put(BAUKASTEN_KZ, PlsEfsElement::setBaukastenKz);
        setter.put(BAUKASTEN_STATUS, PlsEfsElement::setBaukastenStatus);
        setter.put(BAUKASTEN_NODE_ID, PlsEfsElement::setBaukastenNodeId);
        setter.put(EINSATZ, PlsEfsElement::setBeginDateKey);
        setter.put(EINSATZ_DATE, (element, value) -> element.setBeginDate(toDate(value)));
        setter.put(ENTFALL, PlsEfsElement::setEndDateKey);
        setter.put(ENTFALL_DATE, (element, value) -> element.setEndDate(toDate(value)));
        setter.put(WAHLWEISE_FALL, PlsEfsElement::setWahlweiseFall);
        setter.put(WAHLWEISE_NR, (element, value) -> element.setWahlweiseNr(toInteger(value)));
        setter.put(SORT, (element, value) -> element.setPartSort(toInteger(value)));
        setter.put(AGGREGAT, PlsEfsElement::setAggregate);
        setter.put(PART_TYPE, PlsEfsElement::setPartType);
        setter.put(WORK_PACKAGE_NUMBER, PlsEfsElement::setWorkPackageNumber);
        setter.put(PROCESS_STATUS, PlsEfsElement::setProcessStatus);
        setter.put(DMU_RELEVANT, PlsEfsElement::setDMURelevant);
        setter.put(MATERIAL_TYPE, PlsEfsElement::setMaterialType);
        setter.put(WEIGHT_ESTIMATED_DATE, (element, value) -> element.setWeightEstimatedTeDate(toDate(value)));
        setter.put(WEIGHT_CALCULATED_DATE, (element, value) -> element.setWeightCalculatedTeDate(toDate(value)));
        setter.put(WEIGHT_MEASURED_FE_DATE, (element, value) -> element.setWeightWeightedTeDate(toDate(value)));
        setter.put(WEIGHT_MEASURED_PROD_DATE, (element, value) -> element.setWeightWeightedProdDate(toDate(value)));
        setter.put(MATERIAL_THICKNESS, (element, value) -> element.setMaterialThickness(toDouble(value, 0.0)));
        setter.put(EARLIEST_PVS, (element, value) -> element.setEarliestPVS(toDate(value)));
        setter.put(EARLIEST_NS, (element, value) -> element.setEarliestNS(toDate(value)));
        setter.put(EARLIEST_SOP, (element, value) -> element.setEarliestSOP(toDate(value)));
        setter.put(DESIGNER_NAME, (PlsEfsElement::setDesignerName));
        setter.put(DESIGNER_COSTGROUP, PlsEfsElement::setDesignerCostGroup);
        setter.put(DESIGNER_PHONE_NUMBER, PlsEfsElement::setDesignerPhoneNumber);
        setter.put(P_ACTIVATION_DATE, (element, value) -> element.setpActivationDate(toDate(value)));
        setter.put(KONSTRUCTURE_DATE, (element, value) -> element.setKonstructureDate(toDate(value)));
        setter.put(AVON_STATUS, PlsEfsElement::setAvonStatus);
    }

    private final Function<EbomPPF, ?> valueProvider;

    <T> EbomFields(Function<EbomPPF, T> valueProvider) {
        this.valueProvider = valueProvider;
    }

    @Override
    public Function<EbomPPF, ?> getValueProvider() {
        return valueProvider;
    }

    public void setValue(PlsEfsElement element, String value) {
        setter.get(this).set(element, value);
    }
}
