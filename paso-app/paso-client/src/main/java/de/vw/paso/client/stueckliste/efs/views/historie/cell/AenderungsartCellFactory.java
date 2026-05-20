package de.vw.paso.client.stueckliste.efs.views.historie.cell;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

import de.vw.paso.client.stueckliste.efs.control.EfsCellUtil;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;

public class AenderungsartCellFactory<S extends IEfsElementForDTO, T>
  implements Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> {

  private final String propertyName;

  public AenderungsartCellFactory(String propertyName) {
    this.propertyName = propertyName;
  }

  @Override
  public TreeTableCell<S, T> call(TreeTableColumn<S, T> param) {
    AenderungsartTreeTableCell<T> aenderungsartTreeTableCell = new AenderungsartTreeTableCell<>();

    aenderungsartTreeTableCell.setPropertyName(this.propertyName);
    EfsCellUtil.formatCell(aenderungsartTreeTableCell);

    return (TreeTableCell<S, T>) aenderungsartTreeTableCell;
  }

}
