package de.vw.paso.client.control.textfield;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;

import de.vw.paso.client.control.combobox.AutoCompletionBinding;
import de.vw.paso.client.control.combobox.AutoCompletionBinding.ISuggestionRequest;
import de.vw.paso.client.control.combobox.AutoCompletionTextFieldBinding;
import de.vw.paso.utility.StringCommonTermsUtil;

public class PasoCustomTextField<T> extends TextField {

  private static final String STYLE_CLASS_CUSTOM_TEXT_FIELD = "custom-text-field";
  private static final String STYLE_CLASS_ERROR_STATE = "text-field-error-state";

  private StringProperty validCharacter = new SimpleStringProperty(this, StringCommonTermsUtil.VALIDCHARACTER, ".*");
  private IntegerProperty maxTextLength = new SimpleIntegerProperty(this, "maxTextLength", -1);

  private BooleanProperty upperCase = new SimpleBooleanProperty(this, "upperCase", false);
  private BooleanProperty lowerCase = new SimpleBooleanProperty(this, "lowerCase", false);
  private BooleanProperty clearable = new SimpleBooleanProperty(this, "clearable", true);

  private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(this, "converter",
    new StringConverter<>() {
      @Override
      public T fromString(String string) {
        return (T) string;
      }

      @Override
      public String toString(T object) {
        return Objects.toString(object, "");
      }
    });

  private final BooleanProperty valid = new SimpleBooleanProperty();
  private ObjectProperty<Callback<ISuggestionRequest, Collection<T>>> autoCompletion = new SimpleObjectProperty<>(this,
    "autoCompletion");

  private ObjectProperty<Node> left = new SimpleObjectProperty<>(this, "left");

  private ObjectProperty<Node> right = new SimpleObjectProperty<>(this, "right");

  private ObjectProperty<Predicate<T>> validation;
  private AutoCompletionBinding<T> autoCompletionBinding = null;

  public PasoCustomTextField() {
    getStyleClass().add(STYLE_CLASS_CUSTOM_TEXT_FIELD);
    init();
  }

  public final ObjectProperty<Callback<ISuggestionRequest, Collection<T>>> autoCompletionProperty() {
    return autoCompletion;
  }

  public BooleanProperty clearableProperty() {
    return clearable;
  }

  public final ObjectProperty<StringConverter<T>> converterProperty() {
    return converter;
  }

  public final Callback<ISuggestionRequest, Collection<T>> getAutoCompletion() {
    return autoCompletion.getValue();
  }

  public final StringConverter<T> getConverter() {
    return converterProperty().get();
  }

  /**
   * @return the {@link Node} that is placed on the left of
   * the text field.
   */
  public final Node getLeft() {
    return left.get();
  }

  public final Integer getMaxTextLength() {
    return maxTextLength.getValue();
  }

  /**
   * @return The {@link Node} that is placed on the right of
   * the text field.
   */
  public final Node getRight() {
    return right.get();
  }

  public final String getValidCharacter() {
    return validCharacter.getValue();
  }

  public final Predicate<T> getValidation() {
    return validationProperty().getValue();
  }

  public boolean isClearable() {
    return clearable.getValue();
  }

  public final boolean isLowerCase() {
    return lowerCase.getValue();
  }

  public final boolean isUpperCase() {
    return upperCase.getValue();
  }

  /**
   * @return An ObjectProperty wrapping the {@link Node} that is placed
   * on the left ofthe text field.
   */
  public final ObjectProperty<Node> leftProperty() {
    return left;
  }

  public final BooleanProperty lowerCaseProperty() {
    return lowerCase;
  }

