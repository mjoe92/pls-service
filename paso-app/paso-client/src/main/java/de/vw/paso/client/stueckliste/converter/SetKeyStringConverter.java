package de.vw.paso.client.stueckliste.converter;

import javafx.util.StringConverter;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public class SetKeyStringConverter extends StringConverter<String> {

  private static final String SEPARATOR = " - ";

  private final boolean isPopup;

  @Override
  public String toString(final String setKey) {
    if (setKey == null) {
      return "";
    }

    return isPopup ? setKey : StringUtils.substringBefore(setKey, SEPARATOR);
  }

  @Override
  public String fromString(final String setKey) {
    if (StringUtils.isBlank(setKey)) {
      return null;
    }

    return StringUtils.substringBefore(setKey, SEPARATOR);
  }

}
