package de.vw.paso.client.stammdaten.pst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.valueobject.PstVMO;
import de.vw.paso.service.masterdata.pst.PstDTO;
import org.apache.commons.lang3.StringUtils;

public class PstDialog extends BaseDialogController<PstDTO> {

    private final static int DESC_LENGTH = 255;
    private final static int NAME_LENGTH = 3;

    private final TextField nameField = new TextField();
    private final TextField descriptionDe = new TextField();
    private final TextField descriptionEng = new TextField();
    private final ComboBox<String> pstParent = new ComboBox<>();
    private final Map<Long, PstVMO> pstVMOs;

    private final PstVMO selectedItem;

    PstDialog(String title, Map<Long, PstVMO> pstVMOs, PstVMO selectedItem, boolean isEdit) {
        this.selectedItem = selectedItem;
        this.pstVMOs = pstVMOs;

        initialize(title, () -> initContent(
                pstVMOs.values().stream().map(PstVMO::getName).collect(Collectors.toCollection(ArrayList::new)),
                isEdit));
    }

    private void initContent(List<String> allPsts, boolean isEdit) {

        allPsts.sort(String::compareTo);
        allPsts.addFirst(StringUtils.EMPTY);

        initComboBox(pstParent, allPsts,
                (selectedItem.getParentId() != null) ? pstVMOs.get(selectedItem.getParentId()).getName() :
                        StringUtils.EMPTY);

        if (isEdit) {
            nameField.setText(selectedItem.getName() == null ? "" : selectedItem.getName());
            descriptionDe.setText(selectedItem.getDescriptionDe() == null ? "" : selectedItem.getDescriptionDe());
            descriptionEng.setText(selectedItem.getDescriptionEng() == null ? "" : selectedItem.getDescriptionEng());
        }

        addLabelAndInputFieldToGrid(I18N.getString("dialog.pst.add.parent"), pstParent);
        addLabelAndInputFieldToGrid(I18N.getString("dialog.pst.add.name"), nameField);
        addLabelAndInputFieldToGrid(I18N.getString("dialog.pst.add.desc.de"), descriptionDe);
        addLabelAndInputFieldToGrid(I18N.getString("dialog.pst.add.desc.en"), descriptionEng);

        addValidationListenerToInputField(pstParent);
        addValidationListenerToInputField(nameField);
        addValidationListenerToInputField(descriptionDe);
        addValidationListenerToInputField(descriptionEng);
    }

    @Override
    protected <F> ChangeListener<F> getValidationListener() {
        return (o, a, s) -> commitButton.setDisable(notChanged() || isInvalid());
    }

    @Override
    protected <F> ListChangeListener<F> getValidationListenerForList() {
        return o -> commitButton.setDisable(notChanged() || isInvalid());
    }

    @Override
    protected boolean isInvalid() {
        return !hasValidDescription() || !hasValidName();
    }

    private boolean notChanged() {
        return Objects.equals(
                Optional.ofNullable(pstVMOs.get(selectedItem.getParentId())).map(PstVMO::getName).orElse(""),
                pstParent.getValue()) && Objects.equals(selectedItem.getName(), getName()) && Objects.equals(
                selectedItem.getDescriptionDe(), getDescEng()) && Objects.equals(selectedItem.getDescriptionEng(),
                getDescDe());
    }

    private boolean hasValidDescription() {
        return StringUtils.isNotBlank(descriptionDe.getText()) && descriptionDe.getText().length() <= DESC_LENGTH
                && StringUtils.isNotBlank(descriptionEng.getText()) && descriptionEng.getText().length() <= DESC_LENGTH;
    }

    private boolean hasValidName() {
        String currName = getName();
        return StringUtils.isNotBlank(currName) && currName.length() <= NAME_LENGTH;
    }

    private String getName() {
        return nameField.getText().trim();
    }

    private String getDescEng() {
        return descriptionEng.getText().trim();
    }

    private String getDescDe() {
        return descriptionDe.getText().trim();
    }

    @Override
    protected PstDTO dialogResult() {
        String name = getName();
        String descDe = getDescDe();
        String descEng = getDescEng();
        Long parentId = pstVMOs.values().stream()
                .filter(item -> item.getName().equals(pstParent.getSelectionModel().getSelectedItem())).findAny()
                .map(PstVMO::getId).orElse(null);

        if (notChanged()) {
            return selectedItem.getPstProperty();
        }

        return new PstDTO(selectedItem.getId(), name, descEng, descDe, parentId);
    }
}
