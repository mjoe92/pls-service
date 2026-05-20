package de.vw.paso.pll.preprocessing.formats.ppf.field;

import de.vw.paso.pll.model.PlsEfsElement;
import de.vw.paso.pll.preprocessing.formats.ppf.EbkVsdPPF;
import de.vw.paso.pll.preprocessing.formats.raw.MaraWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField.*;

public enum EbkVsdFields implements PPFField<EbkVsdPPF> {
  BAUKASTEN_NODE_ID(ebk -> ebk.getRawRow().getBaukastenNodeId()),
  EBK_NODE_ID(ebk -> ebk.getRawRow().getEbkNodeId()),
  EBK_PARENT_NODE_ID(ebk -> ebk.getRawRow().getParentEbkNodeId()),
  BAUKASTEN_STATUS(ebk -> ebk.getRawRow().getBaukastenStatus()),
  BAUKASTEN_PARTNUMBER(ebk -> ebk.getRawRow().getPartNumber()),
  BAUKASTEN_PARTNUMBER_VORNUMMER(ebk -> ebk.getRawRow().getPartNumberVornummer()),
  BAUKASTEN_PARTNUMBER_MITTELGRUPPE(ebk -> ebk.getRawRow().getPartNumberMittelGruppe()),
  BAUKASTEN_PARTNUMBER_ENDNUMBER(ebk -> ebk.getRawRow().getPartNumberEndNumber()),
  BAUKASTEN_PARTNUMBER_INDEX(ebk -> ebk.getRawRow().getPartNumberIndex()),
  BAUKASTEN_PARTNUMBER_PARENT(ebk -> ebk.getRawRow().getPartnumberParent()),
  NODE_LABEL(ebk -> ebk.getRawRow().getNodeLabel()),

  TRANSLATION_GER(ebk -> Optional.ofNullable(ebk.getMara()).map(MaraWrapper::getTranslationGer).orElse(null)),
  DESCRIPTION2(ebk -> Optional.ofNullable(ebk.getMara()).map(MaraWrapper::getDescription2).orElse(null)),
  TRANSLATION_EN(ebk -> Optional.ofNullable(ebk.getMara()).map(MaraWrapper::getTranslationEn).orElse(null)),

  CONSTRUCTION_GROUP(ebk -> ebk.getRawRow().getKonstructionGroup()),
  COST_GROUP(ebk -> ebk.getRawRow().getCostGroup()),
  PRODUCT_STRUCTURE_KZ(ebk -> ebk.getRawRow().getProductStructureKz()),
  SET_KZ(ebk -> ebk.getRawRow().getSeTKz()),
  VWS(ebk -> ebk.getRawRow().getVWS()),
  WAHLWEISE_FALL(ebk -> ebk.getRawRow().getWahlweiseFall()),
  WAHLWEISE_NR(ebk -> ebk.getRawRow().getWahlweiseNr()),

  EINSATZ(ebk -> ebk.getRawRow().getEinsatzSchl()),
  EINSATZ_DATE(ebk -> ebk.getRawRow().getEinsatzDate()),
  ENTFALL(ebk -> ebk.getRawRow().getEntfallSchl()),
  ENTFALL_DATE(ebk -> ebk.getRawRow().getEntfallDate()),

  QUANTITY(ebk -> ebk.getRawRow().getQuantity()),
  QUANTITY_UNIT(ebk -> ebk.getRawRow().getQuantityUnit()),
  QUANTITY_UNIT_ADDITION(ebk -> ebk.getRawRow().getQuantityUnitAddition()),

  PART_TYPE(ebk -> ebk.getRawRow().getPartType()),
  PROCESSING_STATUS(ebk -> ebk.getRawRow().getProcessingStatus()),
  AGGREGAT(ebk -> ebk.getRawRow().getAggregat()),
  WORK_PACKAGE_NUMBER(ebk -> ebk.getRawRow().getWorkPackageNumber()),

  P_ACTIVATION_DATE(ebk -> ebk.getRawRow().getpActivationDate()),
  B_ACTIVATION_DATE(ebk -> ebk.getRawRow().getEntwicklungsstand()),

  PR_NUMBER_RULE_ID(EbkVsdPPF::getRuleID),
  SORT(ebk -> ebk.getRawRow().getSort());

