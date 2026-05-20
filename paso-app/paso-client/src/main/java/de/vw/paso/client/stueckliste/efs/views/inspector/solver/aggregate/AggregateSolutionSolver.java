package de.vw.paso.client.stueckliste.efs.views.inspector.solver.aggregate;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.util.EventBus;

public class AggregateSolutionSolver extends AbstractSolver {

    @Override
    public String getTitleKey() {
        return "aggregate.add";
    }

    @Override
    public boolean solve() {
        for (TreeItem<InspectorTreeItemObject> inspectorTreeItemObjectTreeItem : getEntries()) {
            EventBus.getInstance().post(new ShowAggregateEvent(
                inspectorTreeItemObjectTreeItem.getValue().getEntry().getElement().getVehiclePartListId()));
            break;
        }

        return true;
    }
}
