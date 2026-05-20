package de.vw.paso.pll.model;

public enum FilterType {

  FILTER_EMPTY_PARTNUMBER("filter.empty.partnunber"),
  FILTER_VALID_DATE_BEFORE_EINSATZ("filter.valid.date.is.before"),
  FILTER_VALID_DATE_NOT_BETWEEN_EINSATZ_ENTFALL("filter.valid.date.not.between"),
  FILTER_PR_NUMBER_RULE("filter.pr.number.rule"),
  FILTER_AUSGLEICHGEWICHTE("filter.ausgleichgewichte"),
  FILTER_KUNDENDIENSTELLE("filter.kundendienstelle"),
  FILTER_QUANTITY_UNIT_EXTENDED("filter.quantity.unit.extended"),
  SUB_FILTER_MEE("filter.sub.mee"),
  SUB_FILTER_BAUKASTEN("filter.sub.baukasten"),
  FILTER_PFLEGEMITTEL("filter.pflegemittel"),
  FILTER_SAMMLER_000890("filter.sammler"),
  FILTER_SOFTWARE("filter.software"),
  SUB_RULE_SOFTWARE("filter.sub.rule.software"),
  FILTER_TEILENUMMER_L("filter.teilenummern.l"),
  FILTER_VERRECHNUNGSNUMMER("filter.verrechnungsnummer"),
  FILTER_TRANSPORTSCHUTZ("filter.transportschutz"),
  FILTER_TRANSPORTSICHERUNG("filter.transportsicherung"),
  FILTER_PASO_RULES("filter.paso.rules"),
  FILTER_PROCESS_STATUS("filter.process.status");

  private final String key;

  FilterType(String key) {
    this.key = key;
  }

  public String getKey() {
    return this.key;
  }

}
