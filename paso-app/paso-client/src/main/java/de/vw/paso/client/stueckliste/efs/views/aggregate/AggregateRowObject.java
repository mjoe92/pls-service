package de.vw.paso.client.stueckliste.efs.views.aggregate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class AggregateRowObject {

    private ObjectProperty<RowAction> selectedData = new SimpleObjectProperty<>(null);

    private EfsElementDTO wrappedElement;

    public AggregateRowObject(EfsElementDTO element) {
        wrappedElement = element;
    }

    public EfsElementDTO getElement() {
        return wrappedElement;
    }

    public ObjectProperty<RowAction> rowActionProperty() {
        return selectedData;
    }
}
