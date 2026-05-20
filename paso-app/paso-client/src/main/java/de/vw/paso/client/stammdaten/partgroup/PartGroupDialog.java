package de.vw.paso.client.stammdaten.partgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.valueobject.PartGroupVMO;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

public class PartGroupDialog extends BaseDialogController<PartGroupVMO> {

    private static final String LABEL_CATEGORY = "label.category";
    private static final String LABEL_MGR = "label.mgr";
    private static final String LABEL_UGR = "label.ugr";
    private static final String LABEL_DESCRIPTION = "label.description";
    private static final String LABEL_WARNING = "dialog.edit.warning.children";

    private static final int INPUT_WIDTH = 80;

    private final TextField categoryTextField = new TextField();
    private final TextField mgrTextField = new TextField();
    private final TextField mgrEndTextField = new TextField();
    private final TextField ugrTextField = new TextField();
    private final TextArea descriptionTextField = new TextArea();

    private PartGroupVMO selectedItem;
    private List<PartGroupVMO> items;

    PartGroupDialog(String title, PartGroupVMO partGroup, List<PartGroupVMO> allPartGroups) {
        this.items = allPartGroups;
        this.selectedItem = partGroup;

        initialize(title, () -> initContent(partGroup));
    }

    private void initContent(PartGroupVMO partGroup) {
        categoryTextField.setPrefWidth(INPUT_WIDTH);
        mgrTextField.setPrefWidth(INPUT_WIDTH);
        mgrEndTextField.setPrefWidth(INPUT_WIDTH);
        ugrTextField.setPrefWidth(INPUT_WIDTH);
        descriptionTextField.setPrefWidth(INPUT_WIDTH);
        descriptionTextField.setPrefRowCount(3);
        descriptionTextField.setWrapText(true);

        setPromptText(StringUtils.EMPTY, categoryTextField);
        setPromptText(StringUtils.EMPTY, mgrTextField);
        setPromptText(StringUtils.EMPTY, mgrEndTextField);
        setPromptText(StringUtils.EMPTY, ugrTextField);
        setPromptText(StringUtils.EMPTY, descriptionTextField);

        setTextToInputField(convertToString(selectedItem.getCategory(), false), categoryTextField);
        setTextToInputField(convertToString(selectedItem.getMgr(), true), mgrTextField);
        setTextToInputField(convertToString(selectedItem.getMgrEnd(), true), mgrEndTextField);
        setTextToInputField(convertToString(selectedItem.getUgr(), true), ugrTextField);
        setTextToInputField(selectedItem.getDescription(), descriptionTextField);

        if (partGroup.getId() != null && partGroup.isMgr()) {
            Label warning = new Label(I18N.getString(LABEL_WARNING));
            warning.prefWidthProperty().bind(grid.widthProperty());
            grid.add(warning, 0, rowIndex++, 4, 1);
            grid.getRowConstraints().add(new RowConstraints());
        }

        grid.add(new Label(I18N.getString(LABEL_CATEGORY)), 0, rowIndex);
        grid.add(categoryTextField, 1, rowIndex++, 3, 1);

        grid.add(new Label(I18N.getString(LABEL_MGR)), 0, rowIndex);
        grid.add(mgrTextField, 1, rowIndex);
        grid.add(new Label("-"), 2, rowIndex);
        grid.add(mgrEndTextField, 3, rowIndex++);

        grid.add(new Label(I18N.getString(LABEL_UGR)), 0, rowIndex);
        grid.add(ugrTextField, 1, rowIndex++, 3, 1);

        Label label = new Label(I18N.getString(LABEL_DESCRIPTION));
        label.setAlignment(Pos.TOP_RIGHT);
        grid.add(label, 0, rowIndex);
        grid.add(descriptionTextField, 1, rowIndex++, 3, 1);

        grid.getColumnConstraints().add(new ColumnConstraints());
        grid.getColumnConstraints().add(createGrowingColumn());
        grid.getColumnConstraints().add(new ColumnConstraints());
        grid.getColumnConstraints().add(createGrowingColumn());

        grid.getRowConstraints().add(new RowConstraints());
        grid.getRowConstraints().add(new RowConstraints());
        grid.getRowConstraints().add(new RowConstraints());
        RowConstraints rc = new RowConstraints();
        rc.setValignment(VPos.TOP);
        grid.getRowConstraints().add(rc);

        addValidationListenerToInputField(categoryTextField);
        addValidationListenerToInputField(mgrTextField);
        addValidationListenerToInputField(mgrEndTextField);
        addValidationListenerToInputField(ugrTextField);
        addValidationListenerToInputField(descriptionTextField);

        setFormatter(mgrTextField);
        mgrTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isNotEmpty(newValue)) {
                categoryTextField.setText(newValue.substring(0, 1));
            } else {
                categoryTextField.setText("");
            }
        });

        setFormatter(mgrEndTextField);
        mgrEndTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isNotEmpty(newValue)) {
                ugrTextField.setText("");
                ugrTextField.setDisable(true);
                ugrTextField.setEditable(false);
            } else {
                ugrTextField.setDisable(false);
                ugrTextField.setEditable(true);
            }
        });

        setFormatter(ugrTextField);
        ugrTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isNotEmpty(newValue)) {
                mgrEndTextField.setText("");
                mgrEndTextField.setDisable(true);
                mgrEndTextField.setEditable(false);
            } else {
                mgrEndTextField.setDisable(false);
                mgrEndTextField.setEditable(true);
            }
        });
        descriptionTextField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 254) {
                return null;
            }
            return change;
        }));

        categoryTextField.setEditable(false);
        categoryTextField.setDisable(true);
        mgrEndTextField.setEditable(selectedItem.getUgr() == null);
        mgrEndTextField.setDisable(selectedItem.getUgr() != null);

        ugrTextField.setEditable(selectedItem.getMgrEnd() == null);
        ugrTextField.setDisable(selectedItem.getMgrEnd() != null);
    }

    private ColumnConstraints createGrowingColumn() {
        ColumnConstraints con = new ColumnConstraints();
        con.setHgrow(Priority.ALWAYS);
        return con;
    }

    private void setFormatter(TextField tf) {
        setFormatter(tf, new TextFormatter<>(change -> {
            if (!change.getControlNewText().matches("([0-9]+)?")) {
                return null;
            }
            if (change.getControlNewText().length() > 3) {
                return null;
            }
            return change;
        }));
    }

    private void setFormatter(TextField tf, TextFormatter formatter) {
        tf.setTextFormatter(formatter);
        tf.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = tf.getText();
                if (StringUtils.length(text) > 0) {
                    tf.setText(StringUtils.leftPad(text, 3, '0'));
                }
            }
        });
    }

    @Override
    protected ChangeListener getValidationListener() {
        return (observable, oldValue, newValue) -> commitButton.setDisable(isInvalid());
    }

    @Override
    protected ListChangeListener getValidationListenerForList() {
        return null;
    }

    @Override
    protected boolean isInvalid() {

        Integer newCategory = convertToInteger(categoryTextField.getText());
        Integer newMgr = convertToInteger(mgrTextField.getText());
        Integer newMgrEnd = convertToInteger(mgrEndTextField.getText());
        Integer newUgr = convertToInteger(ugrTextField.getText());

        List<PartGroupVMO> listPartGroupVMOs = new ArrayList<>(this.items.stream()
                .filter(item -> item.getCategory().equals(newCategory) && !Objects.equals(item, selectedItem))
                .toList());

        if (newMgr == null || StringUtils.isEmpty(descriptionTextField.getText())) {
            return true;
        }
        if (newMgrEnd != null) {
            if (newUgr != null) {
                return true;
            } else {
                if (newMgr >= newMgrEnd) {
                    return true;
                }
                int catMgr = (int) (newMgr / 100d);
                int catMgrEnd = (int) (newMgrEnd / 100d);
                if (catMgr != catMgrEnd) {
                    return true;
                }
            }
        }

        for (PartGroupVMO existingItem : listPartGroupVMOs) {
            if (existingItem.getMgrEnd() != null) {
                Range<Integer> range = Range.of(existingItem.getMgr(), existingItem.getMgrEnd());
                if (newMgrEnd == null) {
                    if (range.contains(newMgr)) {
                        return true;
                    }
                } else if (range.isOverlappedBy(Range.of(newMgr, newMgrEnd))) {
                    return true;
                }
            } else {
                if (newMgrEnd == null) {
                    if (newMgr.equals(existingItem.getMgr())) {
                        if (newUgr == null || newUgr.equals(existingItem.getUgr())) {
                            return true;
                        }
                    }
                } else if (Range.of(newMgr, newMgrEnd).contains(existingItem.getMgr())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected PartGroupVMO dialogResult() {
        final PartGroupVMO partGroupVMO = new PartGroupVMO();
        if (selectedItem != null) {
            partGroupVMO.setId(selectedItem.getId());
        }
        partGroupVMO.setCategory(convertToInteger(categoryTextField.getText()));
        partGroupVMO.setMgr(convertToInteger(mgrTextField.getText()));
        partGroupVMO.setMgrEnd(convertToInteger(mgrEndTextField.getText()));
        partGroupVMO.setUgr(convertToInteger(ugrTextField.getText()));
        partGroupVMO.setDescription(descriptionTextField.getText());

        return partGroupVMO;
    }

    private Integer convertToInteger(String grp) {
        if (StringUtils.isEmpty(grp)) {
            return null;
        } else {
            return Integer.valueOf(grp);
        }
    }

    private String convertToString(Integer i, boolean pad) {
        if (i == null) {
            return "";
        } else if (pad) {
            return StringUtils.leftPad(i + "", 3, '0');
        } else {
            return i + "";
        }
    }
}
