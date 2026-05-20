package de.vw.paso.client.userrightmanagement.rightmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.valueobject.UserVMO;
import de.vw.paso.delegate.right.RightManagementRestClientHolder;
import de.vw.paso.delegate.stueckliste.user.UserRestClientHolder;
import de.vw.paso.service.right.RoleDTO;
import de.vw.paso.service.user.UserDTO;
import org.apache.commons.lang3.StringUtils;

public class AssignUserDialog extends BaseDialogController<List<UserDTO>> {

    private final GridPane gridpane;
    private final CustomTableView<UserVMO> userTableView;

    private final TableColumn<UserVMO, Boolean> colCheckBox;
    private final TableColumn<UserVMO, String> colUserId;
    private final TableColumn<UserVMO, String> colFirstName;
    private final TableColumn<UserVMO, String> colLastName;
    private final TableColumn<UserVMO, String> colEmail;

    private final ComboBox<String> filterModeBox;

    private Collection<UserVMO> items;
    private PasoWildCardPattern patternSearchTerm;
    private ObservableList<UserDTO> selectedUsers;

    AssignUserDialog(String title, RoleDTO role) {
        gridpane = new GridPane();
        userTableView = new CustomTableView<>();
        colCheckBox = new TableColumn<>();
        colUserId = new TableColumn<>();
        colFirstName = new TableColumn<>();
        colLastName = new TableColumn<>();
        colEmail = new TableColumn<>();
        filterModeBox = new ComboBox<>();
        items = new ArrayList<>();
        selectedUsers = FXCollections.observableArrayList();

        initialize(title, () -> {
            commitButton.setDisable(false);

            getUsersForRole(role);
            setHeaderText(I18N.getString("dialog.rightmanagement.header") + StringUtils.SPACE + role.getName());
            addValidationListenerToInputField(selectedUsers);

            getDialogPane().setContent(gridpane);
            getDialogPane().setPrefWidth(1200);

            initContent();
            initTable();
            initColumns();
        });
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return null;
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return c -> {
            c.next();
            commitButton.setDisable(false);
        };
    }

    @Override
    protected List<UserDTO> dialogResult() {
        return selectedUsers;
    }

