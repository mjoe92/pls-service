package de.vw.paso.client.stueckliste.efs.views.inspector.solver;

import java.util.Collection;
import java.util.List;

import de.vw.paso.client.smartfix.SmartFixField;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.costgroup.AddCostGroupSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.costgroup.EditCostGroupSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.setkey.AddSetKeySolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.setkey.EditSetKeySolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.smartfix.SmartFixSolver;
import de.vw.paso.delegate.stueckliste.smartfix.SmartFixRestClientHolder;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.smartfix.SmartFixDTO;

public class SetKeyCostGroupPanel extends SolverPanel {

    private final InspectorEntryType problemType;

    public SetKeyCostGroupPanel(InspectorEntryType problemType) {
        this.problemType = problemType;

        setSolvers();
    }

    @Override
    public String getDescription() {
        return getInspectorTypeMessage(problemType);
    }

    private void setSolvers() {
        AbstractSolver[] solvers = switch (problemType) {
            case MISSING_SET_KEY -> new AbstractSolver[] { new EditSetKeySolver() };
            case UNKNOWN_SET_KEY -> {
                Collection<String> field = List.of(SmartFixField.SET_KEY.name());
                Collection<SmartFixDTO> smartFixes = SmartFixRestClientHolder.getInstance().loadByFields(field)
                    .smartFixDTOList();
                yield new AbstractSolver[] { new AddSetKeySolver(), new EditSetKeySolver(),
                    new SmartFixSolver(smartFixes) };
            }
            case MISSING_COST_GROUP -> new AbstractSolver[] { new EditCostGroupSolver() };
            case UNKNOWN_COST_GROUP -> {
                Collection<String> field = List.of(SmartFixField.COST_GROUP.name());
                Collection<SmartFixDTO> smartFixes = SmartFixRestClientHolder.getInstance().loadByFields(field)
                    .smartFixDTOList();
                yield new AbstractSolver[] { new AddCostGroupSolver(), new EditCostGroupSolver(),
                    new SmartFixSolver(smartFixes) };
            }

            default -> throw new IllegalStateException("Illegal inspector type: " + problemType);
        };

        setSolvers(solvers);
    }
}
