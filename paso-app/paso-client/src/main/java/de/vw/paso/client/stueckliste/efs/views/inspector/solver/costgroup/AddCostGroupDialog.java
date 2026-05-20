package de.vw.paso.client.stueckliste.efs.views.inspector.solver.costgroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolutionDialog;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.costgroup.CostGroupsDTO;
import de.vw.paso.utility.StringConstant;

public class AddCostGroupDialog extends AbstractSolutionDialog<CostGroupsDTO> {

    private final static CostGroupDTO EMPTY_COST_GROUP = new CostGroupDTO(StringConstant.EMPTY, StringConstant.EMPTY,
        StringConstant.EMPTY, "0");
    private final static int MAX_COST_GROUP_LENGTH = 4;

    private final List<CostGroupDTO> costGroups;
    private final ComboBox<CostGroupDTO> costGroupBox;

    private TextField costGroupField;
    private TextField costGroupDescription;

    AddCostGroupDialog(Collection<TreeItem<InspectorTreeItemObject>> selectedItems, List<CostGroupDTO> costGroups) {
        super(selectedItems);
        this.costGroups = costGroups;
        costGroupBox = new ComboBox<>();

        initialize(I18N.getString("addCostGroup.dialog.title"), () -> {
            setHeaderText(I18N.getString("addCostGroup.dialog.headerText"));
            initContent();
            addStylesheet();
        });
    }

    private void initContent() {
        GridPane content = new GridPane();
        initCostGroupBox(content);
        initTextFields(content);
        content.setHgap(5);
        content.setVgap(5);
        getDialogPane().setContent(content);
    }

    private void initCostGroupBox(GridPane content) {
        Label newSetKeyLabel = new Label(I18N.getString("addCostGroup.dialog.add.note"));
        content.add(newSetKeyLabel, 0, 0, 2, 1);
    }

    private void initTextFields(GridPane content) {
        costGroupField = new TextField();
        costGroupField.textProperty()
            .addListener((observable, oldValue, newValue) -> updateCostGroupText(oldValue, newValue));
        costGroupField.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        costGroupField.disableProperty().set(true);
        Label nameFieldLabel = new Label(I18N.getString("addCostGroup.dialog.name"));
        content.add(nameFieldLabel, 0, 1);
        content.add(costGroupField, 1, 1);

        costGroupDescription = new TextField();
        Label descriptionFieldLabel = new Label(I18N.getString("addCostGroup.dialog.description"));
        content.add(descriptionFieldLabel, 0, 2);
        content.add(costGroupDescription, 1, 2);

        addValidationListenerToInputField(costGroupField);
        addValidationListenerToInputField(costGroupDescription);

        Optional<TreeItem<InspectorTreeItemObject>> foundElement = getSelectedItems().stream()
            .filter(e -> e.getValue().getEntry() != null).findFirst();
        foundElement.ifPresent(element -> {
            String costGroup = element.getValue().getEntry().getElement().getCostGroup();
            costGroupField.setText(costGroup);
        });

        isInvalid();
    }

    private void updateCostGroupText(String oldValue, String newValue) {
        CostGroupDTO costGroup = costGroupBox.getValue();
        if (costGroup != null && !costGroup.equals(EMPTY_COST_GROUP)) {
            if (newValue.length() > costGroup.getCostGroupName().length() + 1 || !newValue.contains(
                costGroup.getCostGroupName())) {
                costGroupField.textProperty().set(oldValue);
            }

            return;
        }

        if (newValue.length() > MAX_COST_GROUP_LENGTH || (costGroup != null && !newValue.contains(
            costGroup.getCostGroupName()))) {
            costGroupField.textProperty().set(oldValue);
        }
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(costGroupField.getText().trim().isEmpty());
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected boolean isInvalid() {
        return costGroupField.getText().trim().isEmpty();
    }

    @Override
    protected CostGroupsDTO dialogResult() {
        Long currentVersion = costGroups.getFirst().getVersion();
        Collection<CostGroupDTO> resultList = new ArrayList<>();
        String newCostGroup = costGroupField.getText();
        String lastParent = null;
        for (int i = 0; i < newCostGroup.length() - 1; i++) {
            String parent = newCostGroup.substring(0, i + 1);
            Optional<CostGroupDTO> parentKey = costGroups.stream().filter(e -> parent.equals(e.getCostGroupName()))
                .findFirst();
            if (parentKey.isEmpty()) {
                resultList.add(
                    new CostGroupDTO(parent, StringConstant.EMPTY, lastParent, StringConstant.EMPTY + currentVersion));
            }

            lastParent = parent;
        }

        CostGroupDTO costGroup = new CostGroupDTO(newCostGroup, costGroupDescription.getText(), lastParent,
            StringConstant.EMPTY + currentVersion);
        resultList.add(costGroup);

        return new CostGroupsDTO(resultList);
    }
}
