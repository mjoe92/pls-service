package de.vw.paso.client.stueckliste.teilenummer;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import de.vw.paso.client.control.cell.AbstractTableCell;
import de.vw.paso.client.exception.ControllerException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import org.apache.commons.lang3.StringUtils;

public class PartNumberTableCell extends AbstractTableCell<EfsElementDTO, String> {

    private TeileNrTextFieldController teileNrTextFieldController;

    public PartNumberTableCell() {
        setValidation(partnumber -> teileNrTextFieldController.isValidTeilenummer());
    }

    @Override
    public void startEdit() {
        if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
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
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText(getItemText());
        setGraphic(null);
    }

    @Override
    public void updateItem(final String item, final boolean empty) {
        super.updateItem(item, empty);

        setText((isEmpty() || isEditing()) ? null : getItemText());

        setGraphic(null);
    }

    private String getItemText() {
        if (getConverter() == null) {
            return getItem() == null ? StringUtils.EMPTY : getItem();
        }

        return getConverter().toString(getItem());
    }

    private TeileNrTextFieldController loadTeileNrController() {
        try {
            final TeileNrTextFieldController controller = TeileNrTextFieldController.load(
                    TeileNrTextFieldController.class);
            setOnKeyPressed(controller);
            return controller;
        } catch (final ControllerException exception) {
            throw new RuntimeException("Could not load controller", exception);
        }
    }

    private void setOnKeyPressed(final TeileNrTextFieldController controller) {
        final EventHandler<KeyEvent> keyEvent = createKeyEventHandler(controller);
        controller.getTextfieldPartNumber().setOnKeyPressed(keyEvent);
    }

    private EventHandler<KeyEvent> createKeyEventHandler(final TeileNrTextFieldController controller) {
        return keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                commitEdit(controller.getPartNumber());
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        };
    }
}
