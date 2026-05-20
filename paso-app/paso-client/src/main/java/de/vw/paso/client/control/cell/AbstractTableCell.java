package de.vw.paso.client.control.cell;

import java.util.Objects;
import java.util.function.Predicate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;

public abstract class AbstractTableCell<S, T> extends TableCell<S, T> {

  private final IntegerProperty maxTextLength = new SimpleIntegerProperty(this, "maxTextLength", -1);
  private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(this, "converter",
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
  private final ObjectProperty<Predicate<T>> validation = new SimpleObjectProperty<>(this, "validation");

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

  public final Predicate<T> getValidation() {
    return validation.getValue();
  }

  public void setConverter(StringConverter<T> value) {
    converterProperty().set(value);
  }

  public final void setMaxTextLength(Integer value) {
    maxTextLength.setValue(value);
  }

  public final void setValidation(Predicate<T> value) {
    validation.setValue(value);
  }
}
