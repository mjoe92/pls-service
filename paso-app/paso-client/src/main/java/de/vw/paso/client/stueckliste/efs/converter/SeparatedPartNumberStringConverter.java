package de.vw.paso.client.stueckliste.efs.converter;

import javafx.util.StringConverter;

import de.vw.paso.partlist.domain.SpecialPartNumberType;
import de.vw.paso.utility.EfsElementUtil;

public class SeparatedPartNumberStringConverter extends StringConverter<String> {

    @Override
    public String fromString(String value) {
        return null;
    }

    @Override
    public String toString(String value) {
        return SpecialPartNumberType.GAP.getLabel().equals(value) ? value :
                EfsElementUtil.convertPartNumberString(value);
    }
}