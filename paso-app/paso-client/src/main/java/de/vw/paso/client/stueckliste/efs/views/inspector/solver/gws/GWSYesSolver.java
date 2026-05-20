package de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws;

import de.vw.paso.partlist.domain.WeightControlFlag;

public class GWSYesSolver extends AbstractGWSSolver {

    public GWSYesSolver() {
        super(WeightControlFlag.YES);
    }

    @Override
    public String getTitleKey() {
        return "edit.gws.yes";
    }
}
