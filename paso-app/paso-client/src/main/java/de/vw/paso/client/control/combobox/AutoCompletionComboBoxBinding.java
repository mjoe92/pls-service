package de.vw.paso.client.control.combobox;

import java.util.Collection;
import java.util.Objects;

import javafx.util.Callback;
import javafx.util.StringConverter;

public class AutoCompletionComboBoxBinding<T> extends AutoCompletionTextFieldBinding<T> {

  private static <T> StringConverter<T> defaultStringConverter() {
    return new StringConverter<>() {
      @Override
      public String toString(final T object) {
        return Objects.toString(object, "");
      }

      @Override
      public T fromString(final String string) {
        return (T) string;
      }
    };
  }

  private boolean isPopupShowing = false;
  private boolean isPopupEnabled = true;

  public AutoCompletionComboBoxBinding(final PasoCustomComboBox<T> comboBox,
    final Callback<ISuggestionRequest, Collection<T>> suggestionProvider) {
    this(comboBox, suggestionProvider, AutoCompletionComboBoxBinding.defaultStringConverter());
  }

  public AutoCompletionComboBoxBinding(final PasoCustomComboBox<T> comboBox,
    final Callback<ISuggestionRequest, Collection<T>> suggestionProvider, final StringConverter<T> converter) {
    super(comboBox, suggestionProvider, converter);
  }

  @Override
  protected void showPopup() {
    if (isPopupEnabled) {
      super.showPopup();

      isPopupShowing = true;
    } else {
      isPopupEnabled = true;
    }
  }

  @Override
  protected void hidePopup() {
    super.hidePopup();

    isPopupShowing = false;
  }

  protected void disablePopup() {
    isPopupEnabled = false;
  }

  public boolean isPopupShowing() {
    return isPopupShowing;
  }

}
