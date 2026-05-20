package de.vw.paso.client.stueckliste.teilenummer;

import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import de.vw.paso.client.control.cell.AbstractTreeTableCell;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItem;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import org.apache.commons.lang3.StringUtils;

public class TeilenummerTreeTableCell extends AbstractTreeTableCell<EfsElementDTO, String> {

  private TeileNrTextFieldController teileNrTextFieldController;

  public TeilenummerTreeTableCell() {
    setValidation(teilenummer -> teileNrTextFieldController.isValidTeilenummer());
  }

  @Override
  public void startEdit() {
    if (!isEditable() || !getTreeTableView().isEditable() || !getTableColumn().isEditable()) {
      return;
    }
    super.startEdit();
    if (isEditing()) {
      if (teileNrTextFieldController == null) {
        teileNrTextFieldController = loadTeileNrController();
      }
      teileNrTextFieldController.setPartNumber(getItemText());
      setText(null);
      setGraphic(teileNrTextFieldController.getControl());

      getTreeTableRow().getDisclosureNode().getStyleClass().add("disclosure-mark-hidden");
    }
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();

    getTreeTableRow().getDisclosureNode().getStyleClass().remove("disclosure-mark-hidden");
    setText(getItemText());
    setGraphic(null);
  }

  @Override
  public void commitEdit(String newValue) {
    super.commitEdit(newValue);

    getTreeTableRow().getDisclosureNode().getStyleClass().remove("disclosure-mark-hidden");
  }

  @Override
  public void updateItem(String item, boolean empty) {
    super.updateItem(item, empty);

    setEditable(true);
    setGraphic(null);
    setStyle(null);

    if (isEmpty() || isEditing()) {
      setText(null);
    } else {
      setText(getItemText());

      if (getTreeTableRow().getTreeItem() != null) {
        String value = ((EfsElementTreeItem) getTreeTableRow().getTreeItem()).propertyGroupString().get();

        if (value != null && !value.isEmpty()) {
          if (value.contains("[")) {
            setText(StringUtils.SPACE);
            setGraphic(getItemTextForStyledGroup(value));
          } else {
            setStyle("-fx-font-weight: bold");
            setText(value);
          }

          setEditable(false);
        }
      }
    }
  }

  private String getItemText() {
    if (getConverter() == null) {
      return getItem() == null ? StringUtils.EMPTY : getItem();
    }

    return getConverter().toString(getItem());
  }

  private TextFlow getItemTextForStyledGroup(String value) {
    List<String> parts = List.of(value.split("\\["));

    TextFlow textFlow = new TextFlow();
    textFlow.setPrefWidth(Region.USE_COMPUTED_SIZE);
    textFlow.setMinWidth(Region.USE_PREF_SIZE);
    textFlow.setPrefHeight(20);

    Text textBold = new Text(parts.get(0));
    textBold.setFont(Font.font(getFont().getFamily(), FontWeight.BOLD, getFont().getSize()));

    textFlow.getChildren().add(textBold);
    textFlow.getChildren().add(new Text("[" + parts.get(1)));

    return textFlow;
  }

  private TeileNrTextFieldController loadTeileNrController() {
    TeileNrTextFieldController controller = TeileNrTextFieldController.load(TeileNrTextFieldController.class
    );
    setOnKeyPressed(controller);
    return controller;
  }

  private void setOnKeyPressed(TeileNrTextFieldController controller) {
    EventHandler<KeyEvent> keyEvent = createKeyEventHandler(controller);
    controller.getTextfieldPartNumber().setOnKeyPressed(keyEvent);
  }

  private EventHandler<KeyEvent> createKeyEventHandler(TeileNrTextFieldController controller) {
    return t -> {
      if (t.getCode() == KeyCode.ENTER) {
        commitEdit(controller.getPartNumber());
      } else if (t.getCode() == KeyCode.ESCAPE) {
        cancelEdit();
      }
    };
  }
}
