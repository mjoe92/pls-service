package de.vw.paso.client.userrightmanagement.rightmanagement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.userrightmanagement.AbstractUserRightManagementController;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.delegate.right.RightManagementRestClientHolder;
import de.vw.paso.service.right.AddUsersToRoleDTO;
import de.vw.paso.service.right.RightManagementRestService;
import de.vw.paso.service.right.RoleDTO;
import de.vw.paso.service.user.UserDTO;

@FXController(name = "right-management-tab")
public class RightManagementTabController extends AbstractUserRightManagementController<RoleDTO> {

  @FXML
  public Button manageUsers;

  @FXML
  private SplitPane splitPane;

  @FXML
  private TableColumn<RoleDTO, Long> colRoleId;

  @FXML
  private TableColumn<RoleDTO, String> colRoleName;

  @FXML
  private TableColumn<RoleDTO, String> colRoleDesc;


  @Override
  protected void initializeView() {
    tableView.setEditable(true);
    manageUsers.setDisable(true);
    tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleSelect(newValue));
  }

  @Override
  protected void initTableColumns() {
    colRoleId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
    colRoleName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    colRoleDesc.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
  }

  @Override
  protected void doLoad(Consumer<List<RoleDTO>> callback) {
    doAsync(() -> RightManagementRestClientHolder.getInstance().getAllRoles().roleDTOList(), callback);
  }

  @Override
  protected Comparator<? super RoleDTO> getItemComparator() {
    return Comparator.comparing(RoleDTO::getId);
  }

  @Override
  protected boolean getFilterCriteria(RoleDTO item, PasoWildCardPattern pattern) {
    return (pattern == null)
      || ((item.getDescription() != null) && (pattern.matches(item.getDescription()) != null))
      || ((item.getName() != null) && (pattern.matches(item.getName()) != null));
  }

  private void handleSelect(RoleDTO newValue) {
    manageUsers.setDisable(newValue == null);

    if (newValue != null) {
      List<UserDTO> roles = RightManagementRestClientHolder.getInstance().getUsersForRole(newValue.getId()).userDTOList();
      handleRoleTabChange(roles);
    }
  }

  private void handleRoleTabChange(List<UserDTO> users) {
    ListView<String> listView = new ListView<>();
    List<String> userIDs = new ArrayList<>();
    users.forEach(u -> userIDs.add(u.getId()));
    listView.setItems(FXCollections.observableArrayList(userIDs));
    listView.setPlaceholder(new Label(I18N.getString("label.role.noUsers")));

    BorderPane borderPane = new BorderPane();
    Label label = new Label(I18N.getString("label.assigned.users"));
    label.setPadding(new Insets(12, 5, 10, 5));
    borderPane.setTop(label);
    borderPane.setCenter(listView);

    if (splitPane.getItems().size() > 1) {
      splitPane.getItems().remove(1);
    }

    splitPane.setDividerPositions(0.77);
    splitPane.getItems().add(borderPane);
  }

  public void assignUsersToRole() {
    RoleDTO selectedRole = tableView.getSelectionModel().getSelectedItem();
    AssignUserDialog dialog;
    dialog = new AssignUserDialog(I18N.getString("dialog.rightmanagement.title"), selectedRole);

    openAssignRoleDialog(dialog, result -> result.ifPresent(users -> {
      RightManagementRestService rightManagementService = RightManagementRestClientHolder.getInstance();
      List<UserDTO> usersForRole = rightManagementService.getUsersForRole(selectedRole.getId()).userDTOList();

      List<String> existingUserIds = users.stream().map(UserDTO::getId).toList();
      if (users.isEmpty()) {
        rightManagementService.removeAllUsersFromRole(selectedRole.getId());
      } else {
        usersForRole.forEach(u -> {
          if (!existingUserIds.contains(u.getId())) {
            rightManagementService.removeRoleFromUser(u.getId(), selectedRole.getId());
          }
        });
        Set<String> userIds = new HashSet<>();
        users.forEach(u -> userIds.add(u.getId()));
        rightManagementService.addUsersToRole(new AddUsersToRoleDTO(userIds, selectedRole.getId()));
      }
      tableView.getSelectionModel().clearSelection();
      tableView.getSelectionModel().select(selectedRole);
    }));
  }

  private void openAssignRoleDialog(AssignUserDialog dialog, Consumer<Optional<List<UserDTO>>> callback) {
    callback.accept(dialog.showAndWait());
  }

}
