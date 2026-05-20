package de.vw.paso.client.util.customfilter;

import java.time.LocalDate;
import java.util.Date;

import javafx.collections.ObservableList;

import de.vw.paso.client.control.tablebase.filter.panel.CustomTableFilterValue;

public class CustomFilterSortUtil {

    public static Class determineActualOriginClass(CustomTableFilterValue tableFilterValue,
            CustomTableFilterValue otherTableFilterValue) {
        Class originClass;
        final Class originClass1 = tableFilterValue.getOriginClass();
        final Class originClass2 = otherTableFilterValue.getOriginClass();
        if (!originClass1.equals(originClass2)) {
            if (!originClass1.equals(String.class)) {
                originClass = originClass1;
            } else if (!originClass2.equals(String.class)) {
                originClass = originClass2;
            } else {
                originClass = originClass1;
            }
        } else {
            originClass = originClass1;
        }
        return originClass;
    }

    public static void sort(ObservableList<CustomTableFilterValue> checkListViewItems, Class originClass) {
        if (originClass.equals(String.class)) {
            sortStringBasedCheckListView(checkListViewItems);
        } else if (originClass.equals(Integer.class) || originClass.equals(Long.class) || originClass.equals(
                Float.class) || originClass.equals(Double.class)) {

            sortNumberBasedCheckListView(checkListViewItems);
        } else if (originClass.equals(LocalDate.class) || originClass.equals(Date.class)) {
            sortDateBasedCheckListView(checkListViewItems);
        } else {
            sortStringBasedCheckListView(checkListViewItems);
        }
    }

    private static void sortNumberBasedCheckListView(ObservableList<CustomTableFilterValue> checkListViewItems) {
        checkListViewItems.sort((o1, o2) -> {
            if (o1.getLabelText().matches(CustomFilterUtil.getLocalizedEmptyString())) {
                return -1;
            } else if (o2.getLabelText().matches(CustomFilterUtil.getLocalizedEmptyString())) {
                return 1;
            } else if (o1.selectedProperty().get() && o2.selectedProperty().get()) {
                final double object1DoubleValue = o1.getNumberValue();
                final double object2DoubleValue = o2.getNumberValue();
                return compareNumbers(object1DoubleValue, object2DoubleValue);
            } else {
                if (o1.selectedProperty().get()) {
                    return -1;
                } else if (o2.selectedProperty().get()) {
                    return 1;
                } else {
                    final double object1DoubleValue = o1.getNumberValue();
                    final double object2DoubleValue = o2.getNumberValue();
                    return compareNumbers(object1DoubleValue, object2DoubleValue);
                }
            }
        });
    }

    private static int compareNumbers(double object1DoubleValue, double object2DoubleValue) {
        return Double.compare(object1DoubleValue, object2DoubleValue);
    }

    private static void sortDateBasedCheckListView(ObservableList<CustomTableFilterValue> checkListViewItems) {
        checkListViewItems.sort((o1, o2) -> {

            final long o1Date = o1.getDateAsLong();
            final long o2Date = o2.getDateAsLong();

            if (o1.getLabelText().equals(CustomFilterUtil.getLocalizedEmptyString())) {
                return -1;
            } else if (o2.getLabelText().equals(CustomFilterUtil.getLocalizedEmptyString())) {
                return 1;
            } else if (o1.selectedProperty().get() && o2.selectedProperty().get()) {
                return compareNumbers(o1Date, o2Date);

            } else {
                if (o1.selectedProperty().get()) {
                    return -1;
                } else if (o2.selectedProperty().get()) {
                    return 1;
                } else {
                    return compareNumbers(o1Date, o2Date);
                }
            }
        });
    }

    private static void sortStringBasedCheckListView(ObservableList<CustomTableFilterValue> checkListViewItems) {
        checkListViewItems.sort((o1, o2) -> {
            if (o1.getLabelText().matches(CustomFilterUtil.getLocalizedEmptyString())) {
                return -1;
            } else if (o2.getLabelText().matches(CustomFilterUtil.getLocalizedEmptyString())) {
                return 1;
            } else if (o1.selectedProperty().get() && o2.selectedProperty().get()) {
                return (o1.getLabelText().trim().compareTo(o2.getLabelText().trim()));
            } else {
                if (o1.selectedProperty().get()) {
                    return -1;
                } else if (o2.selectedProperty().get()) {
                    return 1;
                } else {
                    return (o1.getLabelText().trim().compareTo(o2.getLabelText().trim()));
                }
            }
        });
    }
}
