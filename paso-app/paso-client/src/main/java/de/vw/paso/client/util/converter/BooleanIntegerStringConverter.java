package de.vw.paso.client.util.converter;

import javafx.util.StringConverter;

import de.vw.paso.client.base.I18N;
import org.apache.commons.lang3.StringUtils;

public class BooleanIntegerStringConverter extends StringConverter<Integer> {

    private static final String RESOURCE_BUNDLE_YES = "yes";
    private static final String RESOURCE_BUNDLE_NO = "no";
    private static final int INTEGER_YES = 1;
    private static final int INTEGER_NO = 0;

    @Override
    public String toString(final Integer value) {
        if (value == null) {
            return StringUtils.EMPTY;
        }

        return I18N.getString((value == INTEGER_YES) ? RESOURCE_BUNDLE_YES : RESOURCE_BUNDLE_NO);
    }

    @Override
    public Integer fromString(final String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        if (value.equals(I18N.getString(RESOURCE_BUNDLE_YES))) {
            return INTEGER_YES;
        }

        if (value.equals(I18N.getString(RESOURCE_BUNDLE_NO))) {
            return INTEGER_NO;
        }

        return null;
    }

}
