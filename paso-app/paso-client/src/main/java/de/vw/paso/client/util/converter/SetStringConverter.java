package de.vw.paso.client.util.converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javafx.util.StringConverter;

public class SetStringConverter extends StringConverter<Set<String>> {

    private static final String SEPARATOR = ", ";

    @Override
    public String toString(Set<String> object) {
        return String.join(SEPARATOR, object);
    }

    @Override
    public Set<String> fromString(String string) {
        String[] strings = string.split(SEPARATOR);

        return new HashSet<>(Arrays.asList(strings));
    }

}
