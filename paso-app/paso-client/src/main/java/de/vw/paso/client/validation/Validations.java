package de.vw.paso.client.validation;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Validations {

  private final ObservableList<Validator<?>> validators;
  private final BooleanProperty validation;
  private final ObjectProperty<Validator<?>> invalidValidator = new SimpleObjectProperty<>();
  private final StringProperty errorMessage = new SimpleStringProperty();

  private BooleanBinding validationBinding;

  public Validations() {
    this.validation = new SimpleBooleanProperty(false);
    this.validators = FXCollections.observableArrayList();

    this.validators.addListener((InvalidationListener) observable -> createBinding());

    this.errorMessage.bind(createErrorMessageBinding());
  }

  private void createBinding() {
    if (this.validationBinding != null) {
      validation.unbind();
    }
    Observable[] observables = new Observable[this.validators.size()];
    for (int i = 0; i < this.validators.size(); i++) {
      observables[i] = this.validators.get(i).validBinding();
    }
    this.validationBinding = createValidationBinding(observables);
    validation.bind(this.validationBinding);
  }

  private BooleanBinding createValidationBinding(Observable[] observables) {
    return Bindings.createBooleanBinding(() -> {
      for (Validator<?> validator : this.validators) {
        if (!validator.validBinding().get()) {
          invalidValidator.set(validator);
          return false;
        }
      }

      invalidValidator.set(null);
      return true;
    }, observables);
  }

  public void addValidator(Validator<?> validator) {
    this.validators.add(validator);
  }

  public final ReadOnlyBooleanProperty validProperty() {
    return validation;
  }

  public final boolean isValidation() {
    return this.validProperty().get();
  }

  private ReadOnlyObjectProperty<Validator<?>> invalidValidatorProperty() {
    return this.invalidValidator;
  }

  private Validator<?> getInvalidValidator() {
    return this.invalidValidatorProperty().get();
  }

  public final ReadOnlyStringProperty errorMessageProperty() {
    return this.errorMessage;
  }

  private StringBinding createErrorMessageBinding() {
    return Bindings.createStringBinding(
      () -> getInvalidValidator() == null ? null : getInvalidValidator().getInvalidMessage(),
      invalidValidatorProperty());
  }

}
