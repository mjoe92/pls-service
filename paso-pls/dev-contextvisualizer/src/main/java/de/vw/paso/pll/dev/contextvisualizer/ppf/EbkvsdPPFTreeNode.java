package de.vw.paso.pll.dev.contextvisualizer.ppf;

import de.vw.paso.pll.dev.contextvisualizer.checker.PPFVisitor;
import de.vw.paso.pll.dev.contextvisualizer.util.NodeIdentifier;
import de.vw.paso.pll.dev.contextvisualizer.util.PartNumberUtil;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbkVsdFields;

public class EbkvsdPPFTreeNode extends AbstractPPFTreeNode<EbkVsdFields> {

  private static EbkVsdFields[] treeNodeLabelFields = new EbkVsdFields[]{EbkVsdFields.BAUKASTEN_PARTNUMBER, EbkVsdFields.TRANSLATION_GER, EbkVsdFields.DESCRIPTION2};

  public EbkvsdPPFTreeNode(String[] data) {
    super(data, EbkVsdFields.values());
  }

  @Override
  protected EbkVsdFields[] getTreeNodeLabelFields() {
    return treeNodeLabelFields;
  }

  @Override
  protected NodeIdentifier createNodeIdentifier() {
    String id = getData(EbkVsdFields.BAUKASTEN_NODE_ID);
    String sort = getData(EbkVsdFields.SORT);
    String pn = getData(EbkVsdFields.BAUKASTEN_PARTNUMBER);
    String converted = PartNumberUtil.toString(pn);
    return new NodeIdentifier(id, Integer.valueOf(sort), converted);
  }

  @Override
  public void accept(PPFVisitor visitor) {
    getChildren().forEach(ele -> ele.accept(visitor));
    visitor.visit(this);
  }
}
