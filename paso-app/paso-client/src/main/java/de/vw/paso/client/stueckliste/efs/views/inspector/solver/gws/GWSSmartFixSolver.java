package de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws;

import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.stueckliste.efs.event.FzgStuecklisteGewichtEvent;
import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.client.stueckliste.efs.inspector.rule.BaukastenChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.Inspection;
import de.vw.paso.client.stueckliste.efs.views.inspector.event.InspectorEditOfEfsElementSolutionEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.stueckliste.fzgkonfig.VehicleConfigChangedEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.delegate.stueckliste.EfsEditLoadAdapter;
import de.vw.paso.delegate.stueckliste.efsweight.EfsWeightRestClientHolder;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;

public class GWSSmartFixSolver extends AbstractSolver {

    private final Inspection[] inspections;

    public GWSSmartFixSolver(Inspection... inspections) {
        this.inspections = inspections;
    }

    @Override
    public String getTitleKey() {
        return "smart.fix.gws";
    }

    @Override
    public boolean solve() {
        ServiceController<Result> task = new ServiceController<>();
        task.setOnFailed(event -> handleException(task.getException()));
        task.setExecutionTime(5000);
        task.start(this::fix);
        task.setOnSucceeded(event -> onSucceed(task.getValue()));

        return true;
    }

    @Override
    public boolean disable() {
        for (TreeItem<InspectorTreeItemObject> selectedItem : getEntries()) {
            if (selectedItem.isLeaf()) {
                EfsElementDTO parent = selectedItem.getValue().getEntry().getElement().getParent();
                if (BaukastenChecker.isEmpty(parent, inspections)) {
                    return false;
                }

                continue;
            }

            InspectorEntry childEntry = selectedItem.getChildren().getFirst().getValue().getEntry();
            if (childEntry == null || BaukastenChecker.isEmpty(childEntry.getElement().getParent(), inspections)) {
                return false;
            }
        }

        return true;
    }

    //todo: to static and unify with the other GWS solvers
    private Result fix() {
        Collection<EfsElementDTO> changedDescendants = new ArrayList<>();
        Collection<EfsElementDTO> changedAllElements = new ArrayList<>();

        Collection<TreeItem<InspectorTreeItemObject>> selectedItems = getEntries();
        Collection<EfsElementDTO> parents = InspectorUtil.getAllParents(selectedItems);
        for (EfsElementDTO parent : parents) {
            if (BaukastenChecker.isEmpty(parent, inspections) || !BaukastenChecker.isEmpty(parent, Inspection.GWS)) {
                updateGws(parent, changedDescendants, changedAllElements);
            }
        }

        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        Long vehiclePartListId = vehicleConfig.getVehiclePartList().getId();
        Double newWeight = EfsWeightRestClientHolder.getInstance().updateVehiclePartListWeight(vehiclePartListId);

        return new Result(changedDescendants, changedAllElements, vehicleConfig, newWeight);
    }

    private void onSucceed(Result result) {
        try {
            VehicleConfigDTO vehicleConfig = result.vehicleConfig();
            EfsEditLoadAdapter efsEditBD = new EfsEditLoadAdapter();

            Collection<EfsElementDTO> elements = efsEditBD.saveEfsElements(result.changedAllElements);
            EfsElementResolver.registerElements(result.changedDescendants);

            EventBus.getInstance().post(new VehicleConfigChangedEvent(vehicleConfig));
            EventBus.getInstance().post(new InspectorEditOfEfsElementSolutionEvent(elements, vehicleConfig.getId()));
            EventBus.getInstance().post(new FzgStuecklisteGewichtEvent(getVehiclePartListId(), result.newWeight()));
        } catch (Exception e) {
            handleException(e);
        }
    }

    //todo: fix the backend save so that we can update without creating new DTO-s and send only id-GWS pair
    private void updateGws(EfsElementDTO node, Collection<EfsElementDTO> descendants,
        Collection<EfsElementDTO> allElements) {
        Collection<EfsElementDTO> sortedChildren = node.getChildren().stream()
            .sorted((first, next) -> compare(first.isLeaf(), next.isLeaf())).toList();
        for (EfsElementDTO child : sortedChildren) {
            // first assemblies will be updated
            if (!child.isLeaf()) {
                updateGws(child, descendants, allElements);
            }

            EfsElementDTO newChild = InspectorUtil.createNewEfsElement(child);
            newChild.setWeightControlFlag(WeightControlFlag.NO);

            descendants.add(newChild);
            allElements.add(newChild);
        }

        if (node.getWeightControlFlag() == null) {
            node.setWeightControlFlag(WeightControlFlag.YES);
            allElements.add(node);
        }
    }

    private int compare(boolean firstIsLeaf, boolean nextIsLeaf) {
        if (firstIsLeaf && nextIsLeaf) {
            return 0;
        }

        return firstIsLeaf ? 1 : -1;
    }

    private record Result(Collection<EfsElementDTO> changedDescendants, Collection<EfsElementDTO> changedAllElements,
                          VehicleConfigDTO vehicleConfig, Double newWeight) { }
}
