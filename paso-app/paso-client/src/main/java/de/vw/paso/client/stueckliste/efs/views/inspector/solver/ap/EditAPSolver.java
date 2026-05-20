package de.vw.paso.client.stueckliste.efs.views.inspector.solver.ap;

import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;

public class EditAPSolver extends AbstractSolver {

    @Override
    public String getTitleKey() {
        return "edit.AP";
    }

    @Override
    public boolean solve() {
        EditAPDialog solutionDialog = new EditAPDialog(getEntries());
        openDialog(solutionDialog, result -> result.ifPresent(changedElements -> {
            try {
                InspectorUtil.processEfsEdit(changedElements, getVehicleConfig());
            } catch (Exception e) {
                handleException(e);
            }
        }));

        return !solutionDialog.isCancelled();
    }
}
