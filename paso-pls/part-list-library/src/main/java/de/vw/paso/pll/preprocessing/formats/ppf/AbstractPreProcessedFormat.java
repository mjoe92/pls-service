package de.vw.paso.pll.preprocessing.formats.ppf;

import de.vw.paso.pll.PPFUtil;
import de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField;

import java.util.Iterator;
import java.util.List;

public abstract class AbstractPreProcessedFormat {

  private List<PPFField> fields;

  public AbstractPreProcessedFormat() {
    fields = getField();
  }

  protected abstract List<PPFField> getField();

  public abstract PPF getType();

  @SuppressWarnings("unchecked")
  protected String getPPFFieldsString() {
    StringBuilder sb = new StringBuilder();
    Iterator<PPFField> itr = fields.iterator();
    while(itr.hasNext()) {
      Object o = itr.next().getValueProvider().apply(this);
      if (o != null) {
        sb.append(o);
      }

      if (itr.hasNext()) sb.append(PPFUtil.FIELD_SEPARATOR);
    }
    return sb.toString();
  }

  @Override
  public final String toString() {
    return getType().getRowId() + PPFUtil.FIELD_SEPARATOR + getPPFFieldsString();
  }
}
