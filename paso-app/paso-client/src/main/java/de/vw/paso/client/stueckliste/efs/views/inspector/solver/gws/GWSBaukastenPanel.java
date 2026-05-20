package de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws;

import java.util.Collection;
import java.util.HashSet;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.inspector.rule.BaukastenChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.Inspection;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.SolverPanel;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class GWSBaukastenPanel extends SolverPanel {

    private static final Inspection[] BAUKASTEN_INSPECTIONS = { Inspection.GAP, Inspection.GWS_WEIGHT };

    public GWSBaukastenPanel() {
        setSolvers(new GWSSmartFixSolver(BAUKASTEN_INSPECTIONS));
    }

    @Override
    public String getDescription() {
        Collection<TreeItem<InspectorTreeItemObject>> selectedItems = getEntries();

        Collection<EfsElementDTO> parentElements = new HashSet<>(selectedItems.size());
        for (TreeItem<InspectorTreeItemObject> item : selectedItems) {
            FilterableTreeItem<InspectorTreeItemObject> selectedItem = (FilterableTreeItem<InspectorTreeItemObject>) item;
            InspectorTreeItemObject inspectorObject = selectedItem.getValue();
            ObservableList<TreeItem<InspectorTreeItemObject>> children = selectedItem.getSourceChildren();
            if (inspectorObject.isTypeNode()) {
                inspectorObject = children.getFirst().getChildren().getFirst().getValue();
            } else if (inspectorObject.isGroupNode()) {
                inspectorObject = children.getFirst().getValue();
            }

            EfsElementDTO parent = inspectorObject.getEntry().getElement().getParent();
            if (parentElements.contains(parent)) {
                continue;
            }

            if (BaukastenChecker.isEmpty(parent, BAUKASTEN_INSPECTIONS)) {
                return I18N.getString("inspector.type.gws.baukasten.incorrect.active.description");
            }

            if (BaukastenChecker.isEmpty(parent, Inspection.GWS)) {
                return I18N.getString("inspector.type.gws.baukasten.incorrect.disable.description");
            }

            parentElements.add(parent);
        }

        return null;
    }
}
