package de.vw.paso.pll.dev.contextvisualizer.checker;

import de.vw.paso.pll.dev.contextvisualizer.util.CompareStatus;
import de.vw.paso.pll.dev.contextvisualizer.veron.VeronElement;
import de.vw.paso.pll.model.PlsEfsElement;

import java.util.HashMap;
import java.util.Map;

public class PageTreeCompareResult {
  private Map<PlsEfsElement, CompareStatus> efsStatusMap = new HashMap<>();
  private Map<VeronElement, CompareStatus> veronStatusMap = new HashMap<>();

  public void addEfsStatus(PlsEfsElement plsElement, CompareStatus status) {
    efsStatusMap.put(plsElement, status);
  }

  public void addVeronStatus(VeronElement veronRoot, CompareStatus status) {
    veronStatusMap.put(veronRoot, status);
  }

  public Map<PlsEfsElement, CompareStatus> getEfsStatusMap() {
    return efsStatusMap;
  }

  public Map<VeronElement, CompareStatus> getVeronStatusMap() {
    return veronStatusMap;
  }
}
