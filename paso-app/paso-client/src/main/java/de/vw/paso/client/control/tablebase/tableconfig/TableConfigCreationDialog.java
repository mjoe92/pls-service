package de.vw.paso.client.control.tablebase.tableconfig;

import java.util.List;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.service.tableconfig.TableConfigDTO;
import lombok.Getter;
import lombok.Setter;

public class TableConfigCreationDialog extends BaseDialogController<TableConfigDTO> {

    @Getter
    @Setter
    private TableConfigDTO tableConfigDTO;
    private TextField name;
    private CheckBox isPublic;
    private CheckBox isDefault;
    private final List<ColumnInfo> necessaryColumns;
    private final boolean privateTableConfigsCreatable;
    private final boolean publicTableConfigsCreatable;
    private final Set<String> tableConfigNames;

    public TableConfigCreationDialog(Set<String> tableConfigNames, String title, TableConfigDTO tableConfigDTO,
            List<ColumnInfo> necessaryColumns, boolean privateTableConfigsNonCreatable,
            boolean publicTableConfigsNonCreatable) {
        this.tableConfigNames = tableConfigNames;
        this.tableConfigDTO = tableConfigDTO;
        this.necessaryColumns = necessaryColumns;
        this.privateTableConfigsCreatable = !privateTableConfigsNonCreatable;
        this.publicTableConfigsCreatable = !publicTableConfigsNonCreatable;

        initialize(title, this::initContent);
    }

    public TableConfigCreationDialog(Set<String> tableConfigNames, String title, List<ColumnInfo> necessaryColumns,
            boolean privateTableConfigsCreatable, boolean publicTableConfigsCreatable) {
        this(tableConfigNames, title, new TableConfigDTO(), necessaryColumns, privateTableConfigsCreatable,
                publicTableConfigsCreatable);
    }

    private void initContent() {
        Label nameLabel = new Label(I18N.getString("name"));
        name = new TextField(tableConfigDTO.getName() != null ? tableConfigDTO.getName() : "");
        name.textProperty().addListener(getValidationListener());

        Label isPublicLabel = new Label(I18N.getString("public"));
        isPublic = new CheckBox();
        isPublic.setSelected(tableConfigDTO.isPublic());
        isPublic.selectedProperty().addListener(getValidationListener());

        Label isDefaultLabel = new Label(I18N.getString("default"));
        isDefault = new CheckBox();
        isDefault.setSelected(tableConfigDTO.isDefault());
        //if the config is public the user cannot set it as the default one
        isDefault.setDisable(tableConfigDTO.isPublic());
        isDefault.selectedProperty().addListener(getValidationListener());

        addLabelAndInputFieldToGrid(nameLabel, name);
        addLabelAndInputFieldToGrid(isPublicLabel, isPublic);
        addLabelAndInputFieldToGrid(isDefaultLabel, isDefault);

        commitButton.setDisable(checkTextFieldDisabled());
    }

    @Override
    protected <F> ChangeListener<F> getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(
                checkTextFieldDisabled() || checkPrivateFieldDisabled() || checkDefaultDisabled());
    }

    @Override
    protected <F> ListChangeListener<F> getValidationListenerForList() {
        return null;
    }

    @Override
    protected TableConfigDTO dialogResult() {
        TableConfigDTO tableConfigDTOCopy = tableConfigDTO;
        //if a public tableconfig is selected to be the default a copy will be created
        if (isPublic.isSelected() && isDefault.isSelected()) {
            tableConfigDTOCopy = new TableConfigDTO();
            tableConfigDTOCopy.setUserId(UserProperties.getUserId());
            tableConfigDTOCopy.setSelectedColumns(tableConfigDTO.getSelectedColumns());
            tableConfigDTOCopy.setSelectedColumnIds(tableConfigDTO.getSelectedColumnIds());
            tableConfigDTOCopy.setPublic(false);
        } else {
            tableConfigDTOCopy.setPublic(isPublic.isSelected());
            tableConfigDTOCopy.setUserId(UserProperties.getUserId());
        }

        List<String> nonEditableIds = necessaryColumns.stream().map(ColumnInfo::id).toList();
        tableConfigDTOCopy.getSelectedColumnIds().removeAll(nonEditableIds);
        tableConfigDTOCopy.getSelectedColumnIds().addAll(nonEditableIds);

        List<String> nonEditableColumnNames = necessaryColumns.stream().map(ColumnInfo::name).toList();
        tableConfigDTOCopy.getSelectedColumns().removeAll(nonEditableColumnNames);
        tableConfigDTOCopy.getSelectedColumns().addAll(nonEditableColumnNames);

        tableConfigDTOCopy.setName(name.getText());
        tableConfigDTOCopy.setDefault(isDefault.isSelected());
        return tableConfigDTOCopy;
    }

    private boolean checkTextFieldDisabled() {
        return name.getText().isEmpty() || name.getText().isBlank() || tableConfigNames.contains(name.getText());
    }

    private boolean checkPrivateFieldDisabled() {
        return (!privateTableConfigsCreatable && !isPublic.isSelected()) || (!publicTableConfigsCreatable
                && isPublic.isSelected());
    }

    private boolean checkDefaultDisabled() {
        return isPublic.isSelected() && isDefault.isSelected();
    }
}
