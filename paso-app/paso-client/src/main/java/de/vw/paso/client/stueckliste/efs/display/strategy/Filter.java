package de.vw.paso.client.stueckliste.efs.display.strategy;

import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class Filter<T> {

  private final Function<T, Object> getter;

  @Setter
  @Getter
  private String regex = StringUtils.EMPTY;

  public Filter(Function<T, Object> getter) {
    this.getter = getter;
  }

  public boolean isValid(T node) {
    final Object value = getter.apply(node);
    if (StringUtils.isNotEmpty(regex)) {
      return convertValue(value).matches("(?i:.*" + regex + ".*)");
    }
    return true;
  }

  private String convertValue(Object value) {
    if (value == null) {
      value = StringUtils.EMPTY;
    }
    return value.toString();
  }

}
