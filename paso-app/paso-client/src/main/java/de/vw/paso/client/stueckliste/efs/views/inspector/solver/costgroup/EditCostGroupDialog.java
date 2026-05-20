package de.vw.paso.client.stueckliste.efs.views.inspector.solver.costgroup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.combobox.PasoCustomComboBox;
import de.vw.paso.client.stueckliste.converter.CostGroupStringConverter;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolutionDialog;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.EfsElementUtil;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

public class EditCostGroupDialog extends AbstractSolutionDialog<Collection<EfsElementDTO>> {

    private final Collection<CostGroupDTO> costGroups;
    private final Collection<EfsElementDTO> efsElementsToEdit;
    private final GridPane content;

    private PasoCustomComboBox<String> costGroupBox;

    EditCostGroupDialog(Collection<TreeItem<InspectorTreeItemObject>> selectedItems,
            Collection<CostGroupDTO> costGroups) {
        super(selectedItems);

        this.costGroups = costGroups;

        efsElementsToEdit = new ArrayList<>();

        content = new GridPane();
        initialize(I18N.getString("editCostGroup.dialog.title"), () -> {
            setHeaderText(I18N.getString("editCostGroup.dialog.headerText"));
            initContent();
            addStylesheet();
            content.setHgap(5);
            content.setVgap(5);
        });
    }

    private void initContent() {
        int numberOfItemsToBeChanged = 0;
        for (TreeItem<InspectorTreeItemObject> inspectorEntryTreeItem : getSelectedItems()) {
            numberOfItemsToBeChanged = InspectorUtil.collectElementsToEdit(efsElementsToEdit, numberOfItemsToBeChanged,
                    inspectorEntryTreeItem);
        }

        Label affectedItemsLabel = new Label(
                I18N.getString("editCostGroup.dialog.numberOfAffectedItems.label", numberOfItemsToBeChanged));
        content.add(affectedItemsLabel, 0, 0, 2, 1);

        Collection<String> costGroupString = costGroups.stream()
                .map(costGroup -> costGroup.getCostGroupName() + StringConstant.SPACE_DASH_SPACE
                        + costGroup.getDescription()).toList();

        costGroupBox = new PasoCustomComboBox<>(costGroupString);
        costGroupBox.setConverter(new CostGroupStringConverter(false));
        costGroupBox.setPopupItemConverter(new CostGroupStringConverter(true));
        costGroupBox.setMaxTextLength(4);
        costGroupBox.setValidation(costGroup -> StringUtils.isBlank(costGroup) || !costGroup.trim().isEmpty());
        costGroupBox.setUpperCase(true);

        addValidationListenerToInputField(costGroupBox);

        Label newCostGroup = new Label(I18N.getString("editCostGroup.dialog.newCostGroup.label"));

        content.add(newCostGroup, 0, 1);
        content.add(costGroupBox, 1, 1);

        getDialogPane().setContent(content);
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(
                costGroupBox.getText() == null || !isValidCostGroup(costGroupBox.getText()));
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected boolean isInvalid() {
        return costGroupBox.getText() == null;
    }

    @Override
    protected Collection<EfsElementDTO> dialogResult() {
        return efsElementsToEdit.stream().map(this::createNewEfsElement).toList();
    }

    private EfsElementDTO createNewEfsElement(EfsElementDTO efsElement) {
        EfsElementDTO newEfsElement = EfsElementUtil.copyEfsElement(efsElement);

        newEfsElement.setCostGroup(costGroupBox.getText());
        newEfsElement.setTimestampChange(new Timestamp(System.currentTimeMillis()));

        return newEfsElement;
    }

    private boolean isValidCostGroup(String costGroupStr) {
        for (CostGroupDTO costGroup : costGroups) {
            if (costGroupStr != null && costGroupStr.trim().equals(costGroup.getCostGroupName())) {
                return true;
            }
        }

        return false;
    }
}
