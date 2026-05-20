package de.vw.paso.client.stammdaten.setkey;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.valueobject.SetKeyVMO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

public class SetKeyDialog extends BaseDialogController<SetKeyVMO> {

    private static final String LABEL_SET_KEY = "label.setkey";
    private static final String REGEX_SET_KEY_NAME = "([0-9|A-Z]){0," + SetKeyDTO.SET_KEY_MAX_LENGTH + "}";
    private static final String DIALOG_TITLE_ADD = "dialog.set.add.title";

    private final TextField setKeyTextField = new TextField();
    private final TextField descriptionTextField = new TextField();
    private final ComboBox<String> parentComboBox = new ComboBox<>();

    private final List<SetKeyVMO> items;
    private final SetKeyVMO selectedItem;
    private final Long setVersionId;

    SetKeyDialog(String title, SetKeyVMO setKey, List<String> parentSetKeys, List<SetKeyVMO> allSetKeys,
            Long setVersionId) {
        this.items = allSetKeys;
        this.selectedItem = setKey;
        this.setVersionId = setVersionId;

        initialize(title, () -> initContent(parentSetKeys));
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(!hasChanged() || isInvalid());
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected boolean isInvalid() {
        return !hasValidDescription() || !hasValidSetKeyName();
    }

    @Override
    protected SetKeyVMO dialogResult() {
        String selectedItem = parentComboBox.getSelectionModel().getSelectedItem();
        String parentSetKey = StringConstant.EMPTY.equals(selectedItem) ? null : selectedItem;

        SetKeyVMO setKeyVMO = new SetKeyVMO();

        setKeyVMO.setSetKey(getSetKeyText());
        setKeyVMO.setDescription(descriptionTextField.getText());
        setKeyVMO.setParent(parentSetKey);
        setKeyVMO.setVersion(this.selectedItem.getVersion());
        setKeyVMO.setSetVersionId(setVersionId);

        return setKeyVMO;
    }

    private void initContent(List<String> parentSetKeys) {
        setPromptText(StringConstant.EMPTY, setKeyTextField);
        setPromptText(StringConstant.EMPTY, descriptionTextField);

        setKeyTextField.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().toUpperCase());
            String newText = change.getControlNewText();
            return newText.matches(REGEX_SET_KEY_NAME) ? change : null;
        }));

        parentSetKeys.addFirst(StringConstant.EMPTY);

        parentSetKeys.sort(String::compareTo);

        String parentPlaceHolder;
        if (I18N.getString(DIALOG_TITLE_ADD).equals(getTitle())) {
            parentPlaceHolder = selectedItem.getSetKeyString();
        } else {
            parentPlaceHolder = selectedItem.getParent() != null ? selectedItem.getParent() : StringConstant.EMPTY;
        }

        initComboBox(parentComboBox, parentSetKeys, parentPlaceHolder);

        setTextToInputField(selectedItem.getSetKeyString(), setKeyTextField);
        setTextToInputField(selectedItem.getDescription(), descriptionTextField);

        addLabelAndInputFieldToGrid(I18N.getString(LABEL_PARENT), parentComboBox);
        addLabelAndInputFieldToGrid(I18N.getString(LABEL_SET_KEY), setKeyTextField);
        addLabelAndInputFieldToGrid(I18N.getString(LABEL_DESCRIPTION), descriptionTextField);

        addValidationListenerToInputField(setKeyTextField);
        addValidationListenerToInputField(descriptionTextField);
        addValidationListenerToInputField(parentComboBox);
    }

    private boolean hasChanged() {
        return !Objects.equals(selectedItem.getParent(), parentComboBox.getValue()) || !Objects.equals(
                selectedItem.getSetKeyString(), getSetKeyText()) || !Objects.equals(selectedItem.getDescription(),
                descriptionTextField.getText());
    }

    private boolean hasValidDescription() {
        return StringUtils.isNotBlank(descriptionTextField.getText())
                && descriptionTextField.getText().length() <= SetKeyDTO.DESCRIPTION_MAX_LENGTH;
    }

    private boolean hasValidSetKeyName() {
        Collection<String> existingSetKeyNames = items.stream().map(SetKeyVMO::getSetKeyString)
                .collect(Collectors.toSet());
        existingSetKeyNames.remove(selectedItem.getSetKeyString());
        return StringUtils.isNotBlank(getSetKeyText()) && !getSetKeyText().equals(parentComboBox.getValue())
                && !existingSetKeyNames.contains(getSetKeyText());
    }

    private String getSetKeyText() {
        return setKeyTextField.getText().trim();
    }
}
