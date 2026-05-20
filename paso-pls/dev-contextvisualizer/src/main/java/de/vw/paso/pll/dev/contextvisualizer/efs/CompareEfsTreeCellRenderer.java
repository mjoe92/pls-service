package de.vw.paso.pll.dev.contextvisualizer.efs;

import de.vw.paso.pll.dev.contextvisualizer.util.CompareStatus;
import de.vw.paso.pll.dev.contextvisualizer.util.Icons;
import de.vw.paso.pll.model.PlsEfsElement;
import java.awt.Component;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class CompareEfsTreeCellRenderer extends DefaultTreeCellRenderer {

  private Map<PlsEfsElement, CompareStatus> compareStatusMap;

  public CompareEfsTreeCellRenderer(Map<PlsEfsElement, CompareStatus> compareStatusMap) {
    this.compareStatusMap= compareStatusMap;
  }

  @Override
  public Component getTreeCellRendererComponent(JTree jTree, Object o, boolean b, boolean b1, boolean b2, int i, boolean b3) {
    JLabel comp = (JLabel) super.getTreeCellRendererComponent(jTree, o, b, b1, b2, i, b3);

    if (o instanceof EfsPageTreeNode) {
      EfsPageTreeNode node = (EfsPageTreeNode) o;
      ImageIcon icon;
      PlsEfsElement uo = node.getPlsElement();
      CompareStatus status = compareStatusMap.get(uo);
      if (status != null) {
        switch (status) {
          case OK:
            icon = Icons.Compare.COMPARE_FOUND;
            if (errorInChildren(uo)) {
              icon = Icons.Compare.COMPARE_CHILDREN_ERROR;
            }
            break;
          case NOT_FOUND_IN_VERON:
            icon = Icons.EFS.EFS_TREE_ERROR;
            break;
          case NOT_EQUALS:
            icon = Icons.EFS.EFS_TREE_ERROR;
            break;
          default:
            icon = Icons.EFS.EFS_TREE_WARNING;
        }
      } else {
        icon = Icons.Compare.COMPARE_UNKNOWN;
      }
      comp.setIcon(icon);
    }
    return comp;
  }

  private boolean errorInChildren(PlsEfsElement parent) {
    for (PlsEfsElement ele : parent.getChildren()) {
      CompareStatus status = compareStatusMap.get(ele);
      if (status != CompareStatus.OK || errorInChildren(ele)) {
        return true;
      }
    }
    return false;
  }
}
