package de.vw.paso.pll.preprocessing.reader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.vw.paso.pll.preprocessing.PreProcessorTest;
import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.formats.ppf.EbkVsdPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.EbomPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class MaKTxReaderTest {

  @Test
  void testReadMakTxLarge() throws IOException {
    PreprocessingContext ctx = createContext("build/resources/test/veron-3986");
    assertNotNull(ctx, "Check root set");
  }

  @Test
  void testStructureSmall() throws IOException {
    PreprocessingContext ctx = createContext("src/test/resources/ppl");

//    checkTranslationSet(ctx.getRootNode());
  }

  private void checkTranslationSet(NodePPF rootNode) {
    for (EbomPPF ebom : rootNode.getEboms()) {
      assertNotNull(ebom.getMara().getTranslationGer(), "Check german translation exists");
      assertNotNull(ebom.getMara().getTranslationEn(), "Check english translation exists");
      for (EbkVsdPPF ebk : ebom.getEbks()) {
        checkTranslationSet(ebk);
      }
    }
    for (NodePPF childNode : rootNode.getChildren()) {
      checkTranslationSet(childNode);
    }
  }

  private void checkTranslationSet(EbkVsdPPF ebk) {
    assertNotNull(ebk.getMara().getTranslationGer(), "Check german translation exists");
    assertNotNull(ebk.getMara().getTranslationEn(), "Check english translation exists");
    for (EbkVsdPPF childEbk : ebk.getChildren()) {
      checkTranslationSet(childEbk);
    }
  }

  private PreprocessingContext createContext(String path) throws IOException {
    PreprocessingContext ctx = PreProcessorTest.createContext(path);
    new NodeReader().readNodeStructure(ctx);
//    new EbomReader().readEbomMara(ctx, nodeMap);
//    new EbkVSDReader().readEbkVSD(ctx, nodeMap);
//    new MaraReader().readMara(ctx, nodeMap);
//    new MaKTxReader().readTranslations(ctx, nodeMap);
    return ctx;
  }
}
