package de.vw.paso.client.util;

import java.util.Date;
import java.util.Objects;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import javafx.util.converter.DateStringConverter;

import de.vw.paso.client.control.cell.DatePickerTreeTableCell;
import de.vw.paso.client.control.cell.NumberFieldTreeTableCell;
import de.vw.paso.client.control.cell.ReadOnlyTreeTableCell;
import de.vw.paso.client.control.cell.TextFieldTreeTableCell;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import de.vw.paso.client.util.converter.LongStringConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TreeTableCellFactory {

    public static <T> Callback<TreeTableColumn<T, Date>, TreeTableCell<T, Date>> forDateColumn() {
        return column -> new DatePickerTreeTableCell<>(new DateStringConverter("dd.MM.yyyy"));
    }

    public static <T> Callback<TreeTableColumn<T, Integer>, TreeTableCell<T, Integer>> forIntegerColumn() {
        return column -> new NumberFieldTreeTableCell<>(new IntegerStringConverter());
    }

    public static <T> Callback<TreeTableColumn<T, Double>, TreeTableCell<T, Double>> forDoubleColumn() {
        return column -> {
            final NumberFieldTreeTableCell<T, Double> cell = new NumberFieldTreeTableCell<>(
                    new DoubleStringConverter(3, false), true, true);

            cell.setValidation(weight -> weight == null || weight.intValue() >= 0);

            return cell;
        };
    }

    public static <T> Callback<TreeTableColumn<T, Long>, TreeTableCell<T, Long>> forLongColumn() {
        return column -> {
            final NumberFieldTreeTableCell<T, Long> cell = new NumberFieldTreeTableCell<>(new LongStringConverter());

            cell.setValidation(Objects::nonNull);

            return cell;
        };
    }

    public static <T> Callback<TreeTableColumn<T, String>, TreeTableCell<T, String>> forStringColumn(
            final int maxTextLength) {
        return column -> {
            final TextFieldTreeTableCell<T, String> cell = new TextFieldTreeTableCell<>();

            cell.setMaxTextLength(maxTextLength);

            return cell;
        };
    }

    public static <T> Callback<TreeTableColumn<T, Double>, TreeTableCell<T, Double>> forColumnReadOnly() {
        return forColumnReadOnly(false);
    }

    public static <T> Callback<TreeTableColumn<T, Double>, TreeTableCell<T, Double>> forColumnReadOnly(
            final boolean grayOutZero) {
        return column -> new ReadOnlyTreeTableCell<>(new DoubleStringConverter(), grayOutZero, true);
    }

}
