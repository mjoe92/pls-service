package de.vw.paso.pll.preprocessing.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.TiWhFileType;
import de.vw.paso.pll.preprocessing.formats.ppf.EbkVsdPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.EbomPPF;
import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;
import de.vw.paso.pll.preprocessing.formats.raw.EbkVsdWrapper;
import org.apache.commons.lang3.StringUtils;

public class EbkVSDReader extends AbstractReader {

  private RandomAccessFile raf;

  public Multimap<String, Long> indexEbk(PreprocessingContext ctx) throws IOException {
    Multimap<String, Long> index = MultimapBuilder.hashKeys().arrayListValues().build();

    File file = ctx.getMappedFiles().get(TiWhFileType.EBKVSD);
    try (FileReader in = new FileReader(file); BufferedReader reader = new BufferedReader(in)) {
      long row = 0;
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        if (row < rowsToSkip) {
          row++;
          continue;
        }
        try {
          EbkVsdWrapper ebk = new EbkVsdWrapper(line);
          index.put(ebk.getBaukastenNodeId(), row);
        } catch (Exception e) {
          LOG.info("Could not parse ebkvsd line {}: {}", row, line);
        }

        row++;
      }
    }
    return index;
  }

  public void readEbkVSD(PreprocessingContext ctx, Map<String, NodePPF> nodeMap, Multimap<String, Long> ebkIndex)
    throws IOException {
    if (raf == null) {
      raf = new RandomAccessFile(ctx.getMappedFiles().get(TiWhFileType.EBKVSD), FILE_MODE_READ);
    }
    Multimap<String, EbomPPF> ebomByBaukastenId = MultimapBuilder.ListMultimapBuilder.hashKeys().arrayListValues()
      .build();
    Map<String, EbkVsdPPF> ebkMap = new HashMap<>();
    int rowLength = TiWhFileType.EBKVSD.getRowLength();
    byte[] readBytes = new byte[rowLength];
    for (NodePPF nodePPF : nodeMap.values()) {
      for (EbomPPF e : nodePPF.getEboms()) {
        processEbkVSD(ctx, ebkIndex, e, ebomByBaukastenId, rowLength, readBytes, ebkMap);
      }
    }
    List<EbkVsdPPF> rootEbkList = new ArrayList<>();

    ebkMap.values().forEach(ebk -> {
      EbkVsdPPF parentEbk = ebkMap.get(ebk.getBaukastenNodeId() + ebk.getRawRow().getParentEbkNodeId());
      if (parentEbk != null) {
        parentEbk.getChildren().add(ebk);
      } else {
        rootEbkList.add(ebk);
      }
    });

    for (EbkVsdPPF ebk : rootEbkList) {
      Collection<EbomPPF> eboms = ebomByBaukastenId.get(ebk.getBaukastenNodeId());
      eboms.forEach(ebom -> ebom.getEbks().add(ebk));
    }
  }

  private void processEbkVSD(PreprocessingContext ctx, Multimap<String, Long> ebkIndex, EbomPPF e,
    Multimap<String, EbomPPF> ebomByBaukastenId, int rowLength, byte[] readBytes, Map<String, EbkVsdPPF> ebkMap)
    throws IOException {
    if (StringUtils.isNotEmpty(e.getEbom().getBaukastenNodeId())) {
      ebomByBaukastenId.put(e.getEbom().getBaukastenNodeId(), e);
      Collection<Long> ebkRows = ebkIndex.get(e.getEbom().getBaukastenNodeId());
      for (Long ebkRow : ebkRows) {
        raf.seek(ebkRow * rowLength);
        raf.read(readBytes);
        EbkVsdPPF ebk = new EbkVsdPPF(new String(readBytes));
        ebk.setRuleID(ctx.addRule(ebk.getRawRow().getPrNrRule()));
        checksameData(e, ebk);
        ebkMap.put(ebk.getBaukastenNodeId() + ebk.getRawRow().getEbkNodeId(), ebk);
      }
    }
  }

  private void checksameData(EbomPPF ebom, EbkVsdPPF ebk) {
    //    checkEquals("Pr number", ebom, () -> ebom.getEbom().getPrNrRule(), () -> ebk.getRawRow().getPrNrRule());
    //    checkEquals("Construction Group", ebom, () -> ebom.getEbom().getConstructionGroup(), () -> ebk.getRawRow().getKonstructionGroup());
    //    checkEquals("Entfallschluessel", ebom, () -> ebom.getEbom().getEntfallSchl(), () -> ebk.getRawRow().getEntfallSchl());
    //    checkEquals("Entfalldate", ebom, () -> ebom.getEbom().getEntfallDate(), () -> ebk.getRawRow().getEntfallDate());
    //    checkEquals("Einsatzschluessel", ebom, () -> ebom.getEbom().getEinsatzSchl(), () -> ebk.getRawRow().getEinsatzSchl());
    //    checkEquals("Einsatzdate", ebom, () -> ebom.getEbom().getEinsatzDate(), () -> ebk.getRawRow().getEinsatzDate());
    //    checkEquals("Costgroup", ebom, () -> ebom.getEbom().getCostGroup(), () -> ebk.getRawRow().getCostGroup());
  }

  @Override
  public void close() throws IOException {
    if (raf != null) {
      raf.close();
      raf = null;
    }
  }
}
