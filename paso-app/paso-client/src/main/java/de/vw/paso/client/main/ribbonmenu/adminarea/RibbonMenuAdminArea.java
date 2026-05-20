package de.vw.paso.client.main.ribbonmenu.adminarea;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.Controller;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.dialog.PasoDialog;
import de.vw.paso.client.control.ribbonmenu.RibbonButton;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroup;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.util.icon.AdminAreaIcon;
import de.vw.paso.client.util.icon.MessageIcon;
import de.vw.paso.client.util.icon.StammdatenIcon;
import de.vw.paso.client.util.icon.UserManagementIcon;
import de.vw.paso.delegate.message.NotificationRestClientHolder;

public class RibbonMenuAdminArea extends RibbonMenu implements Controller {

    private final RibbonMenuAdminAreaListener listener;

    public RibbonMenuAdminArea(RibbonMenuAdminAreaListener listener) {
        setText(I18N.getString("ribbonmenu.admin.title"));
        this.listener = listener;

        //todo: on disable should not create the buttons, only when we "Refresh the user rights"
        disableProperty().bind(listener.toggleDisableNonAdminArea());

        RibbonMenuGroup groupAdminArea = createGroupAdminArea();
        addMenuGroup(groupAdminArea);
    }

    private RibbonMenuGroup createGroupAdminArea() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenu.title"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

        RibbonButton buttonTiWhRequestQueue = new RibbonButton(I18N.getString("ribbonmenubutton.tiwhrequestqueue"),
                AdminAreaIcon.TI_WH_REQUEST_QUEUE_ICON_32x32.getImage());
        buttonTiWhRequestQueue.setOnAction(e -> listener.handleActionStartTiWhRequestQueue());

        RibbonButton buttonUserManagement = new RibbonButton(I18N.getString("ribbonmenubutton.usermanagement"),
                UserManagementIcon.USER_MANAGEMENT_32x32.getImage());
        buttonUserManagement.setOnAction(e -> listener.handleActionStartUserManagement());

        RibbonButton smartFix = new RibbonButton(I18N.getString("ribbonmenubutton.defaultsolutions"),
                StammdatenIcon.PARTLIST_FIX_32X32.getImage());
        smartFix.setOnAction(e -> listener.handleActionStartSmartFixView());

        RibbonButton manualMBTImport = new RibbonButton(I18N.getString("ribbonmenubutton.mbt-import"),
                StammdatenIcon.PARTLIST_FIX_32X32.getImage());
        manualMBTImport.setOnAction(e -> listener.handleActionStartMbtImport());

        RibbonButton buttonStammdaten = new RibbonButton(I18N.getString("ribbonmenubutton.stammdaten"),
                StammdatenIcon.STAMMDATEN_32X32.getImage());
        buttonStammdaten.setOnAction(e -> listener.handleActionStartStammdaten());

        RibbonButton buttonUserMessage = new RibbonButton(I18N.getString("ribbonmenubutton.usermessage"),
                MessageIcon.UNREAD_32X32.getImage());
        buttonUserMessage.setOnAction(e -> handleActionUserMessage());

        itemBox.addButton(buttonTiWhRequestQueue, buttonUserManagement, smartFix, manualMBTImport, buttonStammdaten,
                buttonUserMessage);

        group.addItemBox(itemBox);
        return group;
    }

    private void handleActionUserMessage() {
        // create the custom dialog.
        Dialog<Boolean> dialog = new PasoDialog<>();
        dialog.setTitle(I18N.getString("notification.create"));
        dialog.setHeaderText(I18N.getString("notification.create.new"));
        dialog.setGraphic(new ImageView(MessageIcon.UNREAD_32X32.getImage()));

        ButtonType createButtonType = new ButtonType(I18N.getString("create"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        TextArea messageArea = new TextArea();
        messageArea.setPromptText(I18N.getString("message"));
        messageArea.textProperty()
                .addListener((observable, oldValue, newValue) -> createButton.setDisable(newValue.trim().isEmpty()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label(I18N.getString("message.colon")), 0, 0);
        grid.add(messageArea, 0, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(type -> type == createButtonType);

        // runLater() to focus the area when the window is visible
        dialog.setOnShown(e -> Platform.runLater(messageArea::requestFocus));

        Optional<Boolean> create = dialog.showAndWait();
        create.ifPresent(change -> {
            if (change) {
                doAsync(() -> NotificationRestClientHolder.getInstance().createUserMessage(messageArea.getText()));
            }
        });
    }
}