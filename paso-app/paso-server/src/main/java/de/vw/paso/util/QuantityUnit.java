package de.vw.paso.util;

import java.io.Serializable;

public enum QuantityUnit implements Serializable {

  UNKNOWN(""),
  PIECE("ST"),
  GRAMM("G"),
  MILLILITER("ML"),
  MILLIMETER("MM"),
  SQUARE_CENTIMETER("CM2");

  private final String shortName;

  QuantityUnit(String shortName) {
    this.shortName = shortName;
  }

  public String getShortName() {
    return shortName;
  }

}