    private void initContent() {
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(100);
        gridpane.getColumnConstraints().add(column);

        GridPane searchPane = new GridPane();
        searchPane.setHgap(5);
        searchPane.setPadding(new Insets(0, 0, 10, 0));

        Label label = new Label(I18N.getString("search"));
        TextField searchTextField = new TextField();
        searchTextField.setOnKeyReleased(event -> handleSearch(searchTextField.getText()));

        filterModeBox.getItems()
                .addAll(I18N.getString("filtermode.users.showall"), I18N.getString("filtermode.users.selectedOnly"),
                        I18N.getString("filtermode.users.notSelectedOnly"),
                        I18N.getString("filtermode.users.disabledUsersOnly"),
                        I18N.getString("filtermode.users.enabledUsersOnly"));
        filterModeBox.setValue(I18N.getString("filtermode.users.showall"));
        filterModeBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            for (String mode : filterModeBox.getItems()) {
                if (mode.matches(newValue)) {
                    fillTable();
                }
            }
        });

        searchPane.add(label, 0, 0);
        searchPane.add(searchTextField, 1, 0);
        searchPane.add(filterModeBox, 2, 0);

        gridpane.add(searchPane, 0, 0);
        gridpane.add(userTableView, 0, 1);
    }

    private void initTable() {
        userTableView.setEditable(true);
        userTableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(UserVMO item, boolean empty) {
                super.updateItem(item, empty);

                setStyle(null);
                setDisable(false);

                if (item != null && !item.isActive()) {
                    setDisable(true);
                    setStyle("-fx-text-background-color: rgb(169,169,169);");
                }
            }
        });

        userTableView.setItems(FXCollections.observableArrayList(items));
        userTableView.getColumns().addAll(colCheckBox, colUserId, colFirstName, colLastName, colEmail);
    }

    private void initColumns() {
        colCheckBox.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colCheckBox.setCellFactory(CheckBoxTableCell.forTableColumn(param -> {
            UserVMO item = userTableView.getItems().get(param);

            item.selectedProperty().addListener(l -> updateSelectedItems(item));

            return item.selectedProperty();
        }));

        colCheckBox.setEditable(true);
        colUserId.setCellValueFactory(cellData -> cellData.getValue().userIdProperty());
        colUserId.setText(I18N.getString("label.userId"));
        colFirstName.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        colFirstName.setText(I18N.getString("label.firstName"));
        colLastName.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        colLastName.setText(I18N.getString("label.lastName"));
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colEmail.setText(I18N.getString("label.email"));
    }

    private void fillTable() {
        userTableView.getSelectionModel().clearSelection();

        if (filterModeBox.getValue().equals(I18N.getString("filtermode.users.showall"))) {
            userTableView.setItems(FXCollections.observableArrayList(
                    items.stream().filter(item -> getFilterCriteria(item, patternSearchTerm))
                            .collect(Collectors.toList())));
        } else if (filterModeBox.getValue().equals(I18N.getString("filtermode.users.selectedOnly"))) {
            userTableView.setItems(FXCollections.observableArrayList(
                    items.stream().filter(item -> getFilterCriteria(item, patternSearchTerm) && item.isSelected())
                            .collect(Collectors.toList())));
        } else if (filterModeBox.getValue().equals(I18N.getString("filtermode.users.notSelectedOnly"))) {
            userTableView.setItems(FXCollections.observableArrayList(
                    items.stream().filter(item -> getFilterCriteria(item, patternSearchTerm) && !item.isSelected())
                            .collect(Collectors.toList())));
        } else if (filterModeBox.getValue().equals(I18N.getString("filtermode.users.disabledUsersOnly"))) {
            userTableView.setItems(FXCollections.observableArrayList(
                    items.stream().filter(item -> getFilterCriteria(item, patternSearchTerm) && !item.isActive())
                            .collect(Collectors.toList())));
        } else if (filterModeBox.getValue().equals(I18N.getString("filtermode.users.enabledUsersOnly"))) {
            userTableView.setItems(FXCollections.observableArrayList(
                    items.stream().filter(item -> getFilterCriteria(item, patternSearchTerm) && item.isActive())
                            .collect(Collectors.toList())));
        }
    }

    private void getUsersForRole(RoleDTO role) {
        this.items = convertToVMOs(UserRestClientHolder.getInstance().getAllUser().userDTOList(),
                RightManagementRestClientHolder.getInstance().getUsersForRole(role.getId()).userDTOList());
    }

    private Collection<UserVMO> convertToVMOs(List<UserDTO> users, List<UserDTO> usersForRole) {
        Collection<String> userIdsForRole = usersForRole.stream().map(UserDTO::getId).toList();
        Collection<UserVMO> userVMOS = new ArrayList<>(users.size());
        for (UserDTO user : users) {
            UserVMO userVMO = UserVMO.toVMO(user);
            if (userIdsForRole.contains(user.getId())) {
                userVMO.setSelected(true);
                selectedUsers.add(user);
            }

            userVMOS.add(userVMO);
        }

        return userVMOS;
    }

    private void handleSearch(String text) {
        try {
            patternSearchTerm = new PasoWildCardPattern(text);
            fillTable();
        } catch (Exception e) {
            ExceptionHandler.instance().handleException(e);
        }
    }

    private void updateSelectedItems(UserVMO item) {
        UserDTO user = new UserDTO();
        user.setId(item.getUserId());
        user.setFirstName(item.getFirstName());
        user.setLastName(item.getLastName());
        user.setEmail(item.getEmail());

        if (!item.isSelected()) {
            selectedUsers = selectedUsers.stream().filter(userDTO -> !userDTO.getId().equals(user.getId()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            return;
        }

        if (!selectedUsers.contains(user)) {
            selectedUsers.add(user);
        }
    }

    private boolean getFilterCriteria(UserVMO item, PasoWildCardPattern pattern) {
        return pattern == null || (item.getUserId() != null && pattern.matches(item.getUserId()) != null) || (
                item.getFirstName() != null && pattern.matches(item.getFirstName()) != null) || (
                item.getLastName() != null && pattern.matches(item.getLastName()) != null) || (item.getEmail() != null
                && pattern.matches(item.getEmail()) != null);
    }
}
