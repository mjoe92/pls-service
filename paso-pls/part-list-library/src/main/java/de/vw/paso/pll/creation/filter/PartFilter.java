package de.vw.paso.pll.creation.filter;

public interface PartFilter {

  PartFilterResult check(String[] nodeData, String[] ebomData, String[] ebkData);

  default boolean isContainer() {
    return false;
  }
}
