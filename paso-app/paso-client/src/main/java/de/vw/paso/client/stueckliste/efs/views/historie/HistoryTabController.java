package de.vw.paso.client.stueckliste.efs.views.historie;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabType;
import de.vw.paso.client.util.icon.StuecklisteIcon;
import de.vw.paso.delegate.stueckliste.efselementhistory.EfsElementHistoryRestClientHolder;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efselementhistory.EfsElementDTOWrapper;
import de.vw.paso.utility.StringConstant;

@FXController(name = "efs-history-tab")
public class HistoryTabController extends AbstractHistoryTabController {

    private EfsElementDTO efsElement;

    @FXML
    private Tab efsHistoryTab;

    @Override
    public Tab getControl() {
        return efsHistoryTab;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        efsHistoryTab.setGraphic(new ImageView(StuecklisteIcon.HISTORIE_16X16.getImage()));
    }

    public void initEfsElement(EfsElementDTO efsElement, Consumer<HistoryTabController> action) {
        this.efsElement = efsElement;

        String historieStr = I18N.getString("tab.efs.history.title");

        if (efsElement == null) {
            efsHistoryTab.setText(historieStr);
            getEfsElementHistoryTreeModel().removeAllElements();

            return;
        }

        String additionalTabTitle = parseAdditionalTabTitle();
        efsHistoryTab.setText(
                historieStr + StringConstant.SPACE_DASH_SPACE + efsElement.getEfsElementMara().getPartNumber()
                        + additionalTabTitle);

        ServiceController<List<EfsElementDTOWrapper>> serviceController = new ServiceController<>();
        serviceController.setOnSucceeded(e -> {
            handleLoadEfsHistorie(serviceController.getValue());
            if (action != null) {
                action.accept(this);
            }
        });
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        serviceController.setExecutionTime(250);
        serviceController.start(
                () -> EfsElementHistoryRestClientHolder.getInstance().loadHistoryList(efsElement.getId())
                        .convertToEfsElementHistoryDTO());
    }

    @Override
    protected EfsViewTabType getType() {
        return EfsViewTabType.HISTORY;
    }

    private String parseAdditionalTabTitle() {
        StringBuilder sb = new StringBuilder();

        if (efsElement.getEfsElementMara().getDescription1De() != null && !efsElement.getEfsElementMara()
                .getDescription1De().equals(StringConstant.EMPTY)) {
            sb.append(StringConstant.SPACE_SLASH_SPACE).append(efsElement.getEfsElementMara().getDescription1De());
        }

        if (efsElement.getEfsElementMara().getDescription2De() != null && !efsElement.getEfsElementMara()
                .getDescription2De().equals(StringConstant.EMPTY)) {
            sb.append(StringConstant.SPACE_SLASH_SPACE).append(efsElement.getEfsElementMara().getDescription2De());
        }

        return sb.toString();
    }

    private void handleLoadEfsHistorie(List<EfsElementDTOWrapper> efsHistoryElements) {
        getEfsElementHistoryTreeModel().setEfsHistoryElemente(efsHistoryElements);
        efsHistoryTreeTableView.getRoot().setExpanded(true);
    }
}
