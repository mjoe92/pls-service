package de.vw.paso.client.control.tablebase.filter.panel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.customfilter.CustomFilterUtil;
import lombok.Getter;

@Getter
public class CustomTableFilterValue extends HBox {

    private final String labelText;

    private final String unformattedLabelText;
    private LocalDate dateValue = null;
    private Double numberValue;
    private final BooleanProperty isSelected = new SimpleBooleanProperty(true);
    private final Class originClass;
    private final long dateAsLong;

    CustomTableFilterValue(String text, String labelTxt, Class classOfOrigin, long date,
            ObservableList<String> treeTableValuesStringList) {

        if (text.trim().isEmpty() || classOfOrigin == null) {
            String emptyString = CustomFilterUtil.getLocalizedEmptyString();
            labelText = emptyString;
            unformattedLabelText = emptyString;
            treeTableValuesStringList.addAll(emptyString, "");
        } else if (classOfOrigin.equals(LocalDate.class) || classOfOrigin.equals(Date.class)) {
            LocalDate localdate = LocalDate.ofEpochDay(date);
            unformattedLabelText = localdate.toString();
            dateValue = localdate;
            labelText = localdate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
            treeTableValuesStringList.add(labelText);
        } else {
            if (classOfOrigin.equals(Float.class) || classOfOrigin.equals(Double.class) || classOfOrigin.equals(
                    Integer.class) || classOfOrigin.equals(Long.class)) {
                labelText = text.replace(".", ",");
                unformattedLabelText = text;
                numberValue = Double.parseDouble(text);
            } else {
                if (labelTxt != null) {
                    this.labelText = labelTxt;
                } else {
                    this.labelText = text;
                }
                unformattedLabelText = text;
            }
            treeTableValuesStringList.add(text);
        }

        final CheckBox checkBox = new CheckBox();
        final Label label = new Label();
        label.setText(labelText);
        originClass = getaClass(classOfOrigin);
        dateAsLong = date;

        checkBox.selectedProperty().bindBidirectional(selectedProperty());
        checkBox.selectedProperty().addListener(
                (observable, oldValue, newValue) -> EventBus.getInstance().post(new FilterSelectionChangeEvent()));
        checkBox.setSelected(true);
        getChildren().addAll(checkBox, label);

        setOnMouseReleased(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                isSelected.set(!isSelected.get());
            }
        });
    }

    private Class getaClass(Class classOfOrigin) {
        final Class originClass;
        if (classOfOrigin == null || labelText.equals(CustomFilterUtil.getLocalizedEmptyString())) {
            originClass = String.class;
        } else {
            originClass = classOfOrigin;
        }
        return originClass;
    }

    public String getLabelText() {
        return labelText;
    }

    public String getUnformattedLabelText() {
        return unformattedLabelText;
    }

    public BooleanProperty selectedProperty() {
        return isSelected;
    }

    public Class getOriginClass() {
        return originClass;
    }

    public long getDateAsLong() {
        return dateAsLong;
    }
}
