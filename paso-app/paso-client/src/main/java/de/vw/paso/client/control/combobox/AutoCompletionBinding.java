package de.vw.paso.client.control.combobox;

import java.util.Collection;
import java.util.UUID;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.util.Callback;
import javafx.util.StringConverter;

import de.vw.paso.client.control.autocomplete.AutoCompletePopup;
import de.vw.paso.client.control.autocomplete.AutoCompletePopupSkin;
import de.vw.paso.client.control.combobox.event.EventHandlerManager;
import de.vw.paso.client.control.textfield.TextFieldUtil;

/**
 * The AutoCompletionBinding is the abstract base class of all auto-completion bindings.
 * This class is the core logic for the auto-completion feature but highly customizable.
 *
 * <p>To use the autocompletion functionality, refer to the {@link TextFieldUtil} class.
 *
 * @param <T>
 *   Model-Type of the suggestions
 */
public abstract class AutoCompletionBinding<T> implements EventTarget {

  /***************************************************************************
   *                                                                         *
   * Private fields                                                          *
   *                                                                         *
   **************************************************************************/
  private final Node completionTarget;
  private final AutoCompletePopup<T> autoCompletionPopup;
  private final Object suggestionsTaskLock = new Object();

  private FetchSuggestionsTask suggestionsTask = null;
  private Callback<ISuggestionRequest, Collection<T>> suggestionProvider;
  private boolean ignoreInputChanges = false;
  private long delay = 20;

  /**
   * Creates a new AutoCompletionBinding
   *
   * @param completionTarget
   *   The target node to which auto-completion shall be added
   * @param suggestionProvider
   *   The strategy to retrieve suggestions
   * @param converter
   *   The converter to be used to convert suggestions to strings
   */
  protected AutoCompletionBinding(Node completionTarget, Callback<ISuggestionRequest, Collection<T>> suggestionProvider,
    StringConverter<T> converter) {

    this.completionTarget = completionTarget;
    this.suggestionProvider = suggestionProvider;
    this.autoCompletionPopup = new AutoCompletePopup<>();
    this.autoCompletionPopup.setConverter(converter);

    autoCompletionPopup.setOnSuggestion(sce -> {
      try {
        setIgnoreInputChanges(true);
        completeUserInput(sce.getSuggestion());
        fireAutoCompletion(sce.getSuggestion());
        hidePopup();
      } finally {
        // Ensure that ignore is always set back to false
        setIgnoreInputChanges(false);
      }
    });
  }

  /**
   * Set the current text the user has entered
   *
   * @param userText
   */
  public final void setUserInput(String userText) {
    if (!isIgnoreInputChanges()) {
      onUserInputChanged(userText);
    }
  }

  /**
   * Gets the target node for auto completion
   *
   * @return the target node for auto completion
   */
  public Node getCompletionTarget() {
    return completionTarget;
  }

  /**
   * Disposes the binding.
   */
  public abstract void dispose();

  /***************************************************************************
   *                                                                         *
   * Protected methods                                                       *
   *                                                                         *
   **************************************************************************/

  /**
   * Complete the current user-input with the provided completion.
   * Sub-classes have to provide a concrete implementation.
   *
   * @param completion
   */
  protected abstract void completeUserInput(T completion);

  /**
   * Show the auto completion popup
   */
  protected void showPopup() {
    autoCompletionPopup.show(completionTarget);
    selectFirstSuggestion(autoCompletionPopup);
  }

  /**
   * Hide the auto completion targets
   */
  protected void hidePopup() {
    autoCompletionPopup.hide();
  }

  protected void fireAutoCompletion(T completion) {
    Event.fireEvent(this, new AutoCompletionEvent<>(completion));
  }

  /***************************************************************************
   *                                                                         *
   * Private methods                                                         *
   *                                                                         *
   **************************************************************************/

  /**
   * Selects the first suggestion (if any), so the user can choose it
   * by pressing enter immediately.
   */
  private void selectFirstSuggestion(AutoCompletePopup<?> autoCompletionPopup) {
    Skin<?> skin = autoCompletionPopup.getSkin();
    if (skin instanceof AutoCompletePopupSkin<?> au) {
      ListView<?> li = (ListView<?>) au.getNode();
      if (li.getItems() != null && !li.getItems().isEmpty()) {
        li.getSelectionModel().select(0);
      }
    }
  }

  /**
   * Occurs when the user text has changed and the suggestions require an update
   *
   * @param userText
   */
  private void onUserInputChanged(final String userText) {
    synchronized (suggestionsTaskLock) {
      if (suggestionsTask != null && suggestionsTask.isRunning()) {
        // cancel the current running task
        suggestionsTask.cancel();
      }
      // create a new fetcher task
      suggestionsTask = new FetchSuggestionsTask(userText, delay);
      new Thread(suggestionsTask).start();
    }
  }

  /**
   * Shall changes to the user input be ignored?
   *
   * @return
   */
  private boolean isIgnoreInputChanges() {
    return ignoreInputChanges;
  }

  /**
   * If IgnoreInputChanges is set to true, all changes to the user input are
   * ignored. This is primary used to avoid self triggering while
   * auto completing.
   *
   * @param state
   */
  private void setIgnoreInputChanges(boolean state) {
    ignoreInputChanges = state;
  }

