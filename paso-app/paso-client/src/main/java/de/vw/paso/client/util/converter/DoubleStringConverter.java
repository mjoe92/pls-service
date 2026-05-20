package de.vw.paso.client.util.converter;

import java.text.ParseException;

import de.vw.paso.client.util.GeneralNumberFormat;

public class DoubleStringConverter extends NumberStringConverter<Double> {

    private static final double ZERO = 0.0d;

    private final Integer fractionDigits;

    public DoubleStringConverter() {
        this(true);
    }

    public DoubleStringConverter(boolean allowNullValues) {
        this(null, allowNullValues);
    }

    public DoubleStringConverter(Integer fractionDigits, boolean allowNullValues) {
        super(allowNullValues);

        this.fractionDigits = fractionDigits;
    }

    @Override
    protected Double parseFromValue(String value) throws ParseException {
        return GeneralNumberFormat.parseToDouble(value);
    }

    @Override
    public String toString(Double value) {
        return GeneralNumberFormat.format(value, isGroupingUsed(), fractionDigits);
    }

    @Override
    public Double getZero() {
        return ZERO;
    }
}
