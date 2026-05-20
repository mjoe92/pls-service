package de.vw.paso.pll.preprocessing.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.PreprocessingException;
import de.vw.paso.pll.preprocessing.TiWhFileType;
import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;
import de.vw.paso.pll.preprocessing.formats.raw.NodeWrapper;

public class NodeReader extends AbstractReader {

  private final List<FilterWrapper<NodeWrapper>> filter = new ArrayList<>();

  public void addFilter(String name, Predicate<NodeWrapper> nodeFilter) {
    filter.add(new FilterWrapper<>(name, nodeFilter));
  }

  @Override
  public void close() {
    // Nothing to cleanup
  }

  /**
   * Read and build node structure
   *
   * @param ctx
   *   Context to store the date in
   * @throws IOException
   *   if the node file cannot be read
   */
  public List<NodePPF> readNodeStructure(PreprocessingContext ctx) throws IOException {
    Map<String, NodePPF> nodeMap = new HashMap<>();
    List<NodePPF> addToParent = new ArrayList<>();
    ObjectHolder<NodePPF> rootNodeHolder = new ObjectHolder<>();

    File nodeFile = ctx.getMappedFiles().get(TiWhFileType.NODE);

    long row = 0;
    try (FileReader in = new FileReader(nodeFile); BufferedReader reader = new BufferedReader(in)) {
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        if (row < rowsToSkip) {
          row++;
          continue;
        }

        parseNodeLines(ctx, line, nodeMap, addToParent, rootNodeHolder);
      }
    }

    buildParentChildTree(addToParent, nodeMap);

    return treeToList(rootNodeHolder.get(), new ArrayList<>());
  }

  private void buildParentChildTree(List<NodePPF> addToParent, Map<String, NodePPF> nodeMap) {
    addToParent.forEach(node -> {
      NodePPF parent = nodeMap.get(node.getParentNodeId());
      if (parent != null) {
        parent.addChild(node);
      } else {
        /*
         * Sometimes the node file does not contain a parent for an existing node. In that case we just log it continue normaly.
         * The node will be not in the resulting PPF.
         */

        //        throw new PreprocessingException("Parent node not found: node ID:" + node.getNodeId() + " parent node ID:" + node.getParentNodeId());
        LOG.info("Parent node not found: node ID: {} parent node ID: {}", node.getNodeId(), node.getParentNodeId());
      }
    });
  }

  private void parseNodeLines(PreprocessingContext ctx, String line, Map<String, NodePPF> nodeMap,
    List<NodePPF> addToParent, ObjectHolder<NodePPF> rootNodeHolder) {
    NodeWrapper nodewrapper = new NodeWrapper(line);
    if (ctx.getProduct() == null) {
      ctx.setProduct(nodewrapper.getProduct());
      LOG.info("Setting product to {}", nodewrapper.getProduct());
    }
    if (filterMatches(ctx, nodewrapper)) {
      NodePPF node = new NodePPF(nodewrapper);
      if (ctx.isIdsReplaced()) {
        node.setNodeId(ctx.mapId(node.getNodeId()));
        node.setParentNodeId(ctx.mapId(node.getParentNodeId()));
      }
      nodeMap.put(node.getNodeId(), node);
      if (node.hasParent()) {
        NodePPF parent = nodeMap.get(node.getParentNodeId());
        if (parent != null) {
          parent.addChild(node);
        } else {
          addToParent.add(node);
        }
      } else {
        if (rootNodeHolder.get() != null) {
          throw new PreprocessingException("Multiple roots found");
        }
        rootNodeHolder.set(node);
      }
    } else {
      ctx.addSkippedNode(nodewrapper.getGUID());
    }
  }

  /**
   * return true when the part is ok. false if one filter matches and the node should be filtered out
   */
  private boolean filterMatches(PreprocessingContext ctx, NodeWrapper nodeWrapper) {
    /*
     * If parent was skipped, this one should be skipped too.
     */
    if (ctx.isSkippedNode(nodeWrapper.getParentGUID())) {
      LOG.info("Parent node skipped. Removing node {}", nodeWrapper.getGUID());
      return false;
    }

    /*
     * Check defined filter
     */
    for (FilterWrapper<NodeWrapper> filterWrapper : filter) {
      if (!filterWrapper.filter.test(nodeWrapper)) {
        LOG.debug("Filter [{}] matches, skip node {}", filterWrapper.name, nodeWrapper.getGUID());
        return false;
      }
    }
    return true;
  }

  private List<NodePPF> treeToList(NodePPF nodePPF, List<NodePPF> list) {
    list.add(nodePPF);
    for (NodePPF ppf : nodePPF.getChildren()) {
      treeToList(ppf, list);
    }
    return list;
  }

  private static class FilterWrapper<T> {

    String name;
    Predicate<T> filter;

    private FilterWrapper(String name, Predicate<T> filter) {
      this.name = name;
      this.filter = filter;
    }
  }

  private static class ObjectHolder<T> {

    private T object;

    public T get() {
      return object;
    }

    public void set(T object) {
      this.object = object;
    }
  }
}
