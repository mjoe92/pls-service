package de.vw.paso.pll.dev.contextvisualizer.checker;

import de.vw.paso.pll.dev.contextvisualizer.efs.EfsPage;
import de.vw.paso.pll.dev.contextvisualizer.efs.EfsPageTreeNode;
import de.vw.paso.pll.dev.contextvisualizer.util.CompareStatus;
import de.vw.paso.pll.dev.contextvisualizer.util.NodeIdentifier;
import de.vw.paso.pll.dev.contextvisualizer.veron.VeronElement;
import de.vw.paso.pll.dev.contextvisualizer.veron.VeronPage;
import de.vw.paso.pll.model.PlsEfsElement;

import java.util.List;

public class PageTreeComparator {

  public PageTreeCompareResult compareTrees(EfsPage efsPage, VeronPage veronPage) {
    PageTreeCompareResult result = new PageTreeCompareResult();
    EfsPageTreeNode efsRoot = (EfsPageTreeNode) efsPage.getTree().getModel().getRoot();
    VeronElement veronRoot = veronPage.getRoot();
    if (!compareNodes(efsPage, veronPage, efsRoot, veronRoot)) {
      result.addEfsStatus(efsRoot.getPlsElement(), CompareStatus.NOT_EQUALS);
      result.addVeronStatus(veronRoot, CompareStatus.NOT_EQUALS);
    } else {
      result.addEfsStatus(efsRoot.getPlsElement(), CompareStatus.OK);
      result.addVeronStatus(veronRoot, CompareStatus.OK);
      checkChildren(result, efsPage, veronPage, efsRoot.getChildren(), veronRoot.getChildren());
    }

    efsPage.setResult(result);
    veronPage.setResult(result);
    return result;
  }

  private void checkChildren(PageTreeCompareResult result, EfsPage efsPage, VeronPage veronPage, List<EfsPageTreeNode> efsChildren, List<VeronElement> veronChildren) {
    for (EfsPageTreeNode efsElement : efsChildren ){
      boolean found = false;
      for (VeronElement veronElement : veronChildren) {
        if (compareNodes(efsPage, veronPage, efsElement, veronElement)) {
          found = true;
          result.addEfsStatus(efsElement.getPlsElement(), CompareStatus.OK);
          result.addVeronStatus(veronElement, CompareStatus.OK);
          checkChildren(result, efsPage, veronPage, efsElement.getChildren(), veronElement.getChildren());
          break;
        }
      }
      if (!found) {
        PlsEfsElement plsElement = efsElement.getPlsElement();
        result.addEfsStatus(plsElement, CompareStatus.NOT_FOUND_IN_VERON);

        if (plsElement.hasChildren()) {
          setStatus(plsElement.getChildren(), CompareStatus.PARENT_NOT_FOUND_IN_VERON, result);
        }
      }
    }

    for (VeronElement veronElement : veronChildren) {
      CompareStatus status = result.getVeronStatusMap().get(veronElement);
      if (status == null) {
        result.addVeronStatus(veronElement, CompareStatus.MISSING_IN_EFS);
      }
    }
  }

  private void setStatus(List<PlsEfsElement> elements, CompareStatus statusToSet, PageTreeCompareResult result) {
    for (PlsEfsElement child : elements) {
      result.addEfsStatus(child, statusToSet);
      setStatus(child.getChildren(), statusToSet, result);
    }
  }

  private boolean compareNodes(EfsPage efsPage, VeronPage veronPage, EfsPageTreeNode efsNode, VeronElement veronNode) {
    NodeIdentifier efsNodeId = efsPage.getNodeId(efsNode);
    NodeIdentifier veronNodeId = veronPage.getNodeId(veronNode);
    return efsNodeId.equals(veronNodeId);
  }
}
