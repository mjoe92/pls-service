package de.vw.paso.pll.preprocessing.reader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.vw.paso.pll.preprocessing.PreProcessorTest;
import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.formats.ppf.EbkVsdPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.EbomPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;

import java.io.IOException;
import org.junit.jupiter.api.Test;


class MaraReaderTest {

  @Test
  void testReadMaraLarge() throws IOException {
    PreprocessingContext ctx = createContext("build/resources/test/veron-3986");
//    assertNotNull("Check root object set", ctx.getRootNode());
  }

  @Test
  void testStructureSmall() throws IOException {
    PreprocessingContext ctx = createContext("src/test/resources/ppl");
//    NodePPF node = ctx.getRootNode();
//    checkMaraSet(node);
//    assertNotNull("Check root object set", ctx.getRootNode());
  }

  private void checkMaraSet(NodePPF node) {
    for (EbomPPF ebom : node.getEboms()) {
      assertNotNull(ebom.getMara(), "Check mara set in ebom");
      for (EbkVsdPPF ebk : ebom.getEbks()) {
        checkMaraSet(ebk);
      }
    }

    for (NodePPF child : node.getChildren()) {
      checkMaraSet(child);
    }
  }

  private void checkMaraSet(EbkVsdPPF ebk) {
    assertNotNull(ebk.getMara(), "Check mara set ebk");
    for (EbkVsdPPF child : ebk.getChildren()) {
      checkMaraSet(child);
    }
  }

  private PreprocessingContext createContext(String path) throws IOException {
    PreprocessingContext ctx = PreProcessorTest.createContext(path);
    new NodeReader().readNodeStructure(ctx);
//    new EbomReader().readEbomMara(ctx, nodeMap);
//    new EbkVSDReader().readEbkVSD(ctx, nodeMap);
//    new MaraReader().readMara(ctx, nodeMap);
    return ctx;
  }
}
