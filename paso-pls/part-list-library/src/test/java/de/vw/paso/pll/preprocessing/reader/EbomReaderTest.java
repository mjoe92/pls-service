package de.vw.paso.pll.preprocessing.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.vw.paso.pll.preprocessing.PreProcessorTest;
import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.formats.ppf.EbomPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class EbomReaderTest {

  @Test
  void testReadEbomLarge() throws IOException {
//    PreprocessingContext ctx = createContext("build/resources/test/veron-3986");
//    assertTrue("Check ebom set", !ctx.getRootNode().getEboms().isEmpty());
//    checkEbomSet(ctx, ctx.getRootNode());
  }

  @Test
  void testStructureSmall() throws IOException {
//    PreprocessingContext ctx = createContext("src/test/resources/ppl");
//    assertTrue("Check ebom set", !ctx.getRootNode().getEboms().isEmpty());
//    checkEbomSet(ctx, ctx.getRootNode());
  }

  @Test
  void testRuleSmall() throws IOException {
//    PreprocessingContext ctx = createContext("src/test/resources/ppl");
//    Map<String, Integer> ruleIdMap = new HashMap<>();
//    checkRules(ruleIdMap, ctx.getRootNode());
  }

  private PreprocessingContext createContext(String path) throws IOException {
    PreprocessingContext ctx = PreProcessorTest.createContext(path);
//    new NodeReader().readNodeStructure(ctx);
//    new EbomReader().readEbomMara(ctx, nodeMap);
//    new PreprocessingContextCleanup().cleanContext(ctx.getRootNode(), ctx);
    return ctx;
  }

  private void checkEbomSet(PreprocessingContext ctx, NodePPF node) {
    assertTrue(!node.getEboms().isEmpty(), "Check ebom for node(" + node.getNodeId() + ") is set");
    for (EbomPPF ebom : node.getEboms()) {
      assertTrue(node.getNodeId().equals(ebom.getEbom().getNodeId()), "hasError correct ebom for node(" + node.getNodeId() +")");
      assertEquals(ebom.getRuleID(), ctx.getRuleId(ebom.getEbom().getPrNrRule()), "Check RuleID");
      assertTrue(StringUtils.isNotEmpty(ebom.getEbom().getEinsatzDate()), "Check Einsatz set");
    }

    for (NodePPF child : node.getChildren()) {
      checkEbomSet(ctx, child);
    }
  }

  private void checkRules(Map<String,Integer> ruleIdMap, NodePPF rootNode) {
    for (EbomPPF ebom : rootNode.getEboms()) {
      if (ruleIdMap.containsKey(ebom.getEbom().getPrNrRule())) {
        assertEquals(ruleIdMap.get(ebom.getEbom().getPrNrRule()), ebom.getRuleID(), "Check rule id");
      } else {
        ruleIdMap.put(ebom.getEbom().getPrNrRule(), ebom.getRuleID());
      }
    }
    for (NodePPF child : rootNode.getChildren()) {
      checkRules(ruleIdMap, child);
    }
  }

}
