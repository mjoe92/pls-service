package de.vw.paso.client.stueckliste.efs.views.historie;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.control.textfield.PasoCustomTextField;
import de.vw.paso.client.control.textfield.PasoNumberField;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementHistoryTreeItem;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabType;
import de.vw.paso.client.stueckliste.efs.views.historie.event.EfsElementSelectionEvent;
import de.vw.paso.client.util.icon.StuecklisteIcon;
import de.vw.paso.delegate.stueckliste.efselementhistory.EfsElementHistoryRestClientHolder;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import de.vw.paso.service.partlist.efselementhistory.EfsElementDTOWrapper;
import de.vw.paso.service.partlist.efselementhistory.EfsElementHistoryDTO;
import de.vw.paso.service.partlist.efselementhistory.RevertToRevisionDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.utility.EfsElementResolver;

@FXController(name = "efs-revision-tab")
public class RevisionTabController extends AbstractHistoryTabController {

    private BooleanProperty disablePropertyRevertHistory;

    @FXML
    private Tab efsRevisionTab;
    @FXML
    private Button buttonRevert;
    @FXML
    private PasoNumberField<?> numberFieldRevision;
    @FXML
    private PasoCustomTextField<?> textFieldPartNumber;

    private FilteredList<EfsElementDTOWrapper> filteredData;

    private VehiclePartListDTO vehiclePartList;

    @Override
    public Tab getControl() {
        return efsRevisionTab;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        buttonRevert.disableProperty().bind(disablePropertyRevertHistory());

        setActionStates();

        initFilters();

        efsRevisionTab.setGraphic(new ImageView(StuecklisteIcon.REVISIONEN_16X16.getImage()));
    }

    public void initRevisions(VehiclePartListDTO vehiclePartList, Consumer<RevisionTabController> action) {
        this.vehiclePartList = vehiclePartList;

        ServiceController<List<EfsElementDTOWrapper>> serviceController = new ServiceController<>();
        serviceController.setOnSucceeded(e -> {
            handleLoadRevisions(serviceController.getValue());

            if (action != null) {
                action.accept(this);
            }
        });
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        serviceController.setExecutionTime(250);
        serviceController.start(
                () -> EfsElementHistoryRestClientHolder.getInstance().loadRevisions(vehiclePartList.getId())
                        .convertToEfsElementHistoryDTO());
    }

    @Override
    protected EfsViewTabType getType() {
        return EfsViewTabType.REVISION;
    }

    @Override
    protected void initTreeTable() {
        super.initTreeTable();

        efsHistoryTreeTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleEfsHistorySelected((EfsElementHistoryTreeItem) newValue));
    }

    private void handleEfsHistorySelected(EfsElementHistoryTreeItem newTreeItem) {
        if (newTreeItem == null || newTreeItem.getValue() == null || newTreeItem.getUserObject() == null) {
            return;
        }

        efsSelectionProperty().get()
                .handle(new EfsElementSelectionEvent(this, EfsElementSelectionEvent.SELECT_EFS_ELEMENT_IN_TREE,
                        getEfsElementId(newTreeItem.getUserObject())));
    }

    private Long getEfsElementId(IEfsElementForDTO userObject) {
        if (userObject instanceof EfsElementDTO) {
            return userObject.getId();
        } else if (userObject instanceof EfsElementHistoryDTO efsElementHistory) {
            return efsElementHistory.getEfsElement().getId();
        }

        return null;
    }

    private void initFilters() {
        numberFieldRevision.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(efsElement -> {
                if ((newValue == null || newValue.isEmpty()) && efsElement.getEfsElementMara().getPartNumber()
                        .contains(numberFieldRevision.getText())) {
                    return true;
                }

                return efsElement.getRevision().toString().equals(newValue) && (
                        efsElement.getEfsElementMara().getPartNumber().contains(textFieldPartNumber.getText())
                                || textFieldPartNumber.getText().isEmpty());
            });

            getEfsElementHistoryTreeModel().setEfsHistoryElemente(filteredData);
        });

        textFieldPartNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(abstractEfsElement -> {
                if ((newValue == null || newValue.isEmpty()) && abstractEfsElement.getRevision().toString()
                        .equals(numberFieldRevision.getText())) {
                    return true;
                }

                return abstractEfsElement.getEfsElementMara().getPartNumber().contains(newValue) && (
                        abstractEfsElement.getRevision().toString().equals(numberFieldRevision.getText())
                                || numberFieldRevision.getText().isEmpty());
            });

            getEfsElementHistoryTreeModel().setEfsHistoryElemente(filteredData);
        });
    }

    private void handleLoadRevisions(List<EfsElementDTOWrapper> list) {
        setEfsElements(list);

        ChangeListener<? super TreeItem<IEfsElementForDTO>> treeItemChangeListener = (observable, oldValue, newValue) -> setActionStates();
        efsHistoryTreeTableView.getSelectionModel().selectedItemProperty().addListener(treeItemChangeListener);
    }

    private void setEfsElements(List<EfsElementDTOWrapper> histories) {
        ObservableList<EfsElementDTOWrapper> efsHistoryElements = FXCollections.observableArrayList(histories);

        filteredData = new FilteredList<>(efsHistoryElements, p -> true);
        getEfsElementHistoryTreeModel().setEfsHistoryElemente(filteredData);

        efsHistoryTreeTableView.getRoot().setExpanded(true);
    }

    private void setActionStates() {
        EfsElementHistoryTreeItem selectedTreeItem = getSelectedTreeItem();
        boolean disable;
        if (selectedTreeItem == null || selectedTreeItem.getUserObject() == null) {
            disable = true;
        } else {
            Long revision = selectedTreeItem.getUserObject().getRevision();
            disable = revision == null || revision.equals(vehiclePartList.getRevision());
        }

        disablePropertyRevertHistory.set(disable);
    }

    protected BooleanProperty disablePropertyRevertHistory() { // NO_UCD (use private)
        if (disablePropertyRevertHistory == null) {
            disablePropertyRevertHistory = new SimpleBooleanProperty(true);
        }

        return disablePropertyRevertHistory;
    }

    @FXML
    private void handleRevertButtonAction() {
        disablePropertyRevertHistory().set(true);
        EfsElementHistoryTreeItem selectedTreeItem = getSelectedTreeItem();
        if (selectedTreeItem == null) {
            throw new RuntimeException("handleRevertButtonAction: Selected tree item is null.");
        }

        IEfsElementForDTO efsElementHistory = selectedTreeItem.getValue();
        doAsync(() -> EfsElementHistoryRestClientHolder.getInstance().revertToRevision(
                        new RevertToRevisionDTO(efsElementHistory.getVehiclePartListId(), efsElementHistory.getRevision()))
                .efsElementList(), result -> {
            initRevisions(vehiclePartList, null);
            EfsElementResolver.registerElements(result);
        });
    }

    private EfsElementHistoryTreeItem getSelectedTreeItem() {
        return efsHistoryTreeTableView.getSelectionModel().isEmpty() ? null
                : (EfsElementHistoryTreeItem) efsHistoryTreeTableView.getSelectionModel().getSelectedItem();
    }
}
