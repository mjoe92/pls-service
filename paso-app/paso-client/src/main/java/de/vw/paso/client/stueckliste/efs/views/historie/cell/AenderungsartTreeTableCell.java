package de.vw.paso.client.stueckliste.efs.views.historie.cell;

import java.sql.Date;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.ReadOnlyTreeTableCell;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import org.apache.commons.lang3.StringUtils;

public class AenderungsartTreeTableCell<T> extends ReadOnlyTreeTableCell<IEfsElementForDTO, T> { // NO_UCD (use default)

  public static final String TYPE_UNDEFINED = StringUtils.EMPTY; // NO_UCD (use default)
  public static final String TYPE_NEW = "N"; // NO_UCD (use default)
  public static final String TYPE_UPDATE = "U"; // NO_UCD (use default)
  public static final String TYPE_DELETE = "D"; // NO_UCD (use default)

  public AenderungsartTreeTableCell() {
    super(Date.class);
  }

  @Override
  public void updateItem(T item, boolean empty) {
    super.updateItem(item, empty);

    setText(null);

    if (item != null && !empty) {
      final Label lbl = new Label();

      if (item.equals(TYPE_NEW)) {
        Image img = ActionIcon.NEW_16X16.getImage();
        ImageView iv = new ImageView(img);
        lbl.setGraphic(iv);
        lbl.setTooltip(new Tooltip(I18N.getString("aenderungsart.new")));
      } else if (item.equals(TYPE_UPDATE)) {
        Image img = ActionIcon.EDIT_16X16.getImage();
        ImageView iv = new ImageView(img);
        lbl.setGraphic(iv);
        lbl.setTooltip(new Tooltip(I18N.getString("aenderungsart.update")));
      } else if (item.equals(TYPE_DELETE)) {
        Image img = ActionIcon.DELETE_16X16.getImage();
        ImageView iv = new ImageView(img);
        lbl.setGraphic(iv);
        lbl.setTooltip(new Tooltip(I18N.getString("aenderungsart.delete")));
      }

      setGraphic(lbl);
    }
  }

}
