package de.vw.paso.client.control.combobox;

import java.util.Collection;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.StringConverter;

import de.vw.paso.client.control.autocomplete.AutoCompletePopup;
import de.vw.paso.client.control.autocomplete.AutoCompletePopup.SuggestionEvent;
import de.vw.paso.client.control.autocomplete.AutoCompletePopupSkin;
import de.vw.paso.client.control.textfield.PasoCustomTextField;
import org.apache.commons.lang3.StringUtils;

public class PasoCustomComboBox<T> extends PasoCustomTextField<T> {

    private static final Duration FADE_DURATION = Duration.millis(350);

    private final Collection<T> items;

    private AutoCompletePopup<T> popupItems;

    public PasoCustomComboBox(Collection<T> items) {
        this.items = items;

        initCombobox();
    }

    private void initCombobox() {
        getStyleClass().add("popup-field");

        setAutoCompletion(SuggestionProvider.create(items));
        getPopupItems().getSuggestions().addAll(items);

        createPopupButton();

        textProperty().addListener(listener -> hidePopup());
        focusedProperty().addListener(listener -> handleFocusEvent());
        setOnKeyReleased(this::handleKeyEvent);
        getAutoCompletionBinding().setOnAutoCompleted(r -> completeUserInput(r.getCompletion()));
        getPopupItems().setOnSuggestion(this::handleSuggestionEvent);
    }

    private void handleFocusEvent() {
        if (!isFocused()) {
            hidePopup();
        }
    }

    private void handleKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DOWN && !getAutoCompletionBinding().isPopupShowing()) {
            showPopup();
        } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
            hidePopup();
        } else if (keyEvent.getCode() == KeyCode.ENTER) {
            getAutoCompletionBinding().disablePopup();
        }
    }

    private void handleSuggestionEvent(SuggestionEvent<T> suggestionEvent) {
        completeUserInput(suggestionEvent.getSuggestion());

        hidePopup();
    }

    private void createPopupButton() {
        Region popupButton = new Region();

        popupButton.getStyleClass().addAll("graphic");

        StackPane popupButtonPane = new StackPane(popupButton);

        popupButtonPane.getStyleClass().addAll("popup-button");
        popupButtonPane.setOpacity(0.0);
        popupButtonPane.setCursor(Cursor.DEFAULT);
        popupButtonPane.setOnMouseReleased(e -> showPopup());

        FadeTransition fader = new FadeTransition(FADE_DURATION, popupButtonPane);

        fader.setCycleCount(1);
        fader.setFromValue(0.0);
        fader.setToValue(1.0);
        fader.play();

        rightProperty().set(popupButtonPane);
    }

    private AutoCompletePopup<T> getPopupItems() {
        if (popupItems == null) {
            popupItems = new AutoCompletePopup<>();

            popupItems.focusedProperty().addListener(observable -> {
                ReadOnlyBooleanProperty prop = (ReadOnlyBooleanProperty) observable;

                if (prop.get()) {
                    getAutoCompletionBinding().disablePopup();
                }
            });
        }

        return popupItems;
    }

    private void completeUserInput(T completion) {
        String newText = getConverter().toString(completion);

        setText(newText);
        setUserData(completion);
        positionCaret(newText.length());
    }

    private void showPopup() {
        getPopupItems().show(this);

        // select the current text.
        Platform.runLater(() -> {
            if (getText() == null) {
                return;
            }

            Skin<?> skin = getPopupItems().getSkin();
            if (skin instanceof AutoCompletePopupSkin<?> au) {
                ListView<?> li = (ListView<?>) au.getNode();
                selectAndScrollToListItem(li);
            }
        });
    }

    private void selectAndScrollToListItem(ListView<?> li) {
        if (li.getItems() == null || li.getItems().isEmpty()) {
            return;
        }

        ObservableList<?> items = li.getItems();
        for (int i = 0; i < items.size(); i++) {
            Object o = items.get(i);
            String startStr = o == null ? null : o.toString();
            if (StringUtils.startsWith(startStr, getText())) {
                li.getSelectionModel().select(i);
                li.scrollTo(i);
                break;
            }
        }
    }

    private void hidePopup() {
        getPopupItems().hide();
    }

    @Override
    public boolean isClearable() {
        return false;
    }

    @Override
    protected void createClearableTextField() {
        //default empty
    }

    private AutoCompletionComboBoxBinding<T> autoCompletionBinding = null;

    @Override
    protected void bindAutoCompletion() {
        if (getAutoCompletion() != null) {
            autoCompletionBinding = new AutoCompletionComboBoxBinding<>(this, SuggestionProvider.create(getItems()));
        } else if (autoCompletionBinding != null) {
            autoCompletionBinding.dispose();
        }
    }

    @Override
    protected AutoCompletionComboBoxBinding<T> getAutoCompletionBinding() {
        return autoCompletionBinding;
    }

    public Collection<T> getItems() {
        return items;
    }

    public void setPopupItemConverter(final StringConverter<T> converter) {
        getPopupItems().setConverter(converter);
    }
}
