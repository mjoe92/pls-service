package de.vw.paso.client.userrightmanagement.usermanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.delegate.right.RightManagementRestClientHolder;
import de.vw.paso.service.right.RightManagementRestService;
import de.vw.paso.service.right.RoleDTO;
import org.apache.commons.lang3.StringUtils;

public class AssignRoleDialog extends BaseDialogController<List<RoleDTO>> {

    private final GridPane gridpane;
    private final CustomTableView<RoleVMO> roleTableView;

    private final TableColumn<RoleVMO, Boolean> colCheckBox;
    private final TableColumn<RoleVMO, Long> colRoleId;
    private final TableColumn<RoleVMO, String> colRoleName;
    private final TableColumn<RoleVMO, String> colRoleDesc;

    private final ComboBox<String> filterModeBox;

    private final ObservableList<RoleDTO> selectedRoles;

    private Collection<RoleVMO> items;
    private PasoWildCardPattern patternSearchTerm;

    AssignRoleDialog(String title, String userId) {
        gridpane = new GridPane();
        roleTableView = new CustomTableView<>();
        colCheckBox = new TableColumn<>();
        colRoleId = new TableColumn<>();
        colRoleName = new TableColumn<>();
        colRoleDesc = new TableColumn<>();
        filterModeBox = new ComboBox<>();
        selectedRoles = FXCollections.observableArrayList();

        initialize(title, () -> {
            commitButton.setDisable(false);

            getRolesForUser(userId);
            setHeaderText(I18N.getString("dialog.roles.header") + StringUtils.SPACE + userId);
            addValidationListenerToInputField(selectedRoles);

            getDialogPane().setContent(gridpane);
            getDialogPane().setPrefWidth(1200);

            initContent();
            initTable();
            initColumns();
            roleTableView.makeFilterable();
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
    protected List<RoleDTO> dialogResult() {
        return selectedRoles;
    }

    private void initContent() {
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(100);
        gridpane.getColumnConstraints().add(column);

        GridPane searchPane = new GridPane();
        searchPane.setHgap(5);
        searchPane.setPadding(new Insets(0, 0, 10, 0));
        Label label = new Label(I18N.getString("search"));
        label.setPadding(new Insets(0, 5, 0, 0));
        TextField searchTextField = new TextField();
        searchTextField.setOnKeyReleased(event -> handleSearch(searchTextField.getText()));

        filterModeBox.getItems()
                .addAll(I18N.getString("filtermode.users.showall"), I18N.getString("filtermode.users.selectedOnly"),
                        I18N.getString("filtermode.users.notSelectedOnly"));
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
        gridpane.add(roleTableView, 0, 1);
    }

    private void initTable() {
        roleTableView.setEditable(true);
        roleTableView.setItems(FXCollections.observableArrayList(items));
        roleTableView.getColumns().addAll(colCheckBox, colRoleId, colRoleName, colRoleDesc);
    }

    private void initColumns() {
        colCheckBox.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colCheckBox.setCellFactory(CheckBoxTableCell.forTableColumn(param -> {
            RoleVMO item = roleTableView.getItems().get(param);

            item.selectedProperty().addListener(l -> updateSelectedItems(item));

            return item.selectedProperty();
        }));
        colCheckBox.setEditable(true);
        colRoleId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRole().getId()));
        colRoleId.setText(I18N.getString("label.roleId"));
        colRoleName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().getName()));
        colRoleName.setText(I18N.getString("label.roleName"));
        colRoleDesc.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getRole().getDescription()));
        colRoleDesc.setText(I18N.getString("label.roleDesc"));
    }

    private void fillTable() {
        roleTableView.getSelectionModel().clearSelection();

        if (filterModeBox.getValue().equals(I18N.getString("filtermode.users.showall"))) {
            roleTableView.setItems(FXCollections.observableArrayList(
                    items.stream().filter(item -> getFilterCriteria(item, patternSearchTerm))
                            .collect(Collectors.toList())));
        } else if (filterModeBox.getValue().equals(I18N.getString("filtermode.users.selectedOnly"))) {
            roleTableView.setItems(FXCollections.observableArrayList(
                    items.stream().filter(item -> getFilterCriteria(item, patternSearchTerm) && item.isSelected())
                            .collect(Collectors.toList())));
        } else if (filterModeBox.getValue().equals(I18N.getString("filtermode.users.notSelectedOnly"))) {
            roleTableView.setItems(FXCollections.observableArrayList(
                    items.stream().filter(item -> getFilterCriteria(item, patternSearchTerm) && !item.isSelected())
                            .collect(Collectors.toList())));
        }
    }

    private void getRolesForUser(String userId) {
        RightManagementRestService rightManagementService = RightManagementRestClientHolder.getInstance();
        this.items = convertToVMOs(rightManagementService.getAllRoles().roleDTOList(),
                rightManagementService.getRolesForUser(userId).roleDTOList());
    }

    private void updateSelectedItems(RoleVMO item) {
        if (item.isSelected()) {
            if (!selectedRoles.contains(item.getRole())) {
                selectedRoles.add(item.getRole());
            }
        } else {
            selectedRoles.remove(item.getRole());
        }
    }

    private void handleSearch(String text) {
        try {
            patternSearchTerm = new PasoWildCardPattern(text);
            fillTable();
        } catch (Exception e) {
            ExceptionHandler.instance().handleException(e);
        }
    }

    private boolean getFilterCriteria(RoleVMO item, PasoWildCardPattern pattern) {
        return pattern == null || (item.getRole().getDescription() != null
                && pattern.matches(item.getRole().getDescription()) != null) || (item.getRole().getName() != null
                && pattern.matches(item.getRole().getName()) != null);
    }

    private Collection<RoleVMO> convertToVMOs(List<RoleDTO> roles, List<RoleDTO> userRoles) {
        Collection<Long> userRoleIds = userRoles.stream().map(RoleDTO::getId).toList();
        Collection<RoleVMO> roleVMOS = new ArrayList<>(roles.size());
        for (RoleDTO role : roles) {
            RoleVMO vmo = new RoleVMO();
            vmo.setRole(role);

            boolean hasRole = userRoleIds.contains(role.getId());
            vmo.setSelected(hasRole);
            if (hasRole) {
                selectedRoles.add(role);
            }

            roleVMOS.add(vmo);
        }

        return roleVMOS;
    }
}
