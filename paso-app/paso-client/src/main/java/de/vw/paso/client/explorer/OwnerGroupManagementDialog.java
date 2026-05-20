package de.vw.paso.client.explorer;

import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.service.usergroup.UserGroupDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import org.apache.commons.lang3.StringUtils;

public class OwnerGroupManagementDialog extends BaseDialogController<VehicleConfigDTO> {

    private final Label selectedVehicleConfigName = new Label();
    private final ComboBox<UserGroupDTO> ownerGroupComboBox = new ComboBox<>();
    private final VehicleConfigDTO vehicleConfigDTO;
    private final UserGroupDTO selectedOwnerGroup;

    public OwnerGroupManagementDialog(VehicleConfigDTO vehicleConfigDTO) {
        this.vehicleConfigDTO = vehicleConfigDTO;
        selectedOwnerGroup = vehicleConfigDTO.getOwnerGroup();
        initialize(I18N.getString("owner.group.dialog.title"), () -> {
            selectedVehicleConfigName.textProperty().set(vehicleConfigDTO.getName());
            Set<UserGroupDTO> userGroups = UserProperties.getUser().getUserGroups().stream()
                    .filter(UserGroupDTO::isWriteAccess).collect(Collectors.toSet());
            ownerGroupComboBox.getItems().addAll(userGroups);
            ownerGroupComboBox.getSelectionModel().select(vehicleConfigDTO.getOwnerGroup());

            addLabelAndInputFieldToGrid(I18N.getString("label.selected.vehicle.name"), selectedVehicleConfigName);
            addLabelAndInputFieldToGrid(I18N.getString("label.owner.group.name"), ownerGroupComboBox);

            addValidationListenerToInputField(ownerGroupComboBox);
        });
    }

    @Override
    protected ChangeListener getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(
                StringUtils.isEmpty(ownerGroupComboBox.getSelectionModel().getSelectedItem().toString())
                        || ownerGroupComboBox.getSelectionModel().getSelectedItem().toString()
                        .equals(selectedOwnerGroup.toString()));
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
    protected VehicleConfigDTO dialogResult() {
        vehicleConfigDTO.setOwnerGroup(ownerGroupComboBox.getSelectionModel().getSelectedItem());
        return vehicleConfigDTO;
    }

}
