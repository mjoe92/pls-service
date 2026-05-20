package de.vw.paso.client.control.cell;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;

/**
 * @author eryllan
 * @version $Revision: $
 * @created 12.08.2015
 */
public class RadioButtonTableCell<S> extends TableCell<S, Boolean> {

  private final RadioButton radioButton;

  public RadioButtonTableCell() {
    this.radioButton = new RadioButton();
    this.radioButton.setOnAction(e -> handleRadioButtonAction());
  }

  private void handleRadioButtonAction() {
    getTableView().edit(getTableRow().getIndex(), getTableColumn());
    commitEdit(true);
  }

  public void disable() {
    this.radioButton.setDisable(true);
  }

  @Override
  public void updateItem(Boolean selected, boolean empty) {
    super.updateItem(selected, empty);

    if (empty) {
      this.setGraphic(null);
    } else {
      this.setGraphic(radioButton);
      this.radioButton.setSelected(selected);
    }
  }

}
