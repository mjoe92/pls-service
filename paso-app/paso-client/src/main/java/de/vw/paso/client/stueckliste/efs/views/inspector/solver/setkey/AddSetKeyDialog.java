package de.vw.paso.client.stueckliste.efs.views.inspector.solver.setkey;

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
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.partlist.setkey.SetKeysDTO;
import de.vw.paso.utility.StringConstant;

public class AddSetKeyDialog extends AbstractSolutionDialog<SetKeysDTO> {

    private final static SetKeyDTO EMPTY_SET = new SetKeyDTO(StringConstant.EMPTY, StringConstant.EMPTY,
        StringConstant.EMPTY, 1L);
    private final static int MAX_SET_KEY_LENGTH = 3;

    private final ComboBox<SetKeyDTO> setKeyComboBox;
    private final List<SetKeyDTO> setKeys;

    private TextField setKeyField;
    private TextField setKeyDescription;

    AddSetKeyDialog(Collection<TreeItem<InspectorTreeItemObject>> selectedItems, List<SetKeyDTO> setKeys) {
        super(selectedItems);
        setKeyComboBox = new ComboBox<>();
        this.setKeys = setKeys;

        initialize(I18N.getString("addSetKey.dialog.title"), () -> {
            setHeaderText(I18N.getString("addSetKey.dialog.headerText"));
            initContent();
            addStylesheet();
        });
    }

    private void initContent() {
        GridPane content = new GridPane();
        content.setHgap(5);
        content.setVgap(5);

        initSetKeyBox(content);
        initTextFields(content);
        getDialogPane().setContent(content);
    }

    private void initSetKeyBox(GridPane content) {
        Label newSetKeyLabel = new Label(I18N.getString("addSetKey.dialog.add.note"));
        content.add(newSetKeyLabel, 0, 0, 2, 1);
    }

    private void initTextFields(GridPane content) {
        setKeyField = new TextField();

        SetKeyDTO chosenSetKey = setKeyComboBox.getValue();
        setKeyField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (chosenSetKey != null && !chosenSetKey.equals(EMPTY_SET)) {
                if (newValue.length() > chosenSetKey.getSetKeyName().length() + 1 || !newValue.contains(
                    chosenSetKey.getSetKeyName())) {
                    setKeyField.textProperty().set(oldValue);
                }

                return;
            }

            if (newValue.length() > MAX_SET_KEY_LENGTH || (chosenSetKey != null && !newValue.contains(
                chosenSetKey.getSetKeyName()))) {
                setKeyField.textProperty().set(oldValue);
            }
        });

        setKeyField.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        setKeyField.disableProperty().set(true);
        Label nameFieldLabel = new Label(I18N.getString("addSetKey.dialog.name"));
        content.add(nameFieldLabel, 0, 1);
        content.add(setKeyField, 1, 1);

        setKeyDescription = new TextField();
        Label descriptionFieldLabel = new Label(I18N.getString("addSetKey.dialog.description"));
        content.add(descriptionFieldLabel, 0, 2);
        content.add(setKeyDescription, 1, 2);

        addValidationListenerToInputField(setKeyField);
        addValidationListenerToInputField(setKeyDescription);

        String setKey = null;
        for (TreeItem<InspectorTreeItemObject> item : getSelectedItems()) {
            if (item.getValue().getEntry() != null) {
                setKey = item.getValue().getEntry().getElement().getSetKey();
                break;
            }
        }

        setKeyField.setText(setKey);
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(setKeyField.getText().trim().isEmpty());
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected boolean isInvalid() {
        return setKeyField.getText().trim().isEmpty();
    }

    @Override
    protected SetKeysDTO dialogResult() {
        Long currentVersion = setKeys.getFirst().getSetVersionId();
        String newSetKey = setKeyField.getText();
        String lastParent = null;

        Collection<SetKeyDTO> resultList = new ArrayList<>(newSetKey.length() + 1);
        for (int i = 0; i < newSetKey.length() - 1; i++) {
            String parent = newSetKey.substring(0, i + 1);
            Optional<SetKeyDTO> parentKey = setKeys.stream().filter(setKey -> parent.equals(setKey.getSetKeyName()))
                .findFirst();
            if (parentKey.isEmpty()) {
                SetKeyDTO setKeyGroup = new SetKeyDTO(parent, StringConstant.EMPTY, lastParent, currentVersion);
                resultList.add(setKeyGroup);
            }

            lastParent = parent;
        }

        SetKeyDTO leafSetKey = new SetKeyDTO(newSetKey, setKeyDescription.getText(), lastParent, currentVersion);
        resultList.add(leafSetKey);
        return new SetKeysDTO(resultList);
    }
}
