package de.vw.paso.client.stueckliste.efs.views.summarised;

import javafx.scene.control.TableColumn;
import javafx.util.StringConverter;

import de.vw.paso.client.util.customfilter.IFilterableColumn;

public class SummarizedTableColumn<T, S> extends TableColumn<T, S> implements IFilterableColumn<S> {

    private StringConverter<S> converter;

    @Override
    public StringConverter<S> getConverter() {
        return converter;
    }

    public void setConverter(StringConverter<S> converter) {
        this.converter = converter;
    }
}
