package de.vw.paso.client.stueckliste.teilenummer;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.textfield.PasoCustomTextField;
import de.vw.paso.client.stueckliste.util.PartNumberUtil;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.service.partlist.EfsEditValidations;
import de.vw.paso.service.partlist.PartNumberInappropriateException;

@FXController(name = "teilenr-textfield")
public class TeileNrTextFieldController extends BaseController<GridPane> implements Initializable {

    @FXML
    private GridPane gridPanePartNumber;

    @FXML
    private Button buttonKopieren;

    @FXML
    private Button buttonEinfuegen;

    @FXML
    private PasoCustomTextField<String> textfieldPartNumber;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        textfieldPartNumber.setValidCharacter("[a-zA-Z0-9 ]*");
        textfieldPartNumber.setValidation(isPartNumberAppropriate());
        textfieldPartNumber.setUpperCase(true);

        gridPanePartNumber.addEventFilter(KeyEvent.ANY,
                event -> PartNumberUtil.keyEventFilter(event, textfieldPartNumber));

        buttonKopieren.setGraphic(new ImageView(ActionIcon.COPY_16X16.getImage()));
        buttonKopieren.setTooltip(new Tooltip(I18N.getString("copy")));
        buttonKopieren.setOnAction(e -> handleActionCopy());

        buttonEinfuegen.setGraphic(new ImageView(ActionIcon.PASTE_16X16.getImage()));
        buttonEinfuegen.setTooltip(new Tooltip(I18N.getString("paste")));
        buttonEinfuegen.setOnAction(e -> handleActionPaste());
    }

    public String getPartNumber() {
        return textfieldPartNumber.getText();
    }

    public void setPartNumber(String partNumber) {
        textfieldPartNumber.setText(partNumber);
    }

    public boolean isValidTeilenummer() {
        return isPartNumberAppropriate().test(textfieldPartNumber.getText());
    }

    @Override
    public GridPane getControl() {
        return gridPanePartNumber;
    }

    @Override
    public Parent getStyleableParent() {
        return getControl();
    }

    public PasoCustomTextField<String> getTextfieldPartNumber() {
        return textfieldPartNumber;
    }

    private Predicate<String> isPartNumberAppropriate() {
        return partNumber -> {
            try {
                return EfsEditValidations.evaluateMaraIsAppropriate(partNumber);
            } catch (NullElementException | PartNumberInappropriateException e) {
                return false;
            }
        };
    }

    private void handleActionCopy() {
        String teilenummer = getPartNumber();
        if (teilenummer != null && !teilenummer.trim().isEmpty()) {
            StringSelection data = new StringSelection(teilenummer);

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(data, data);
        }
    }

    private void handleActionPaste() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transfer = clipboard.getContents(null);
        for (DataFlavor dataFlavor : transfer.getTransferDataFlavors()) {
            try {
                Object content = transfer.getTransferData(dataFlavor);
                if (content instanceof String) {
                    setPartNumber(content.toString());
                    break;
                }
            } catch (Exception exception) {
                handleException(exception);
            }
        }
    }
}