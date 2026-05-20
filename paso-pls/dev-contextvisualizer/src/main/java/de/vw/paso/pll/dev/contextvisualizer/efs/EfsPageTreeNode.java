package de.vw.paso.pll.dev.contextvisualizer.efs;

import de.vw.paso.pll.dev.contextvisualizer.util.PartNumberUtil;
import de.vw.paso.pll.model.PlsEfsElement;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class EfsPageTreeNode extends DefaultMutableTreeNode {
  private List<PlsEfsElement> duplicates = new ArrayList<>();
  private Map<PlsEfsElement, Integer> countMap;

  public EfsPageTreeNode(PlsEfsElement usedElement, Map<PlsEfsElement, Integer> countMap) {
    super(usedElement);
    this.countMap = countMap;
  }

  public PlsEfsElement getPlsElement() {
    return (PlsEfsElement) getUserObject();
  }

  public List<PlsEfsElement> getDuplicates() {
    return duplicates;
  }

  public boolean hasDuplicates() {
    return !duplicates.isEmpty();
  }

  public List<EfsPageTreeNode> getChildren() {
    Enumeration children = children();
    return Collections.list(children);
  }

  @Override
  public String toString() {
    if (getPlsElement().isGap()) {
      return String.format("RISS: %s (%d)", getPlsElement().getNodeLabel(), countMap.get(getPlsElement()));
    }
    if (!getPlsElement().isPartFound()) {
      return "Part not found";
    }
    return String.format("%s  %s  %s (%d)",
      PartNumberUtil.toString(getPlsElement().getPartNumber()),
      getPlsElement().getDescription1De(),
      getPlsElement().getDescription2De(),
      countMap.get(getPlsElement()));
  }
}
