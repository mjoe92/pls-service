package de.vw.paso.client.stueckliste.efs.views.inspector.solver;

import java.util.Collection;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;

public abstract class AbstractSolutionDialog<R> extends BaseDialogController<R> {

    private final Collection<TreeItem<InspectorTreeItemObject>> selectedItems;

    public AbstractSolutionDialog(Collection<TreeItem<InspectorTreeItemObject>> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public Collection<TreeItem<InspectorTreeItemObject>> getSelectedItems() {
        return selectedItems;
    }

    protected void addStylesheet() {
        Node content = getDialogPane().getContent();
        if (content instanceof Parent parent) {
            parent.getStylesheets()
                .add(AbstractSolutionDialog.class.getResource("solution-dialog.css").toExternalForm());
        }
    }
}
