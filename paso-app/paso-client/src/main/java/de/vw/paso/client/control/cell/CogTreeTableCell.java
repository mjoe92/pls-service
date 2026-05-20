package de.vw.paso.client.control.cell;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import de.vw.paso.client.stueckliste.column.alignment.ColumnAlignment;
import de.vw.paso.client.stueckliste.util.CogUtil;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import org.apache.commons.lang3.StringUtils;

public class CogTreeTableCell<T extends IEfsElementForDTO> extends AbstractTreeTableCell<T, CogCoordinates> {


  private CogConverter converter;

  private CogCellDisplayNode display;

  private CogTreeTableEditor editor;

  public CogTreeTableCell() {
    converter = new CogConverter();
    setConverter(converter);
    setValidation(e -> isCogValid());

    display = new CogCellDisplayNode();
    display.setStyle(ColumnAlignment.COG.getAlignment());
  }

  @Override
  public void startEdit() {
    if (!isEditable() || !getTreeTableView().isEditable() || !getTableColumn().isEditable()) {
      return;
    }
    if (editor == null) {
      editor = createEditor();
    }
    setEditable(true);
    editor.setCoordinates(CogUtil.getCoordinate(getTreeTableRow().getTreeItem().getValue()));
    super.startEdit();
    setGraphic(editor);
    editor.getXNode().requestFocus();
    editor.getXNode().selectAll();
  }

  private CogTreeTableEditor createEditor() {
    CogTreeTableEditor editor = new CogTreeTableEditor();
    editor.setOnKeyPressed(createKeyEventHandler());
    return editor;
  }

  private EventHandler<KeyEvent> createKeyEventHandler() {
    return keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.TAB) {
        CogCoordinates cogCoordinates = editor.getCogCoordinates();
        commitEdit(cogCoordinates);
        display.setCoordinates(cogCoordinates);
      } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
        cancelEdit();
      }
    };
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    setText(null);
    setGraphic(display);
  }

  @Override
  protected void updateItem(CogCoordinates item, boolean empty) {
    super.updateItem(item, empty);

    if (item != null && (item.getCogX() != null || item.getCogY() != null || item.getCogZ() != null)) {
      if (isEditing()) {
        editor.setCoordinates(item);
        setGraphic(editor);
      } else {
        display.setCoordinates(item);
        setGraphic(display);
      }
    } else {
      setText(null);
      setGraphic(null);
    }
  }

  private boolean isCogValid() {
    return editor.isCogValid();
  }

  public static class CogConverter extends StringConverter<CogCoordinates> {
    @Override
    public String toString(CogCoordinates object) {
      if (object != null) {
        return object.toString();
      }
      return null;
    }

    @Override
    public CogCoordinates fromString(String string) {
      try {
        if (StringUtils.isNotEmpty(string)) {
          Double x = null;
          Double y = null;
          Double z = null;
          String[] split = string.split(CogUtil.COG_SEPARATOR);
          if (split.length == 1) {
            x = Double.valueOf(split[0]);
          } else if (split.length == 2) {
            x = Double.valueOf(split[0]);
            y = Double.valueOf(split[1]);
          } else if (split.length > 2) {
            x = Double.valueOf(split[0]);
            y = Double.valueOf(split[1]);
            z = Double.valueOf(split[2]);
          }
          return new CogCoordinates(x, y, z);
        }
      } catch (NumberFormatException nfe) {
        return null;
      }
      return null;
    }
  }
}
