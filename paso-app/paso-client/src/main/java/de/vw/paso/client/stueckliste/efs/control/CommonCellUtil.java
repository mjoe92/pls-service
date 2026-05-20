package de.vw.paso.client.stueckliste.efs.control;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;

import de.vw.paso.client.control.cell.AbstractTreeTableCell;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonCellUtil {

    private static final PseudoClass PSEUDO_CLASS_PARENT_NODE = PseudoClass.getPseudoClass("parent-node");

    /**
     * E == (Abstract)EfsElement
     * S == (Abstract)EfsElementTreeItem
     * T == Eigenschaft von EfsElement
     */
    public static <E, T> void formatCell(final TreeTableCell<E, T> cell) {
        cell.indexProperty().addListener((observable, oldValue, newValue) -> styleCellLater(cell));
        if (cell instanceof AbstractTreeTableCell) {
            ((AbstractTreeTableCell) cell).propertyChanged()
                    .addListener((observable, oldValue, newValue) -> styleCellLater(cell));
        }
        cell.tableRowProperty().addListener(
                (observable1, oldValue1, newValue1) -> cell.getTreeTableRow().treeItemProperty()
                        .addListener((observable2, oldValue2, newValue2) -> styleCellLater(cell)));
    }

    private static <E, T> void styleCellLater(final TreeTableCell<E, T> cell) {
        Platform.runLater(() -> cell.pseudoClassStateChanged(PSEUDO_CLASS_PARENT_NODE, isActive(cell)));
    }

    private static <E, S extends TreeItem<E>, T> Boolean isActive(final TreeTableCell<E, T> cell) {
        final S treeItem = getTreeItem(cell);

        return ((treeItem != null) && (!treeItem.isLeaf() || treeItem.getValue().equals("")));
    }

    @SuppressWarnings("unchecked")
    private static <E, S extends TreeItem<E>, T> S getTreeItem(final TreeTableCell<E, T> cell) {
        final TreeTableRow<E> tableRow = cell.getTreeTableRow();

        return (tableRow == null) ? null : ((S) tableRow.getTreeItem());
    }

}
