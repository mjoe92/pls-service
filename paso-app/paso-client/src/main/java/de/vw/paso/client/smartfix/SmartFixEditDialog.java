package de.vw.paso.client.smartfix;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.delegate.stueckliste.costgroup.CostGroupRestClientHolder;
import de.vw.paso.delegate.stueckliste.setkey.SetKeyRestClientHolder;
import de.vw.paso.delegate.stueckliste.smartfix.SmartFixRestClientHolder;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.partlist.smartfix.SmartFixDTO;

public class SmartFixEditDialog extends BaseDialogController<SmartFixDTO> {

    private final SmartFixDTO smartFix;
    private final ObservableList<String> setKeys;
    private final ObservableList<String> costGroups;
    private final Collection<String> smartFixNames;

    private TextField nameTextField;
    private TextArea descriptionArea;
    private TextField oldValue;
    private CheckBox active;
    private ComboBox<String> newValue;
    private ComboBox<SmartFixField> smartFixFieldComboBox;
    private Label errorLabel;
    private Label labelForErrorLabel;

    public SmartFixEditDialog(SmartFixDTO fix) {
        this.smartFix = fix;

        try {
            smartFixNames = getAlreadyExistingSmartFixes().get();
            costGroups = getAlreadyExistingCostGroups().get();
            setKeys = getAlreadyExistingSetKeys().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        String title = I18N.getString("smart.fix");
        initialize(title, this::initContent);
    }

    @Override
    protected ChangeListener<Object> getValidationListener() {
        return (observable, oldValue, newValue) -> {
            String errorMessageKey = getCurrentIssueWithSubmit();
            boolean isValueCorrect = errorMessageKey != null;

            if (isValueCorrect) {
                errorLabel.setText(I18N.getString(errorMessageKey));
                labelForErrorLabel.setText(I18N.getString("dialog.smartfix.warning"));
            } else {
                errorLabel.setText(null);
                labelForErrorLabel.setText(null);
            }

            commitButton.setDisable(isValueCorrect);
        };
    }

    @Override
    protected ListChangeListener<Object> getValidationListenerForList() {
        return null;
    }

    @Override
    protected SmartFixDTO dialogResult() {
        String name = nameTextField.getText();
        smartFix.setName(name == null ? null : name.trim());
        smartFix.setField(smartFixFieldComboBox.valueProperty().getValue().name());
        smartFix.setOldValue(oldValue.getText());
        smartFix.setNewValue(newValue.valueProperty().get());
        smartFix.setActive(active.isSelected());

        String description = descriptionArea.getText();
        smartFix.setDescription(description == null ? null : description.trim());

        return smartFix;
    }

    private void initContent() {
        grid.setHgap(6);
        grid.setVgap(3);

        errorLabel = new Label(null);
        labelForErrorLabel = new Label(null);

        smartFixFieldComboBox = new ComboBox<>(FXCollections.observableArrayList(SmartFixField.values()));
        smartFixFieldComboBox.setCellFactory(view -> initComboBoxCell());
        smartFixFieldComboBox.setButtonCell(initComboBoxCell());

        addLabelAndInputFieldToGrid(new Label(I18N.getString("dialog.smartfix.label.property")), smartFixFieldComboBox);

        newValue = new ComboBox<>();

        smartFixFieldComboBox.valueProperty().addListener(e -> {
            SmartFixField smartFix = smartFixFieldComboBox.getValue();
            if (SmartFixField.SET_KEY == smartFix) {
                newValue.setItems(setKeys);
            } else if (SmartFixField.COST_GROUP == smartFix) {
                newValue.setItems(costGroups);
            } else {
                throw new RuntimeException("Smart fix combobox was not properly set up");
            }
        });

        SmartFixField smartFixField =
            smartFix.getField() == null || smartFix.getField().equals(SmartFixField.SET_KEY.name())
                ? SmartFixField.SET_KEY : SmartFixField.COST_GROUP;
        smartFixFieldComboBox.setValue(smartFixField);

        Label regelLbl = new Label(I18N.getString("dialog.smartfix.label.rule"));
        Label replaceLbl = new Label(I18N.getString("dialog.smartfix.label.replace"));

        nameTextField = new TextField(smartFix.getName());
        addValidationListenerToInputField(nameTextField);
        addLabelAndInputFieldToGrid(I18N.getString("dialog.smartfix.label.name"), nameTextField, 3);

        oldValue = new TextField();
        oldValue.selectionProperty().addListener(getValidationListener());
        addLabelAndInputFieldToGrid(regelLbl, oldValue, replaceLbl, newValue);
        if (newValue.getItems().stream().anyMatch(rule -> rule.equals(smartFix.getNewValue()))) {
            newValue.setValue(smartFix.getNewValue());
        }

        active = new CheckBox();
        active.setSelected(smartFix.isActive());
        active.selectedProperty().addListener(getValidationListener());
        addLabelAndInputFieldToGrid(I18N.getString("dialog.smartfix.label.active"), active);

        descriptionArea = new TextArea(smartFix.getDescription());
        addValidationListenerToInputField(descriptionArea);
        addLabelAndInputFieldToGrid(I18N.getString("dialog.smartfix.label.description"), descriptionArea, 3);

        addLabelAndInputFieldToGrid(labelForErrorLabel, errorLabel, 3);

        newValue.valueProperty().addListener(getValidationListener());
    }

    private ListCell<SmartFixField> initComboBoxCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(SmartFixField item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getMessage());
            }
        };
    }

    private Future<Set<String>> getAlreadyExistingSmartFixes() {
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            return executorService.submit(
                () -> SmartFixRestClientHolder.getInstance().loadAll().smartFixDTOList().stream().parallel()
                    .map(SmartFixDTO::getName).collect(Collectors.toSet()));
        }
    }

    private Future<ObservableList<String>> getAlreadyExistingCostGroups() {
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            return executorService.submit(() -> FXCollections.observableList(
                CostGroupRestClientHolder.getInstance().loadCostGroups().costGroupDTOs().stream()
                    .map(CostGroupDTO::getCostGroupName).toList()));
        }
    }

    private Future<ObservableList<String>> getAlreadyExistingSetKeys() {
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            return executorService.submit(() -> FXCollections.observableList(
                SetKeyRestClientHolder.getInstance().loadSetKeys().setKeys().stream().map(SetKeyDTO::getSetKeyName)
                    .toList()));
        }
    }

    private String getCurrentIssueWithSubmit() {
        String oldValueText = oldValue.getText();
        String newValueText = newValue.valueProperty().get();
        String smartFixName =
            nameTextField.getText() != null ? nameTextField.getText().trim() : nameTextField.getText();
        boolean isNameFieldEmpty = nameTextField.getText() == null || nameTextField.getText().isBlank();
        boolean isOldValueEmpty = oldValueText == null || oldValue.getText().isBlank();
        boolean isNewValueEmpty = newValueText == null;

        if (smartFixNames.contains(smartFixName)) {
            return "dialog.smartfix.warning.alreadyexists";
        } else if (newValueText != null && newValueText.equals(oldValueText)) {
            return "dialog.smartfix.warning.samename";
        } else if (isNameFieldEmpty) {
            return "dialog.smartfix.warning.namefieldempty";
        } else if (isOldValueEmpty) {
            return "dialog.smartfix.warning.oldvalueempty";
        } else if (isNewValueEmpty) {
            return "dialog.smartfix.warning.newvalueempty";
        }

        return null;
    }
}
