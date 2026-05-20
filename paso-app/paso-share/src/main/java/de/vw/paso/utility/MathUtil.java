package de.vw.paso.utility;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MathUtil {

    public static Double nullSafeSubtract(final Double... values) {
        boolean isAllNull = true;

        Double result = values[0];

        if (result == null) {
            result = 0.0D;
        } else {
            isAllNull = false;
        }

        for (int index = 1; index < values.length; index++) {
            if (values[index] != null) {
                result -= values[index];

                isAllNull = false;
            }
        }

        return isAllNull ? null : result;
    }

    public static Double nullSafeAddition(final Double... values) {
        boolean isAllNull = true;

        Double result = 0.0D;

        for (final Double value : values) {
            if (value != null) {
                result += value;

                isAllNull = false;
            }
        }

        return isAllNull ? null : result;
    }

    public static Double nullSafeMultiplication(final Double... values) {
        final List<Double> notNullValues = Arrays.stream(values).filter(Objects::nonNull).collect(Collectors.toList());

        if (notNullValues.isEmpty() || (notNullValues.size() < 2)) {
            return null;
        }

        Double result = 1.0D;

        for (final Double value : notNullValues) {
            result *= value;
        }

        return result;
    }

}
