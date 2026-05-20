package de.vw.paso.client.userrightmanagement.usergroupmanagement;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.valueobject.UserGroupVMO;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.vehicle.OwnedVehicleConfigDTO;
import org.apache.commons.lang3.StringUtils;

public class GroupManagementDialog extends BaseDialogController<UserGroupVMO> {

  private final TextField groupNameTextField = new TextField();
  private final ComboBox<String> brandComboBox = new ComboBox<>();
  private final CheckBox isWriteAccess = new CheckBox();
  private final UserGroupVMO selectedUserGroup;
  private final Set<String> allUserGroupNames;

  GroupManagementDialog(String title, Collection<UserGroupVMO> allUserGroupVMOs, UserGroupVMO userGroup) {

    this.selectedUserGroup = userGroup;
    this.allUserGroupNames = allUserGroupVMOs.stream().map(UserGroupVMO::getUserGroupName).collect(Collectors.toSet());

    initialize(title, this::initContent);
  }

  private void initContent() {
    setPromptText(StringUtils.EMPTY, brandComboBox);
    brandComboBox.getItems().addAll(Brand.getAllBrands().stream().map(Brand::getBrandName).collect(Collectors.toSet()));
    setPromptText(StringUtils.EMPTY, groupNameTextField);
    isWriteAccess.setSelected(false);

    if (selectedUserGroup != null) {
      setTextToInputField(selectedUserGroup.getUserGroupName(), groupNameTextField);
      brandComboBox.getSelectionModel().select(selectedUserGroup.getBrand());
      isWriteAccess.setSelected(selectedUserGroup.isWriteAccess());
      List<Long> ownedVehicleConfigIds = selectedUserGroup.getOwnedVehicleConfigs().stream()
        .map(OwnedVehicleConfigDTO::getId).toList();
      if (!ownedVehicleConfigIds.isEmpty()) {
        isWriteAccess.setDisable(true);
      }
    }

    addLabelAndInputFieldToGrid(I18N.getString("label.brand"), brandComboBox);
    addLabelAndInputFieldToGrid(I18N.getString("label.groupName"), groupNameTextField);
    addLabelAndInputFieldToGrid(I18N.getString("label.hasWriteAccess"), isWriteAccess);

    addValidationListenerToInputField(brandComboBox);
    addValidationListenerToInputField(groupNameTextField);
    addValidationListenerToInputField(isWriteAccess);
  }

  @Override
  protected ChangeListener getValidationListener() {
    return (observable, oldValue, newValue) -> commitButton.setDisable(
      StringUtils.isEmpty(groupNameTextField.getText()) || StringUtils.isEmpty(
        brandComboBox.getSelectionModel().getSelectedItem()) || allUserGroupNames.contains(groupNameTextField.getText())
        || (groupNameTextField.getText().equals(selectedUserGroup.getUserGroupName())
        && brandComboBox.getSelectionModel().getSelectedItem().equals(selectedUserGroup.getBrand()) && (
        isWriteAccess.isSelected() == selectedUserGroup.isWriteAccess())));
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

    selectedUserGroup.setBrand(brandComboBox.getSelectionModel().getSelectedItem());
    selectedUserGroup.setUserGroupName(groupNameTextField.getText());
    selectedUserGroup.setWriteAccess(isWriteAccess.isSelected());

    return selectedUserGroup;
  }
}
