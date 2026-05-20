package de.vw.paso.pll.creation;

import java.time.LocalDate;

import de.vw.paso.pll.PPFUtil;
import de.vw.paso.pll.preprocessing.formats.ppf.PPF;
import de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField;
import org.apache.commons.lang3.StringUtils;

public final class PartListCreatorUtil {

  private static final int TNR_MITTELGRUPPE_INDEX = 3;

  private PartListCreatorUtil() {
    // noop
  }

  public static String[] splitData(String data, PPF ppf) {
    //Second parameter is necessary, so an empty field at the end will be in the resulting array.
    return data.split(PPFUtil.FIELD_SEPARATOR, ppf.getFields().length + 1);
  }

  public static PPF getLineType(String line) {
    for (PPF ppf : PPF.values()) {
      if (line.startsWith(ppf.getRowId())) {
        return ppf;
      }
    }
    throw new PartListCreationException("Unknown line type for line: " + line);
  }

  public static String getPartData(String[] split, PPFField field) {
    Enum enu = (Enum) field;
    return split[1 + enu.ordinal()];
  }

  public static LocalDate toLocalDate(String value) {
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    return LocalDate.parse(value);
  }

  public static String removeVornummer(String partNumber) {
    if (StringUtils.isNotEmpty(partNumber) && partNumber.length() >= 3) {
      return partNumber.substring(TNR_MITTELGRUPPE_INDEX);
    }
    throw new RuntimeException("Cannot remove TNR_VORNUMMER from " + partNumber);
  }
}
