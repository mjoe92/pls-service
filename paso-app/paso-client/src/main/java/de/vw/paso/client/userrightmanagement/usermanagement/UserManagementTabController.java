package de.vw.paso.client.userrightmanagement.usermanagement;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.personaldata.PersonalDataManager;
import de.vw.paso.client.userrightmanagement.AbstractUserRightManagementController;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.util.converter.BooleanStringConverter;
import de.vw.paso.client.valueobject.UserVMO;
import de.vw.paso.delegate.right.RightManagementRestClientHolder;
import de.vw.paso.delegate.stueckliste.user.UserRestClientHolder;
import de.vw.paso.service.right.AddRolesToUserDTO;
import de.vw.paso.service.right.RightManagementRestService;
import de.vw.paso.service.right.RoleDTO;

@FXController(name = "user-management-tab")
public class UserManagementTabController extends AbstractUserRightManagementController<UserVMO> {

    public static final String HIGHLIGHT_ROW_INACTIVE = "highlight-row-inactive";

    @FXML
    private Button manageRoles;
    @FXML
    private Button enableDisableUserButton;
    @FXML
    private Button deleteUserDataButton;
    @FXML
    private Button resetUserButton;
    @FXML
    private SplitPane splitPane;
    @FXML
    private TableColumn<UserVMO, Boolean> colActive;
    @FXML
    private TableColumn<UserVMO, String> colUserId;
    @FXML
    private TableColumn<UserVMO, String> colFirstName;
    @FXML
    private TableColumn<UserVMO, String> colLastName;
    @FXML
    private TableColumn<UserVMO, String> colEmail;
    @FXML
    private TableColumn<UserVMO, String> colRoles;
    @FXML
    private TableColumn<UserVMO, String> colInactivityInfo;

