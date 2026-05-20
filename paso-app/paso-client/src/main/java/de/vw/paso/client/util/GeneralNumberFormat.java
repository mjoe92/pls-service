package de.vw.paso.client.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import de.vw.paso.utility.StringConstant;

/**
 * @author emikoen
 * @version $Revision:  $
 * created 08.09.2014
 */
public class GeneralNumberFormat {

    private static final int MAX_FRACTION_DIGITS = 2;
    private static final int MIN_FRACTION_DIGITS = 0;

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(
        Locale.of(UserProperties.getPreferredLanguage()));

    public static String format(Object number, boolean isGroupingUsed, Integer exactFractionDigits) {
        if (number == null) {
            return StringConstant.EMPTY;
        }

        int minFractionDigits, maxFractionDigits;
        if (exactFractionDigits == null) {
            minFractionDigits = MIN_FRACTION_DIGITS;
            maxFractionDigits = MAX_FRACTION_DIGITS;
        } else {
            minFractionDigits = exactFractionDigits;
            maxFractionDigits = exactFractionDigits;
        }

        NUMBER_FORMAT.setMinimumFractionDigits(minFractionDigits);
        NUMBER_FORMAT.setMaximumFractionDigits(maxFractionDigits);
        NUMBER_FORMAT.setGroupingUsed(isGroupingUsed);

        return NUMBER_FORMAT.format(number);
    }

    public static Double parseToDouble(String value) throws ParseException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        NUMBER_FORMAT.setGroupingUsed(true);
        return NUMBER_FORMAT.parse(value).doubleValue();
    }

    public static Long parseToLong(String value) throws ParseException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        NUMBER_FORMAT.setGroupingUsed(true);
        return NUMBER_FORMAT.parse(value).longValue();
    }

    public static Integer parseToInteger(String value) throws ParseException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        NUMBER_FORMAT.setGroupingUsed(true);
        long newValue = NUMBER_FORMAT.parse(value).longValue();

        if (newValue > Integer.MAX_VALUE || newValue < 0) {
            throw new ParseException("Integer overflow detected", newValue > Integer.MAX_VALUE ? 10 : 1);
        }

        return (int) newValue;
    }
}
