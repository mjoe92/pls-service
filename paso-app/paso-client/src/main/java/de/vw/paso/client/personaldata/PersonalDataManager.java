package de.vw.paso.client.personaldata;

import java.util.prefs.BackingStoreException;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.dialog.PasoDialog;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.preference.PreferenceHandler;
import de.vw.paso.delegate.stueckliste.userproperty.UserPropertyRestClientHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonalDataManager {

    private static final Logger LOG = LoggerFactory.getLogger(PersonalDataManager.class);

    public void askDeletePersonalData(String userId) {
        LOG.info("Ask user for deletion");

        Dialog<Void> dialog = new PasoDialog<>();
        dialog.setTitle(I18N.getString("deletepersonaldata.dialog.title"));
        dialog.setContentText(I18N.getString("deletepersonaldata.dialog.content"));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
        Button yesButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.YES);
        yesButton.setOnAction(eh -> closeAndDelete(userId, dialog));
        dialog.showAndWait();
    }

    private void closeAndDelete(String userId, Dialog<Void> dialog) {
        dialog.close();

        LOG.info("User requested to delete his personal information");
        ServiceController<Void> task = new ServiceController<>();
        task.setOnFailed(e -> ExceptionHandler.instance().handleException(task.getException()));
        task.start(() -> deletePersonalData(userId));
    }

    private void deletePersonalData(String userId) throws BackingStoreException {
        PreferenceHandler preference = PreferenceHandler.getInstance();
        preference.clear();
        UserPropertyRestClientHolder.getInstance().deleteUserData(userId);
        EventBus.getInstance().post(new UserDataDeletedEvent());
    }
}
