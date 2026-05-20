package de.vw.paso.pll.preprocessing;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;
import de.vw.paso.pll.preprocessing.reader.FileChecker;
import org.junit.jupiter.api.Test;

public class PreProcessorTest {

  private PartListPreprocessor preProc = new PartListPreprocessor();

  @Test
  void testCompleteness() throws Exception {
    PreprocessingContext ctx = createContext("build/resources/test/veron-3986");
    new FileChecker().checkCompleteness(ctx);
  }

  @Test
  void testCompletenessOneFileMissing() {
    PreprocessingContext ctx = createContext("build/resources/test/veron-3986");
    ctx.getInputFiles().remove(3);

    assertThrows(PreprocessingException.class, () -> new FileChecker().checkCompleteness(ctx));
  }

  @Test
  void testPreProcessing() throws PreprocessingException, IOException {
    PreprocessingContext ctx = createContext("build/resources/test/veron-3986");
    File tmpFile = File.createTempFile("ppf_", ".txt");
    tmpFile.deleteOnExit();
    FileWriter out = new FileWriter(tmpFile);
    preProc.processPartLists(ctx, out);
    out.close();
  }

  @Test
  void testPreProcessingNoEmptyNodes() throws PreprocessingException, IOException {
    PreprocessingContext ctx = createContext("build/resources/test/veron-3990");
    //    preProc.processPartLists(ctx);
    //    checkForEmptyNodes(ctx.getRootNode());
  }

  private void checkForEmptyNodes(NodePPF node) {
    assertTrue(!node.getEboms().isEmpty() || !node.getChildren().isEmpty(), "Check parts or children exist");
    for (NodePPF child : node.getChildren()) {
      checkForEmptyNodes(child);
    }
  }

  public static PreprocessingContext createContext(String directory) {
    return createContext(directory, true);
  }

  private static PreprocessingContext createContext(String directory, boolean checkCompleteness) {
    List<File> files = getAllFiles(directory).collect(Collectors.toList());
    PreprocessingContext ctx = new PreprocessingContext(files);
    if (checkCompleteness) {
      new FileChecker().checkCompleteness(ctx);
    }
    return ctx;
  }

  private static Stream<File> getAllFiles(String directory) {
    return Arrays.stream(new File(directory).listFiles()).filter(File::isFile);
  }
}
