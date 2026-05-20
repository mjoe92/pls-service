package de.vw.paso.pll.preprocessing.formats.ppf.field;

import de.vw.paso.pll.PPFUtil;
import de.vw.paso.pll.model.QuantityUnit;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.function.Function;

public interface PPFField<ROW> {

  static Integer toInteger(String value) {
    return toInteger(value, null);
  }

  static Integer toInteger(String value, Integer nullValue) {
    if (StringUtils.isEmpty(value)) {
      return nullValue;
    }
    return Integer.parseInt(value);
  }

  static Double toDouble(String value, Double nullValue) {
    if (StringUtils.isEmpty(value)) {
      return nullValue;
    }

    value = value.replace(",", ".");

    return Double.parseDouble(value);
  }

  static QuantityUnit toQuantityUnit(String value) {
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    return QuantityUnit.getByShortName(value);
  }

  static Date toDate(String value) {
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    return PPFUtil.parseDate(value);
  }

  Function<ROW, ?> getValueProvider();
}