  /**
   * Represents a suggestion fetch request
   */
  public interface ISuggestionRequest {

    /**
     * Is this request canceled?
     *
     * @return {@code true} if the request is canceled, otherwise {@code false}
     */
    boolean isCancelled();

    /**
     * Get the user text to which suggestions shall be found
     *
     * @return {@link String} containing the user text
     */
    String getUserText();
  }

  /**
   * This task is responsible to fetch suggestions asynchronous
   * by using the current defined suggestionProvider
   */
  private class FetchSuggestionsTask extends Task<Void> implements ISuggestionRequest {

    private final String userText;
    private final long delay;

    public FetchSuggestionsTask(String userText, long delay) {
      this.userText = userText;
      this.delay = delay;
    }

    @Override
    protected Void call() throws Exception {
      Callback<ISuggestionRequest, Collection<T>> provider = suggestionProvider;
      if (provider != null) {
        long startTime = System.currentTimeMillis();
        long sleepTime = startTime + delay - System.currentTimeMillis();
        if (sleepTime > 0 && !isCancelled()) {
          Thread.sleep(sleepTime);
        }
        if (!isCancelled()) {
          final Collection<T> fetchedSuggestions = provider.call(this);
          Platform.runLater(() -> {
            // check whether completionTarget is still valid
            boolean validNode = completionTarget.getScene() != null && completionTarget.getScene().getWindow() != null;
            if (fetchedSuggestions != null && !fetchedSuggestions.isEmpty() && validNode) {
              autoCompletionPopup.getSuggestions().setAll(fetchedSuggestions);
              showPopup();
            } else {
              // No suggestions found, so hide the popup
              hidePopup();
            }
          });
        }
      } else {
        // No suggestion provider
        hidePopup();
      }
      return null;
    }

    @Override
    public String getUserText() {
      return userText;
    }
  }

  /**
   * Represents an Event which is fired after an auto completion.
   */
  public static class AutoCompletionEvent<TE> extends Event {

    /**
     * The event type that should be listened to by people interested in
     * knowing when an auto completion has been performed.
     */
    public static final EventType<AutoCompletionEvent<?>> AUTO_COMPLETED = new EventType<>(
      "AUTO_COMPLETED" + UUID.randomUUID()); //$NON-NLS-1$

    private final TE completion;

    /**
     * Creates a new event that can subsequently be fired.
     */
    public AutoCompletionEvent(TE completion) {
      super(AUTO_COMPLETED);
      this.completion = completion;
    }

    /**
     * Returns the chosen completion.
     */
    public TE getCompletion() {
      return completion;
    }
  }

  private ObjectProperty<EventHandler<AutoCompletionEvent<T>>> onAutoCompleted;

  /**
   * Set a event handler which is invoked after an auto completion.
   *
   * @param value
   */
  public final void setOnAutoCompleted(EventHandler<AutoCompletionEvent<T>> value) {
    onAutoCompletedProperty().set(value);
  }

  public final EventHandler<AutoCompletionEvent<T>> getOnAutoCompleted() {
    return onAutoCompleted == null ? null : onAutoCompleted.get();
  }

  public final ObjectProperty<EventHandler<AutoCompletionEvent<T>>> onAutoCompletedProperty() {
    if (onAutoCompleted == null) {
      onAutoCompleted = new ObjectPropertyBase<>() {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        protected void invalidated() {
          eventHandlerManager.setEventHandler(AutoCompletionEvent.AUTO_COMPLETED,
            (EventHandler<AutoCompletionEvent>) (Object) get());
        }

        @Override
        public Object getBean() {
          return AutoCompletionBinding.this;
        }

        @Override
        public String getName() {
          return "onAutoCompleted"; //$NON-NLS-1$
        }
      };
    }
    return onAutoCompleted;
  }

  /***************************************************************************
   *                                                                         *
   * EventTarget Implementation                                              *
   *                                                                         *
   **************************************************************************/

  final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);

  /**
   * Registers an event handler to this EventTarget. The handler is called when the
   * menu item receives an {@code Event} of the specified type during the bubbling
   * phase of event delivery.
   *
   * @param <E>
   *   the specific event class of the handler
   * @param eventType
   *   the type of the events to receive by the handler
   * @param eventHandler
   *   the handler to register
   * @throws NullPointerException
   *   if the event type or handler is null
   */
  public <E extends Event> void addEventHandler(EventType<E> eventType, EventHandler<? super E> eventHandler) {
    eventHandlerManager.addEventHandler(eventType, eventHandler);
  }

  /**
   * Unregisters a previously registered event handler from this EventTarget. One
   * handler might have been registered for different event types, so the
   * caller needs to specify the particular event type from which to
   * unregister the handler.
   *
   * @param <E>
   *   the specific event class of the handler
   * @param eventType
   *   the event type from which to unregister
   * @param eventHandler
   *   the handler to unregister
   * @throws NullPointerException
   *   if the event type or handler is null
   */
  public <E extends Event> void removeEventHandler(EventType<E> eventType, EventHandler<? super E> eventHandler) {
    eventHandlerManager.removeEventHandler(eventType, eventHandler);
  }

  /** {@inheritDoc} */
  @Override
  public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
    return tail.prepend(eventHandlerManager);
  }

}
