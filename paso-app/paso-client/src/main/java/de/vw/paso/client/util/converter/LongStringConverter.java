package de.vw.paso.client.util.converter;

import java.text.ParseException;

import de.vw.paso.client.util.GeneralNumberFormat;

public class LongStringConverter extends NumberStringConverter<Long> {

    private static final long ZERO = 0L;

    @Override
    protected Long parseFromValue(String value) throws ParseException {
        return GeneralNumberFormat.parseToLong(value);
    }

    @Override
    public String toString(Long value) {
        return GeneralNumberFormat.format(value, isGroupingUsed(), 0);
    }

    @Override
    public Long getZero() {
        return ZERO;
    }
}
