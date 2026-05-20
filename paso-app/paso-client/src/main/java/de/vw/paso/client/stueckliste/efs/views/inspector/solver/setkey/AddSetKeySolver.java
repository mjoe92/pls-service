package de.vw.paso.client.stueckliste.efs.views.inspector.solver.setkey;

import java.util.List;

import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.stammdaten.setkey.SetKeyChangedEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.delegate.stueckliste.setkey.SetKeyRestClientHolder;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;

public class AddSetKeySolver extends AbstractSolver {

    @Override
    public String getTitleKey() {
        return "add.setKey";
    }

    @Override
    public boolean solve() {
        List<SetKeyDTO> setKeys = CacheManager.getSetKeys(getVehicleConfig().getSetVersionId());
        AddSetKeyDialog solutionDialog = new AddSetKeyDialog(getEntries(), setKeys);

        openDialog(solutionDialog, result -> result.ifPresent(newSetKeys -> {
            SetKeyRestClientHolder.getInstance().saveSetKeys(newSetKeys);

            CacheManager.invalidateSetKeys();
            EventBus.getInstance().post(new SetKeyChangedEvent());
        }));

        return !solutionDialog.isCancelled();
    }

    @Override
    public boolean disable() {
        return InspectorUtil.hasDifferentGroup(getEntries());
    }
}
