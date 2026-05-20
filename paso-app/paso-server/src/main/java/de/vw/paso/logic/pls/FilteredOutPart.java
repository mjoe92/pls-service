package de.vw.paso.logic.pls;

public class FilteredOutPart {

  private String nodeId;
  private PlsEfsElement filteredOutPart;
  private String reason;
  private String productId;
  private boolean removeChildren;

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public PlsEfsElement getFilteredOutPart() {
    return filteredOutPart;
  }

  public void setFilteredOutPart(PlsEfsElement filteredOutPart) {
    this.filteredOutPart = filteredOutPart;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public boolean isRemoveChildren() {
    return removeChildren;
  }

  public void setRemoveChildren(boolean removeChildren) {
    this.removeChildren = removeChildren;
  }
}
