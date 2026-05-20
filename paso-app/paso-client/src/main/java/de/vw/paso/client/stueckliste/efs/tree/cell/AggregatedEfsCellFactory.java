package de.vw.paso.client.stueckliste.efs.tree.cell;

import java.util.function.BiConsumer;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import de.vw.paso.client.control.cell.AbstractTreeTableCell;
import de.vw.paso.client.control.cell.PartGroupTreeTableCell;
import de.vw.paso.client.control.cell.ReadOnlyTreeTableCell;
import de.vw.paso.client.control.cell.RemoveSummaryHighlightStylingEvent;
import de.vw.paso.client.stueckliste.efs.control.CommonCellUtil;
import de.vw.paso.client.stueckliste.efs.tree.model.PartGroupTreeObject;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AggregatedEfsCellFactory {

  private static final int FRACTION_DIGITS = 0;

  public static <TO> Callback<TreeTableColumn<TO, Double>, TreeTableCell<TO, Double>> forReadOnlyDoubleColumn(
    final String propertyName, BiConsumer<Boolean, String> action) {
    return column -> {
      final ReadOnlyTreeTableCell<TO, Double> cell = new ReadOnlyTreeTableCell<>(
        new DoubleStringConverter(FRACTION_DIGITS, true));

      cell.setPropertyName(propertyName);

      initCell(cell);

      if (action != null) {
        cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
          if ((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2)) {
            EventBus.getInstance()
              .post(new RemoveSummaryHighlightStylingEvent<>(cell.getTreeTableView().getRoot().getValue()));
            action.accept(true, propertyName);
            cell.setStylingForSummaryItem();
            event.consume();
          }
        });
      }

      return cell;
    };
  }

  public static <TO> Callback<TreeTableColumn<TO, String>, TreeTableCell<TO, String>> forReadOnlyStringColumn(
    final String propertyName) {
    return column -> {
      final ReadOnlyTreeTableCell<TO, String> cell = new ReadOnlyTreeTableCell<>(String.class);

      cell.setPropertyName(propertyName);

      initCell(cell);

      return cell;
    };
  }

  public static <TO> Callback<TreeTableColumn<TO, Integer>, TreeTableCell<TO, Integer>> forReadOnlyIntegerColumn(
    final String propertyName, BiConsumer<Boolean, String> action) {
    return column -> {
      final ReadOnlyTreeTableCell<TO, Integer> cell = new ReadOnlyTreeTableCell<>(Integer.class);

      cell.setConverter(new IntegerStringConverter());
      cell.setPropertyName(propertyName);

      initCell(cell);

      if (action != null) {
        cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
          if ((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2)) {
            EventBus.getInstance()
              .post(new RemoveSummaryHighlightStylingEvent<>(cell.getTreeTableView().getRoot().getValue()));
            action.accept(true, propertyName);
            cell.setStylingForSummaryItem();
            event.consume();
          }
        });
      }

      return cell;
    };
  }

  public static Callback<TreeTableColumn<PartGroupTreeObject, Integer>, TreeTableCell<PartGroupTreeObject, Integer>> forReadOnlyPartGroupIntegerColumn(
    final String propertyName) {
    return column -> {
      final PartGroupTreeTableCell<PartGroupTreeObject> cell = new PartGroupTreeTableCell<>(Integer.class);

      cell.setConverter(new IntegerStringConverter());
      cell.setPropertyName(propertyName);

      initCell(cell);

      return cell;
    };
  }

  private static <T, TO> void initCell(final AbstractTreeTableCell<TO, T> cell) {
    CommonCellUtil.formatCell(cell);

    cell.selectedProperty().addListener((observable, oldValue, newValue) -> setEditable(cell));
  }

  private static <T, TO> void setEditable(final AbstractTreeTableCell<TO, T> cell) {
    final TO costGroup = getCostGroup(cell);

    cell.setEditable(costGroup != null);
  }

  private static <T, TO> TO getCostGroup(final AbstractTreeTableCell<TO, T> cell) {
    final TreeTableRow<TO> tableRow = cell.getTreeTableRow();

    return tableRow == null ? null : tableRow.getItem();
  }

}
