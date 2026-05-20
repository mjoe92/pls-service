package de.vw.paso.client.main.ribbonmenu.start;

import java.util.Arrays;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.Controller;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.dialog.PasoDialog;
import de.vw.paso.client.control.ribbonmenu.RibbonButton;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroup;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.i18n.AvailableLanguages;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.util.icon.AppIcon;
import de.vw.paso.client.util.icon.ExplorerIcon;
import de.vw.paso.client.util.icon.FlagIcon;
import de.vw.paso.client.util.icon.UserRightIcon;
import de.vw.paso.delegate.stueckliste.userproperty.UserPropertyRestClientHolder;
import de.vw.paso.service.userproperty.SaveUserPropertyDTO;
import de.vw.paso.user.PropertyType;

public class RibbonMenuStart extends RibbonMenu implements Controller {

    private final RibbonMenuStartListener listener;

    public RibbonMenuStart(RibbonMenuStartListener listener) {
        setText(I18N.getString("ribbonmenu.start.title"));

        this.listener = listener;

        addMenuGroup(createGroupAnsicht());
        addMenuGroup(createGroupSettings());
    }

    private RibbonMenuGroup createGroupSettings() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.desktop"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

        RibbonButton buttonCreateDesktopLink = new RibbonButton(I18N.getString("ribbonmenubutton.desktoplink"),
            AppIcon.APP_32_PNG.getImage());
        buttonCreateDesktopLink.setOnAction(e -> listener.handleActionDesktopLink());
        itemBox.addButton(buttonCreateDesktopLink);
        group.addItemBox(itemBox);
        return group;
    }

    private RibbonMenuGroup createGroupAnsicht() {
        RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.ansichtanzeigen"));
        RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

        RibbonButton buttonExplorer = new RibbonButton(I18N.getString("ribbonmenubutton.explorer"),
            ExplorerIcon.EXPLORER_32X32.getImage());
        buttonExplorer.setOnAction(e -> listener.handleActionStartExplorer());

        RibbonButton buttonManageLanguage = new RibbonButton(I18N.getString("ribbonmenubutton.managelanguage"),
            FlagIcon.LANGUAGE_32X32.getImage());
        buttonManageLanguage.setOnAction(e -> handleActionManageLanguage());

        RibbonButton buttonRefreshUserRights = new RibbonButton(I18N.getString("ribbonmenubutton.refreshrights"),
            UserRightIcon.REFRESH.getImage());
        buttonRefreshUserRights.setOnAction(e -> listener.handleActionRefreshRights());

        itemBox.addButton(buttonExplorer);
        itemBox.addButton(buttonManageLanguage);
        itemBox.addButton(buttonRefreshUserRights);
        group.addItemBox(itemBox);

        return group;
    }

    private void handleActionManageLanguage() {
        Dialog<Boolean> dialog = new PasoDialog<>();
        dialog.setTitle(I18N.getString("manage.language.dialog.title"));
        dialog.setHeaderText(I18N.getString("manage.language.dialog.header"));
        dialog.setGraphic(new ImageView(FlagIcon.LANGUAGE_32X32.getImage()));

        ButtonType changeButtonType = new ButtonType(I18N.getString("manage.language.dialog.button.change"),
            ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        ComboBox<AvailableLanguages> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableList(Arrays.stream(AvailableLanguages.values()).toList()));

        grid.add(new Label(I18N.getString("manage.language.dialog.label")), 0, 0);
        grid.add(comboBox, 0, 1);

        Node changeButton = dialog.getDialogPane().lookupButton(changeButtonType);
        changeButton.setDisable(true);
        String currentLanguage = UserProperties.getPreferredLanguage();
        comboBox.getSelectionModel().select(
            AvailableLanguages.isAvailable(currentLanguage) ? AvailableLanguages.valueOf(currentLanguage)
                : AvailableLanguages.EN);

        comboBox.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> changeButton.setDisable(
                UserProperties.getPreferredLanguage().equals(comboBox.getSelectionModel().getSelectedItem().name())));

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(e -> e == changeButtonType);

        Optional<Boolean> changeLanguage = dialog.showAndWait();
        changeLanguage.ifPresent(change -> {
            if (change) {
                doAsync(() -> UserPropertyRestClientHolder.getInstance().saveOrUpdate(
                    new SaveUserPropertyDTO(PropertyType.PREFERRED_LANGUAGE,
                        comboBox.getSelectionModel().getSelectedItem().name())));
            }
        });
    }
}
