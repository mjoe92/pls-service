package de.vw.paso.pll.creation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.vw.paso.pll.model.FilteredOutPart;
import de.vw.paso.pll.model.PlsEfsElement;

public class PartListCreationResult {

  private final Map<String, List<PlsEfsElement>> efsElementByNodeId = new HashMap<>();
  private final Map<String, List<FilteredOutPart>> filteredOutEfsElementsByNodeId = new HashMap<>();

  private PlsEfsElement rootElement;

  public PlsEfsElement getRootElement() {
    return rootElement;
  }

  public void removeFilteredOutEfsElementsByNodeId(String nodeId) {
    filteredOutEfsElementsByNodeId.remove(nodeId);
  }

  public void setRootElement(PlsEfsElement rootElement) {
    this.rootElement = rootElement;
  }

  public Collection<String> getEfsElementByNodeIds() {
    return filteredOutEfsElementsByNodeId.keySet();
  }

  public List<PlsEfsElement> getEfsElementByNodeId(String nodeId) {
    return efsElementByNodeId.getOrDefault(nodeId, List.of());
  }

  public void addEfsElement(String nodeId, PlsEfsElement element) {
    efsElementByNodeId.computeIfAbsent(nodeId, a -> new ArrayList<>()).add(element);
  }

  public Collection<String> getFilteredOutEfsElementNodeIds() {
    return filteredOutEfsElementsByNodeId.keySet();
  }

  public List<FilteredOutPart> getFilteredOutEfsElements() {
    return filteredOutEfsElementsByNodeId.values().stream().flatMap(Collection::stream).toList();
  }

  public List<FilteredOutPart> getFilteredOutEfsElementsByNodeId(String nodeId) {
    return filteredOutEfsElementsByNodeId.getOrDefault(nodeId, List.of());
  }

  public void addFilteredOutEfsElement(String nodeId, FilteredOutPart element) {
    filteredOutEfsElementsByNodeId.computeIfAbsent(nodeId, a -> new ArrayList<>()).add(element);
  }

}
