package de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws;

import java.util.Collection;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.SolverPanel;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;

public class GWSIncorrectPanel extends SolverPanel {

    public GWSIncorrectPanel() {
        setSolvers(new GWSYesSolver(), new GWSNoSolver(), new GWSEmptySolver());
    }

    @Override
    public String getDescription() {
        Collection<TreeItem<InspectorTreeItemObject>> selectedItems = getEntries();
        double yesDifference = GWSSolutionCalculator.calculateGWSWeightDiff(selectedItems, WeightControlFlag.YES);
        double noDifference = GWSSolutionCalculator.calculateGWSWeightDiff(selectedItems, WeightControlFlag.NO);
        double emptyDifference = GWSSolutionCalculator.calculateGWSWeightDiff(selectedItems, null);

        String description = getInspectorTypeMessage(InspectorEntryType.GWS_INCORRECT);
        return "<html><body><p>" + description + "</p><br/><p>" + I18N.getString("inspector.type.gws.incorrect.title")
            + "</p><table><tr><td>" + I18N.getString("inspector.type.gws.incorrect.yes.desc1") + "</td><td>"
            + I18N.getString("inspector.type.gws.incorrect.yes.desc2") + "</td><td align=\"right\">" + formatWeight(
            yesDifference) + "</td></tr><tr><td>" + I18N.getString("inspector.type.gws.incorrect.empty.desc1")
            + "</td><td>" + I18N.getString("inspector.type.gws.incorrect.empty.desc2") + "</td><td align=\"right\">"
            + formatWeight(emptyDifference) + "</td></tr><tr><td>" + I18N.getString(
            "inspector.type.gws.incorrect.no.desc1") + "</td><td>" + I18N.getString(
            "inspector.type.gws.incorrect.no.desc2") + "</td><td align=\"right\">" + formatWeight(noDifference)
            + "</td></tr></table></body></html>";
    }
}
