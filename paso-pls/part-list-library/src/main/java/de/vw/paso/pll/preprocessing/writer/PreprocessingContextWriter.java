package de.vw.paso.pll.preprocessing.writer;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;

import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.formats.ppf.AbstractPreProcessedFormat;
import de.vw.paso.pll.preprocessing.formats.ppf.EbkVsdPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.EbomPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;

public class PreprocessingContextWriter {

  private static final String CONTENT_SEPARATOR = "---";

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  public void writeHeaderHeader(PreprocessingContext ctx, Writer out) throws IOException {
    writeProperty(out, "Created", dateFormat.format(new Date()));
    writeProperty(out, "Library-Version", "TODO");
    writeProperty(out, "Format-Version", "1.0");
    writeProperty(out, "Product", ctx.getProduct());
    writeProperty(out, "Charset", StandardCharsets.UTF_8.displayName());
    writeProperty(out, "ReplacedUIDs", ctx.isIdsReplaced());
    writeContentSeparator(out);
  }

  public void writePrNumberRules(PreprocessingContext ctx, Writer out) throws IOException {
    ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(ctx.getRules());
    entries.sort(Map.Entry.comparingByValue());
    for (Map.Entry<String, Integer> entry : entries) {
      writeProperty(out, entry.getValue(), entry.getKey());
    }
    writeContentSeparator(out);
  }

  public void writeNode(NodePPF node, Writer out) throws IOException {
    writePPFNode(out, node);

    for (EbomPPF part : node.getEboms()) {
      writePPFNode(out, part);

      SortedSet<EbkVsdPPF> ebks = part.getEbks();
      writeEbk(ebks, out);
    }
  }

  private void writeEbk(Collection<EbkVsdPPF> ebks, Writer out) throws IOException {
    for (EbkVsdPPF ebk : ebks) {
      writePPFNode(out, ebk);
      writeEbk(ebk.getChildren(), out);
    }
  }

  private void writeProperty(Writer out, Object key, Object value) throws IOException {
    out.write(key == null ? "" : key.toString());
    out.write(":");
    out.write(value == null ? "" : value.toString());
    out.write("\n");
  }

  private void writePPFNode(Writer out, AbstractPreProcessedFormat ppf) throws IOException {
    out.write(ppf.toString());
    out.write("\n");
  }

  public void writeContentSeparator(Writer out) throws IOException {
    out.write("\n");
    out.write(CONTENT_SEPARATOR);
    out.write("\n");
  }

}
