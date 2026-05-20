package de.vw.paso.client.util.converter;

import java.text.ParseException;

import javafx.util.StringConverter;

public abstract class NumberStringConverter<T extends Number> extends StringConverter<T> {

    private boolean isGroupingUsed;

    private final boolean allowNull;

    public NumberStringConverter() {
        this(true);
    }

    public NumberStringConverter(boolean allowNull) {
        this.allowNull = allowNull;
        isGroupingUsed = true;
    }

    @Override
    public final T fromString(String value) {
        if (value == null) {
            return getNullValue();
        }

        value = value.trim();
        try {
            return value.isEmpty() ? getNullValue() : parseFromValue(value);
        } catch (ParseException pe) {
            return getNullValue();
        }

    }

    public boolean isZero(T val) {
        return val == null || getZero().equals(val);
    }

    protected abstract T getZero();

    protected abstract T parseFromValue(String value) throws ParseException;

    private T getNullValue() {
        return allowNull ? null : getZero();
    }

    public boolean isGroupingUsed() {
        return isGroupingUsed;
    }

    public void setGroupingUsed(boolean isGroupingUsed) {
        this.isGroupingUsed = isGroupingUsed;
    }
}
