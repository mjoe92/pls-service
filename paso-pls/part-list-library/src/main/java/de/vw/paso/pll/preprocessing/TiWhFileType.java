package de.vw.paso.pll.preprocessing;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import de.vw.paso.pll.preprocessing.formats.raw.EbkVsdWrapper;
import de.vw.paso.pll.preprocessing.formats.raw.EbomWrapper;
import de.vw.paso.pll.preprocessing.formats.raw.MaKTxWrapper;
import de.vw.paso.pll.preprocessing.formats.raw.MaraWrapper;
import de.vw.paso.pll.preprocessing.formats.raw.NodeWrapper;
import de.vw.paso.pll.preprocessing.formats.raw.RowWrapper;

public enum TiWhFileType {

  NODE(NodeWrapper::new, 306),
  EBOM(EbomWrapper::new, 1234),
  MAKTX(MaKTxWrapper::new, 60),
  MARA(MaraWrapper::new, 648),
  EBKVSD(EbkVsdWrapper::new, 553);

  public static final List<String> FILE_TYPES = Arrays.stream(TiWhFileType.values()).map(TiWhFileType::name)
    .map(String::toLowerCase).toList();

  private final Function<String, RowWrapper> creator;
  private final int rowLength;

  TiWhFileType(Function<String, RowWrapper> creator, int rowLength) {
    this.creator = creator;
    this.rowLength = rowLength;
  }

  public RowWrapper createWrapper(String secondLine) {
    return creator.apply(secondLine);
  }

  public int getRowLength() {
    return rowLength;
  }

  public static boolean isKnownType(String fileName) {
    String fileNameLower = fileName.toLowerCase();

    for (String type : FILE_TYPES) {
      if (isType(type, fileNameLower)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isType(TiWhFileType type, String name) {
    return isType(type.name().toLowerCase(), name.toLowerCase());
  }

  private static boolean isType(String type, String fileName) {
    return fileName.startsWith(type) || fileName.endsWith(type);
  }
}
