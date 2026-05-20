package de.vw.paso.client.stueckliste.converter;

import java.util.HashMap;
import java.util.Map;

import javafx.util.StringConverter;

import de.vw.paso.client.util.QuantityUnit;

public class QuantityUnitStringConverter extends StringConverter<QuantityUnit> {

    private static final Map<String, QuantityUnit> mapBezeichnungEinheit = new HashMap<>();

    static {
        for (QuantityUnit unit : QuantityUnit.values()) {
            if (unit == QuantityUnit.UNKNOWN) {
                continue;
            }
            mapBezeichnungEinheit.put(unit.getBezeichnung().toUpperCase(), unit);
        }
    }

    private static QuantityUnit getEinheitForBezeichnung(String bezeichnung) {
        if (bezeichnung == null || bezeichnung.trim().equals("")) {
            return QuantityUnit.UNKNOWN;
        }
        QuantityUnit einheit = mapBezeichnungEinheit.get(bezeichnung.toUpperCase());
        if (einheit == null) {
            return QuantityUnit.UNKNOWN;
        }
        return einheit;
    }

    @Override
    public String toString(QuantityUnit quantityUnit) {
        if (quantityUnit == null) {
            return "";
        }
        return quantityUnit.getBezeichnung();
    }

    @Override
    public QuantityUnit fromString(String bezeichnung) {
        return getEinheitForBezeichnung(bezeichnung);
    }
}
