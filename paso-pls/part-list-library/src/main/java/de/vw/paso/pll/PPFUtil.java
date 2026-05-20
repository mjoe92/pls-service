package de.vw.paso.pll;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public final class PPFUtil {

  public static final String SECTION_SEPARATOR = "---";

  public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

  public static final String FIELD_SEPARATOR = "\t";

  private PPFUtil() {
    throw new IllegalArgumentException("Util class");
  }

  public static String formatDate(Date date) {
    if (date == null) {
      return "";
    }
    return FORMATTER.format(date);
  }

  public static Date parseDate(String date) {
    if (StringUtils.isEmpty(date)) {
      return null;
    }

    try {
      return FORMATTER.parse(date);
    } catch (ParseException e) {
      return null;
    }
  }
}
