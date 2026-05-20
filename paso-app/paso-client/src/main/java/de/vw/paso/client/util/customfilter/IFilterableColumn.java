package de.vw.paso.client.util.customfilter;

import javafx.util.StringConverter;

public interface IFilterableColumn<T> {

  StringConverter<T> getConverter();
}
