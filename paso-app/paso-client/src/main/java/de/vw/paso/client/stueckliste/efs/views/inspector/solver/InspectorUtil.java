package de.vw.paso.client.stueckliste.efs.views.inspector.solver;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.stueckliste.efs.views.inspector.event.InspectorEditOfEfsElementSolutionEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorEntryTreeItemObject;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.stueckliste.fzgkonfig.VehicleConfigChangedEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.delegate.stueckliste.EfsEditLoadAdapter;
import de.vw.paso.partlist.domain.SpecialPartNumberType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.EfsElementUtil;

public class InspectorUtil {

    public static int collectElementsToEdit(Collection<EfsElementDTO> efsElementsToEdit, int numberOfItemsToBeChanged,
        TreeItem<InspectorTreeItemObject> inspectorEntryTreeItem) {
        if (!(inspectorEntryTreeItem.getValue() instanceof InspectorEntryTreeItemObject itemObject)) {
            return numberOfItemsToBeChanged;
        }

        EfsElementDTO element = itemObject.getEntry().getElement();
        if (element == null) {
            return numberOfItemsToBeChanged;
        }

        efsElementsToEdit.add(element);
        return numberOfItemsToBeChanged + 1;
    }

    public static void processEfsEdit(Collection<EfsElementDTO> changedElements, VehicleConfigDTO vehicleConfig)
        throws Exception {

        EfsEditLoadAdapter efsEditBD = new EfsEditLoadAdapter();

        Collection<EfsElementDTO> elements = efsEditBD.saveEfsElements(changedElements);
        EfsElementResolver.registerElements(elements);

        EventBus.getInstance().post(new VehicleConfigChangedEvent(vehicleConfig));
        EventBus.getInstance().post(new InspectorEditOfEfsElementSolutionEvent(changedElements, vehicleConfig.getId()));
    }

    public static Collection<EfsElementDTO> getAllParents(Collection<TreeItem<InspectorTreeItemObject>> items) {
        Collection<EfsElementDTO> parents = new HashSet<>();
        for (TreeItem<InspectorTreeItemObject> item : items) {
            if (!item.getValue().isEntryNode()) {
                continue;
            }

            EfsElementDTO parent = item.getValue().getEntry().getElement().getParent();
            if (parent != null && !parent.getEfsElementMara().getPartNumber()
                .equals(SpecialPartNumberType.GAP.getLabel())) {
                parents.add(parent);
            }
        }

        return parents;
    }

    public static Collection<TreeItem<InspectorTreeItemObject>> getEntries(
        Collection<TreeItem<InspectorTreeItemObject>> items, boolean withIgnored) {
        Collection<TreeItem<InspectorTreeItemObject>> result = new ArrayList<>(items.size());
        for (TreeItem<InspectorTreeItemObject> selectedItem : items) {
            if (withIgnored || !selectedItem.getValue().isIgnored()) {
                traverse((FilterableTreeItem<InspectorTreeItemObject>) selectedItem, result, withIgnored);
            }
        }

        return result;
    }

    public static boolean hasDifferentGroup(Collection<TreeItem<InspectorTreeItemObject>> entries) {
        for (TreeItem<InspectorTreeItemObject> entry : entries) {
            InspectorTreeItemObject group = entry.getParent().getValue();

            return entries.stream().anyMatch(item -> !item.getParent().getValue().equals(group));
        }

        return true;
    }

    public static EfsElementDTO createNewEfsElement(EfsElementDTO efsElement) {
        EfsElementDTO newEfsElement = EfsElementUtil.copyEfsElement(efsElement);

        newEfsElement.getEfsElementMara().setWeightEstimatedTe(1D);
        newEfsElement.setTimestampChange(new Timestamp(System.currentTimeMillis()));

        return newEfsElement;
    }

    private static void traverse(FilterableTreeItem<InspectorTreeItemObject> selectedItem,
        Collection<TreeItem<InspectorTreeItemObject>> result, boolean withIgnored) {
        if (selectedItem.getValue().isEntryNode() && (withIgnored || !selectedItem.getValue().isIgnored())) {
            result.add(selectedItem);
            return;
        }

        for (TreeItem<InspectorTreeItemObject> child : selectedItem.getSourceChildren()) {
            if (withIgnored || !selectedItem.getValue().isIgnored()) {
                traverse((FilterableTreeItem<InspectorTreeItemObject>) child, result, withIgnored);
            }
        }
    }
}
