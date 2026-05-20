package de.vw.paso.pll.preprocessing.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.stream.Stream;

import de.vw.paso.pll.preprocessing.PartListPreprocessor;
import de.vw.paso.pll.preprocessing.PreProcessorTest;
import de.vw.paso.pll.preprocessing.PreprocessingContext;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PreprocessingContextWriterTest {

  @Test
  void testWriteCompressedFormatLarge() throws IOException {
    PreprocessingContext ctx = PreProcessorTest.createContext("build/resources/test/veron-3986");

    StringWriter out = new StringWriter();
    new PartListPreprocessor().processPartLists(ctx, out);
    String generated = out.toString();
    int lineCount = StringUtils.countMatches(generated, "\n") + 1;
    assertEquals(38598, lineCount, "Check output length");
  }

  //Because of several changes to the creation, the example data has to be extende. Until then, this test will be Disabledd
  //TODO DSt fix test data
  @Test
  @Disabled
  void testWriteCompressedFormatSmall() throws IOException {
    PreprocessingContext ctx = PreProcessorTest.createContext("src/test/resources/ppl");

    PreprocessingContextWriter writer = new PreprocessingContextWriter();
    StringWriter out = new StringWriter();
    new PartListPreprocessor().processPartLists(ctx, out);

    String generated = out.toString();
    String savedResult = getSavedResult();
    assertTrue(generated.endsWith(savedResult), "Check result file");
  }

  private String getSavedResult() {
    BufferedReader savedResultReader = new BufferedReader(
      new InputStreamReader(getClass().getResourceAsStream("/ppl/result.txt")));
    StringBuilder sb = new StringBuilder();
    try (Stream<String> lines = savedResultReader.lines()) {
      lines.skip(8).forEach(l -> sb.append(l).append("\n"));
    }
    return sb.toString();
  }
}
