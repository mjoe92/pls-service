package de.vw.paso.pll.creation.filter;

import java.util.ArrayList;
import java.util.List;

import static de.vw.paso.pll.creation.filter.PartFilterContainer.Mode.AND;

public class PartFilterContainer implements PartFilter {

  public enum Mode {
    AND, OR;

  }

  private Mode and;

  private List<PartFilter> partFilterList = new ArrayList<>();

  private String filterName;

  private boolean removeChildren;

  public PartFilterContainer(String name) {
    this(AND, name);
  }

  public PartFilterContainer(String name, boolean removeChildren) {
    this(AND, name, removeChildren);
  }

  public PartFilterContainer(Mode mode, String name) {
    this(mode, name, false);
  }

  public PartFilterContainer(Mode mode, String name, boolean removeChildren) {
    this.and = mode;
    this.filterName = name;
    this.removeChildren = removeChildren;
  }

  public void addPartilter(PartFilter partFilter) {
    partFilterList.add(partFilter);
  }

  @Override
  public PartFilterResult check(String[] nodeData, String[] partData, String[] ebkData) {
    PartFilterResult result;
    if (and == AND) {
      result = checkAnd(partData, nodeData, ebkData);
    } else {
      result = checkOr(partData, nodeData, ebkData);
    }
    result.setRemoveChildren(removeChildren);
    return result;
  }

  private PartFilterResult checkOr(String[] partData, String[] nodeData, String[] ebkData) {
    PartFilterResult result = PartFilterResult.filteredOut(getFilterName());
    for (PartFilter pf : partFilterList) {
      PartFilterResult check = pf.check(nodeData, partData, ebkData);
      if (check.isFilteredOut()) {
        result.addSubMatchingResult(check);
      }
    }
    if (result.hasSubResults()) {
      return result;
    }
    return PartFilterResult.notFilteredOut();
  }

  private PartFilterResult checkAnd(String[] partData, String[] nodeData, String[] ebkData) {
    PartFilterResult result = PartFilterResult.filteredOut(getFilterName());
    for (PartFilter pf : partFilterList) {
      PartFilterResult check = pf.check(nodeData, partData, ebkData);
      if (check.isFilteredOut()) {
        result.addSubMatchingResult(check);
      } else {
        return PartFilterResult.notFilteredOut();
      }
    }
    if (result.isFilteredOut()) {
      return result;
    } else {
      return PartFilterResult.notFilteredOut();
    }
  }

  @Override
  public boolean isContainer() {
    return true;
  }

  public String getFilterName() {
    return filterName;
  }

  public boolean isRemoveChildren() {
    return removeChildren;
  }
}
