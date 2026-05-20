package de.vw.paso.client.stueckliste.efs.views.inspector.solver.costgroup;

import java.util.Collection;

import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.stammdaten.costgroup.CostGroupChangedEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;

public class EditCostGroupSolver extends AbstractSolver {

    @Override
    public String getTitleKey() {
        return "edit.costGroup";
    }

    @Override
    public boolean solve() {
        Collection<CostGroupDTO> costGroups = CacheManager.getCostGroups(getVehicleConfig().getCostGroupVersion());
        EditCostGroupDialog solutionDialog = new EditCostGroupDialog(getEntries(), costGroups);
        openDialog(solutionDialog, result -> result.ifPresent(changedElements -> {
            try {
                InspectorUtil.processEfsEdit(changedElements, getVehicleConfig());

                CacheManager.invalidateCostGroups();
                EventBus.getInstance().post(new CostGroupChangedEvent());
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
