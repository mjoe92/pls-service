package de.vw.paso.client.stammdaten.converter;

import javafx.util.StringConverter;

import de.vw.paso.masterdata.Brand;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public class BrandStringConverter extends StringConverter<Brand> {

    private final boolean isAbbreviation;

    @Override
    public String toString(final Brand value) {
        if (value == null) {
            return StringUtils.EMPTY;
        }

        return isAbbreviation ? value.name() : value.getBrandName();
    }

    @Override
    public Brand fromString(final String value) {
        return isAbbreviation ? Brand.valueOf(value) : Brand.getBrandByName(value);
    }

}
