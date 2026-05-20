package de.vw.paso.client.stueckliste.efs.views.inspector.solver.costgroup;

import java.util.List;

import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.stammdaten.costgroup.CostGroupChangedEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.delegate.stueckliste.costgroup.CostGroupRestClientHolder;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;

public class AddCostGroupSolver extends AbstractSolver {

    @Override
    public String getTitleKey() {
        return "add.costGroup";
    }

    @Override
    public boolean solve() {
        List<CostGroupDTO> costGroups = CacheManager.getCostGroups(getVehicleConfig().getCostGroupVersion());
        AddCostGroupDialog solutionDialog = new AddCostGroupDialog(getEntries(), costGroups);

        openDialog(solutionDialog, result -> result.ifPresent(costGroupDTO -> {
            CostGroupRestClientHolder.getInstance().saveCostGroup(costGroupDTO);

            EventBus.getInstance().post(new CostGroupChangedEvent());
        }));

        return !solutionDialog.isCancelled();
    }

    @Override
    public boolean disable() {
        return InspectorUtil.hasDifferentGroup(getEntries());
    }
}
