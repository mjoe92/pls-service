package de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws;

import de.vw.paso.partlist.domain.WeightControlFlag;

public class GWSNoSolver extends AbstractGWSSolver {

    public GWSNoSolver() {
        super(WeightControlFlag.NO);
    }

    @Override
    public String getTitleKey() {
        return "edit.gws.no";
    }
}
