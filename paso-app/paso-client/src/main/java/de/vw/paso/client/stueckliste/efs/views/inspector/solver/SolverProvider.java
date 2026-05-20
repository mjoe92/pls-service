package de.vw.paso.client.stueckliste.efs.views.inspector.solver;

import java.util.HashMap;
import java.util.Map;

import de.vw.paso.client.stueckliste.efs.views.inspector.solver.aggregate.AggregateSolutionSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.ap.EditAPSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.gap.DeleteSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.gap.ShowAlternativesForGapSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws.GWSBaukastenPanel;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws.GWSIncorrectPanel;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.weight.EditUnitSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.weight.NoOrDifferentWeightPanel;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.weight.WeightSolutionSolver;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;

/**
 * Provides the {@link AbstractSolver}s for the given {@link InspectorEntryType}, which has an inner cache.
 */
public final class SolverProvider {

    private static final Map<InspectorEntryType, SolverPanel> SOLVER_AREA_CACHE = HashMap.newHashMap(13);
    private static final Map<InspectorEntryType, AbstractSolver[]> SOLVER_CACHE = HashMap.newHashMap(1);

    public static SolverPanel getSolverArea(InspectorEntryType problemType) {
        SolverPanel solvers = SOLVER_AREA_CACHE.get(problemType);
        if (solvers != null) {
            return solvers;
        }

        solvers = switch (problemType) {
            case GAP -> createDefaultSolverPanel(problemType, new ShowAlternativesForGapSolver(), new DeleteSolver());
            case GWS_BAUKASTEN -> new GWSBaukastenPanel();
            case GWS_INCORRECT -> new GWSIncorrectPanel();
            case GWS_INCORRECT_NO_WEIGHT, WEIGHT_DIFFERENCE -> new NoOrDifferentWeightPanel(problemType);
            case MISSING_AGGREGATE_ENGINE, MISSING_AGGREGATE_GEARBOX ->
                createDefaultSolverPanel(problemType, new AggregateSolutionSolver());
            case MISSING_SET_KEY, UNKNOWN_SET_KEY, MISSING_COST_GROUP, UNKNOWN_COST_GROUP ->
                new SetKeyCostGroupPanel(problemType);
            case WEIGHT_BUT_NO_UNIT -> createDefaultSolverPanel(problemType, new EditUnitSolver());
            case UNIT_GRAMM_WITHOUT_WEIGHT -> createDefaultSolverPanel(problemType, new WeightSolutionSolver());
            case UNKNOWN_AP -> createDefaultSolverPanel(problemType, new EditAPSolver());

            default -> createDefaultSolverPanel(problemType);
        };

        SOLVER_AREA_CACHE.put(problemType, solvers);

        return solvers;
    }

    private static DefaultSolverPanel createDefaultSolverPanel(InspectorEntryType problemType,
        AbstractSolver... solvers) {
        return new DefaultSolverPanel(problemType, solvers);
    }

    public static AbstractSolver[] getGeneralSolvers() {
        AbstractSolver[] solvers = SOLVER_CACHE.get(null);
        if (solvers != null) {
            return solvers;
        }

        solvers = toArray(new IgnoreEntrySolver());

        SOLVER_CACHE.put(null, solvers);

        return solvers;
    }

    private static AbstractSolver[] toArray(AbstractSolver... solvers) {
        return solvers;
    }
}
