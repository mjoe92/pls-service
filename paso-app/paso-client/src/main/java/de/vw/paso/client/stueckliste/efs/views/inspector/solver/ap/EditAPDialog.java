package de.vw.paso.client.stueckliste.efs.views.inspector.solver.ap;

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
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolutionDialog;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.partlist.domain.AP;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.EfsElementUtil;

public class EditAPDialog extends AbstractSolutionDialog<Collection<EfsElementDTO>> {

    private final Collection<EfsElementDTO> efsElementsToEdit;
    private final GridPane content;

    private PasoCustomComboBox<String> apComboBox;

    EditAPDialog(Collection<TreeItem<InspectorTreeItemObject>> selectedItems) {
        super(selectedItems);
        efsElementsToEdit = new ArrayList<>();
        content = new GridPane();

        initialize(I18N.getString("editAP.dialog.title"), () -> {
            setHeaderText(I18N.getString("editAP.dialog.headerText"));
            initContent();
            content.setHgap(5);
            content.setVgap(5);
            addStylesheet();
        });
    }

    private void initContent() {
        int numberOfItemsToBeChanged = 0;
        for (TreeItem<InspectorTreeItemObject> inspectorEntryTreeItem : getSelectedItems()) {
            numberOfItemsToBeChanged = InspectorUtil.collectElementsToEdit(efsElementsToEdit, numberOfItemsToBeChanged,
                inspectorEntryTreeItem);
        }

        Label affectedItemsLabel = new Label(
            I18N.getString("editAP.dialog.numberOfAffectedItems.label", numberOfItemsToBeChanged));
        content.add(affectedItemsLabel, 0, 0, 2, 1);

        Label newApLabel = new Label(I18N.getString("editAP.dialog.newCostGroup.label"));

        apComboBox = new PasoCustomComboBox<>(AP.toStrList());
        apComboBox.setMaxTextLength(10);

        addValidationListenerToInputField(apComboBox);

        content.add(newApLabel, 0, 1);
        content.add(apComboBox, 1, 1);

        getDialogPane().setContent(content);
        commitButton.setDisable(false);
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(false);
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected Collection<EfsElementDTO> dialogResult() {
        return efsElementsToEdit.stream().map(this::createNewEfsElement).toList();
    }

    private EfsElementDTO createNewEfsElement(EfsElementDTO efsElement) {
        EfsElementDTO newEfsElement = EfsElementUtil.copyEfsElement(efsElement);

        newEfsElement.setAp(apComboBox.getText());
        newEfsElement.setTimestampChange(new Timestamp(System.currentTimeMillis()));

        return newEfsElement;
    }
}
