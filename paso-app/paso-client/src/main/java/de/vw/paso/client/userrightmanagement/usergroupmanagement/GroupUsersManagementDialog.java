package de.vw.paso.client.userrightmanagement.usergroupmanagement;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.listview.DualListView;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.valueobject.UserGroupVMO;
import de.vw.paso.delegate.stueckliste.user.UserRestClientHolder;
import de.vw.paso.service.user.UserDTO;

public class GroupUsersManagementDialog extends BaseDialogController<UserGroupVMO> {

    private final Comparator<? super UserDTO> userComparator;
    private final UserGroupVMO selectedUserGroup;

    private DualListView<UserDTO> userDualListView;
    private List<UserDTO> allActiveUsers;

    GroupUsersManagementDialog(UserGroupVMO userGroup) {
        userComparator = UserDTO.getComparator();
        this.selectedUserGroup = userGroup;

        initialize(I18N.getString("dialog.groupmanagement.title"), this::initContent);
    }

    private void initContent() {
        allActiveUsers = UserRestClientHolder.getInstance().getAllActiveUsers().userDTOList();

        List<UserDTO> selected = selectedUserGroup.getUsers();
        selected.sort(userComparator);
        Collection<String> selectedUserIds = selected.stream().map(UserDTO::getId).toList();

        List<UserDTO> availableUsers = getAvailableUsers(selectedUserIds);

        userDualListView = new DualListView<>(availableUsers, selected, false);
        userDualListView.addChangeListener(change -> {
            List<UserDTO> dualListSelectedItems = userDualListView.getSelectedItems();
            dualListSelectedItems.sort(userComparator);
            commitButton.setDisable(selected.equals(dualListSelectedItems));
        });

        GridPane searchPane = new GridPane();
        searchPane.setHgap(5);
        searchPane.setPadding(new Insets(0, 0, 10, 0));

        Label label = new Label(I18N.getString("search"));
        TextField searchTextField = new TextField();
        searchTextField.setOnKeyReleased(event -> handleSearch(searchTextField.getText()));

        searchPane.add(label, 0, 0);
        searchPane.add(searchTextField, 1, 0);

        VBox content = new VBox(5);
        VBox.setVgrow(userDualListView, Priority.ALWAYS);
        content.setPrefWidth(1200);
        content.setPrefHeight(650);
        content.getChildren().addAll(searchPane, userDualListView);

        grid.add(content, 0, 0);

        getDialogPane().setContent(content);
    }

    private List<UserDTO> getAvailableUsers(Collection<String> selectedUserIds) {
        return allActiveUsers.stream().filter(userDTO -> !selectedUserIds.contains(userDTO.getId()))
                .sorted(UserDTO.getComparator()).collect(Collectors.toList());
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return null;
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected UserGroupVMO dialogResult() {
        selectedUserGroup.setUsers(userDualListView.getSelectedItems());

        return selectedUserGroup;
    }

    private void handleSearch(String text) {
        try {
            PasoWildCardPattern patternSearchTerm = new PasoWildCardPattern(text);

            Collection<String> selectedIds = userDualListView.getSelectedItems().stream().map(UserDTO::getId).toList();
            List<UserDTO> filtered = getAvailableUsers(selectedIds).stream()
                    .filter(item -> patternSearchTerm.matches(item.toString()) != null).toList();
            userDualListView.setAvailableItems(filtered);
        } catch (Exception e) {
            ExceptionHandler.instance().handleException(e);
        }
    }
}