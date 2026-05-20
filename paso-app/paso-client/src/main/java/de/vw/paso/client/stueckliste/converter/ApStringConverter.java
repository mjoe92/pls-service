package de.vw.paso.client.stueckliste.converter;

import javafx.util.StringConverter;

import de.vw.paso.partlist.domain.AP;

public class ApStringConverter extends StringConverter<AP> {

    @Override
    public String toString(final AP ap) {
        return ap.getApAbbreviation();
    }

    @Override
    public AP fromString(final String apAbbreviation) {
        return AP.getApByAbbreviation(apAbbreviation);
    }

}
