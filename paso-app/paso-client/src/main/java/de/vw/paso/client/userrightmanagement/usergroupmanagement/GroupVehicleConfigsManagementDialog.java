package de.vw.paso.client.userrightmanagement.usergroupmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.listview.DualListView;
import de.vw.paso.client.valueobject.UserGroupVMO;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class GroupVehicleConfigsManagementDialog extends BaseDialogController<UserGroupVMO> {

    private final UserGroupVMO selectedUserGroup;
    private DualListView<VehicleConfigDTO> dualList;

    GroupVehicleConfigsManagementDialog(UserGroupVMO userGroup) {
        this.selectedUserGroup = userGroup;

        initialize(I18N.getString("dialog.vehicleconfig.title"), () -> {
            resizableProperty().setValue(true);
            initContent();
        });
    }

    private void initContent() {

        List<VehicleConfigDTO> allVehicleConfigs = VehicleConfigRestClientHolder.getInstance()
                .loadNonDeletedVehicleConfigs().vehicleConfigDTOList();

        List<VehicleConfigDTO> selected = selectedUserGroup.getVehicleConfigs().stream()
                .filter(vehicleConfigDTO -> vehicleConfigDTO.getDeletionDate() == null)
                .sorted(VehicleConfigDTO.getComparator()).collect(Collectors.toCollection(ArrayList::new));
        List<Long> selectedConfigIds = selected.stream().map(VehicleConfigDTO::getId).toList();
        List<VehicleConfigDTO> availableConfigs = allVehicleConfigs.stream()
                .filter(config -> !selectedConfigIds.contains(config.getId())).sorted(VehicleConfigDTO.getComparator())
                .collect(Collectors.toList());

        dualList = new DualListView<>(availableConfigs, selected, false);
        dualList.addChangeListener(change -> {
            List<VehicleConfigDTO> dualListSelectedItems = dualList.getSelectedItems();
            dualListSelectedItems.sort(VehicleConfigDTO.getComparator());
            commitButton.setDisable(selected.equals(dualListSelectedItems));
        });

        VBox content = new VBox(5);
        VBox.setVgrow(dualList, Priority.ALWAYS);
        content.setPrefWidth(1200);
        content.setPrefHeight(650);
        content.getChildren().add(dualList);
        grid.add(content, 0, 0);
        getDialogPane().setContent(content);
    }

    @Override
    protected ChangeListener getValidationListener() {
        return null;
    }

    @Override
    protected ListChangeListener getValidationListenerForList() {
        return null;
    }

    @Override
    protected boolean isInvalid() {
        return false;
    }

    @Override
    protected UserGroupVMO dialogResult() {

        selectedUserGroup.setVehicleConfigs(dualList.getSelectedItems());

        return selectedUserGroup;
    }

}
