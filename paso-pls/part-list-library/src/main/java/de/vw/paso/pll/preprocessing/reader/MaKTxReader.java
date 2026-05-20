package de.vw.paso.pll.preprocessing.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.TiWhFileType;
import de.vw.paso.pll.preprocessing.formats.raw.MaKTxWrapper;
import de.vw.paso.pll.preprocessing.formats.raw.MaraWrapper;

public class MaKTxReader extends AbstractReader {

  private static final int LINE_LENGTH = 60;

  private static final String LANG_DE = "D";
  private static final String LANG_EN = "E";

  private RandomAccessFile raf;
  private byte[] readBytes = new byte[LINE_LENGTH];

  public Table<String, String, Long> indexMakTx(PreprocessingContext ctx) throws IOException {
    Table<String, String, Long> makTxIndex = HashBasedTable.create();
    long row = 0;
    File makTxFile = ctx.getMappedFiles().get(TiWhFileType.MAKTX);

    try (FileReader in = new FileReader(makTxFile); BufferedReader reader = new BufferedReader(in)) {
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        if (row < rowsToSkip) {
          row++;
          continue;
        }
        try {
          MaKTxWrapper makTx = new MaKTxWrapper(line);
          makTxIndex.put(makTx.getPartnumber(), makTx.getLanguage(), row);
        } catch (Exception e) {
          LOG.info("Could not parse maktx line {}: {}", row, line);
        }
        row++;
      }
    }
    return makTxIndex;
  }

  public void readTranslations(PreprocessingContext ctx, Map<String, MaraWrapper> maraMap,
    Table<String, String, Long> makTxIndex) throws IOException {
    if (raf == null) {
      raf = new RandomAccessFile(ctx.getMappedFiles().get(TiWhFileType.MAKTX), FILE_MODE_READ);
    }
    for (MaraWrapper mw : maraMap.values()) {
      Long deRow = makTxIndex.get(mw.getPartnumber(), LANG_DE);
      if (deRow != null) {
        raf.seek(deRow * LINE_LENGTH);
        raf.read(readBytes);
        MaKTxWrapper deTx = new MaKTxWrapper(new String(readBytes));
        mw.setTranslationGer(deTx.getDescription());
      }

      Long enRow = makTxIndex.get(mw.getPartnumber(), LANG_EN);
      if (enRow != null) {
        raf.seek(enRow * LINE_LENGTH);
        raf.read(readBytes);
        MaKTxWrapper enTx = new MaKTxWrapper(new String(readBytes));
        mw.setTranslationEn(enTx.getDescription());
      }
    }
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
