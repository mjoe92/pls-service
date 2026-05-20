package de.vw.paso.client.stueckliste.efs.views.inspector.tree;

import java.util.Collection;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;

import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;

public class InspectorTreeTableRow extends TreeTableRow<InspectorTreeItemObject> {

    private static final String STYLE_CLASS = "inspector-tree-row";

    @Override
    protected void updateItem(InspectorTreeItemObject item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().remove(STYLE_CLASS);

        if (item == null || empty) {
            return;
        }

        if (item.isEntryNode()) {
            if (item.isIgnored()) {
                getStyleClass().add(STYLE_CLASS);
            }

            return;
        }

        //todo: optimize avoiding traverse the tree on the entries -> create counting and increment when entry ignore changes
        Collection<TreeItem<InspectorTreeItemObject>> entries = InspectorUtil.getEntries(getTreeItem().getChildren(),
            true);
        for (TreeItem<InspectorTreeItemObject> entry : entries) {
            if (!entry.getValue().isIgnored()) {
                return;
            }
        }

        getStyleClass().add(STYLE_CLASS);
    }
}
