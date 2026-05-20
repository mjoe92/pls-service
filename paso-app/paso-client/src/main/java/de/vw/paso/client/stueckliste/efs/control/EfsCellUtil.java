package de.vw.paso.client.stueckliste.efs.control;

import java.util.Formatter;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;

import de.vw.paso.client.control.cell.AbstractTableCell;
import de.vw.paso.client.control.cell.AbstractTreeTableCell;
import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.client.stueckliste.efs.views.historie.columns.EfsTreeTableColumn;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EfsCellUtil {

    private static final PseudoClass PSEUDO_CLASS_DELETED_EFS = PseudoClass.getPseudoClass("deleted-efs");
    private static final PseudoClass PSEUDO_CLASS_UPDATED_EFS = PseudoClass.getPseudoClass("updated-efs");
    private static final Character COMMA_THOUSAND_SEPARATOR = ',';
    private static final Character PERIOD_THOUSAND_SEPARATOR = '.';

    /**
     * E == (Abstract)EfsElement
     * T == Eigenschaft von EfsElement
     */
    public static <E, T> void formatCell(final AbstractTreeTableCell<E, T> cell) {
        initCell(cell);
    }

    public static <S, T> void normalizeNumberTextField(final AbstractTreeTableCell<S, T> textField) {
        if (textField.getText() != null) {
            normalizeNumberTextField(textField.getConverter().fromString(textField.getText()), textField);
        }
    }

    public static <S, T> void normalizeNumberTextField(final AbstractTableCell<S, T> textField) {
        if (textField.getText() != null) {
            normalizeNumberTextField(textField.getConverter().fromString(textField.getText()), textField);
        }
    }

    private static <T> void normalizeNumberTextField(final T value, Labeled textField) {
        if (value instanceof Double) {
            final Long roundedStringValue = Math.round((Double) value);
            final StringBuilder stringBuilder = new StringBuilder();
            try (Formatter formatter = new Formatter(stringBuilder)) {
                formatter.format("%,d", roundedStringValue);
            }
            textField.setText(stringBuilder.toString().replace(COMMA_THOUSAND_SEPARATOR, PERIOD_THOUSAND_SEPARATOR));
        }

        if (value instanceof Float) {
            final Long roundedStringValue = (long) Math.round((Float) value);
            final StringBuilder stringBuilder = new StringBuilder();
            try (Formatter formatter = new Formatter(stringBuilder)) {
                formatter.format("%,d", roundedStringValue);
            }
            textField.setText(stringBuilder.toString().replace(COMMA_THOUSAND_SEPARATOR, PERIOD_THOUSAND_SEPARATOR));
        }
    }

    private static <E, T> void initCell(final AbstractTreeTableCell<E, T> cell) {
        cell.indexProperty().addListener((observable, oldValue, newValue) -> styleCellLater(cell));
        cell.propertyChanged().addListener((observable, oldValue, newValue) -> styleCellLater(cell));
        cell.tableRowProperty().addListener(
                (observable1, oldValue1, newValue1) -> cell.getTreeTableRow().treeItemProperty()
                        .addListener((observable2, oldValue2, newValue2) -> styleCellLater(cell)));
    }

    private static <E, T> void styleCellLater(final AbstractTreeTableCell<E, T> cell) {
        Platform.runLater(() -> {
            bindChangeProperty(cell);
            bindTooltipProperty(cell);

            pseudoClassStateChangeUpdate(cell, isEfsElementChanged(cell));
            pseudoClassStateChangeDelete(cell, isEfsElementDeleted(cell));
        });
    }

    private static <E, TI extends AbstractTreeItem<E>, T> void bindChangeProperty(AbstractTreeTableCell<E, T> cell) {
        final TI treeItem = getEfsElementTreeItem(cell);
        final EfsTreeTableColumn<E, T> tableColumn = (EfsTreeTableColumn<E, T>) cell.getTableColumn();

        if (treeItem != null && tableColumn.isCompareCell()) {
            final BooleanProperty propertyChange = treeItem.propertyChange(cell.getPropertyName());

            if (propertyChange != null) {
                cell.propertyChanged().bind(propertyChange);
            } else {
                cell.propertyChanged().unbind();
            }
        } else {
            cell.propertyChanged().unbind();
        }
    }

    private static <E, TI extends AbstractTreeItem<E>, T> void bindTooltipProperty(AbstractTreeTableCell<E, T> cell) {
        final String propertyName = cell.getPropertyName();
        final TI treeItem = getEfsElementTreeItem(cell);

        if (treeItem != null) {
            final ObjectProperty<Tooltip> treeItemTooltipProperty = treeItem.getTooltip(propertyName);

            if (treeItemTooltipProperty != null) {
                cell.tooltipProperty().bind(treeItemTooltipProperty);
            }
        } else {
            cell.tooltipProperty().unbind();
        }
    }

    private static <E, T> void pseudoClassStateChangeUpdate(AbstractTreeTableCell<E, T> cell, Boolean active) {
        cell.pseudoClassStateChanged(PSEUDO_CLASS_UPDATED_EFS, active);
    }

    private static <E, TI extends AbstractTreeItem<E>, T> Boolean isEfsElementChanged(
            AbstractTreeTableCell<E, T> cell) {
        TI treeItem = getEfsElementTreeItem(cell);
        return treeItem != null && treeItem.isChange(cell.getPropertyName());
    }

    private static <E, T> void pseudoClassStateChangeDelete(AbstractTreeTableCell<E, T> cell, Boolean active) {
        cell.pseudoClassStateChanged(PSEUDO_CLASS_DELETED_EFS, active);
    }

    private static <E, TI extends AbstractTreeItem<E>, T> Boolean isEfsElementDeleted(
            AbstractTreeTableCell<E, T> cell) {
        TI treeItem = getEfsElementTreeItem(cell);
        return ((treeItem != null) && treeItem.isDeleted());
    }

    @SuppressWarnings("unchecked")
    private static <E, TI extends AbstractTreeItem<E>, T> TI getEfsElementTreeItem(TreeTableCell<E, T> cell) {
        TreeTableRow<E> tableRow = cell.getTreeTableRow();
        return (tableRow == null) ? null : ((TI) tableRow.getTreeItem());
    }
}
