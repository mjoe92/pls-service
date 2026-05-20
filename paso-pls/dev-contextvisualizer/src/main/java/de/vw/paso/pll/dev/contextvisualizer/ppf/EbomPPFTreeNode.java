package de.vw.paso.pll.dev.contextvisualizer.ppf;

import de.vw.paso.pll.dev.contextvisualizer.checker.PPFVisitor;
import de.vw.paso.pll.dev.contextvisualizer.util.NodeIdentifier;
import de.vw.paso.pll.preprocessing.formats.ppf.field.EbomFields;
import org.apache.commons.lang3.StringUtils;

public class EbomPPFTreeNode extends AbstractPPFTreeNode<EbomFields> {

  private static EbomFields[] treeNodeLabelFields = new EbomFields[]{EbomFields.PART_NUMBER, EbomFields.TRANSLATION_GER, EbomFields.DESCRIPTION2};

  private String nodeId;

  private String[] nodeData;

  public EbomPPFTreeNode(String nodeID, String[] partData, String[] nodeData) {
    super(partData, EbomFields.values());
    this.nodeId = nodeID;
    this.nodeData = nodeData;
  }

  @Override
  protected EbomFields[] getTreeNodeLabelFields() {
    return treeNodeLabelFields;
  }

  @Override
  protected NodeIdentifier createNodeIdentifier() {
    String sort = getData(EbomFields.SORT);
    return new NodeIdentifier(nodeId, Integer.valueOf(sort));
  }

  @Override
  public void accept(PPFVisitor visitor) {
    getChildren().forEach(ele -> ele.accept(visitor));
    visitor.visit(this);
  }

  public String getNodeId() {
    return nodeId;
  }

  public String[] getNodeData() {
    return nodeData;
  }

  @Override
  public String toString() {
    if (StringUtils.isEmpty(getData(EbomFields.PART_NUMBER))) {
      return "No Mara";
    }
    return super.toString();
  }
}
