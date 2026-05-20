package de.vw.paso.pll.dev.contextvisualizer.ppf;

import de.vw.paso.pll.dev.contextvisualizer.checker.PPFVisitor;
import de.vw.paso.pll.dev.contextvisualizer.util.NodeIdentifier;
import de.vw.paso.pll.preprocessing.formats.ppf.field.NodeFields;

public class NodePPFTreeNode extends AbstractPPFTreeNode<NodeFields> {

  private static NodeFields[] treeNodeLabelFields = new NodeFields[]{NodeFields.NODE_LABEL};

  public NodePPFTreeNode(String[] data) {
    super(data, NodeFields.values());
  }

  @Override
  protected NodeFields[] getTreeNodeLabelFields() {
    return treeNodeLabelFields;
  }

  @Override
  public NodeIdentifier createNodeIdentifier() {
    String id = getData(NodeFields.NODE_ID);
    return new NodeIdentifier(id);
  }

  @Override
  public void accept(PPFVisitor visitor) {
    getChildren().forEach(ele -> ele.accept(visitor));
    visitor.visit(this);
  }
}
