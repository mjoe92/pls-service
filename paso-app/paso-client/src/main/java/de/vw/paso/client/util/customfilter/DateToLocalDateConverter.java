package de.vw.paso.client.util.customfilter;

import java.time.LocalDate;
import java.util.Date;

public class DateToLocalDateConverter {

  public static LocalDate convertToLocalDate(Date date) {

    java.sql.Date sqlDate = new java.sql.Date(date.getTime());

    return sqlDate.toLocalDate();
  }
}
