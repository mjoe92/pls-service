package de.vw.paso.client.stammdaten.costgroup;

import java.util.Collection;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.valueobject.CostGroupVMO;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

public class CostGroupDialog extends BaseDialogController<CostGroupVMO> {

    private static final String LABEL_COST_GROUP = "label.costgroup";

    private final TextField costGroupTextField;
    private final TextField descriptionTextField;
    private final ComboBox<String> parentComboBox;

    private final Collection<CostGroupVMO> items;
    private final CostGroupVMO selectedItem;

    CostGroupDialog(String title, CostGroupVMO costGroup, List<String> parentCostGroups,
            Collection<CostGroupVMO> allCostGroups) {
        this.items = allCostGroups;
        this.selectedItem = costGroup;

        costGroupTextField = new TextField();
        descriptionTextField = new TextField();
        parentComboBox = new ComboBox<>();

        initialize(title, () -> initContent(parentCostGroups));
    }

    private void initContent(List<String> parentCostGroups) {
        setPromptText(StringConstant.EMPTY, costGroupTextField);
        setPromptText(StringConstant.EMPTY, descriptionTextField);

        costGroupTextField.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        parentCostGroups.addFirst(StringConstant.EMPTY);

        parentCostGroups.sort(String::compareTo);

        initComboBox(parentComboBox, parentCostGroups,
                selectedItem.getParent() != null ? selectedItem.getParent() : StringConstant.EMPTY);

        setTextToInputField(selectedItem.getCostGroup() == null ? parentComboBox.getSelectionModel().getSelectedItem() :
                selectedItem.getCostGroup(), costGroupTextField);
        setTextToInputField(selectedItem.getDescription(), descriptionTextField);

        addLabelAndInputFieldToGrid(I18N.getString(LABEL_PARENT), parentComboBox);
        addLabelAndInputFieldToGrid(I18N.getString(LABEL_COST_GROUP), costGroupTextField);
        addLabelAndInputFieldToGrid(I18N.getString(LABEL_DESCRIPTION), descriptionTextField);

        addValidationListenerToInputField(costGroupTextField);
        addValidationListenerToInputField(descriptionTextField);
        addValidationListenerToInputField(parentComboBox);

        costGroupTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            int costGroupLength = costGroupTextField.getText().length();
            int parentCostGroupLength = parentComboBox.getSelectionModel().getSelectedItem().length();

            int offsetParentCostGroupLength = parentCostGroupLength + 2;
            if (parentCostGroupLength == 1 && costGroupLength > offsetParentCostGroupLength) {
                costGroupTextField.setText(costGroupTextField.getText().substring(0, offsetParentCostGroupLength));
            }

            offsetParentCostGroupLength = parentCostGroupLength + 1;
            if ((parentCostGroupLength == 0 || parentCostGroupLength == 3)
                    && costGroupLength > offsetParentCostGroupLength) {
                costGroupTextField.setText(costGroupTextField.getText().substring(0, offsetParentCostGroupLength));
            }

            if (costGroupLength < parentCostGroupLength || !costGroupTextField.getText()
                    .substring(0, parentCostGroupLength).equals(parentComboBox.getValue())) {
                costGroupTextField.setText(parentComboBox.getValue());
            }
        });

        parentComboBox.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> costGroupTextField.setText(
                        parentComboBox.getSelectionModel().getSelectedItem()));
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(
                newValue.toString().isEmpty() || StringUtils.isEmpty(descriptionTextField.getText()) || newValue.equals(
                        parentComboBox.getValue()) || isInvalid());
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected boolean isInvalid() {
        return items.stream().anyMatch(item -> item.getCostGroup().equals(getCostGroupText()) && !item.getCostGroup()
                .equals(selectedItem.getCostGroup()) && item.getVersion().equals(selectedItem.getVersion())) || (
                getCostGroupText().equals(selectedItem.getCostGroup()) && descriptionTextField.getText()
                        .equals(selectedItem.getDescription()));
    }

    @Override
    protected CostGroupVMO dialogResult() {
        String parentCostGroup = !parentComboBox.getSelectionModel().getSelectedItem().equals(StringConstant.EMPTY) ?
                parentComboBox.getSelectionModel().getSelectedItem() : null;

        CostGroupVMO costGroupVMO = new CostGroupVMO();

        costGroupVMO.setCostGroup(getCostGroupText());
        costGroupVMO.setDescription(descriptionTextField.getText());
        costGroupVMO.setParent(parentCostGroup);
        costGroupVMO.setVersion(selectedItem.getVersion());

        return costGroupVMO;
    }

    private String getCostGroupText() {
        return costGroupTextField.getText().trim();
    }
}
