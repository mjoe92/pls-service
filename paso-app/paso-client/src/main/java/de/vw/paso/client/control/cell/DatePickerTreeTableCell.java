package de.vw.paso.client.control.cell;

import java.util.Date;

import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.util.converter.DateStringConverter;

import de.vw.paso.utility.DateUtil;

public class DatePickerTreeTableCell<S> extends AbstractTreeTableCell<S, Date> {

    private DatePicker datePicker;

    public DatePickerTreeTableCell(final DateStringConverter converter) {
        super();

        this.getStyleClass().add("align-center");

        setConverter(converter);
    }

    @Override
    public void startEdit() {
        if (!isEditable() || !getTreeTableView().isEditable() || !getTableColumn().isEditable()) {
            return;
        }

        if (datePicker == null) {
            datePicker = CellUtils.createDatePicker(this, getConverter());

            datePicker.setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    this.requestFocus();
                    this.commitEdit(DateUtil.toDate(datePicker.getValue()));
                }
            });
        }
        super.startEdit();

        CellUtils.startEdit(this, datePicker);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        CellUtils.cancelEdit(this, getConverter());
    }

    @Override
    public void updateItem(final Date date, final boolean empty) {
        super.updateItem(date, empty);

        CellUtils.updateItem(this, getConverter(), datePicker);
    }

}
