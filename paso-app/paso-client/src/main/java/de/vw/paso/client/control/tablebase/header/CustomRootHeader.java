package de.vw.paso.client.control.tablebase.header;

import javafx.scene.control.skin.TableColumnHeader;

public class CustomRootHeader extends CustomNestedTableColumnHeader {

  /** Creates a new {@link CustomRootHeader} instance. */
  public CustomRootHeader() {
    super(null);
  }

  @Override
  protected void resizeColumnToFitContent(int maxRows) {
    // Note: This method is never called by JavaFX.
    resizeColumnToFitContent();
  }

  @Override
  public void resizeColumnToFitContent() {
    for (TableColumnHeader columnHeader : getColumnHeaders()) {
      if (columnHeader instanceof CustomTableColumnHeader extendedHeader) {
        extendedHeader.resizeColumnToFitContent();
      } else if (columnHeader instanceof CustomNestedTableColumnHeader extendedNestedHeader) {
        extendedNestedHeader.resizeColumnToFitContent();
      }
    }
  }

}
