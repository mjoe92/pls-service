package de.vw.paso.client.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.vw.paso.client.base.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum QuantityUnit implements Serializable {

  UNKNOWN(""),
  PIECE("ST"),
  GRAMM("G"),
  MILLILITER("ML"),
  MILLIMETER("MM"),
  SQUARE_CENTIMETER("CM2");

  private static final Map<String, QuantityUnit> MAPPING = Arrays.stream(values())
    .collect(Collectors.toMap(QuantityUnit::getShortName, Function.identity()));

  private static final Logger LOG = LoggerFactory.getLogger(QuantityUnit.class);

  private final String shortName;

  QuantityUnit(String shortName) {
    this.shortName = shortName;
  }

  public String getBezeichnung() {
    if (Objects.equals(shortName, "")) {
      return "";
    }

    try {
      return I18N.getString(shortName);
    } catch (Exception e) {
      LOG.warn(e.getMessage());
      return shortName;
    }
  }

  public static String getLocalizedValue(final String abbreviation) {
    for (final QuantityUnit unit : values()) {
      if (unit.getShortName().equals(abbreviation)) {
        return unit.getBezeichnung();
      }
    }
    return "";
  }

  public String getShortName() {
    return shortName;
  }

  @Override
  public String toString() {
    return getBezeichnung();
  }

  public static QuantityUnit getByShortName(final String shortName) {
    if (shortName == null) {
      return UNKNOWN;
    }
    return MAPPING.getOrDefault(shortName.trim(), UNKNOWN);
  }

  public static QuantityUnit getByName(final String name) {
    if (name == null) {
      return UNKNOWN;
    }
    for (QuantityUnit value : values()) {
      if (value.getBezeichnung().equals(name)) {
        return value;
      }
    }
    return UNKNOWN;
  }

}
