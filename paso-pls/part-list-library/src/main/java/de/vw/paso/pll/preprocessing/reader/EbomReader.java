package de.vw.paso.pll.preprocessing.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.TiWhFileType;
import de.vw.paso.pll.preprocessing.formats.ppf.EbomPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;
import de.vw.paso.pll.preprocessing.formats.raw.EbomWrapper;
import org.apache.commons.lang3.StringUtils;

public class EbomReader extends AbstractReader {

  private RandomAccessFile raf;

  @Override
  public void close() throws IOException {
    if (raf != null) {
      raf.close();
      raf = null;
    }
  }

  public Multimap<String, Long> indexEbom(PreprocessingContext ctx) throws IOException {
    Multimap<String, Long> index = MultimapBuilder.hashKeys().arrayListValues().build();
    long row = 0;
    File nodeFile = ctx.getMappedFiles().get(TiWhFileType.EBOM);

    try (FileReader in = new FileReader(nodeFile); BufferedReader reader = new BufferedReader(in)) {
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        if (row < rowsToSkip) {
          row++;
          continue;
        }
        try {
          EbomWrapper mw = new EbomWrapper(line);
          index.put(mw.getNodeId(), row);
        } catch (Exception e) {
          LOG.info("Could not parse ebom line {}: {}", row, line);
        }

        row++;
      }
    }
    return index;
  }

  public void readEbomMara(PreprocessingContext ctx, Map<String, NodePPF> nodeMap, Multimap<String, Long> ebomIndex)
    throws IOException {
    createRandomAccessFile(ctx);

    int rowLength = TiWhFileType.EBOM.getRowLength();
    byte[] readBytes = new byte[rowLength];
    for (NodePPF node : nodeMap.values()) {
      Collection<Long> rows = ebomIndex.get(node.getNodeId());
      for (Long row : rows) {
        raf.seek(row * rowLength);
        raf.read(readBytes);
        EbomWrapper ebomW = new EbomWrapper(new String(readBytes));
        if (checkFilterRules(ctx, ebomW)) {
          EbomPPF ebom = new EbomPPF(ebomW);
          node.addEbom(ebom);
          Integer ruleId = ctx.addRule(ebomW.getPrNrRule());
          ebom.setRuleID(ruleId);
        }
      }
    }
  }

  public void readRulesFast(PreprocessingContext ctx) throws FileNotFoundException {
    createRandomAccessFile(ctx);

    long rowLength = TiWhFileType.EBOM.getRowLength();
    long offset = 58;
    byte[] readBytes = new byte[80];
    for (int row = 1; ; row++) {
      try {
        raf.seek((row * rowLength) + offset);
        int numOfBytes = raf.read(readBytes);
        if (numOfBytes > 0) {
          String rule = new String(readBytes).trim();
          ctx.addRule(rule);
        } else {
          break;
        }
      } catch (IOException e) {
        LOG.info("Could not parse ebom line {}", row);
        break;
      }
    }
  }

  private boolean checkFilterRules(PreprocessingContext ctx, EbomWrapper ebomW) {
    if (ctx.isSkippedNode(ebomW.getNodeId())) {
      return false;
    }
    return !StringUtils.isEmpty(ebomW.getEinsatzDate());
  }

  private void createRandomAccessFile(PreprocessingContext ctx) throws FileNotFoundException {
    if (raf == null) {
      raf = new RandomAccessFile(ctx.getMappedFiles().get(TiWhFileType.EBOM), FILE_MODE_READ);
    }
  }
}
