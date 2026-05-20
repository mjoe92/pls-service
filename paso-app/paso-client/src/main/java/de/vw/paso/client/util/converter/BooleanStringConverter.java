package de.vw.paso.client.util.converter;

import javafx.util.StringConverter;

import de.vw.paso.client.base.I18N;
import org.apache.commons.lang3.StringUtils;

public class BooleanStringConverter extends StringConverter<Boolean> {

    private static final String RESOURCE_BUNDLE_YES = "yes";
    private static final String RESOURCE_BUNDLE_NO = "no";

    @Override
    public String toString(final Boolean value) {
        if (value == null) {
            return StringUtils.EMPTY;
        }

        return I18N.getString(value ? RESOURCE_BUNDLE_YES : RESOURCE_BUNDLE_NO);
    }

    @Override
    public Boolean fromString(final String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        if (value.equals(I18N.getString(RESOURCE_BUNDLE_YES))) {
            return true;
        }

        if (value.equals(I18N.getString(RESOURCE_BUNDLE_NO))) {
            return false;
        }

        return null;
    }

}
