package de.vw.paso.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;

public class TimestampUtils {
  public static Timestamp getTimeStampDaysMinus(int days) {
    return Timestamp.from(
      LocalDate.now().minusDays(days)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
    );
  }
}
