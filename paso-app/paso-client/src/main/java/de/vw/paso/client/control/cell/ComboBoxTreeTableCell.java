package de.vw.paso.client.control.cell;

import java.util.Collection;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import de.vw.paso.client.control.combobox.AutoCompletionTextFieldBinding;
import de.vw.paso.client.control.combobox.PasoCustomComboBox;
import de.vw.paso.client.control.combobox.SuggestionProvider;

public class ComboBoxTreeTableCell<S, T> extends AbstractTreeTableCell<S, T> {

    private final Collection<T> items;
    private final BooleanProperty comboBoxEditable;
    private final ObjectProperty<StringConverter<T>> popupItemConverter;

    private PasoCustomComboBox<T> comboBox;

    public ComboBoxTreeTableCell(Collection<T> items) {
        this.items = items;

        comboBoxEditable = new SimpleBooleanProperty(this, "comboBoxEditable");
        popupItemConverter = new SimpleObjectProperty<>();
    }

    public final BooleanProperty comboBoxEditableProperty() {
        return comboBoxEditable;
    }

    public final ObjectProperty<StringConverter<T>> popupItemConverterProperty() {
        return popupItemConverter;
    }

    public final void setPopupItemConverter(final StringConverter<T> value) {
        popupItemConverterProperty().set(value);
    }

    public Collection<T> getItems() {
        return items;
    }

    @Override
    public void startEdit() {
        if (!isEditable() || !getTreeTableView().isEditable() || !getTableColumn().isEditable()) {
            return;
        }

        if (comboBox == null) {
            comboBox = createComboBox();
        }

        super.startEdit();
        CellUtils.startEdit(this, comboBox);
    }

    private PasoCustomComboBox<T> createComboBox() {
        PasoCustomComboBox<T> newComboBox = CellUtils.createComboBox(this, items);

        if (comboBoxEditableProperty().get()) {
            bindAutoCompletion(newComboBox, getItems());
        }

        newComboBox.setPopupItemConverter(popupItemConverterProperty().get());

        return newComboBox;
    }

    private static <T> void bindAutoCompletion(TextField textField, Collection<T> possibleSuggestions) {
        new AutoCompletionTextFieldBinding<>(textField, SuggestionProvider.create(possibleSuggestions));
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        CellUtils.cancelEdit(this, getConverter());
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        CellUtils.updateItem(this, comboBox);
    }
}
