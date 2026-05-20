package de.vw.paso.client.control.cell;

import java.util.Map;

import javafx.scene.control.Tooltip;

import de.vw.paso.client.base.I18N;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class ReasonTableCell extends ReadOnlyTableCell<EfsElementDTO, String> {

  private Map<EfsElementDTO, String> alternatives;

  public ReasonTableCell(Class<?> dataType, Map<EfsElementDTO, String> alternatives) {
    super(dataType);

    this.alternatives = alternatives;
  }

  @Override
  public void updateItem(String item, boolean empty) {
    super.updateItem(item, empty);

    if (item != null) {
      setTooltip(new Tooltip(I18N.getString(alternatives.get(getTableView().getItems().get(getIndex())))));
    }
  }
}
