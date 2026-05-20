package de.vw.paso.client.control.cell;

import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import de.vw.paso.client.control.textfield.PasoCustomTextField;

public class TextFieldTreeTableCell<S, T> extends AbstractTreeTableCell<S, T> {

    private TextField textField;

    public TextFieldTreeTableCell() {
        super();
        this.getStyleClass().add("text-field-tree-table-cell");
    }

    @Override
    public void startEdit() {
        if (!isEditable() || !getTreeTableView().isEditable() || !getTableColumn().isEditable()) {
            return;
        }
        if (textField == null) {
            textField = createTextField(getConverter());
        }
        super.startEdit();
        CellUtils.startEdit(this, textField);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        CellUtils.cancelEdit(this, getConverter());
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        CellUtils.updateItem(this, textField);
    }

    protected TextField createTextField(StringConverter<T> converter) {
        PasoCustomTextField<T> textField = CellUtils.createTextField(this);
        return textField;
    }
}
