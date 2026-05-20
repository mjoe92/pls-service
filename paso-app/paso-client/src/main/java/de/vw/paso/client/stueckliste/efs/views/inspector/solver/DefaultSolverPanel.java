package de.vw.paso.client.stueckliste.efs.views.inspector.solver;

import de.vw.paso.partlist.domain.inspector.InspectorEntryType;

public class DefaultSolverPanel extends SolverPanel {

    private final InspectorEntryType problemType;

    public DefaultSolverPanel(InspectorEntryType problemType, AbstractSolver... solvers) {
        this.problemType = problemType;

        setSolvers(solvers);
    }

    @Override
    public String getDescription() {
        return getInspectorTypeMessage(problemType);
    }
}
