package de.vw.paso.pll.dev.contextvisualizer.ppf;

import de.vw.paso.pll.creation.filter.PartFilterResult;
import de.vw.paso.pll.dev.contextvisualizer.checker.PPFVisitor;
import de.vw.paso.pll.dev.contextvisualizer.util.NodeIdentifier;
import de.vw.paso.pll.dev.contextvisualizer.util.components.TooltipProviderNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public abstract class AbstractPPFTreeNode<T extends Enum> extends DefaultMutableTreeNode implements TooltipProviderNode {

  private String[] data;
  private T[] fields;
  private String nodeText;

  private PartFilterResult filterResult = PartFilterResult.notFilteredOut();
  private boolean ruleActive = true;

  public AbstractPPFTreeNode(String[] data, T[] fields) {
    this.data = data;
    this.fields = fields;
  }

  public T[] getFields() {
    return fields;
  }

  public String getData(Enum field) {
    return data[field.ordinal() + 1];
  }

  public String[] getData() {
    return data;
  }

  protected abstract T[] getTreeNodeLabelFields();

  protected abstract NodeIdentifier createNodeIdentifier();

  @Override
  public String toString() {
    if (nodeText == null) {
      StringBuilder sb = new StringBuilder();
      Arrays.stream(getTreeNodeLabelFields()).forEach(e -> sb.append(getData(e)).append(" "));
      nodeText = sb.toString();
    }
    return nodeText;
  }

  public List<AbstractPPFTreeNode> getChildren() {
    Enumeration children = children();
    return Collections.list(children);
  }

  public abstract void accept(PPFVisitor visitor);

  public void setFilterResult(PartFilterResult filterResult) {
    this.filterResult = filterResult;
  }

  public PartFilterResult getFilterResult() {
    return filterResult;
  }

  public boolean isRuleActive() {
    return ruleActive;
  }

  public void setRuleActive(boolean ruleActive) {
    this.ruleActive = ruleActive;
  }

  @Override
  public String getToolTip() {
    if (filterResult.isFilteredOut()) {
      return filterResult.getErrorMessageAsHtml();
    }
    return null;
  }
}
