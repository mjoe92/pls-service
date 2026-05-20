package de.vw.paso.pll.dev.contextvisualizer.checker;

import de.vw.paso.pll.creation.PartChecker;
import de.vw.paso.pll.creation.PartListCreatorUtil;
import de.vw.paso.pll.dev.contextvisualizer.ppf.EbkvsdPPFTreeNode;
import de.vw.paso.pll.dev.contextvisualizer.ppf.EbomPPFTreeNode;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbkVsdFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbomFields;


public class NotRelevantChecker implements PPFVisitor {

  PartChecker checker;

  public NotRelevantChecker(PartChecker checker) {
    this.checker = checker;
  }

  @Override
  public void visit(EbomPPFTreeNode ebomPPFTreeNode) {
    ebomPPFTreeNode.setFilterResult(checker.checkEbomFilterAndDate(ebomPPFTreeNode.getData(), ebomPPFTreeNode.getNodeData()));
    ebomPPFTreeNode.setRuleActive(checker.isRuleActive(
        PartListCreatorUtil.getPartData(ebomPPFTreeNode.getData(), EbomFields.RULE_ID)));
  }

  @Override
  public void visit(EbkvsdPPFTreeNode ebkvsdPPFTreeNode) {
    ebkvsdPPFTreeNode.setFilterResult(checker.checkEbkPartRelevant(ebkvsdPPFTreeNode.getData()));
    if (!checker.isRuleActive(
        PartListCreatorUtil.getPartData(ebkvsdPPFTreeNode.getData(), EbkVsdFields.PR_NUMBER_RULE_ID))) {
      ebkvsdPPFTreeNode.setRuleActive(false);
    }
  }
}
