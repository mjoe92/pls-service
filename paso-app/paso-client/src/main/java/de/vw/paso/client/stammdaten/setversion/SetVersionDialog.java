package de.vw.paso.client.stammdaten.setversion;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.valueobject.SetVersionVMO;

public class SetVersionDialog extends BaseDialogController<SetVersionVMO> {

    private static final String LABEL_SET_VERSION = "label.setversionname";
    private static final int MAX_SET_VERSION_NAME_LENGTH = 255;
    private static final String LABEL_COPY = "label.setkey.copy";
    private static final String DO_NOT_COPY = "label.setkey.donot.copy";

    private final TextField setVersionNameTextField = new TextField();
    private final ComboBox<SetVersionVMO> comboBox = new ComboBox<>();
    private final Collection<String> existingSetVersionNames;
    private final SetVersionVMO selectedSetVersion;

    SetVersionDialog(final String title, Collection<SetVersionVMO> existingSetVersionVMOs) {
        this(title, existingSetVersionVMOs, null);
        initComboBox(I18N.getString(DO_NOT_COPY), existingSetVersionVMOs);
        addLabelAndInputFieldToGrid(I18N.getString(LABEL_COPY), comboBox);
    }

    SetVersionDialog(final String title, Collection<SetVersionVMO> existingSetVersionVMOs,
            SetVersionVMO selectedSetVersion) {
        this.existingSetVersionNames = existingSetVersionVMOs.stream().map(SetVersionVMO::getSetVersionName)
                .map(String::toLowerCase).collect(Collectors.toSet());
        this.selectedSetVersion = selectedSetVersion;
        initialize(title, () -> {
            if (Objects.nonNull(selectedSetVersion)) {
                this.setVersionNameTextField.setText(selectedSetVersion.getSetVersionName());
            }

            addLabelAndInputFieldToGrid(I18N.getString(LABEL_SET_VERSION), setVersionNameTextField);
            addValidationListenerToInputField(setVersionNameTextField);
        });
    }

    private void initComboBox(String doNotCopyVersionName, Collection<SetVersionVMO> existingSetVersionVMOs) {
        SetVersionVMO doNotCopy = new SetVersionVMO();
        doNotCopy.setSetVersionName(doNotCopyVersionName);
        this.comboBox.getItems().add(doNotCopy);
        this.comboBox.getSelectionModel().select(doNotCopy);
        this.comboBox.getItems().addAll(existingSetVersionVMOs);
    }

    @Override
    protected ChangeListener getValidationListener() {
        return (observable, oldValue, newValue) -> {
            String newSetVersionName = newValue.toString();
            commitButton.setDisable(isInvalidSetVersionName(newSetVersionName));
        };
    }

    private boolean isInvalidSetVersionName(String setVersionName) {
        return setVersionName.isBlank() || existingSetVersionNames.contains(setVersionName.toLowerCase())
                || setVersionName.length() > MAX_SET_VERSION_NAME_LENGTH;
    }

    @Override
    protected ListChangeListener getValidationListenerForList() {
        return null;
    }

    @Override
    protected boolean isInvalid() {
        return false;
    }

    @Override
    protected SetVersionVMO dialogResult() {
        final SetVersionVMO setVersionVMO = Optional.ofNullable(selectedSetVersion).orElseGet(SetVersionVMO::new);
        setVersionVMO.setSetVersionName(setVersionNameTextField.getText());
        setVersionVMO.setCopyFromSetVersionId(
                Optional.of(comboBox).map(ComboBox::getValue).map(SetVersionVMO::getId).orElse(null));
        return setVersionVMO;
    }
}
