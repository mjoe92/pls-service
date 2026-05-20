package de.vw.paso.client.control.textarea;

import java.util.function.Predicate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import de.vw.paso.utility.StringCommonTermsUtil;

/**
 *
 * Kommentar
 *
 * @author emeinee
 * @version $Revision:  $
 * @created 13.11.2014
 */
public class CustomTextArea<T> extends TextArea {

    private static final String STYLE_CLASS_ERROR_STATE = "text-area-error-state";

    /**
     * Constructor
     */
    public CustomTextArea() {
        getStyleClass().add("custom-text-area");
        init();
    }

    private void init() {
        textProperty().addListener(observable -> validateValue());
    }

    /***************************************************************************
     *                                                                         *
     * Zeichen die eingegeben werden koennen                                   *
     *                                                                         *
     **************************************************************************/

    private StringProperty validCharacter = new SimpleStringProperty(this, StringCommonTermsUtil.VALIDCHARACTER, ".*");

    public final String getValidCharacter() {
        return validCharacter.getValue();
    }

    public final void setValidCharacter(String regex) {
        validCharacter.setValue(regex);
    }

    public final StringProperty validCharacterProperty() {
        return validCharacter;
    }

    /***************************************************************************
     *                                                                         *
     * maximale Textlaenge die eingegeben werden kann                          *
     *                                                                         *
     **************************************************************************/

    private IntegerProperty maxTextLength = new SimpleIntegerProperty(this, "maxTextLength", -1);

    public final Integer getMaxTextLength() {
        return maxTextLength.getValue();
    }

    public final void setMaxTextLength(Integer value) {
        maxTextLength.setValue(value);
    }

    public final IntegerProperty maxTextLengthProperty() {
        return maxTextLength;
    }

    /***************************************************************************
     *                                                                         *
     * upper Case                                                              *
     *                                                                         *
     **************************************************************************/

    private BooleanProperty upperCase = new SimpleBooleanProperty(this, "upperCase", false);

    public final boolean isUpperCase() {
        return upperCase.getValue();
    }

    public final void setUpperCase(boolean value) {
        upperCase.setValue(value);
    }

    public final BooleanProperty upperCaseProperty() {
        return upperCase;
    }

    /***************************************************************************
     *                                                                         *
     * lower Case                                                              *
     *                                                                         *
     **************************************************************************/

    private BooleanProperty lowerCase = new SimpleBooleanProperty(this, "lowerCase", false);

    public final boolean isLowerCase() {
        return lowerCase.getValue();
    }

    public final void setLowerCase(boolean value) {
        lowerCase.setValue(value);
    }

    public final BooleanProperty lowerCaseProperty() {
        return lowerCase;
    }

    /***************************************************************************
     *                                                                         *
     * wenn true, Clear-Button wird in TextArea angezeigt                     *
     *                                                                         *
     **************************************************************************/

    private BooleanProperty clearable = new SimpleBooleanProperty(this, "clearable", true);

    public boolean isClearable() {
        return clearable.getValue();
    }

    public void setClearable(boolean value) {
        clearable.setValue(value);
    }

    public BooleanProperty clearableProperty() {
        return clearable;
    }

    /****************************************************************************
     *                                                                          *
     * StringConverter - Konvertieren von <T> nach String                       *
     *                                                                          *
     ****************************************************************************/

    @SuppressWarnings("unchecked")
    private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(this, "converter",
            (StringConverter<T>) new DefaultStringConverter());

    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }

    /****************************************************************************
     *                                                                          *
     * Validation - Ueberpruefung des eingegebenen Wertes                       *
     *                                                                          *
     ****************************************************************************/

    private ObjectProperty<Predicate<T>> validation = new SimpleObjectProperty<>(this, "validation");

    public final void setValidation(Predicate<T> value) {
        validation.setValue(value);
    }

    public final Predicate<T> getValidation() {
        return validation.getValue();
    }

    public final ObjectProperty<Predicate<T>> validationProperty() {
        return validation;
    }

    private void validateValue() {
        Predicate<T> predicate = validationProperty().getValue();
        if (predicate != null) {
            if (predicate.test(getConverter().fromString(getText()))) {
                getStyleClass().remove(STYLE_CLASS_ERROR_STATE);
            } else if (!getStyleClass().contains(STYLE_CLASS_ERROR_STATE)) {
                getStyleClass().add(STYLE_CLASS_ERROR_STATE);
            }
        }
    }

    /***************************************************************************
     *                                                                         *
     * replace Methods                                                         *
     *                                                                         *
     **************************************************************************/

    @Override
    public void replaceSelection(String replacement) {
        if (checkMaxTextLength(getText(getSelection().getStart(), getSelection().getEnd(), replacement))
                && replacement.matches(getValidCharacter())) {
            if (isUpperCase() && replacement != null) {
                replacement = replacement.toUpperCase();
            } else if (isLowerCase() && replacement != null) {
                replacement = replacement.toLowerCase();
            }
            super.replaceSelection(replacement);
        }
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (checkMaxTextLength(getText(start, end, text)) && text.matches(getValidCharacter())) {
            if (isUpperCase() && text != null) {
                text = text.toUpperCase();
            } else if (isLowerCase() && text != null) {
                text = text.toLowerCase();
            }
            super.replaceText(start, end, text);
        }
    }

    private String getText(int start, int end, String replacement) {
        String text = getText();
        if (text == null) {
            return replacement;
        }
        String startText = text.substring(0, start);
        String endText = text.substring(end, text.length());
        return startText + replacement + endText;
    }

    private boolean checkMaxTextLength(String text) {
        if (getMaxTextLength().intValue() < 0) {
            return true;
        }
        return text.length() <= getMaxTextLength().intValue();
    }

}
