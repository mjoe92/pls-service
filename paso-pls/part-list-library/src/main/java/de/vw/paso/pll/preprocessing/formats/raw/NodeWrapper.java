package de.vw.paso.pll.preprocessing.formats.raw;

/**
 * Import format specification for for one line of NODE raw format as delivered
 * by TI-WH. Will be translated to NodePPF in structure of a part list.
 */
public class NodeWrapper extends RowWrapper {

	private String product;
	private String parentGUID;
	private String GUID;
	private int nodeLevel;
	private String nodeValue;
	private String nodeValueParent;
	private String nodeType;
	private String nodeLabel;
	private int sortOrder;

	public NodeWrapper(String line) {
		product = line.substring(0, 4).trim();
		parentGUID = line.substring(4, 36);
		GUID = line.substring(36, 68);
		nodeLevel = Integer.parseInt(line.substring(68, 78).trim());
    sortOrder = Integer.parseInt(line.substring(79, 85));
    nodeType = line.substring(97, 105).trim();
    nodeValueParent = line.substring(105, 145).trim();
    nodeValue = line.substring(145, 185).trim();
    nodeLabel = line.substring(245, 305).trim();
	}

	@Override
	public boolean testRowFormat() {
		return getGUID().length() == 32;
	}

	public String getProduct() {
		return product;
	}

	/**
	 * 32 byte UUID of parent node
	 */
	public String getParentGUID() {
		return parentGUID;
	}

	/**
	 * unique 32 byte UUID of node
	 */
	public String getGUID() {
		return GUID;
	}

	public int getNodeLevel() {
	  return nodeLevel;
  }

	/**
	 * Up 40 bytes of node value. Usually contains values like '311_187' that
	 * resemble part numbers.
	 */
	public String getNodeValue() {
		return nodeValue;
	}

  /**
   * Up 40 bytes of node value. Contains the parent node value
   */
  public String getNodeValueParent() {
    return nodeValueParent;
  }

	/**
	 * Max 8 bytes of specifying the node type. Examples 'Z_PBE', 'Z_DOKU'
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * Descriptive name of the node. Often but not always similar to the actual
	 * parts description.
	 */
	public String getNodeLabel() {
		return nodeLabel;
	}

	/**
	 * Sort order number as given by TI-WH. Use this for sorting the part list.
	 */
	public int getSortOrder() {
		return sortOrder;
	}
}
