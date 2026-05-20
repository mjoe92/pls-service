package de.vw.paso.client.control.cell;

import java.util.Objects;
import java.util.function.Predicate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeTableCell;
import javafx.util.StringConverter;

import de.vw.paso.utility.StringCommonTermsUtil;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractTreeTableCell<S, T> extends TreeTableCell<S, T> {

  private StringProperty propertyName = new SimpleStringProperty(StringUtils.EMPTY);
  private StringProperty validCharacter = new SimpleStringProperty(this, StringCommonTermsUtil.VALIDCHARACTER, ".*");
  private BooleanProperty propertyChanged = new SimpleBooleanProperty() {
    @Override
    public void unbind() {
      super.unbind();
      setChanged(false);
    }
  };
  private IntegerProperty maxTextLength = new SimpleIntegerProperty(this, "maxTextLength", -1);
  private BooleanProperty upperCase = new SimpleBooleanProperty(this, "upperCase", false);
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
  private ObjectProperty<Predicate<T>> validation = new SimpleObjectProperty<>(this, "validation");

  public final BooleanProperty clearableProperty() {
    return clearable;
  }

  @Override
  public void commitEdit(T newValue) {
    if (getValidation() != null && !getValidation().test(newValue)) {
      return;
    }
    super.commitEdit(newValue);
  }

  public ObjectProperty<StringConverter<T>> converterProperty() {
    return converter;
  }

  public StringConverter<T> getConverter() {
    return converterProperty().get();
  }

  public final Integer getMaxTextLength() {
    return maxTextLength.getValue();
  }

  public final String getPropertyName() {
    return propertyName.getValue();
  }

  public final Predicate<T> getValidation() {
    return validation.getValue();
  }

  public final IntegerProperty maxTextLengthProperty() {
    return maxTextLength;
  }

  public BooleanProperty propertyChanged() {
    return propertyChanged;
  }

  public void setChanged(Boolean change) {
    propertyChanged().set(change);
  }

  public void setConverter(StringConverter<T> value) {
    converterProperty().set(value);
  }

  public final void setMaxTextLength(Integer value) {
    maxTextLength.setValue(value);
  }

  public final void setPropertyName(String propertyName) {
    this.propertyName.setValue(propertyName);
  }

  public final void setUpperCase(boolean value) {
    upperCase.setValue(value);
  }

  public final void setValidation(Predicate<T> value) {
    validation.setValue(value);
  }

  public final BooleanProperty upperCaseProperty() {
    return upperCase;
  }

  public final StringProperty validCharacterProperty() {
    return validCharacter;
  }

  public final ObjectProperty<Predicate<T>> validationProperty() {
    return validation;
  }




}
