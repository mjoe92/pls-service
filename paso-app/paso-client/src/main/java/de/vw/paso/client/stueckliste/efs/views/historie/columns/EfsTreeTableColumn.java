package de.vw.paso.client.stueckliste.efs.views.historie.columns;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TreeTableColumn;
import javafx.util.StringConverter;

import de.vw.paso.client.util.customfilter.IFilterableColumn;

public class EfsTreeTableColumn<S, T> extends TreeTableColumn<S, T> implements IFilterableColumn {

    private StringConverter<?> converter;

    private BooleanProperty compareCell = new SimpleBooleanProperty(this, "compareCell", true);

    public final boolean isCompareCell() {
        return compareCell.getValue();
    }

    public final void setCompareCell(boolean value) {
        compareCell.setValue(value);
    }

    public final BooleanProperty compareCellProperty() {
        return compareCell;
    }

    @Override
    public StringConverter<?> getConverter() {
        return converter;
    }

    public void setConverter(StringConverter<?> converter) {
        this.converter = converter;
    }
}
