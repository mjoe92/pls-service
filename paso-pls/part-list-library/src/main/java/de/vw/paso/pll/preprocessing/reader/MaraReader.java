package de.vw.paso.pll.preprocessing.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.TiWhFileType;
import de.vw.paso.pll.preprocessing.formats.ppf.EbkVsdPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.EbomPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;
import de.vw.paso.pll.preprocessing.formats.raw.MaraWrapper;

public class MaraReader extends AbstractReader implements AutoCloseable {

  private static final int LINE_LENGTH = TiWhFileType.MARA.getRowLength();

  private RandomAccessFile raf;

  private byte[] readBytes = new byte[LINE_LENGTH];

  public Map<String, Long> indexMara(PreprocessingContext ctx) throws IOException {
    Map<String, Long> index = new HashMap<>();
    File nodeFile = ctx.getMappedFiles().get(TiWhFileType.MARA);

    long row = 0;
    try (FileReader in = new FileReader(nodeFile); BufferedReader reader = new BufferedReader(in)) {
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        if (row < rowsToSkip) {
          row++;
          continue;
        }

        try {
          MaraWrapper mw = new MaraWrapper(line);
          index.put(mw.getPartnumber(), row);
        } catch (Exception e) {
          LOG.info("Could not parse mara line {}: {}", row, line);
        }
        row++;
      }
    }
    return index;
  }

  public Map<String, MaraWrapper> readMara(PreprocessingContext ctx, Map<String, NodePPF> nodeMap,
    Map<String, Long> maraIndex) throws IOException {
    Map<String, MaraWrapper> maraMap = new HashMap<>();
    if (raf == null) {
      raf = new RandomAccessFile(ctx.getMappedFiles().get(TiWhFileType.MARA), FILE_MODE_READ);
    }
    for (NodePPF node : nodeMap.values()) {
      for (EbomPPF ebom : node.getEboms()) {
        MaraWrapper mw = readMara(ebom.getPartNumber(), maraIndex, maraMap);
        if (mw == null) {
          LOG.debug("No Mara found for Ebom: {}", ebom.getNodeId());
        }
        ebom.setMara(mw);
        for (EbkVsdPPF ebk : ebom.getEbks()) {
          setMaraToEbk(ebk, maraIndex, maraMap);
        }
      }
    }
    return maraMap;
  }

  private void setMaraToEbk(EbkVsdPPF ebk, Map<String, Long> maraIndex, Map<String, MaraWrapper> maraMap)
    throws IOException {
    MaraWrapper mwEbk = readMara(ebk.getRawRow().getPartNumber(), maraIndex, maraMap);
    if (mwEbk == null) {
      LOG.info("No Mara found for EBK: {}: {}", ebk.getBaukastenNodeId(), ebk.getRawRow().getPartNumber());
    }
    ebk.setMara(mwEbk);
    for (EbkVsdPPF e : ebk.getChildren()) {
      setMaraToEbk(e, maraIndex, maraMap);
    }
  }

  private MaraWrapper readMara(String partNumber, Map<String, Long> maraIndex, Map<String, MaraWrapper> maraMap)
    throws IOException {
    if (maraMap.containsKey(partNumber)) {
      return maraMap.get(partNumber);
    }

    Long row = maraIndex.get(partNumber);
    if (row != null) {
      raf.seek(row * LINE_LENGTH);
      raf.read(readBytes);
      String line = new String(readBytes);

      MaraWrapper mw = new MaraWrapper(line);
      maraMap.put(mw.getPartnumber(), mw);
      return mw;
    }
    return null;
  }

  @Override
  public void close() throws IOException {
    if (raf != null) {
      raf.close();
      raf = null;
    }
    readBytes = null;
  }
}