  public final IntegerProperty maxTextLengthProperty() {
    return maxTextLength;
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
      if (isUpperCase()) {
        replacement = replacement.toUpperCase();
      } else if (isLowerCase()) {
        replacement = replacement.toLowerCase();
      }
      super.replaceSelection(replacement);
    }
  }

  @Override
  public void replaceText(int start, int end, String text) {
    if (checkMaxTextLength(getText(start, end, text)) && text.matches(getValidCharacter())) {
      if (isUpperCase()) {
        text = text.toUpperCase();
      } else if (isLowerCase()) {
        text = text.toLowerCase();
      }
      super.replaceText(start, end, text);
    }
  }

  /**
   * Property representing the {@link Node} that is placed on the right of
   * the text field.
   *
   * @return An ObjectProperty.
   */
  public final ObjectProperty<Node> rightProperty() {
    return right;
  }

  public final void setAutoCompletion(Callback<ISuggestionRequest, Collection<T>> value) {
    autoCompletion.setValue(value);
  }

  public void setClearable(boolean value) {
    clearable.setValue(value);
  }

  public final void setConverter(StringConverter<T> value) {
    converterProperty().set(value);
  }

  /**
   * Sets the {@link Node} that is placed on the left of
   * the text field.
   *
   * @param value
   */
  public final void setLeft(Node value) {
    left.set(value);
  }

  public final void setLowerCase(boolean value) {
    lowerCase.setValue(value);
  }

  public final void setMaxTextLength(Integer value) {
    maxTextLength.setValue(value);
  }

  /**
   * Sets the {@link Node} that is placed on the right of
   * the text field.
   *
   * @param value
   */
  public final void setRight(Node value) {
    right.set(value);
  }

  public final void setUpperCase(boolean value) {
    upperCase.setValue(value);
  }

  public final void setValidCharacter(String regex) {
    validCharacter.setValue(regex);
  }

  public final void setValidation(Predicate<T> value) {
    validationProperty().setValue(value);
  }

  public final BooleanProperty upperCaseProperty() {
    return upperCase;
  }

  public final StringProperty validCharacterProperty() {
    return validCharacter;
  }

  public final ReadOnlyBooleanProperty validProperty() {
    return valid;
  }

  public final ObjectProperty<Predicate<T>> validationProperty() {
    if (validation == null) {
      validation = new SimpleObjectProperty<>(this, "validation");
    }
    return validation;
  }

  protected void bindAutoCompletion() {
    if (getAutoCompletion() != null) {
      autoCompletionBinding = bindAutoCompletion(this, getAutoCompletion());
    } else if (autoCompletionBinding != null) {
      autoCompletionBinding.dispose();
    }
  }

  protected void createClearableTextField() {
    if (isClearable()) {
      TextFieldUtil.createClearableTextField(this);
    } else {
      rightProperty().set(null);
    }
  }

  private static <T> AutoCompletionBinding<T> bindAutoCompletion(TextField textField,
    Callback<ISuggestionRequest, Collection<T>> suggestionProvider) {
    return new AutoCompletionTextFieldBinding<>(textField, suggestionProvider);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new PasoCustomTextFieldSkin<>(this);
  }

  protected AutoCompletionBinding<T> getAutoCompletionBinding() {
    return autoCompletionBinding;
  }

  private boolean checkMaxTextLength(String text) {
    if (getMaxTextLength() < 0) {
      return true;
    }
    return text.length() <= getMaxTextLength();
  }

  private String getText(int start, int end, String replacement) {
    String text = getText();
    if (text == null) {
      return replacement;
    }
    String startText = text.substring(0, start);
    String endText = text.substring(end);
    return startText + replacement + endText;
  }

  private void init() {
    clearableProperty().addListener(observable -> createClearableTextField());
    textProperty().addListener(observable -> validateValue());
    autoCompletion.addListener(observable -> bindAutoCompletion());
  }

  private void validateValue() {
    if (validation == null) {
      valid.set(true);
      return;
    }
    valid.set(getValidation().test(getConverter().fromString(getText())));

    if (valid.get()) {
      getStyleClass().remove(STYLE_CLASS_ERROR_STATE);
    } else if (!getStyleClass().contains(STYLE_CLASS_ERROR_STATE)) {
      getStyleClass().add(STYLE_CLASS_ERROR_STATE);
    }
  }

}