    @Override
    protected void initializeView() {
        manageRoles.setDisable(true);
        enableDisableUserButton.setDisable(true);
        deleteUserDataButton.setDisable(true);
        resetUserButton.setDisable(true);
        tableView.setEditable(true);
        tableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleSelect(newValue));
        tableView.setRowFactory(param -> new TableRow<>() {
            @Override
            protected void updateItem(UserVMO item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().remove(HIGHLIGHT_ROW_INACTIVE);
                if (item == null) {
                    return;
                }

                item.activeProperty().addListener((observable, oldValue, newValue) -> {
                    getStyleClass().remove(HIGHLIGHT_ROW_INACTIVE);
                    if (!newValue) {
                        getStyleClass().add(HIGHLIGHT_ROW_INACTIVE);
                    }
                });

                if (!item.isActive()) {
                    getStyleClass().add(HIGHLIGHT_ROW_INACTIVE);
                }
            }
        });
    }

    @Override
    protected void initTableColumns() {
        colActive.setCellValueFactory(cellData -> cellData.getValue().activeProperty());
        colActive.setMinWidth(70);
        colActive.setCellFactory(column -> {
            TextFieldTableCell<UserVMO, Boolean> cell = new TextFieldTableCell<>();

            cell.setConverter(new BooleanStringConverter());

            return cell;
        });
        colUserId.setCellValueFactory(cellData -> cellData.getValue().userIdProperty());
        colFirstName.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        colLastName.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        colEmail.setMinWidth(150);
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colRoles.setCellValueFactory(cellData -> cellData.getValue().rolesProperty());
        colInactivityInfo.setMinWidth(400);
        colInactivityInfo.setMaxWidth(500);
        colInactivityInfo.setCellValueFactory(cellData -> cellData.getValue().inactivityInfoProperty());
    }

    @Override
    protected void doLoad(Consumer<List<UserVMO>> callback) {
        doAsync(() -> UserVMO.toVMOs(UserRestClientHolder.getInstance().getAllUser().userDTOList()), callback);
    }

    @Override
    protected Comparator<? super UserVMO> getItemComparator() {
        return Comparator.comparing(UserVMO::getUserId);
    }

    @Override
    protected boolean getFilterCriteria(UserVMO item, PasoWildCardPattern pattern) {
        return pattern == null || (item.getUserId() != null && pattern.matches(item.getUserId()) != null) || (
                item.getFirstName() != null && pattern.matches(item.getFirstName()) != null) || (
                item.getLastName() != null && pattern.matches(item.getLastName()) != null) || (item.getEmail() != null
                && pattern.matches(item.getEmail()) != null);
    }

    @FXML
    private void assignRolesToUser() {
        TableView.TableViewSelectionModel<UserVMO> selectionModel = tableView.getSelectionModel();
        UserVMO userVMO = selectionModel.getSelectedItem();
        if (!userVMO.isActive()) {
            return;
        }

        String userId = userVMO.getUserId();
        AssignRoleDialog dialog = new AssignRoleDialog(I18N.getString("dialog.roles.title"), userId);
        openAssignRoleDialog(dialog, result -> result.ifPresent(roles -> {
            RightManagementRestService rightManagementService = RightManagementRestClientHolder.getInstance();
            Collection<RoleDTO> rolesForUser = rightManagementService.getRolesForUser(userId).roleDTOList();

            //todo: simplify with one server call
            if (roles.isEmpty()) {
                rightManagementService.removeAllRolesFromUser(userId);
            } else {
                for (RoleDTO role : rolesForUser) {
                    if (!roles.contains(role)) {
                        rightManagementService.removeRoleFromUser(userId, role.getId());
                    }
                }

                List<Long> roleIds = roles.stream().map(RoleDTO::getId).toList();
                rightManagementService.addRolesToUser(new AddRolesToUserDTO(userId, roleIds));
            }

            selectionModel.clearSelection();
            selectionModel.select(userVMO);

            handleActionRefresh();
        }));
    }

    @FXML
    private void enableDisableUser() {
        UserVMO selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser.isActive()) {
            selectedUser.setActive(false);
            UserRestClientHolder.getInstance().disableUser(selectedUser.getUserId());
        } else {
            selectedUser.setActive(true);
            UserRestClientHolder.getInstance().enableUser(selectedUser.getUserId());
        }

        handleActionRefresh();
    }

    @FXML
    private void deleteUserData() {
        String selectedUserId = tableView.getSelectionModel().getSelectedItem().getUserId();
        new PersonalDataManager().askDeletePersonalData(selectedUserId);
    }

    @FXML
    private void resetCostCenterChangedAt() {
        String selectedUserId = tableView.getSelectionModel().getSelectedItem().getUserId();
        UserRestClientHolder.getInstance().resetCostCenterChangedAt(selectedUserId);
        handleActionRefresh();
    }

    private void handleSelect(UserVMO newValue) {
        manageRoles.setDisable(newValue == null);
        deleteUserDataButton.setDisable(newValue == null);

        if (newValue == null) {
            enableDisableUserButton.setDisable(true);
            resetUserButton.setDisable(true);

            return;
        }

        Collection<RoleDTO> roles = RightManagementRestClientHolder.getInstance().getRolesForUser(newValue.getUserId())
                .roleDTOList();
        handleRoleTabChange(roles);

        enableDisableUserButton.setDisable(newValue.getUserId().equals(UserProperties.getUser().getId()));
        resetUserButton.setDisable(!newValue.isActive() || newValue.getInactivityInfo().isEmpty());
    }

    private void handleRoleTabChange(Collection<RoleDTO> roles) {
        if (splitPane.getItems().size() > 1) {
            splitPane.getItems().remove(1);
        }

        if (roles == null) {
            return;
        }

        ObservableList<String> roleNames = roles.stream().map(RoleDTO::getName)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        ListView<String> listView = new ListView<>();
        listView.setItems(roleNames);
        listView.setPlaceholder(new Label(I18N.getString("label.noRoles")));

        Label label = new Label(I18N.getString("label.assigned.roles"));
        label.setPadding(new Insets(12, 5, 10, 5));

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(label);
        borderPane.setCenter(listView);

        splitPane.setDividerPositions(0.77);
        splitPane.getItems().add(borderPane);
    }

    private void openAssignRoleDialog(AssignRoleDialog dialog, Consumer<Optional<List<RoleDTO>>> callback) {
        callback.accept(dialog.showAndWait());
    }
}