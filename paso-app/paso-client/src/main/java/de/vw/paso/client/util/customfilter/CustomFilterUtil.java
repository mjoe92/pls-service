package de.vw.paso.client.util.customfilter;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.CogCoordinates;
import de.vw.paso.client.control.tablebase.filter.panel.CustomTableFilterValue;
import de.vw.paso.client.model.tree.AbstractFlatTreeItem;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.PasoPredicate;
import de.vw.paso.utility.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CustomFilterUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CustomFilterUtil.class);

    private static final String PARSE_EXCEPTION_MESSAGE = "Could not parse date";

    private CustomFilterUtil() {
        throw new IllegalArgumentException("Util class");
    }

    public static boolean allItemsInScopeSelected(ObservableList<CustomTableFilterValue> visibleItems) {
        return visibleItems.stream().allMatch(item -> item.selectedProperty().get());
    }

    public static boolean allItemsSelected(ObservableList<CustomTableFilterValue> backingList,
            ObservableList<CustomTableFilterValue> visibleItems) {
        if (backingList == null || visibleItems == null) {
            return false;
        }

        for (CustomTableFilterValue item : visibleItems) {
            if (!item.selectedProperty().get()) {
                return false;
            }
        }

        if (backingList.size() == visibleItems.size()) {
            return true;
        }

        for (CustomTableFilterValue item : backingList) {
            if (!item.selectedProperty().get()) {
                return false;
            }
        }

        return true;
    }

    public static boolean noSelectedItems(final ObservableList<CustomTableFilterValue> selectedItems) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            return true;
        }

        return selectedItems.stream().noneMatch(item -> item.selectedProperty().get());
    }

    public static <S> PasoPredicate<S> createPredicate(TableColumnBase<S, ?> column,
            ObservableList<CustomTableFilterValue> valueItems,
            Map<TableColumnBase<S, ?>, PredicateData> columnPredicateDataMap) {

        if (columnPredicateDataMap.get(column) != null) {
            columnPredicateDataMap.remove(column);
        }

        ObservableList<CustomTableFilterValue> selectedItems = FXCollections.observableArrayList();
        if (valueItems != null && !valueItems.isEmpty()) {
            for (CustomTableFilterValue item : valueItems) {
                if (item.selectedProperty().get()) {
                    selectedItems.add(item);
                }
            }
        }

        columnPredicateDataMap.put(column, new PredicateData(selectedItems));
        if (valueItems == null || valueItems.isEmpty()) {
            return value -> true;
        }

        return value -> {
            if (valueItems.isEmpty()) {
                return false;
            }

            Class<?> originclass = valueItems.size() == 1 ? valueItems.getFirst().getOriginClass() :
                    CustomFilterSortUtil.determineActualOriginClass(valueItems.getFirst(), valueItems.get(1));
            if (originclass.equals(String.class)) {
                return isInStringBasedFilter(valueItems, column, value);
            }

            if (originclass.equals(Double.class) || originclass.equals(Float.class) || originclass.equals(Long.class)
                    || originclass.equals(Integer.class) || originclass.equals(Boolean.class) || originclass.isEnum()) {
                return isInObjectBasedFilter(valueItems, column, value);
            }

            if (originclass.equals(Date.class) || originclass.equals(LocalDate.class)) {
                return isInDateBasedFilter(valueItems, column, value);
            }

            if (originclass.equals(CogCoordinates.class)) {
                return isInCogFilter(valueItems, column, value);
            }

            return false;
        };
    }

    public static <S> void setGraphicForColumn(TableColumnBase<S, ?> column, String columnName) {
        Label label = new Label(columnName);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(label);
        stackPane.setPrefWidth(column.getPrefWidth());
        stackPane.setPrefHeight(24);

        column.setGraphic(stackPane);
    }

    public static String getLocalizedEmptyString() {
        return I18N.getString("empty");
    }

    public static <S> PasoPredicate<S> createAdvancedFilterPredicate(String templateString, String textFieldContent) {
        return value -> {
            if (!(value instanceof CustomTableFilterValue filterValue)) {
                return false;
            }

            if (templateString.equals(FilterStrings.getLocalizedContainsString())) {
                return isStringContainedInFilterValues(textFieldContent, String.class, filterValue, textFieldContent);

            }

            return !filterValue.getLabelText().toLowerCase().contains(textFieldContent.toLowerCase());
        };
    }

    public static <S> PasoPredicate<S> createAdvancedFilterPredicate(String templateString, String firstTextfieldString,
            String secondTextfieldString, Class<?> originClass, ObservableList<CustomTableFilterValue> filterItems) {
        return value -> {
            if (!(value instanceof CustomTableFilterValue filterValueToExamine)) {
                return false;
            }

            if (templateString.equals(FilterStrings.getLocalizedContainsString())) {
                return isStringContainedInFilterValues(firstTextfieldString, originClass, filterValueToExamine,
                        firstTextfieldString);
            }

            if (templateString.equals(FilterStrings.getLocalizedDoesNotContainString())) {
                return isStringNotContainedInFilterValues(firstTextfieldString, filterValueToExamine);
            }

            if (templateString.equals(FilterStrings.getLocalizedEqualsString()) && isEqualToFilterValue(
                    firstTextfieldString, filterValueToExamine)) {
                return true;
            }

            if (templateString.equals(FilterStrings.getLocalizedWithinRangeString())) {
                return isWithinDefinedRange(firstTextfieldString, secondTextfieldString, originClass,
                        filterValueToExamine);

            }

            if (templateString.equals(FilterStrings.getLocalizedOutsideOfRangeString())) {
                return isOutsideOfDefinedRange(firstTextfieldString, secondTextfieldString, originClass,
                        filterValueToExamine);
            }

            if (templateString.equals(FilterStrings.getLocalizedTopString())
                    && !filterValueToExamine.getUnformattedLabelText()
                    .equals(CustomFilterUtil.getLocalizedEmptyString())) {

                String replaced = replaceFromCommaToDot(firstTextfieldString);
                int numberOfElementsToBeShown = filterItems.size() - Integer.parseInt(replaced);
                return filterItems.contains(filterValueToExamine)
                        && filterItems.indexOf(filterValueToExamine) >= numberOfElementsToBeShown;
            }

            if (templateString.equals(FilterStrings.getLocalizedInYearString())) {
                return isWithinDefinedYear(firstTextfieldString, filterValueToExamine);
            }

            return false;
        };
    }

    public static FilterPanelPredicateData createFilterPanelPredicate(VBox searchArea, HBox searchAreaTop,
            HBox searchAreaBottom) {
        String ruleString = null;
        for (Node node : searchArea.getChildren()) {
            if (node instanceof ComboBox<?> comboBox) {
                ruleString = comboBox.getValue().toString().trim();
            }
        }

        String topSearchAreaString = searchAreaTop == null ? null : getSubSearchAreaString(searchAreaTop);
        String bottomSearchAreaString = getSubSearchAreaString(searchAreaBottom);

        return new FilterPanelPredicateData(ruleString, topSearchAreaString, bottomSearchAreaString);
    }

    private static <S> boolean isInCogFilter(ObservableList<CustomTableFilterValue> valueItems,
            TableColumnBase<S, ?> column, S value) {
        for (CustomTableFilterValue customTableFilterValue : valueItems) {
            String filterValueLabelText = customTableFilterValue.getUnformattedLabelText();

            ObservableValue<?> cellObservableValue = column.getCellObservableValue(value);
            if (cellObservableValue != null && cellObservableValue.getValue() != null
                    && customTableFilterValue.selectedProperty().get()) {

                CogCoordinates cellValue = (CogCoordinates) cellObservableValue.getValue();

                if (cellValue.toString().equals(filterValueLabelText) || (cellValue.isEmpty()
                        && filterValueLabelText.equals(getLocalizedEmptyString()))) {
                    return true;
                }

                continue;
            }

            Boolean testItemSizeOfFilteredChildren = isTestedTreeItemSelectedEmptyAbstractFlatTreeItem(value,
                    customTableFilterValue, filterValueLabelText, cellObservableValue);
            if (testItemSizeOfFilteredChildren != null) {
                return testItemSizeOfFilteredChildren;
            }
        }

        return false;
    }

    private static <S> boolean isInDateBasedFilter(ObservableList<CustomTableFilterValue> valueItems,
            TableColumnBase<S, ?> column, S value) {
        for (CustomTableFilterValue customTableFilterValue : valueItems) {
            String filterValueLabelText = customTableFilterValue.getUnformattedLabelText();
            ObservableValue<?> cellObservableValue = column.getCellObservableValue(value);

            if (cellObservableValue != null && cellObservableValue.getValue() != null
                    && customTableFilterValue.selectedProperty().get()) {

                String cellValueString = cellObservableValue.getValue().toString();
                if (cellValueString.equals(filterValueLabelText) || (cellValueString.isEmpty()
                        && filterValueLabelText.equals(getLocalizedEmptyString())) || isValidDateInScope(column, value,
                        customTableFilterValue)) {
                    return true;
                }

                continue;
            }

            Boolean testItemSizeOfFilteredChildren = isTestedTreeItemSelectedEmptyAbstractFlatTreeItem(value,
                    customTableFilterValue, filterValueLabelText, cellObservableValue);
            if (testItemSizeOfFilteredChildren != null) {
                return testItemSizeOfFilteredChildren;
            }
        }

        return false;
    }

    private static <S> Boolean isTestedTreeItemSelectedEmptyAbstractFlatTreeItem(S value,
            CustomTableFilterValue customTableFilterValue, String filterValueLabelText,
            ObservableValue<?> cellObservableValue) {
        if (customTableFilterValue.selectedProperty().get() && (cellObservableValue == null
                || cellObservableValue.getValue() == null)
                && getLocalizedEmptyString().compareToIgnoreCase(filterValueLabelText) == 0) {

            if (value instanceof AbstractFlatTreeItem<?> flatTreeItem) {
                int testItemSizeOfFilteredChildren = flatTreeItem.getFilteredChildren().size();
                return (flatTreeItem.getSourceChildren().size() == testItemSizeOfFilteredChildren
                        || testItemSizeOfFilteredChildren != 0) && flatTreeItem.getParent().getValue() != null;
            }

            return true;
        }

        return null;
    }

    private static <S> boolean isInStringBasedFilter(ObservableList<CustomTableFilterValue> valueItems,
            TableColumnBase<S, ?> column, S v) {
        for (CustomTableFilterValue customTableFilterValue : valueItems) {
            String filterValueLabelText = customTableFilterValue.getUnformattedLabelText();

            ObservableValue<?> cellObservableValue = column.getCellObservableValue(v);
            if (cellObservableValue != null && cellObservableValue.getValue() != null
                    && customTableFilterValue.selectedProperty().get()) {

                String cellValueString = (String) cellObservableValue.getValue();

                if (cellValueString.equals(filterValueLabelText) || (cellValueString.isEmpty()
                        && filterValueLabelText.equals(getLocalizedEmptyString()))) {

                    return true;
                }

                continue;
            }

            Boolean testItemSizeOfFilteredChildren = isTestedTreeItemSelectedEmptyAbstractFlatTreeItem(v,
                    customTableFilterValue, filterValueLabelText, cellObservableValue);
            if (testItemSizeOfFilteredChildren != null) {
                return testItemSizeOfFilteredChildren;
            }
        }

        return false;
    }

    private static <S> boolean isInObjectBasedFilter(ObservableList<CustomTableFilterValue> valueItems,
            TableColumnBase<S, ?> column, S value) {
        ObservableValue<?> observableValue = column.getCellObservableValue(value);
        for (CustomTableFilterValue filterValue : valueItems) {
            String filterValueLabelText = filterValue.getUnformattedLabelText();
            if (observableValue != null && observableValue.getValue() != null && filterValue.selectedProperty().get()) {

                String cellValueString = observableValue.getValue().toString();

                if (cellValueString.equals(filterValueLabelText) || (cellValueString.isEmpty()
                        && filterValueLabelText.equals(getLocalizedEmptyString()))) {

                    return true;
                }

                continue;
            }

            Boolean testItemSizeOfFilteredChildren = isTestedTreeItemSelectedEmptyAbstractFlatTreeItem(value,
                    filterValue, filterValueLabelText, observableValue);
            if (testItemSizeOfFilteredChildren != null) {
                return testItemSizeOfFilteredChildren;
            }
        }

        return false;
    }

    private static <S> boolean isValidDateInScope(TableColumnBase<S, ?> column, S value,
            CustomTableFilterValue customTableFilterValue) {
        Object cellContentValue = column.getCellObservableValue(value).getValue();
        return customTableFilterValue.getOriginClass().equals(LocalDate.class)
                || customTableFilterValue.getOriginClass().equals(Date.class) && (
                cellContentValue instanceof Date dateValue && LocalDate.parse(
                                customTableFilterValue.getUnformattedLabelText())
                        .equals((DateToLocalDateConverter.convertToLocalDate(dateValue))));
    }

    private static boolean isEqualToFilterValue(String firstTextfieldString, CustomTableFilterValue v) {
        try {
            String replaced = replaceFromCommaToDot(firstTextfieldString);
            Double parsedFirstTextField = Double.parseDouble(replaced);
            Double parsedObjectLabel = v.getNumberValue();

            return parsedFirstTextField.equals(parsedObjectLabel);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isStringNotContainedInFilterValues(String firstTextfieldString, CustomTableFilterValue v) {
        String[] splitStrings = firstTextfieldString.split("\\*");
        for (String subString : splitStrings) {
            if (v.getLabelText().toLowerCase().contains(subString.trim().toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    private static boolean isWithinDefinedRange(String firstTextfieldString, String secondTextfieldString,
            Class<?> originClass, CustomTableFilterValue filterValue) {
        if (firstTextfieldString.isEmpty() && secondTextfieldString.isEmpty()) {
            return true;
        }

        if (firstTextfieldString.trim().isEmpty() && !secondTextfieldString.trim().isEmpty()) {
            if (originClass.equals(LocalDate.class)
                    || originClass.equals(Date.class) && !filterValue.getUnformattedLabelText()
                    .equals(CustomFilterUtil.getLocalizedEmptyString())) {
                try {
                    LocalDate filterValueDate = filterValue.getDateValue();
                    LocalDate enteredDate = DateToLocalDateConverter.convertToLocalDate(
                            DateUtil.parseDefaultString(secondTextfieldString.trim()));

                    return enteredDate != null && (enteredDate.isAfter(filterValueDate) || enteredDate.isEqual(
                            filterValueDate));
                } catch (ParseException e) {
                    LOG.warn(PARSE_EXCEPTION_MESSAGE, e);

                    return true;
                }
            }

            String replaced = replaceFromCommaToDot(secondTextfieldString);
            double parsedTextField = Double.parseDouble(replaced);
            double parsedObjectLabel = filterValue.getNumberValue();

            return parsedTextField >= parsedObjectLabel;
        }

        if (!firstTextfieldString.isEmpty() && secondTextfieldString.isEmpty()) {
            if (originClass.equals(LocalDate.class)
                    || originClass.equals(Date.class) && !filterValue.getUnformattedLabelText()
                    .equals(CustomFilterUtil.getLocalizedEmptyString())) {

                try {
                    LocalDate filterValueDate = filterValue.getDateValue();
                    LocalDate enteredDate = DateToLocalDateConverter.convertToLocalDate(
                            DateUtil.parseDefaultString(firstTextfieldString.trim()));

                    return enteredDate != null && (enteredDate.isBefore(filterValueDate) || enteredDate.isEqual(
                            filterValueDate));
                } catch (ParseException e) {
                    LOG.warn(PARSE_EXCEPTION_MESSAGE, e);

                    return true;
                }
            }

            String replaced = replaceFromCommaToDot(firstTextfieldString);
            double parsedTextField = Double.parseDouble(replaced);
            double parsedObjectLabel = filterValue.getNumberValue();

            return parsedTextField <= parsedObjectLabel;
        }

        if (originClass.equals(LocalDate.class)
                || originClass.equals(Date.class) && !filterValue.getUnformattedLabelText()
                .equals(CustomFilterUtil.getLocalizedEmptyString())) {
            try {
                LocalDate filterValueDate = filterValue.getDateValue();
                LocalDate firstEnteredDate = DateToLocalDateConverter.convertToLocalDate(
                        DateUtil.parseDefaultString(firstTextfieldString.trim()));
                LocalDate secondEnteredDate = DateToLocalDateConverter.convertToLocalDate(
                        DateUtil.parseDefaultString(secondTextfieldString.trim()));

                return firstEnteredDate != null && (
                        (firstEnteredDate.isBefore(filterValueDate) || firstEnteredDate.isEqual(filterValueDate)) && (
                                secondEnteredDate.isAfter(filterValueDate) || secondEnteredDate.isEqual(
                                        filterValueDate)));
            } catch (ParseException e) {
                LOG.warn(PARSE_EXCEPTION_MESSAGE, e);

                return true;
            }
        }

        String replaced = replaceFromCommaToDot(firstTextfieldString);
        double parsedFirstTextField = Double.parseDouble(replaced);

        replaced = replaceFromCommaToDot(secondTextfieldString);
        double parsedSecondTextField = Double.parseDouble(replaced);
        double parsedObjectLabel = filterValue.getNumberValue();

        return parsedFirstTextField <= parsedObjectLabel && parsedSecondTextField >= parsedObjectLabel;
    }

    private static boolean isWithinDefinedYear(String firstTextfieldString, CustomTableFilterValue v) {
        try {
            int valueEntryYear = DateToLocalDateConverter.convertToLocalDate(
                    DateUtil.parseDefaultString(v.getLabelText())).getYear();
            int firstEnteredDateYear = Integer.parseInt(firstTextfieldString.trim());

            if (valueEntryYear == firstEnteredDateYear) {
                return true;
            }
        } catch (ParseException e) {
            LOG.warn(PARSE_EXCEPTION_MESSAGE, e);
        }

        return false;
    }

    private static boolean isOutsideOfDefinedRange(String firstTextfieldString, String secondTextfieldString,
            Class<?> originClass, CustomTableFilterValue filterValue) {
        if (firstTextfieldString.isEmpty() && secondTextfieldString.isEmpty()) {
            return true;
        }

        if (!firstTextfieldString.isEmpty() && secondTextfieldString.isEmpty()) {
            if (originClass.equals(LocalDate.class)
                    || originClass.equals(Date.class) && !filterValue.getUnformattedLabelText()
                    .equals(CustomFilterUtil.getLocalizedEmptyString())) {

                try {
                    LocalDate filterValueDate = filterValue.getDateValue();
                    LocalDate enteredDate = DateToLocalDateConverter.convertToLocalDate(
                            DateUtil.parseDefaultString(firstTextfieldString.trim()));

                    return enteredDate != null && enteredDate.isBefore(filterValueDate);
                } catch (ParseException e) {
                    LOG.warn(PARSE_EXCEPTION_MESSAGE, e);
                }
            }

            String replaced = replaceFromCommaToDot(firstTextfieldString);
            double parsedTextField = Double.parseDouble(replaced);
            double parsedObjectLabel = filterValue.getNumberValue();

            return parsedTextField > parsedObjectLabel;
        }

        if (firstTextfieldString.isEmpty()) {
            if (originClass.equals(LocalDate.class)
                    || originClass.equals(Date.class) && !filterValue.getUnformattedLabelText()
                    .equals(CustomFilterUtil.getLocalizedEmptyString())) {
                try {
                    LocalDate filterValueDate = filterValue.getDateValue();
                    LocalDate enteredDate = DateToLocalDateConverter.convertToLocalDate(
                            DateUtil.parseDefaultString(secondTextfieldString.trim()));

                    return enteredDate != null && enteredDate.isAfter(filterValueDate);
                } catch (ParseException e) {
                    LOG.warn(PARSE_EXCEPTION_MESSAGE, e);
                }
            }

            String replaced = replaceFromCommaToDot(secondTextfieldString);
            double parsedTextField = Double.parseDouble(replaced);
            double parsedObjectLabel = filterValue.getNumberValue();

            return parsedTextField < parsedObjectLabel;
        }

        if (originClass.equals(LocalDate.class)
                || originClass.equals(Date.class) && !filterValue.getUnformattedLabelText()
                .equals(CustomFilterUtil.getLocalizedEmptyString())) {
            try {
                LocalDate filterValueDate = filterValue.getDateValue();
                LocalDate firstEnteredDate = DateToLocalDateConverter.convertToLocalDate(
                        DateUtil.parseDefaultString(firstTextfieldString.trim()));
                LocalDate secondEnteredDate = DateToLocalDateConverter.convertToLocalDate(
                        DateUtil.parseDefaultString(secondTextfieldString.trim()));

                return firstEnteredDate != null && secondEnteredDate != null && (firstEnteredDate.isAfter(
                        filterValueDate))
                        || (secondEnteredDate != null && secondEnteredDate.isBefore(filterValueDate)) && (
                        !(firstEnteredDate != null && firstEnteredDate.equals(filterValueDate))
                                && !secondEnteredDate.equals(filterValueDate));
            } catch (ParseException e) {
                LOG.warn(PARSE_EXCEPTION_MESSAGE, e);
            }
        }

        String replaced = replaceFromCommaToDot(firstTextfieldString);
        double parsedFirstTextField = Double.parseDouble(replaced);

        replaced = replaceFromCommaToDot(secondTextfieldString);
        double parsedSecondTextField = Double.parseDouble(replaced);
        double parsedObjectLabel = filterValue.getNumberValue();

        return parsedFirstTextField > parsedObjectLabel || parsedSecondTextField < parsedObjectLabel;
    }

    private static boolean isStringContainedInFilterValues(String firstTextfieldString, Class<?> originClass,
            CustomTableFilterValue filterValue, String processingString) {
        boolean isNumber =
                originClass.equals(Double.class) || originClass.equals(Float.class) || originClass.equals(Integer.class)
                        || originClass.equals(Long.class);
        if (isNumber) {
            processingString = replaceFromCommaToDot(firstTextfieldString);
        }

        String[] splitStrings = processingString.split("\\*");
        String filterLabelText = filterValue.getLabelText();
        String replacedFilterValue = replaceFromCommaToDot(filterLabelText);
        for (String subString : splitStrings) {
            if (isNumber) {
                subString = replaceFromCommaToDot(subString).trim();
                if (!subString.isEmpty() && !replacedFilterValue.contains(subString)) {
                    return false;
                }
            } else if (!filterLabelText.toLowerCase().contains(subString.toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    private static String getSubSearchAreaString(HBox searchAreaBottom) {
        String bottomSearchAreaString = null;
        for (Node node : searchAreaBottom.getChildren()) {
            if (node instanceof TextField textField) {
                bottomSearchAreaString = textField.textProperty().get().trim();
            } else if (node instanceof DatePicker datePicker) {
                bottomSearchAreaString = datePicker.getEditor().textProperty().get().trim();
            }
        }

        return bottomSearchAreaString;
    }

    private static String replaceFromCommaToDot(String text) {
        return text.replace(",", ".");
    }
}
