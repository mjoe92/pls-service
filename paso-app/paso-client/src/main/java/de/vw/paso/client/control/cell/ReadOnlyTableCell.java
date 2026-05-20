package de.vw.paso.client.control.cell;

import java.util.Date;

import javafx.util.StringConverter;
import javafx.util.converter.DateStringConverter;

import de.vw.paso.client.stueckliste.efs.control.EfsCellUtil;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import de.vw.paso.client.util.converter.LongStringConverter;
import de.vw.paso.client.util.converter.NumberStringConverter;

public class ReadOnlyTableCell<S, T> extends AbstractTableCell<S, T> {

    private boolean grayOutZero;
    private boolean isNormalized;

    public ReadOnlyTableCell(Class<?> dataType) {
        if (dataType != null) {
            if (Integer.class.isAssignableFrom(dataType) || Double.class.isAssignableFrom(dataType)
                    || Long.class.isAssignableFrom(dataType)) {
                this.getStyleClass().add("align-center-right");
            } else if (Date.class.isAssignableFrom(dataType)) {
                this.getStyleClass().add("align-center");
            }
        }
    }

    public ReadOnlyTableCell(final StringConverter<T> converter, final boolean grayOutZero,
            final boolean isNormalized) {
        super();

        this.grayOutZero = grayOutZero;
        this.isNormalized = isNormalized;

        if ((converter instanceof IntegerStringConverter) || (converter instanceof DoubleStringConverter)
                || (converter instanceof LongStringConverter)) {
            this.getStyleClass().add("align-center-right");
        } else if (converter instanceof DateStringConverter) {
            this.getStyleClass().add("align-center");
        }

        setConverter(converter);
    }

    @Override
    public void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);

        CellUtils.updateItem(this);

        if (getConverter() != null && NumberStringConverter.class.isAssignableFrom(getConverter().getClass())) {
            getStyleClass().remove(NumberFieldTreeTableCell.ZERO_WEIGHT_CLASS);

            final NumberStringConverter conv = (NumberStringConverter) getConverter();

            if (grayOutZero && conv.isZero((Number) item)) {
                getStyleClass().add(NumberFieldTreeTableCell.ZERO_WEIGHT_CLASS);
            }
        }

        if (isNormalized) {
            EfsCellUtil.normalizeNumberTextField(this);
        }
    }

}
