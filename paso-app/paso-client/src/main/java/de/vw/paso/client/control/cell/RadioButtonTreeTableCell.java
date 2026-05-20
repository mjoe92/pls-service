package de.vw.paso.client.control.cell;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;

public class RadioButtonTreeTableCell<S> extends TreeTableCell<S, Boolean> {

  private final RadioButton radioButton;

  public RadioButtonTreeTableCell(final ToggleGroup toggleGroup) {
    radioButton = new RadioButton();

    radioButton.setOnAction(e -> handleRadioButtonAction());
    radioButton.setToggleGroup(toggleGroup);
  }

  private void handleRadioButtonAction() {
    getTreeTableView().edit(getTreeTableRow().getIndex(), getTableColumn());

    commitEdit(true);
  }

  @Override
  public void updateItem(final Boolean selected, final boolean empty) {
    super.updateItem(selected, empty);

    setText(null);
    setGraphic(null);

    if (!empty && getTreeTableRow().getTreeItem() != null) {
      final TreeItem<S> selectedItem = getTreeTableRow().getTreeItem();

      if (selectedItem.isLeaf()) {
        setGraphic(this.radioButton);

        radioButton.setSelected(selectedItem.getValue().equals(getTableColumn().getUserData()));
      }
    }
  }

}
