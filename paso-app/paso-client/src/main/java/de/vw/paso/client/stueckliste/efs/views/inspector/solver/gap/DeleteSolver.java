package de.vw.paso.client.stueckliste.efs.views.inspector.solver.gap;

import java.util.Collection;
import java.util.List;

import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;

import de.vw.paso.client.control.dialog.DialogUtil;
import de.vw.paso.client.stueckliste.efs.views.inspector.event.InspectorDeleteEfsElementSolutionEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class DeleteSolver extends AbstractSolver {

    @Override
    public String getTitleKey() {
        return "delete";
    }

    @Override
    public boolean solve() {
        Collection<TreeItem<InspectorTreeItemObject>> selectedItems = getEntries();
        ButtonType resultButton = DialogUtil.showDeleteDialog(selectedItems.size());
        if (resultButton == ButtonType.YES) {
            deleteEfsElements(selectedItems);
            return true;
        }

        return false;
    }

    private void deleteEfsElements(Collection<TreeItem<InspectorTreeItemObject>> toDelete) {
        List<EfsElementDTO> deletingEntries = toDelete.stream().map(item -> item.getValue().getEntry().getElement())
            .toList();
        deletingEntries.getFirst().getParent().getChildren().removeAll(deletingEntries);

        EventBus.getInstance()
            .post(new InspectorDeleteEfsElementSolutionEvent(deletingEntries, getVehiclePartListId()));
    }
}
