package de.vw.paso.pll.dev.contextvisualizer.veron;

import de.vw.paso.pll.dev.contextvisualizer.checker.PageTreeCompareResult;
import de.vw.paso.pll.dev.contextvisualizer.util.BackgroundExecutor;
import de.vw.paso.pll.dev.contextvisualizer.util.CompareStatus;
import de.vw.paso.pll.dev.contextvisualizer.util.Icons;
import de.vw.paso.pll.dev.contextvisualizer.util.NodeIdentifier;
import de.vw.paso.pll.dev.contextvisualizer.util.components.VisPage;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VeronPage extends VisPage {

  Map<NodeIdentifier, VeronElement> nodeMap = new HashMap<>();
  private PageTreeCompareResult result;

  public VeronPage(String title, BackgroundExecutor bgExec) {
    super(title, bgExec);
  }

  public void openVeron(File f) throws IOException {
    VeronElement veronElement = new VeronReader().readVeronFile(f);
    addToMap(veronElement);
    tree.setModel(new DefaultTreeModel(veronElement));
  }

  private void addToMap(VeronElement ele ){
    nodeMap.put(getNodeId(ele), ele);
    nodeMap.put(new NodeIdentifier(ele.get(VeronFields.NODE_ID)), ele);
    for (int i = 0; i < ele.getChildCount(); i++ ){
      addToMap((VeronElement) ele.getChildAt(i));
    }
  }

  @Override
  protected TableModel createTablemodel(DefaultMutableTreeNode... nodes) {
    DefaultTableModel defModel = new DefaultTableModel();
    defModel.addColumn("Property");
    for (DefaultMutableTreeNode node : nodes) {
      defModel.addColumn("Value");
    }

    for (VeronFields vf : VeronFields.values()) {
      Object[] row = new Object[nodes.length + 1];
      row[0] = vf;
      for (int i = 0; i < nodes.length; i++) {
        row[ i +1] = ((VeronElement) nodes[0]).get(vf);
      }
      defModel.addRow(row);
    }
    return defModel;
  }

  @Override
  public NodeIdentifier getNodeId(DefaultMutableTreeNode node) {
    VeronElement ve = (VeronElement) node;
    String nodeId = ve.get(VeronFields.NODE_ID);
    String baukastenNodeId = ve.get(VeronFields.BAUKASTEN_NODE_ID);
    if (StringUtils.isNotEmpty(baukastenNodeId) && nodeId.startsWith(baukastenNodeId)) {
      String partNumber = ve.get(VeronFields.Teilenummer);
      long sort_ = Long.valueOf(ve.get(VeronFields.SORT_));
      int sort = (int) (sort_ / 100000d);
      int subSort = (int) (sort_ % sort);
      return new NodeIdentifier(baukastenNodeId, subSort, partNumber);
    } else {
      long sort_ = Long.valueOf(ve.get(VeronFields.SORT_));
      int sort = (int) (sort_ / 100000d);
      return new NodeIdentifier(nodeId, sort);
    }
  }

  @Override
  protected JPanel createBottomPanel() {
    return new JPanel();
  }

  @Override
  protected DefaultMutableTreeNode getTreeNode(NodeIdentifier nodeId) {
    return nodeMap.get(nodeId);
  }

  public VeronElement getRoot() {
    return (VeronElement) tree.getModel().getRoot();
  }

  private boolean errorInChildren(VeronElement parent, Map<VeronElement, CompareStatus> statusMap) {
    for (VeronElement ele : parent.getChildren()) {
      CompareStatus status = statusMap.get(ele);
      if (status != CompareStatus.OK ||errorInChildren(ele, statusMap)) {
        return true;
      }
    }
    return false;
  }

  public void setResult(PageTreeCompareResult result) {
    this.result = result;
    Map<VeronElement, CompareStatus> status = result.getVeronStatusMap();
    tree.setCellRenderer(new DefaultTreeCellRenderer() {
      @Override
      public Component getTreeCellRendererComponent(JTree jTree, Object o, boolean b, boolean b1, boolean b2, int i, boolean b3) {
        JLabel comp = (JLabel) super.getTreeCellRendererComponent(jTree, o, b, b1, b2, i, b3);
        CompareStatus compareStatus = status.get(o);
        if (CompareStatus.MISSING_IN_EFS.equals(compareStatus)) {
          comp.setIcon(Icons.EFS.EFS_TREE_ERROR);
        } else if (CompareStatus.OK.equals(compareStatus)) {
          if (errorInChildren((VeronElement) o, status)) {
            comp.setIcon(Icons.Compare.COMPARE_CHILDREN_ERROR);
          } else comp.setIcon(Icons.EFS.EFS_TREE_OK);
        } else {
          comp.setIcon(Icons.Compare.COMPARE_UNKNOWN);
        }
        return comp;
      }
    });
  }

  public void clear() {
    tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
    nodeMap.clear();
    detailsTable.setModel(new DefaultTableModel());
  }
}
