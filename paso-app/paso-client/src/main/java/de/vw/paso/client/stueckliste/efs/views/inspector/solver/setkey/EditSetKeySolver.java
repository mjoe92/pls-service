package de.vw.paso.client.stueckliste.efs.views.inspector.solver.setkey;

import java.util.Collection;
import java.util.Optional;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.stammdaten.setkey.SetKeyChangedEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;

public class EditSetKeySolver extends AbstractSolver {

    @Override
    public String getTitleKey() {
        return "edit.setKey";
    }

    @Override
    public boolean solve() {
        Collection<TreeItem<InspectorTreeItemObject>> selectedItems = getEntries();
        Optional<TreeItem<InspectorTreeItemObject>> type = selectedItems.stream()
            .filter(item -> item.getValue().isTypeNode()).findFirst();
        if (type.isPresent()) {
            selectedItems = type.get().getChildren();
        }

        Collection<SetKeyDTO> setKeysToChoose = CacheManager.getSetKeys(getVehicleConfig().getSetVersionId());
        EditSetKeyDialog solutionDialog = new EditSetKeyDialog(selectedItems, setKeysToChoose);
        openDialog(solutionDialog, result -> result.ifPresent(changes -> {
            try {
                InspectorUtil.processEfsEdit(changes, getVehicleConfig());
                EventBus.getInstance().post(new SetKeyChangedEvent());
            } catch (Exception e) {
                handleException(e);
            }
        }));

        return !solutionDialog.isCancelled();
    }

    @Override
    public boolean disable() {
        return InspectorUtil.hasDifferentGroup(getEntries());
    }
}
