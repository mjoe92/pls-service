package de.vw.paso.client.util.customfilter;

import de.vw.paso.client.base.I18N;

public class FilterStrings {

  private static final String KEY_CONTAINS = "filter.contains";
  private static final String KEY_CONTAINS_NOT = "filter.contains.not";
  private static final String KEY_EQUALS = "filter.equals";
  private static final String KEY_YEAR = "filter.year";
  private static final String KEY_RANGE = "filter.range";
  private static final String KEY_RANGE_OUTSIDE = "filter.range.outside";
  private static final String KEY_TOP = "filter.top";
  private static final String KEY_LIMIT_LOW = "filter.limit.low";
  private static final String KEY_LIMIT_HIGH = "filter.limit.high";


  public static String getLocalizedContainsString() {
    return I18N.getString(KEY_CONTAINS);
  }

  public static String getLocalizedDoesNotContainString() {
    return I18N.getString(KEY_CONTAINS_NOT);
  }

  public static String getLocalizedEqualsString() {
    return I18N.getString(KEY_EQUALS);
  }

  public static String getLocalizedInYearString() {
    return I18N.getString(KEY_YEAR);
  }

  public static String getLocalizedWithinRangeString() {
    return I18N.getString(KEY_RANGE);
  }

  public static String getLocalizedOutsideOfRangeString() {
    return I18N.getString(KEY_RANGE_OUTSIDE);
  }

  public static String getLocalizedTopString() {
    return I18N.getString(KEY_TOP);
  }

  public static String getLocalizedLowerLimitString() {
    return I18N.getString(KEY_LIMIT_LOW);
  }

  public static String getLocalizedUpperLimitString() {
    return I18N.getString(KEY_LIMIT_HIGH);
  }
}
