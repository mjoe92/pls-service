package de.vw.paso.client.stueckliste.efs.converter;

import javafx.util.StringConverter;

import org.apache.commons.lang3.StringUtils;

public class EinsatzEntfallConverter extends StringConverter<String> {

  @Override
  public String fromString(String value) {
    return null;
  }

  @Override
  public String toString(String value) {
    if (value == null) {
      return StringUtils.EMPTY;
    }

    return value.replace(".", StringUtils.EMPTY);
  }

}
