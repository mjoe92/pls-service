package de.vw.paso.client.stueckliste.efs.views.inspector.solver.weight;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolutionDialog;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.util.QuantityUnit;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.EfsElementUtil;

public class EditUnitDialog extends AbstractSolutionDialog<Collection<EfsElementDTO>> {

    private final Collection<EfsElementDTO> efsElementsToEdit;
    private final ComboBox<QuantityUnit> unitBox;
    private final GridPane content;

    EditUnitDialog(Collection<TreeItem<InspectorTreeItemObject>> selectedItems) {
        super(selectedItems);

        efsElementsToEdit = new ArrayList<>();
        unitBox = new ComboBox<>();
        content = new GridPane();

        initialize(I18N.getString("setUnit.dialog.title"), () -> {
            this.setHeaderText(I18N.getString("setUnit.dialog.headerText"));

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
            I18N.getString("editSetKey.dialog.numberOfAffectedItems.label", numberOfItemsToBeChanged));
        content.add(affectedItemsLabel, 0, 0, 2, 1);

        ObservableList<QuantityUnit> wrappedQuantityUnits = FXCollections.observableArrayList(QuantityUnit.values());
        unitBox.setItems(wrappedQuantityUnits);
        unitBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(QuantityUnit unit) {
                return unit.getShortName();
            }

            @Override
            public QuantityUnit fromString(String unitShortHandle) {
                for (QuantityUnit quantityUnit : wrappedQuantityUnits) {
                    if (quantityUnit.getShortName().equals(unitShortHandle)) {
                        return quantityUnit;
                    }
                }

                return null;
            }
        });
        addValidationListenerToInputField(unitBox);

        Label unitLabel = new Label(I18N.getString("setUnit.dialog.unit"));

        content.add(unitLabel, 0, 1);
        content.add(unitBox, 1, 1);
        getDialogPane().setContent(content);
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(unitBox.getValue() == null);
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected boolean isInvalid() {
        return unitBox.getValue() == null;
    }

    @Override
    protected Collection<EfsElementDTO> dialogResult() {
        return efsElementsToEdit.stream().map(this::createNewEfsElement).toList();
    }

    private EfsElementDTO createNewEfsElement(EfsElementDTO efsElement) {
        EfsElementDTO newEfsElement = EfsElementUtil.copyEfsElement(efsElement);

        newEfsElement.setQuantityUnit(unitBox.getValue().getShortName());
        newEfsElement.setTimestampChange(new Timestamp(System.currentTimeMillis()));

        return newEfsElement;
    }
}
