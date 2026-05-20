package de.vw.paso.client.control.tablebase.tableconfig;

import java.util.List;

import javafx.scene.control.TableColumnBase;

public final class TableColumnSelectionUtils {

  public static final Object COLUMN_CONFIG = "COLUMN_CONFIG";

  private TableColumnSelectionUtils() {
    throw new IllegalArgumentException("Util class");
  }

  public static void applyLayout(List<? extends TableColumnBase<?, ?>> columns, ColumnSelectionResult selectedColumns) {
    for (TableColumnBase<?, ?> column : columns) {
      column.setVisible(selectedColumns.isIdSelected(column.getId()));
    }

    columns.sort((o1, o2) -> {
      int i1 = selectedColumns.getIndexOfId(o1.getId());
      int i2 = selectedColumns.getIndexOfId(o2.getId());
      return i1 - i2;
    });
  }
}
