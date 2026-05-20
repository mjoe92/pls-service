package de.vw.paso.pll.preprocessing.reader;

import de.vw.paso.pll.preprocessing.PreProcessorTest;
import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;
import de.vw.paso.pll.preprocessing.reader.filter.ValidTypeFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;


class NodeReaderTest {

  @Test
  void testReadNodesNotFilteredLarge() throws IOException {
    PreprocessingContext ctx = PreProcessorTest.createContext("build/resources/test/veron-3986");
    new NodeReader().readNodeStructure(ctx);
//    assertNotNull("Root node set", ctx.getRootNode());

//    int nodeCount = countNode(ctx.getRootNode());
//    assertEquals("Number of nodes in tree",6306, nodeCount);
  }

  @Test
  void testReadNodesFilteredTypeLarge() throws IOException {
    PreprocessingContext ctx = PreProcessorTest.createContext("build/resources/test/veron-3986");
    List<String> nodeTypes = Arrays.asList("Z_HD", "Z_MIO", "Z_MTK");
    NodeReader reader = new NodeReader();
    reader.addFilter("Check types", new ValidTypeFilter(nodeTypes));
    reader.readNodeStructure(ctx);
//    assertNotNull("Root node set", ctx.getRootNode());

//    int nodeCount = countNode(ctx.getRootNode());
//    assertEquals("Number of nodes in tree",310, nodeCount);

//    checkNode(ctx.getRootNode(), node -> assertTrue("Invalid type found", nodeTypes.contains(node.getRawNode().getNodeType())));
  }

  @Test
  void testReadNodesFilteredNameLarge() throws IOException {
    PreprocessingContext ctx = PreProcessorTest.createContext("build/resources/test/veron-3986");
    List<String> nodeNames = Arrays.asList("Carrera Coupe", "WAGEN", "A GETRIEBE", "EINGANGSWELLE");
    NodeReader reader = new NodeReader();
    reader.addFilter("Check Node Label", nodeWrapper -> nodeNames.contains(nodeWrapper.getNodeLabel()));
    reader.readNodeStructure(ctx);
//    assertNotNull("Root node set", ctx.getRootNode());

//    int nodeCount = countNode(ctx.getRootNode());
//    assertEquals("Number of nodes in tree",4, nodeCount);
//
//    checkNode(ctx.getRootNode(), node -> assertTrue("Invalid type found", nodeNames.contains(node.getRawNode().getNodeLabel())));
  }

  @Test
  void testStructureSmall() throws IOException {
    PreprocessingContext ctx = PreProcessorTest.createContext("src/test/resources/ppl");
    NodeReader reader = new NodeReader();
    reader.readNodeStructure(ctx);
//    assertNotNull("Root node set", ctx.getRootNode());
//
//    NodePPF rootNode = ctx.getRootNode();
//    assertEquals("Root node type", "Z_HD", ctx.getRootNode().getRawNode().getNodeType());
//    assertEquals("Number of nodes in tree",8, countNode(rootNode));
//    assertEquals("Check children size", 1, rootNode.getEbks().size());
//
//    NodePPF wagen = rootNode.getEbks().first();
//    assertEquals("Check type of node", "Z_MIO", wagen.getRawNode().getNodeType());
//    assertEquals("Check children size", 1, wagen.getEbks().size());
//
//    NodePPF einbaubereich = wagen.getEbks().first();
//    assertEquals("Check type of node", "Z_MIO", einbaubereich.getRawNode().getNodeType());
//    assertEquals("Check children size", 4, einbaubereich.getEbks().size());
  }

  private void checkNode(NodePPF node, Consumer<NodePPF> check) {
    check.accept(node);
    for (NodePPF child :node.getChildren()) {
      checkNode(child, check);
    }
  }

  /**
   * Count nodes begining from the provided root node
   * @param node start counting from this node
   * @return number of nodes in the structure
   */
  private int countNode(NodePPF node) {
    if (node == null) {
      return 0;
    }
    int childCount = 0;
    for (NodePPF child : node.getChildren()) {
      childCount += countNode(child);
    }
    return 1 + childCount;
  }
}
