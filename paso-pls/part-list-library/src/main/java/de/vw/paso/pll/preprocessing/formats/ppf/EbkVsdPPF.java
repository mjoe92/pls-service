package de.vw.paso.pll.preprocessing.formats.ppf;

import de.vw.paso.pll.preprocessing.formats.ppf.field.EbkVsdFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField;
import de.vw.paso.pll.preprocessing.formats.raw.EbkVsdWrapper;
import de.vw.paso.pll.preprocessing.formats.raw.MaraWrapper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class EbkVsdPPF extends AbstractPreProcessedFormat {

  private static List<PPFField> fields = Arrays.asList(EbkVsdFields.values());

  private EbkVsdWrapper rawRow;

  private MaraWrapper mara;

  private TreeSet<EbkVsdPPF> children = new TreeSet<>(Comparator.comparing(EbkVsdPPF::getSort));

  private Integer ruleID;

  public EbkVsdPPF(String row) {
    rawRow = new EbkVsdWrapper(row);
  }

  @Override
  protected List<PPFField> getField() {
    return fields;
  }

  @Override
  public PPF getType() {
    return PPF.EBK;
  }

  public TreeSet<EbkVsdPPF> getChildren() {
    return children;
  }

  public EbkVsdWrapper getRawRow() {
    return rawRow;
  }

  public MaraWrapper getMara() {
    return mara;
  }

  public void setMara(MaraWrapper mara) {
    this.mara = mara;
  }

  public String getBaukastenNodeId() {
    return rawRow.getBaukastenNodeId();
  }

  public int getSort() {
    return rawRow.getSort();
  }

  public Integer getRuleID() {
    return ruleID;
  }

  public void setRuleID(Integer ruleID) {
    this.ruleID = ruleID;
  }
}
