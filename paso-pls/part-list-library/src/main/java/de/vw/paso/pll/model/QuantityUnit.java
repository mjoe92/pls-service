package de.vw.paso.pll.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum QuantityUnit {

  UNKNOWN("UNKNOWN"),
  PIECE("ST"),
  GRAMM("G"),
  MILLILITER("ML"),
  MILLIMETER("MM"),
  SQUARE_CENTIMETER("CM2");

  private static final Map<String, QuantityUnit> MAPPING = Arrays.stream(values())
    .collect(Collectors.toMap(QuantityUnit::getShortName, Function.identity()));

  private final String shortName;

  QuantityUnit(String shortName) {
    this.shortName = shortName;
  }

  public String getShortName() {
    return this.shortName;
  }

  public static QuantityUnit getByShortName(String shortName) {
    if (shortName == null) {
      return UNKNOWN;
    }
    return MAPPING.getOrDefault(shortName.trim(), UNKNOWN);
  }
}
