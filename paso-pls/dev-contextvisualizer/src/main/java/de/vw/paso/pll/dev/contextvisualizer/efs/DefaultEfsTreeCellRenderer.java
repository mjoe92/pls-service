package de.vw.paso.pll.dev.contextvisualizer.efs;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.vw.paso.pll.creation.PartListCreationResult;
import de.vw.paso.pll.dev.contextvisualizer.util.Icons;
import de.vw.paso.pll.model.PlsEfsElement;

public class DefaultEfsTreeCellRenderer extends DefaultTreeCellRenderer {

  PartListCreationResult result;

  DefaultEfsTreeCellRenderer(PartListCreationResult result) {
    this.result = result;
  }

  @Override
  public Component getTreeCellRendererComponent(JTree jTree, Object o, boolean b, boolean b1, boolean b2, int i,
    boolean b3) {
    JLabel comp = (JLabel) super.getTreeCellRendererComponent(jTree, o, b, b1, b2, i, b3);
    if (o instanceof EfsPageTreeNode) {
      EfsPageTreeNode node = (EfsPageTreeNode) o;
      ImageIcon icon;
      PlsEfsElement uo = node.getPlsElement();
      if (uo != null) {
        if (uo.isGap()) {
          icon = Icons.EFS.EFS_TREE_WARNING;
        } else if (isParentRiss(uo)) {
          icon = Icons.EFS.EFS_TREE_RISS;
        } else if (!uo.isPartFound()) {
          icon = Icons.EFS.EFS_TREE_ERROR;
        } else if (errorInChildren(uo)) {
          icon = Icons.EFS.EFS_TREE_WARNING;
        } else if (node.hasDuplicates()) {
          icon = Icons.EFS.EFS_TREE_DUPLICATE_PART;
        } else {
          icon = Icons.EFS.EFS_TREE_OK;
        }
        comp.setIcon(icon);
      }
    }
    return comp;
  }

  private boolean errorInChildren(PlsEfsElement parent) {
    for (PlsEfsElement ele : parent.getChildren()) {
      if (!ele.isPartFound() || errorInChildren(ele)) {
        return true;
      }
    }
    return false;
  }

  private boolean isParentRiss(PlsEfsElement element) {
    return result.getEfsElementByNodeId(element.getOriginParentNodeId()).stream().findFirst().map(PlsEfsElement::isGap)
      .orElse(false);
  }
}
