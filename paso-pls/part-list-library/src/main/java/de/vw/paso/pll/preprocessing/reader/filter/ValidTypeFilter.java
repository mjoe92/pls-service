package de.vw.paso.pll.preprocessing.reader.filter;

import de.vw.paso.pll.preprocessing.formats.raw.NodeWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ValidTypeFilter implements Predicate<NodeWrapper> {

  private final List<String> nodeTypes;

  public ValidTypeFilter(String... types) {
    nodeTypes = Arrays.asList(types);
  }

  public ValidTypeFilter(List<String> types) {
    nodeTypes = types;
  }

  @Override
  public boolean test(NodeWrapper nodeWrapper) {
    return nodeTypes.contains(nodeWrapper.getNodeType());
  }
}
