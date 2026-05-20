package de.vw.paso.client.util.converter;

import java.text.ParseException;

import de.vw.paso.client.util.GeneralNumberFormat;

public class IntegerStringConverter extends NumberStringConverter<Integer> {

    private static final int ZERO = 0;

    public IntegerStringConverter() {
        this(true);
    }

    public IntegerStringConverter(boolean allowNull) {
        super(allowNull);
    }

    @Override
    protected Integer parseFromValue(String value) throws ParseException {
        return GeneralNumberFormat.parseToInteger(value);
    }

    @Override
    public String toString(Integer value) {
        return GeneralNumberFormat.format(value, isGroupingUsed(), 0);
    }

    @Override
    public Integer getZero() {
        return ZERO;
    }
}
