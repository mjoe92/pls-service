package de.vw.paso.client.stueckliste.efs.views.inspector.solver.weight;

import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;

public class EditUnitSolver extends AbstractSolver {

    @Override
    public String getTitleKey() {
        return "edit.quantityUnit";
    }

    @Override
    public boolean solve() {
        EditUnitDialog editUnitDialog = new EditUnitDialog(getEntries());
        openDialog(editUnitDialog, result -> result.ifPresent(changedElements -> {
            try {
                InspectorUtil.processEfsEdit(changedElements, getVehicleConfig());
            } catch (Exception e) {
                handleException(e);
            }
        }));

        return !editUnitDialog.isCancelled();
    }
}
