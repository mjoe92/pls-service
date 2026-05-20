package de.vw.paso.pll.dev.contextvisualizer.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NodeIdentifier {
  private String nodeId;
  private Integer partSort;
  private String partNumber;

  public NodeIdentifier(String nodeId) {
    this(nodeId, null, null);
  }

  public NodeIdentifier(String nodeId, Integer partSort) {
    this(nodeId, partSort, null);
  }

  public NodeIdentifier(String nodeId, Integer partSort, String partNumber) {
    this.nodeId = nodeId;
    this.partSort = partSort;
    this.partNumber = partNumber;
  }

  public String getPartNumber() {
    return partNumber;
  }

  public String getNodeId() {
    return nodeId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    NodeIdentifier that = (NodeIdentifier) o;

    return new EqualsBuilder()
      .append(nodeId, that.nodeId)
      .append(partSort, that.partSort)
      .append(partNumber, that.partNumber)
      .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
      .append(nodeId)
      .append(partSort)
      .append(partNumber)
      .toHashCode();
  }
}
