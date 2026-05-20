package de.vw.paso.client.util;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;
import javafx.util.converter.DateStringConverter;

import de.vw.paso.client.control.cell.PartPropertyTableCell;
import de.vw.paso.client.control.cell.ReadOnlyTableCell;
import de.vw.paso.client.control.cell.ReasonTableCell;
import de.vw.paso.client.stueckliste.efs.converter.SeparatedPartNumberStringConverter;
import de.vw.paso.client.stueckliste.teilenummer.PartNumberTableCell;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import de.vw.paso.client.util.converter.LongStringConverter;
import de.vw.paso.partlist.domain.AP;
import de.vw.paso.partlist.domain.PartProperty;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class TableCellFactory {

  private TableCellFactory() {
    throw new IllegalArgumentException("Util class");
  }

  public static Callback<TableColumn<EfsElementDTO, Double>, TableCell<EfsElementDTO, Double>> forDoubleColumnReadOnly(
    boolean grayOutZero) {
    return column -> new ReadOnlyTableCell<>(new DoubleStringConverter(), grayOutZero, true);
  }

  public static Callback<TableColumn<EfsElementDTO, Date>, TableCell<EfsElementDTO, Date>> forDateColumn() {
    return column -> new ReadOnlyTableCell<>(new DateStringConverter("dd.MM.yyyy"), false, false);
  }

  public static Callback<TableColumn<EfsElementDTO, Long>, TableCell<EfsElementDTO, Long>> forLongColumn() {
    return column -> {
      final ReadOnlyTableCell<EfsElementDTO, Long> cell = new ReadOnlyTableCell<>(new LongStringConverter(), false,
        false);

      cell.setValidation(Objects::nonNull);

      return cell;
    };
  }

  public static Callback<TableColumn<EfsElementDTO, Integer>, TableCell<EfsElementDTO, Integer>> forIntegerColumn() {
    return column -> {
      final ReadOnlyTableCell<EfsElementDTO, Integer> cell = new ReadOnlyTableCell<>(new IntegerStringConverter(),
        false, false);

      cell.setValidation(menge -> Objects.nonNull(menge) && menge >= 0);

      return cell;
    };
  }

  public static Callback<TableColumn<EfsElementDTO, String>, TableCell<EfsElementDTO, String>> forColumnTeilenummer() {
    return column -> {
      final PartNumberTableCell cell = new PartNumberTableCell();

      cell.setConverter(new SeparatedPartNumberStringConverter());

      return cell;
    };
  }

  public static Callback<TableColumn<EfsElementDTO, String>, TableCell<EfsElementDTO, String>> forColumnAp() {
    final ObservableList<String> aps = FXCollections.observableArrayList(AP.toStrList());

    return column -> new ComboBoxTableCell<>(aps);
  }

  public static Callback<TableColumn<EfsElementDTO, String>, TableCell<EfsElementDTO, String>> forColumnReason(
    Map<EfsElementDTO, String> alternatives) {
    return column -> new ReasonTableCell(String.class, alternatives);
  }

  public static Callback<TableColumn<PartProperty, String>, TableCell<PartProperty, String>> forPartPropertyValueColumn() {
    return column -> new PartPropertyTableCell(String.class);
  }
}
