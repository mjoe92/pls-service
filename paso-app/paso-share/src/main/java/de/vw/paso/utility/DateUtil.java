package de.vw.paso.utility;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.FastDateFormat;

public class DateUtil {

    /*
     * Works like SimpleDateFormatter but is faster and thread save.
     */
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("dd.MM.yyyy", Locale.GERMANY);

    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        Instant instant = localDate.atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static String formatDate(Date date, String pattern) {
        return FastDateFormat.getInstance(pattern, Locale.GERMANY).format(date);
    }

    /**
     * Converts the given String to dd.MM.yyy
     *
     * @param date
     *     date to format
     * @return a String returning the date as dd.MM.yyyy
     */
    public static String toDefaultDateString(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static Date parseDefaultString(String date) throws ParseException {
        return DATE_FORMAT.parse(date);
    }

    public static Date convertToDate(LocalDateTime dateToConvert) {
        return java.util.Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }
}
