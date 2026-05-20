package de.vw.paso.client.stueckliste.efs.views.inspector.solver.smartfix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolutionDialog;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.service.partlist.smartfix.SmartFixDTO;

public class SmartFixSolutionDialog extends AbstractSolutionDialog<Collection<SmartFixDTO>> {

    private static class SmartFixRow {

        private final SimpleBooleanProperty active = new SimpleBooleanProperty(true);
        private final SmartFixDTO smartFix;

        public SmartFixRow(SmartFixDTO smartFix) {
            this.smartFix = smartFix;
        }

        public SimpleBooleanProperty getActive() {
            return active;
        }

        public SmartFixDTO getSmartFix() {
            return smartFix;
        }
    }

    private final Collection<SmartFixDTO> smartFixes;

    private CustomTableView<SmartFixRow> tableView;

    public SmartFixSolutionDialog(Collection<TreeItem<InspectorTreeItemObject>> selectedItems,
        Collection<SmartFixDTO> smartFixes) {
        super(selectedItems);

        this.smartFixes = smartFixes;
        initialize(I18N.getString("smart.fix"), () -> {
            setHeaderText(I18N.getString("smartfix.dialog.headerText"));
            initContent();

            setWidth(700);
            setHeight(300);
            addStylesheet();
        });
    }

    private void initContent() {
        GridPane content = new GridPane();
        content.setHgap(5);
        content.setVgap(5);

        List<SmartFixRow> rows = new ArrayList<>(smartFixes.size());
        ChangeListener<Boolean> cl = (observableValue, aBoolean, t1) -> updateButtonState();
        for (SmartFixDTO smartFix : smartFixes) {
            SmartFixRow smartFixRow = new SmartFixRow(smartFix);
            smartFixRow.getActive().addListener(cl);
            rows.add(smartFixRow);
        }

        tableView = new CustomTableView<>();
        tableView.setItems(FXCollections.observableList(rows));
        tableView.setMinWidth(600);
        tableView.setPrefWidth(600);
        tableView.setEditable(true);

        TableColumn<SmartFixRow, Boolean> activeColumn = new TableColumn<>(
            I18N.getString("smartfix.dialog.table.column.active"));
        activeColumn.setCellValueFactory(e -> e.getValue().getActive());
        activeColumn.setCellFactory(e -> new CheckBoxTableCell<>());
        activeColumn.setEditable(true);
        tableView.getColumns().add(activeColumn);

        TableColumn<SmartFixRow, String> nameColumn = new TableColumn<>(
            I18N.getString("smartfix.dialog.table.column.name"));
        nameColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getSmartFix().getName()));
        tableView.getColumns().add(nameColumn);

        TableColumn<SmartFixRow, String> descCol = new TableColumn<>(
            I18N.getString("smartfix.dialog.table.column.desc"));
        descCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getSmartFix().getDescription()));
        tableView.getColumns().add(descCol);

        GridPane.setFillHeight(tableView, true);
        GridPane.setFillWidth(tableView, true);
        GridPane.setHgrow(tableView, Priority.ALWAYS);
        GridPane.setVgrow(tableView, Priority.ALWAYS);
        content.add(tableView, 0, 0);

        tableView.makeFilterable();

        getDialogPane().setContent(content);

        updateButtonState();
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return null;
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected Collection<SmartFixDTO> dialogResult() {
        Collection<SmartFixDTO> selectedSmartFixes = new ArrayList<>();
        for (SmartFixRow item : tableView.getItems()) {
            if (item.getActive().get()) {
                selectedSmartFixes.add(item.getSmartFix());
            }
        }

        return selectedSmartFixes;
    }

    private void updateButtonState() {
        boolean noSelection = true;
        boolean sameOldValue = false;
        Collection<String> oldValues = new HashSet<>();
        for (SmartFixRow item : tableView.getItems()) {
            if (item.getActive().get()) {
                noSelection = false;
                if (!oldValues.add(item.getSmartFix().getOldValue())) {
                    sameOldValue = true;
                    break;
                }
            }
        }

        commitButton.setDisable(noSelection || sameOldValue);
    }
}
