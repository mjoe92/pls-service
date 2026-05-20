package de.vw.paso.pll.preprocessing.formats.ppf;

import de.vw.paso.pll.preprocessing.formats.ppf.field.NodeFields;
import de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField;
import de.vw.paso.pll.preprocessing.formats.raw.NodeWrapper;

import java.util.*;

public class NodePPF extends AbstractPreProcessedFormat {

  private static List<PPFField> fields = Arrays.asList(NodeFields.values());

	/**
	 * In TI-WH each product contains one node of this type. It defines the single
	 * root node of a partlist
	 */
	private static final String ROOT_NODE_TYPE = "Z_HD";

	private NodeWrapper rawNode;

	private SortedSet<NodePPF> children = new TreeSet<>(Comparator.comparingInt(NodePPF::getSort));

	private SortedSet<EbomPPF> eboms = new TreeSet<>(Comparator.comparingInt(EbomPPF::getSort));

	private String nodeId;
	private String parentNodeId;

	public NodePPF(NodeWrapper w) {
		rawNode = w;
    nodeId = w.getGUID();
    parentNodeId = w.getParentGUID();
	}

  @Override
  protected List<PPFField> getField() {
    return fields;
  }

	@Override
	public PPF getType() {
		return PPF.NODE;
	}

	public NodeWrapper getRawNode() {
		return rawNode;
	}

	public SortedSet<NodePPF> getChildren() {
		return children;
	}

	public void addChild(NodePPF child) {
		children.add(child);
	}

	public SortedSet<EbomPPF> getEboms() {
		return eboms;
	}

	public void addEbom(EbomPPF part) {
		eboms.add(part);
	}

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public String getParentNodeId() {
    return parentNodeId;
  }

  public void setParentNodeId(String parentNodeId) {
    this.parentNodeId = parentNodeId;
  }

  public int getSort() {
		return rawNode.getSortOrder();
	}

	public boolean hasParent() {
		return !rawNode.getNodeType().equalsIgnoreCase(ROOT_NODE_TYPE);
	}
}
