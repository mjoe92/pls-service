package de.vw.paso.client.util.customfilter;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

import de.vw.paso.client.control.tablebase.filter.panel.CustomTableFilterValue;
import lombok.Getter;

@Getter
public class PredicateData {

    List<String> valueItems = new ArrayList<>();

    PredicateData(ObservableList<CustomTableFilterValue> selectionValues) {
        if (selectionValues != null) {

            selectionValues.forEach(selectionValue -> {
                valueItems.add(selectionValue.getUnformattedLabelText());
            });
        } else {
            valueItems = null;
        }
    }
}
