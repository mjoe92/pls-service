package de.vw.paso.pll.preprocessing.formats.ppf;

import de.vw.paso.pll.preprocessing.formats.ppf.field.EbkVsdFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbomFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.NodeFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField;

public enum PPF {
  NODE("N", NodeFields.values()),
  PART("P", EbomFields.values()),
  EBK("B", EbkVsdFields.values());

  private String rowId;

  private PPFField[] fields;

  PPF(String rowId, PPFField[] fields) {
    this.rowId = rowId;
    this.fields = fields;
  }

  public String getRowId() {
    return rowId;
  }

  public PPFField[] getFields() {
    return fields;
  }
}
