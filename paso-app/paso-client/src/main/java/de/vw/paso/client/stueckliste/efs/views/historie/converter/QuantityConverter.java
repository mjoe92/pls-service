package de.vw.paso.client.stueckliste.efs.views.historie.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.util.StringConverter;

import de.vw.paso.client.util.QuantityUnit;
import org.apache.commons.lang3.StringUtils;

/**
 * @author eryllan
 * @version $Revision:  $
 * @created 10.03.2015
 */
public class QuantityConverter extends StringConverter<String> {

    private static final Map<String, String> kuerzelToBezeichnungMap = new HashMap<>();

    static {
        for (int i = 0; i < QuantityUnit.values().length; i++) {
            QuantityUnit einheit = QuantityUnit.values()[i];
            if (einheit == QuantityUnit.UNKNOWN) {
                continue;
            }
            kuerzelToBezeichnungMap.put(einheit.getShortName(), einheit.getBezeichnung());
        }
    }

    public static String getBezeichnungForKuerzel(String kuerzel) { // NO_UCD (use private)
        if (kuerzel == null || kuerzel.trim().equals(StringUtils.EMPTY)) {
            return QuantityUnit.UNKNOWN.getShortName();
        }
        return kuerzelToBezeichnungMap.get(kuerzel);
    }

    public static String getKuerzelForBezeichnung(String bezeichnung) {
        for (Entry<String, String> entry : kuerzelToBezeichnungMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value.equals(bezeichnung)) {
                return key;
            }
        }
        return QuantityUnit.UNKNOWN.getShortName();
    }

    public static QuantityUnit getEinheitForKuerzel(String kuerzel) {
        if (kuerzel == null || kuerzel.trim().equals(StringUtils.EMPTY)) {
            return QuantityUnit.UNKNOWN;
        }

        for (QuantityUnit quantityUnit : QuantityUnit.values()) {
            if (quantityUnit.getShortName().equals(kuerzel)) {
                return quantityUnit;
            }
        }

        return QuantityUnit.UNKNOWN;
    }

    @Override
    public String toString(String kuerzel) {
        if (kuerzel == null) {
            return StringUtils.EMPTY;
        }

        String bezeichnungForKuerzel = getBezeichnungForKuerzel(kuerzel);
        if (bezeichnungForKuerzel != null) {
            return bezeichnungForKuerzel;
        }
        return kuerzel;
    }

    @Override
    public String fromString(String bezeichnung) {
        return StringUtils.EMPTY;
    }

}
