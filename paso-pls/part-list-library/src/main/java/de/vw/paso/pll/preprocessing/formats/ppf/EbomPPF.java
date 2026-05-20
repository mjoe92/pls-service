package de.vw.paso.pll.preprocessing.formats.ppf;

import de.vw.paso.pll.preprocessing.formats.ppf.field.EbomFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField;
import de.vw.paso.pll.preprocessing.formats.raw.EbomWrapper;
import de.vw.paso.pll.preprocessing.formats.raw.MaraWrapper;

import java.util.*;

public class EbomPPF extends AbstractPreProcessedFormat {

  private static List<PPFField> fields = Arrays.asList(EbomFields.values());

  private EbomWrapper ebom;

  private MaraWrapper mara;

  private Integer ruleID;

  private SortedSet<EbkVsdPPF> ebks = new TreeSet<>(Comparator.comparingInt(EbkVsdPPF::getSort));

  private String nodeId;

  public EbomPPF(EbomWrapper ebom) {
    if (ebom == null) {
      throw new RuntimeException("Ebom cannot be null");
    }
    this.ebom = ebom;
    nodeId = ebom.getNodeId();
  }

  @Override
  protected List<PPFField> getField() {
    return fields;
  }

  @Override
  public PPF getType() {
    return PPF.PART;
  }

  public EbomWrapper getEbom() {
    return ebom;
  }

  public MaraWrapper getMara() {
    return mara;
  }

  public void setMara(MaraWrapper mara) {
    this.mara = mara;
  }

  public Integer getRuleID() {
    return ruleID;
  }

  public void setRuleID(Integer ruleID) {
    this.ruleID = ruleID;
  }

  public String getRule() {
    return  ebom.getPrNrRule();
  }

  public SortedSet<EbkVsdPPF> getEbks() {
    return ebks;
  }

  public String getPartNumber() {
    return ebom.getPartNumber();
  }

  public int getSort() {
    return ebom.getSort();
  }

  public String getNodeId() {
    return nodeId;
  }
}
