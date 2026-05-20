package de.vw.paso.client.control.tablebase.header;

import javafx.scene.control.TableColumnBase;
import javafx.scene.control.skin.TableColumnHeader;

public class CustomTableColumnHeader extends TableColumnHeader {

  /**
   * Creates a new {@link CustomTableColumnHeader} instance.
   *
   * @param tableColumnBase
   *   the {@link TableColumnBase} where this header will be created from
   */
  public CustomTableColumnHeader(TableColumnBase<?, ?> tableColumnBase) {
    super(tableColumnBase);
  }

  /** Resizes this {@code TableColumnHeader}'s column to fit the width of its content. */
  public void resizeColumnToFitContent() {
    // When there is no scene we should not do resizing as it will result in an exception and a wrong calculation
    // anyway.
    if (getScene() == null) {
      return;
    }

    if (!getTableColumn().isResizable()) {
      return;
    }

    // Since maxRows can be arbitrary large we only just take 15 rows into account, which is almost always enough.
    super.resizeColumnToFitContent(15);
  }

  @Override
  protected void resizeColumnToFitContent(int maxRows) {
    resizeColumnToFitContent();
  }

}
