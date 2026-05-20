package de.vw.paso.client.control.textfield;

import java.text.ParseException;

import de.vw.paso.client.util.GeneralNumberFormat;
import de.vw.paso.utility.StringConstant;

public class PasoNumberField<T extends Number> extends PasoCustomTextField<T> {

    public static final String STYLE_CLASS_NUMBER_TEXT_FIELD = "align-center-right";
    public static final String STYLE_CLASS_TEXT_FIELD_CENTER = "align-center";

    public PasoNumberField() {
        getStyleClass().add(STYLE_CLASS_NUMBER_TEXT_FIELD);
    }

    public T getNumber() {
        return getText() == null || getText().trim().isEmpty() ? null : getConverter().fromString(getText());
    }

    public void setNumber(T number) {
        setText(getConverter().toString(number));
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (notNumber(text)) {
            return;
        }

        super.replaceText(start, end, text);
    }

    @Override
    public void replaceSelection(String text) {
        if (notNumber(text)) {
            return;
        }

        super.replaceSelection(text);
    }

    private boolean notNumber(String value) {
        if (value.isBlank()) {
            return false;
        }

        value = value.trim();
        if (value.startsWith(StringConstant.DOT) || value.startsWith(StringConstant.COMMA) || value.startsWith(
            StringConstant.DASH)) {
            value = "0" + value;
        }

        try {
            GeneralNumberFormat.parseToDouble(value);
            return false;
        } catch (ParseException e) {
            return true;
        }
    }
}
