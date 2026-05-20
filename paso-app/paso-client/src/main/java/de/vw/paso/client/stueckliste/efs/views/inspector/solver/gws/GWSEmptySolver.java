package de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws;

public class GWSEmptySolver extends AbstractGWSSolver {

    public GWSEmptySolver() {
        super(null);
    }

    @Override
    public String getTitleKey() {
        return "edit.gws.empty";
    }
}
