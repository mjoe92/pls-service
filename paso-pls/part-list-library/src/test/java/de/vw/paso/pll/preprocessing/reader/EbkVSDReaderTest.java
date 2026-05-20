package de.vw.paso.pll.preprocessing.reader;

import de.vw.paso.pll.preprocessing.PreProcessorTest;
import de.vw.paso.pll.preprocessing.PreprocessingContext;

import java.io.IOException;

import org.junit.jupiter.api.Test;


class EbkVSDReaderTest {

  @Test
  void testReadEbkVSDLarge() throws IOException {
//    PreprocessingContext context = createContext("build/resources/test/veron-3986");
//    assertNotNull("Check root set", context.getRootNode());
  }

  @Test
  void testStructureSmall() throws IOException {
//    PreprocessingContext context = createContext("src/test/resources/ppl");
//    assertNotNull("Check root set", context.getRootNode());
//    NodePPF node = context.getTreeNode("D48564EE727A1ED583B7A8C8CA255A1F");
//    EbomPPF ebomWithBaukasten = node.getEboms().stream().filter(e -> StringUtils.isNotEmpty(e.getEbom().getBaukastenNodeId())).findFirst().get();
//    assertNotNull("Check ebom with baukasten exists", ebomWithBaukasten);
//    assertEquals("Check one root ebk", 1, ebomWithBaukasten.getEbks().size());
//    EbkVsdPPF first = ebomWithBaukasten.getEbks().first();
//    assertEquals("Check ebk children count", 5, first.getEbks().size());
//    Set<String> baukastenIds = new HashSet<>();
//    baukastenIds.add(first.getBaukastenNodeId());
//    first.getEbks().forEach(e ->baukastenIds.add(e.getBaukastenNodeId()));
//    assertEquals("Check ebk baukastenIds",1, baukastenIds.size());
  }

  private PreprocessingContext createContext(String path) throws IOException {
    PreprocessingContext ctx = PreProcessorTest.createContext(path);
//    new NodeReader().readNodeStructure(ctx);
//    new EbomReader().readEbomMara(ctx, nodeMap);
//    new EbkVSDReader().readEbkVSD(ctx, nodeMap);
    return ctx;
  }
}
