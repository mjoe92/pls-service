package de.vw.paso.client.privacy;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.scene.control.ButtonType;

import de.vw.paso.client.base.AbstractDialogController;
import de.vw.paso.client.base.I18N;

public class PrivacyDialog extends AbstractDialogController<Void> {

  public PrivacyDialog() {
    setTitle(I18N.getString("privacypolicy.dialog.title"));
    setHeight(600);
    setWidth(600);

    getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
  }

  public void loadPrivacyPolicy() throws URISyntaxException, IOException {
    Desktop.getDesktop().browse(new URI("https://dev.paso.gep.run/privacypolicy"));
  }
}
