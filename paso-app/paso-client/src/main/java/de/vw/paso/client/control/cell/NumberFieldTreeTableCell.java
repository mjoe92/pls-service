package de.vw.paso.client.control.cell;

import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import de.vw.paso.client.stueckliste.efs.control.EfsCellUtil;
import de.vw.paso.client.util.converter.NumberStringConverter;

public class NumberFieldTreeTableCell<S, T extends Number> extends TextFieldTreeTableCell<S, T> {

    private static final String NUMBER_FIELD_CELL_CLASS = "align-center-right";
    public static final String ZERO_WEIGHT_CLASS = "tree-table-cell-empty-weight";

    private boolean grayOutZero;
    private boolean isNormalized;

    public NumberFieldTreeTableCell(final NumberStringConverter<T> converter) {
        this(converter, false);
    }

    public NumberFieldTreeTableCell(final NumberStringConverter<T> converter, final boolean grayOutZero) {
        this(converter, grayOutZero, false);
    }

    public NumberFieldTreeTableCell(final NumberStringConverter<T> converter, final boolean grayOutZero,
            final boolean isNormalized) {
        super();

        this.grayOutZero = grayOutZero;
        this.isNormalized = isNormalized;
        this.getStyleClass().add(NUMBER_FIELD_CELL_CLASS);

        setConverter(converter);
    }

    @Override
    protected TextField createTextField(final StringConverter<T> converter) {
        return CellUtils.createNumberField(this);
    }

    @Override
    public void startEdit() {
        if (isEditable()) {
            getConverter().setGroupingUsed(false);

            super.startEdit();
        }
    }

    @Override
    public void commitEdit(final T newValue) {
        getConverter().setGroupingUsed(true);

        super.commitEdit(newValue);
    }

    @Override
    public void cancelEdit() {
        getConverter().setGroupingUsed(true);

        super.cancelEdit();

        if (isNormalized) {
            EfsCellUtil.normalizeNumberTextField(this);
        }
    }

    @Override
    public NumberStringConverter<T> getConverter() {
        return ((NumberStringConverter<T>) super.getConverter());
    }

    @Override
    public void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);

        getStyleClass().remove(ZERO_WEIGHT_CLASS);

        if (grayOutZero && getConverter().isZero(item)) {
            getStyleClass().add(ZERO_WEIGHT_CLASS);
        }

        if (isNormalized) {
            EfsCellUtil.normalizeNumberTextField(this);
        }
    }

}
