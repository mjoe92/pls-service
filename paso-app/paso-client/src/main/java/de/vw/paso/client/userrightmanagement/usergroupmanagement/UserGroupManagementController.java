package de.vw.paso.client.userrightmanagement.usergroupmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.userrightmanagement.AbstractUserRightManagementController;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.util.converter.BooleanStringConverter;
import de.vw.paso.client.valueobject.UserGroupVMO;
import de.vw.paso.delegate.usergroupservice.UserGroupRestClientHolder;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.service.usergroup.UserGroupDTO;
import de.vw.paso.service.usergroup.UserGroupRestService;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.Pair;

@FXController(name = "user-group-management")
public class UserGroupManagementController extends AbstractUserRightManagementController<UserGroupVMO> {

    @FXML
    private SplitPane splitPane;
    @FXML
    private TableColumn<UserGroupVMO, String> colGroupName;
    @FXML
    private TableColumn<UserGroupVMO, String> colBrand;
    @FXML
    private TableColumn<UserGroupVMO, Boolean> colHasWriteAccess;
    @FXML
    private Button editGroup;
    @FXML
    private Button manageUsersOfGroup;
    @FXML
    private Button manageVehicleConfigsOfGroup;

    @Override
    protected void initializeView() {
        disableButtons();
        tableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleSelect(newValue));
    }

    private void disableButtons() {
        this.editGroup.setDisable(true);
        this.manageUsersOfGroup.setDisable(true);
        this.manageVehicleConfigsOfGroup.setDisable(true);
    }

    @Override
    protected void doLoad(Consumer<List<UserGroupVMO>> callback) {
        doAsync(() -> UserGroupVMO.toVMOs(
                UserGroupRestClientHolder.getInstance().getAllUserGroups().userGroupDTOList()), callback);
    }

    @Override
    protected Comparator<? super UserGroupVMO> getItemComparator() {
        return Comparator.comparing(UserGroupVMO::getUserGroupId);
    }

    @Override
    protected boolean getFilterCriteria(UserGroupVMO item, PasoWildCardPattern pattern) {
        return pattern == null || (item.getUserGroupName() != null && pattern.matches(item.getUserGroupName()) != null)
                || (item.getBrand() != null && pattern.matches(item.getBrand()) != null);
    }

    @Override
    protected void initTableColumns() {
        colHasWriteAccess.setCellValueFactory(cellData -> cellData.getValue().writeAccessProperty());
        colHasWriteAccess.setCellFactory(column -> {
            TextFieldTableCell<UserGroupVMO, Boolean> cell = new TextFieldTableCell<>();

            cell.setConverter(new BooleanStringConverter());

            return cell;
        });
        colBrand.setCellValueFactory(cellData -> cellData.getValue().brandProperty());
        colGroupName.setCellValueFactory(cellData -> cellData.getValue().userGroupNameProperty());
    }

    private void handleSelect(UserGroupVMO newValue) {
        if (splitPane.getItems().size() > 2) {
            splitPane.getItems().remove(1, 3);
        }

        if (newValue == null) {
            disableButtons();
            return;
        }

        doAsync(() -> {
            UserGroupRestService userGroupRestService = UserGroupRestClientHolder.getInstance();
            List<UserDTO> groupUsers = userGroupRestService.getGroupUsers(newValue.getUserGroupId()).userDTOList();
            List<VehicleConfigDTO> vehicleConfigsFromUserGroup = userGroupRestService.getVehicleConfigsFromUserGroup(
                    newValue.getUserGroupId()).vehicleConfigDTOList();
            return new Pair<>(groupUsers, vehicleConfigsFromUserGroup);
        }, pair -> {
            editGroup.setDisable(false);
            manageUsersOfGroup.setDisable(false);
            manageVehicleConfigsOfGroup.setDisable(false);

            handleGroupTabChange(pair.first(), pair.second());
        });
    }

    private void handleGroupTabChange(Collection<UserDTO> groupUsers, List<VehicleConfigDTO> vehicleConfigs) {
        if (groupUsers != null) {
            ObservableList<String> userIds = groupUsers.stream().sorted(UserDTO.getComparator()).map(UserDTO::toString)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            ListView<String> listView = new ListView<>();
            listView.setItems(userIds);
            listView.setPlaceholder(new Label(I18N.getString("label.group.noUsers")));

            BorderPane borderPane = new BorderPane();
            Label label = new Label(I18N.getString("label.assigned.users"));
            label.setPadding(new Insets(12, 5, 10, 5));
            borderPane.setTop(label);
            borderPane.setCenter(listView);
            borderPane.setMinWidth(200);
            borderPane.prefWidth(300);

            splitPane.getItems().add(borderPane);
        }

        if (vehicleConfigs != null) {
            ObservableList<String> vehicleConfigNames = vehicleConfigs.stream().sorted(VehicleConfigDTO.getComparator())
                    .map(VehicleConfigDTO::toString)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            ListView<String> listView = new ListView<>();
            listView.setItems(vehicleConfigNames);
            listView.setPlaceholder(new Label(I18N.getString("label.noVehicleConfigs")));

            BorderPane borderPane = new BorderPane();
            Label label = new Label(I18N.getString("label.assigned.vehicle.configs"));
            label.setPadding(new Insets(12, 5, 10, 5));
            borderPane.setTop(label);
            borderPane.setCenter(listView);
            borderPane.setMinWidth(200);
            borderPane.prefWidth(300);

            splitPane.setDividerPositions(0.70, 0.80);
            splitPane.getItems().add(borderPane);
        }
    }

    @FXML
    private void createUserGroup() {
        UserGroupVMO userGroupVMO = new UserGroupVMO();
        GroupManagementDialog dialog = createGroupManagementDialog(I18N.getString("dialog.costgroup.title"),
                userGroupVMO, tableView.getItems());
        if (dialog.isCancelled()) {
            return;
        }

        UserGroupDTO userGroup = UserGroupVMO.toUserGroup(dialog.dialogResult());
        saveOrUpdateUserGroup(userGroup);
    }

    @FXML
    private void editUserGroup() {
        UserGroupVMO userGroupVMO = tableView.getSelectionModel().getSelectedItem();
        Collection<UserGroupVMO> allUserGroupVMOs = new ArrayList<>(tableView.getItems());
        allUserGroupVMOs.remove(userGroupVMO);

        GroupManagementDialog dialog = createGroupManagementDialog(I18N.getString("dialog.costgroup.edit.title"),
                userGroupVMO, allUserGroupVMOs);
        if (dialog.isCancelled()) {
            return;
        }

        UserGroupDTO userGroup = UserGroupVMO.toUserGroup(dialog.dialogResult());
        saveOrUpdateUserGroup(userGroup);
    }

    @FXML
    private void manageUsersOfGroup() {
        UserGroupVMO userGroupVMO = tableView.getSelectionModel().getSelectedItem();
        GroupUsersManagementDialog dialog = createGroupUsersManagementDialog(userGroupVMO);
        if (dialog.isCancelled()) {
            return;
        }

        UserGroupDTO userGroup = UserGroupVMO.toUserGroup(dialog.dialogResult());
        saveOrUpdateUserGroup(userGroup);
    }

    @FXML
    private void manageVehicleConfigsOfGroup() {
        UserGroupVMO userGroupVMO = tableView.getSelectionModel().getSelectedItem();
        GroupVehicleConfigsManagementDialog dialog = createGroupVehicleConfigsManagementDialog(userGroupVMO);
        if (dialog.isCancelled()) {
            return;
        }

        UserGroupDTO userGroup = UserGroupVMO.toUserGroup(dialog.dialogResult());
        saveOrUpdateUserGroup(userGroup);
    }

    private void saveOrUpdateUserGroup(UserGroupDTO userGroup) {
        doAsync(() -> {
            userGroup.setChange(UserProperties.getUserId());
            UserGroupRestClientHolder.getInstance().saveUserGroup(userGroup);
            handleActionRefresh();
        }, () -> tableView.getSelectionModel().clearSelection());
    }

    private GroupManagementDialog createGroupManagementDialog(String title, UserGroupVMO userGroupVMO,
            Collection<UserGroupVMO> allUserGroupVMOs) {
        GroupManagementDialog dialog = new GroupManagementDialog(title, allUserGroupVMOs, userGroupVMO);
        dialog.showAndWait().ifPresent(groups -> tableView.getSelectionModel().select(userGroupVMO));
        return dialog;
    }

    private GroupUsersManagementDialog createGroupUsersManagementDialog(UserGroupVMO userGroupVMO) {
        GroupUsersManagementDialog dialog = new GroupUsersManagementDialog(userGroupVMO);
        dialog.showAndWait().ifPresent(groups -> tableView.getSelectionModel().select(userGroupVMO));
        return dialog;
    }

    private GroupVehicleConfigsManagementDialog createGroupVehicleConfigsManagementDialog(UserGroupVMO userGroupVMO) {
        GroupVehicleConfigsManagementDialog dialog = new GroupVehicleConfigsManagementDialog(userGroupVMO);
        dialog.showAndWait().ifPresent(groups -> tableView.getSelectionModel().select(userGroupVMO));
        return dialog;
    }
}