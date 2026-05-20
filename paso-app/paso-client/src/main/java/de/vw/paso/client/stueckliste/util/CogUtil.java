package de.vw.paso.client.stueckliste.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import de.vw.paso.client.control.cell.CogCoordinates;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import org.apache.commons.lang3.StringUtils;

public class CogUtil {

    public static final String COG_SEPARATOR = ";";

    private static DecimalFormat formatter = new DecimalFormat("#,###.###", new DecimalFormatSymbols(Locale.GERMAN));
    private static DecimalFormat formatterRounded = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.GERMAN));

    public static String toString(CogCoordinates coordinates) {
        return combineCog(coordinates);
    }

    private static String combineCog(CogCoordinates item) {
        if (item != null) {
            if (item.getCogX() != null || item.getCogY() != null || item.getCogZ() != null) {
                return String.format("[%s;%s;%s]", formatNotRounded(item.getCogX()), formatNotRounded(item.getCogY()),
                        formatNotRounded(item.getCogZ()));
            }
        }
        return "";
    }

    public static Double parseCoordinate(String str) {
        if (str == null) {
            return null;
        }
        try {
            Number number = formatter.parse(str);
            if (number != null) {
                return number.doubleValue();
            } else {
                return null;
            }
        } catch (ParseException e) {
            return null;
        }
    }

    public static String format(Double d, boolean rounded) {
        if (d == null) {
            return "";
        }
        if (rounded) {
            return formatterRounded.format(d);
        }
        return formatter.format(d);
    }

    private static String formatNotRounded(Double d) {
        return format(d, false);
    }

    public static boolean isValidCoordinate(String str) {
        return StringUtils.isEmpty(str) || parseCoordinate(str) != null;
    }

    public static CogCoordinates getCoordinate(IEfsElementForDTO element) {
        if (element == null) {
            return null;
        }
        return new CogCoordinates(element.getCogX(), element.getCogY(), element.getCogZ());
    }
}
