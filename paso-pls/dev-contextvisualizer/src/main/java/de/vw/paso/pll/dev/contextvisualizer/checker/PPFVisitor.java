package de.vw.paso.pll.dev.contextvisualizer.checker;

import de.vw.paso.pll.dev.contextvisualizer.ppf.EbkvsdPPFTreeNode;
import de.vw.paso.pll.dev.contextvisualizer.ppf.EbomPPFTreeNode;
import de.vw.paso.pll.dev.contextvisualizer.ppf.NodePPFTreeNode;

public interface PPFVisitor {

  default void visit(NodePPFTreeNode nodePPFTreeNode) {

  }

  default void visit(EbomPPFTreeNode ebomPPFTreeNode) {

  }

  default void visit(EbkvsdPPFTreeNode ebkvsdPPFTreeNode) {

  }
}
