package de.vw.paso.client.stueckliste.converter;

import javafx.util.StringConverter;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public class CostGroupStringConverter extends StringConverter<String> {

  private static final String SEPARATOR = " - ";

  private final boolean isPopup;

  @Override
  public String toString(final String costGroup) {
    if (costGroup == null) {
      return StringUtils.EMPTY;
    }

    return isPopup ? costGroup : StringUtils.substringBefore(costGroup, SEPARATOR);
  }

  @Override
  public String fromString(final String costGroup) {
    return StringUtils.substringBefore(costGroup, SEPARATOR);
  }

}
