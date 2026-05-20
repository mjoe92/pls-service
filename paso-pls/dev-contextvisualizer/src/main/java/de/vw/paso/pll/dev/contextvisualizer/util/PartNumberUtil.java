package de.vw.paso.pll.dev.contextvisualizer.util;

import org.apache.commons.lang3.StringUtils;

public class PartNumberUtil {

  private static final String REGEX_EMPTY_END = "\\. {1,2}$";

  public static String toString(final String value) {
    if (value == null) {
      return StringUtils.EMPTY;
    }

    final StringBuilder stringBuilder = new StringBuilder();

    for (int index = 0; index < value.length();) {
      if (stringBuilder.length() % 4 != 3) {
        stringBuilder.append(value.charAt(index++));
      } else {
        stringBuilder.append(' ');
      }
    }

    final String result = stringBuilder.toString();

    return result.replaceFirst(REGEX_EMPTY_END, StringUtils.EMPTY);
  }

}
