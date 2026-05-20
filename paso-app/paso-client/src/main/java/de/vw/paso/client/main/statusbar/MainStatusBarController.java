package de.vw.paso.client.main.statusbar;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.delegate.mbtimport.MbtImportRestClientHolder;
import de.vw.paso.service.buildinfo.ServerBuildInfoDTO;
import de.vw.paso.utility.StringConstant;

@FXController(name = "main-status-bar")
public class MainStatusBarController extends BaseController<BorderPane> implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private Label buildInfoLabel;
    @FXML
    private Label labelStatusText;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label labelConfidential;

    @Override
    public BorderPane getControl() {
        return borderPane;
    }

    @Override
    public Parent getStyleableParent() {
        return borderPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    public void setBuildInfo(ServerBuildInfoDTO buildInfo) {
        labelConfidential.setText(I18N.getString("status.text.confidential"));

        String text = I18N.getString("status.text.connected") + StringConstant.COLON_SPACE + buildInfo.getStage();
        if (UserProperties.getUser().isAdmin()) {
            Date importDate = MbtImportRestClientHolder.getInstance().getImportDateForFile();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(importDate);

            text +=
                StringConstant.COMMA_SPACE + I18N.getString("status.mbt.import") + StringConstant.COLON_SPACE + date;
        }
        buildInfoLabel.setText(text);

        String buildNumber =
            I18N.getString("status.tooltip.build.number") + StringConstant.COLON_SPACE + buildInfo.getBuildNumber();
        String buildDate =
            I18N.getString("status.tooltip.build.date") + StringConstant.COLON_SPACE + buildInfo.getBuildDate();
        String profiles = I18N.getString("status.tooltip.profiles") + StringConstant.COLON_SPACE + Arrays.asList(
            buildInfo.getProfiles());
        String toolTipText = buildNumber + "\n" + buildDate + "\n" + profiles;
        buildInfoLabel.setTooltip(new Tooltip(toolTipText));
    }

    @Subscribe
    private void handleStatusBarEvent(MainStatusBarEvent e) {
        labelStatusText.textProperty().bind(e.progressTask().messageProperty());
        progressBar.progressProperty().bind(e.progressTask().progressProperty());

        labelStatusText.visibleProperty().bind(e.service().runningProperty());
        progressBar.visibleProperty().bind(e.service().runningProperty());
    }
}
