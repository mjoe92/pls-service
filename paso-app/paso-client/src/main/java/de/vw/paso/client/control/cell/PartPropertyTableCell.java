package de.vw.paso.client.control.cell;

import javafx.scene.control.Tooltip;

import de.vw.paso.client.base.I18N;
import de.vw.paso.partlist.domain.PartProperty;

public class PartPropertyTableCell extends ReadOnlyTableCell<PartProperty, String> {

    public PartPropertyTableCell(Class<?> dataType) {
        super(dataType);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setTooltip(null);

        CellUtils.updateItem(this);

        if (item == null) {
            return;
        }

        //todo: improve cell update -> solve with enum instead substrings
        PartProperty partProperty = getTableView().getItems().get(getIndex());
        if (partProperty.getPropertyValueDescription() != null && partProperty.getPropertyValue().length() > 1) {
            String key = partProperty.getPropertyValue().substring(0, 1).toUpperCase();

            setTooltip(new Tooltip(I18N.getString(partProperty.getPropertyNameKey() + "Descr" + key)));
        }
    }
}