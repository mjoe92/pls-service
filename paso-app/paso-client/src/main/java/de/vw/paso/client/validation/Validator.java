package de.vw.paso.client.validation;

import java.util.function.Predicate;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Validator<T> {

  private final BooleanBinding valid;
  private final StringProperty invalidMessage;

  public Validator(ObservableValue<T> observable, Predicate<T> predicate, String invalidMessage) {
    this.valid = Bindings.createBooleanBinding(() -> predicate.test(observable.getValue()), observable);
    this.invalidMessage = new SimpleStringProperty(invalidMessage);
  }

  public Validator(ObservableValue<T> observable, Predicate<T> predicate, StringProperty invalidMessage) {
    this.valid = Bindings.createBooleanBinding(() -> predicate.test(observable.getValue()), observable);
    this.invalidMessage = invalidMessage;
  }

  public final BooleanBinding validBinding() { // NO_UCD (use default)
    return valid;
  }

  private ReadOnlyStringProperty invalidMessageProperty() {
    return this.invalidMessage;
  }

  public final String getInvalidMessage() {
    return this.invalidMessageProperty().get();
  }

}
