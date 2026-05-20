package de.vw.paso.client.stueckliste.efs.views.inspector.solver.setkey;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.combobox.PasoCustomComboBox;
import de.vw.paso.client.stueckliste.converter.SetKeyStringConverter;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolutionDialog;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.utility.EfsElementUtil;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

public class EditSetKeyDialog extends AbstractSolutionDialog<List<EfsElementDTO>> {

    private final Collection<EfsElementDTO> efsElementsToEdit;

    private Collection<SetKeyDTO> setKeys;
    private PasoCustomComboBox<String> setKeyBox;

    EditSetKeyDialog(Collection<TreeItem<InspectorTreeItemObject>> selectedItems, Collection<SetKeyDTO> setKeys) {
        super(selectedItems);
        efsElementsToEdit = new ArrayList<>();

        initialize(I18N.getString("editSetKey.dialog.title"), () -> {
            this.setHeaderText(I18N.getString("editSetKey.dialog.headerText"));
            this.setKeys = setKeys;
            initContent();
            addStylesheet();
        });
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(
                setKeyBox.getText() == null || !isValidSetKey(setKeyBox.getText()));
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected boolean isInvalid() {
        return setKeyBox.getText() == null;
    }

    @Override
    protected List<EfsElementDTO> dialogResult() {
        return efsElementsToEdit.stream().map(this::createNewEfsElement).toList();
    }

    private void initContent() {
        int numberOfItemsToBeChanged = 0;
        for (TreeItem<InspectorTreeItemObject> inspectorEntryTreeItem : getSelectedItems()) {
            numberOfItemsToBeChanged = InspectorUtil.collectElementsToEdit(efsElementsToEdit, numberOfItemsToBeChanged,
                    inspectorEntryTreeItem);
        }

        GridPane content = new GridPane();
        content.setHgap(5);
        content.setVgap(5);

        Label affectedItemsLabel = new Label(
                I18N.getString("editSetKey.dialog.numberOfAffectedItems.label", numberOfItemsToBeChanged));
        content.add(affectedItemsLabel, 0, 0, 2, 1);

        Label newSetKey = new Label(I18N.getString("editSetKey.dialog.newSetKey.label"));

        Collection<String> setKeyString = setKeys.stream()
                .map(sk -> sk.getSetKeyName() + StringConstant.SPACE_DASH_SPACE + sk.getDescription()).toList();
        setKeyBox = new PasoCustomComboBox<>(setKeyString);

        setKeyBox.setConverter(new SetKeyStringConverter(false));
        setKeyBox.setPopupItemConverter(new SetKeyStringConverter(true));
        setKeyBox.setMaxTextLength(3);
        setKeyBox.setValidation(setKey -> StringUtils.isBlank(setKey) || !setKey.trim().isEmpty());
        setKeyBox.setUpperCase(true);

        addValidationListenerToInputField(setKeyBox);

        content.add(newSetKey, 0, 1);
        content.add(setKeyBox, 1, 1);

        getDialogPane().setContent(content);
    }

    private EfsElementDTO createNewEfsElement(EfsElementDTO efsElement) {
        EfsElementDTO newEfsElement = EfsElementUtil.copyEfsElement(efsElement);

        newEfsElement.setSetKey(setKeyBox.getText());
        newEfsElement.setTimestampChange(new Timestamp(System.currentTimeMillis()));

        return newEfsElement;
    }

    private boolean isValidSetKey(String keyStr) {
        for (SetKeyDTO key : setKeys) {
            if (keyStr != null && keyStr.trim().equals(key.getSetKeyName())) {
                return true;
            }
        }

        return false;
    }
}