  /**
   * Defines how to set the fields in the efs element.
   * Could be added to the enum definition above, but is really hard to read.
   */
  static final Map<EbkVsdFields, FieldSetter> setter = new HashMap<>();
  static {
    setter.put(BAUKASTEN_NODE_ID, PlsEfsElement::setOriginNodeId);
    setter.put(EBK_NODE_ID, (element, value) -> {});
    setter.put(EBK_PARENT_NODE_ID, (element, value) -> {});
    setter.put(BAUKASTEN_STATUS, PlsEfsElement::setBaukastenStatus);
    setter.put(BAUKASTEN_PARTNUMBER, PlsEfsElement::setPartNumber);
    setter.put(BAUKASTEN_PARTNUMBER_VORNUMMER, PlsEfsElement::setPartNumberVornummer);
    setter.put(BAUKASTEN_PARTNUMBER_MITTELGRUPPE, PlsEfsElement::setPartNumberMittelGruppe);
    setter.put(BAUKASTEN_PARTNUMBER_ENDNUMBER, PlsEfsElement::setPartNumberEndNumber);
    setter.put(BAUKASTEN_PARTNUMBER_INDEX, PlsEfsElement::setPartNumberIndex);
    setter.put(BAUKASTEN_PARTNUMBER_PARENT, (element, value) -> {});

    setter.put(NODE_LABEL, PlsEfsElement::setNodeLabel);
    setter.put(TRANSLATION_GER, PlsEfsElement::setDescription1De);
    setter.put(DESCRIPTION2, PlsEfsElement::setDescription2De);
    setter.put(TRANSLATION_EN, PlsEfsElement::setDescription1En);

    setter.put(CONSTRUCTION_GROUP, PlsEfsElement::setConstructionsGroup);
    setter.put(COST_GROUP, PlsEfsElement::setCostGroup);
    setter.put(PRODUCT_STRUCTURE_KZ, PlsEfsElement::setProductStructure);
    setter.put(SET_KZ, PlsEfsElement::setSetKey);
    setter.put(VWS, (element, value) -> element.setBomNumber(toInteger(value)));
    setter.put(WAHLWEISE_FALL, PlsEfsElement::setWahlweiseFall);
    setter.put(WAHLWEISE_NR, (element, value) -> element.setWahlweiseNr(toInteger(value)));

    setter.put(EINSATZ, PlsEfsElement::setBeginDateKey);
    setter.put(EINSATZ_DATE, (element, value) -> element.setBeginDate(toDate(value)));
    setter.put(ENTFALL, PlsEfsElement::setEndDateKey);
    setter.put(ENTFALL_DATE, (element, value) -> element.setEndDate(toDate(value)));

    setter.put(QUANTITY, (element, value) -> element.setQuantity(toInteger(value, 0)));
    setter.put(QUANTITY_UNIT, (element, value) -> element.setQuantityUnit(toQuantityUnit(value)));
    setter.put(QUANTITY_UNIT_ADDITION, PlsEfsElement::setQuantityUnitExtended);

    setter.put(PART_TYPE, PlsEfsElement::setPartType);
    setter.put(PROCESSING_STATUS, PlsEfsElement::setProcessStatus);
    setter.put(AGGREGAT, PlsEfsElement::setAggregate);
    setter.put(WORK_PACKAGE_NUMBER, PlsEfsElement::setWorkPackageNumber);

    setter.put(P_ACTIVATION_DATE, ((element, value) -> element.setpActivationDate(toDate(value))));
    setter.put(B_ACTIVATION_DATE, (element, value) -> element.setKonstructureDate(toDate(value)));

    setter.put(PR_NUMBER_RULE_ID, (element, value) -> {});
    setter.put(SORT, (element, value) -> element.setPartSort(toInteger(value)));
  }

  final Function<EbkVsdPPF, ?> valueProvider;

  <T> EbkVsdFields(Function<EbkVsdPPF, T> valueProvider) {
    this.valueProvider = valueProvider;
  }

  @Override
  public Function<EbkVsdPPF, ?> getValueProvider() {
    return valueProvider;
  }

  public void setValue(PlsEfsElement element, String value) {
    setter.get(this).set(element, value);
  }
}
