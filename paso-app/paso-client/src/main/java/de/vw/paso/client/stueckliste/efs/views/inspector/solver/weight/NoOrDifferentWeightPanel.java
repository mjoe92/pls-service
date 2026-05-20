package de.vw.paso.client.stueckliste.efs.views.inspector.solver.weight;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.SolverPanel;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws.GWSSolutionCalculator;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws.GWSYesSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class NoOrDifferentWeightPanel extends SolverPanel {

    private final InspectorEntryType problemType;

    public NoOrDifferentWeightPanel(InspectorEntryType problemType) {
        this.problemType = problemType;

        setSolvers(new GWSYesSolver());
    }

    @Override
    public String getDescription() {
        Collection<TreeItem<InspectorTreeItemObject>> selectedItems = getEntries();
        return switch (problemType) {
            case GWS_INCORRECT_NO_WEIGHT -> getGWSNoWeightDescription(selectedItems);
            case WEIGHT_DIFFERENCE -> getWeightDifferenceDescription(selectedItems);

            default -> throw new IllegalStateException("Illegal inspector type: " + problemType);
        };
    }

    private String getGWSNoWeightDescription(Collection<TreeItem<InspectorTreeItemObject>> selectedItems) {
        double yesDifference = GWSSolutionCalculator.calculateGWSWeightDiff(selectedItems, WeightControlFlag.YES);

        String description = getInspectorTypeMessage(InspectorEntryType.GWS_INCORRECT_NO_WEIGHT);
        return "<html><body><p>" + description + "</p><br/><p>" + I18N.getString(
            "inspector.type.gws.incorrect.no.weight.title") + "</p><table><tr><td>" + I18N.getString(
            "inspector.type.gws.incorrect.no.weight.yes.desc1") + "</td><td>" + I18N.getString(
            "inspector.type.gws.incorrect.no.weight.yes.desc2") + "</td><td align=\"right\">" + formatWeight(
            yesDifference) + "</td></tr><tr><td>" + I18N.getString("inspector.type.gws.incorrect.no.weight.empty.desc1")
            + "</td><td>" + I18N.getString("inspector.type.gws.incorrect.no.weight.empty.desc2")
            + "</td><td align=\"right\">" + formatWeight(0) + "</td></tr></table></body></html>";
    }

    private String getWeightDifferenceDescription(Collection<TreeItem<InspectorTreeItemObject>> selectedItems) {
        double yesDifference = GWSSolutionCalculator.calculateGWSWeightDiff(selectedItems, WeightControlFlag.YES);
        double nodeWeight = 0;
        double totalWeight = 0;

        Optional<TreeItem<InspectorTreeItemObject>> typeNode = findTypeNode(selectedItems);
        Collection<EfsElementDTO> groupElements = typeNode.map(treeItem -> findAllGroupElements(treeItem.getChildren()))
            .orElseGet(() -> findAllGroupElements(selectedItems));

        for (EfsElementDTO groupElement : groupElements) {
            nodeWeight += groupElement.getNodeWeight();
            totalWeight += groupElement.getTotalWeight();
        }

        String weightDifferenceMessage = MessageFormat.format(
            I18N.getString("inspector.type.weight.difference.description"), totalWeight, nodeWeight);
        return "<html><body><p>" + weightDifferenceMessage + "</p></br><p>" + I18N.getString(
            "inspector.type.weight.difference.title") + "</p><table><tr><td>" + I18N.getString(
            "inspector.type.gws.incorrect.yes.desc1") + "</td><td>" + I18N.getString(
            "inspector.type.gws.incorrect.yes.desc2") + "</td><td align=\"right\">" + formatWeight(yesDifference)
            + "</td></tr><tr><td>" + I18N.getString("inspector.type.weight.difference.empty.desc1") + "</td><td>"
            + I18N.getString("inspector.type.weight.difference.empty.desc2") + "</td><td align=\"right\">"
            + formatWeight(0) + "</td></tr></table></body></html>";
    }

    private Optional<TreeItem<InspectorTreeItemObject>> findTypeNode(
        Collection<TreeItem<InspectorTreeItemObject>> selectedItems) {
        return selectedItems.stream().filter(selectedItem -> selectedItem.getValue().isTypeNode()).findFirst();
    }

    private Collection<EfsElementDTO> findAllGroupElements(
        Collection<TreeItem<InspectorTreeItemObject>> selectedItems) {
        Collection<EfsElementDTO> groupElements = new HashSet<>(selectedItems.size());
        for (TreeItem<InspectorTreeItemObject> selectedItem : selectedItems) {
            if (selectedItem == null) {
                continue;
            }

            EfsElementDTO groupElement = mapToEfsElement(selectedItem);
            if (groupElement != null && !groupElement.isDeleted()) {
                groupElements.add(groupElement);
            }
        }

        return groupElements;
    }

    private EfsElementDTO mapToEfsElement(TreeItem<InspectorTreeItemObject> treeItem) {
        InspectorTreeItemObject value = treeItem.getValue();
        if (value.isEntryNode()) {
            return value.getEntry().getElement().getParent();
        }

        if (value.isGroupNode()) {
            return ((FilterableTreeItem<InspectorTreeItemObject>) treeItem).getSourceChildren().getFirst().getValue()
                .getEntry().getElement().getParent();
        }

        throw new IllegalArgumentException("Expected entry or group node");
    }
}
