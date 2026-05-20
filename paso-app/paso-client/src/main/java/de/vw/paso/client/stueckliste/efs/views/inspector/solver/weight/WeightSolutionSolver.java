package de.vw.paso.client.stueckliste.efs.views.inspector.solver.weight;

import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.Controller;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.stueckliste.efs.views.inspector.event.InspectorEditOfEfsElementSolutionEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.stueckliste.fzgkonfig.VehicleConfigChangedEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.delegate.stueckliste.EfsEditLoadAdapter;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.Pair;

public class WeightSolutionSolver extends AbstractSolver implements Controller {

    @Override
    public String getTitleKey() {
        return "set.to.one.gram";
    }

    @Override
    public boolean solve() {
        Collection<TreeItem<InspectorTreeItemObject>> selectedItems = getEntries();

        Collection<EfsElementDTO> changedElements = new ArrayList<>(selectedItems.size());
        for (TreeItem<InspectorTreeItemObject> selectedItem : selectedItems) {
            if (selectedItem.isLeaf()) {
                EfsElementDTO elementToUpdate = selectedItem.getValue().getEntry().getElement();
                EfsElementDTO newElement = InspectorUtil.createNewEfsElement(elementToUpdate);
                changedElements.add(newElement);
            }
        }

        ServiceController<Result> task = new ServiceController<>();
        task.setOnFailed(event -> handleException(task.getException()));
        task.setExecutionTime(5000);
        task.setOnSucceeded(event -> onSucceeded(task));
        task.start(() -> updateEfsElements(changedElements));

        return true;
    }

    @Override
    public boolean disable() {
        return getEntries().isEmpty();
    }

    private void onSucceeded(ServiceController<Result> task) {
        Result result = task.getValue();
        Collection<EfsElementDTO> elements = result.pair.first();
        EfsElementResolver.registerElements(elements);

        VehicleConfigDTO vehicleConfig = result.pair.second();
        EventBus.getInstance().post(new VehicleConfigChangedEvent(vehicleConfig));
        EventBus.getInstance().post(new InspectorEditOfEfsElementSolutionEvent(elements, vehicleConfig.getId()));
    }

    private Result updateEfsElements(Collection<EfsElementDTO> changedElements) {
        EfsEditLoadAdapter efsEditBD = new EfsEditLoadAdapter();
        try {
            Collection<EfsElementDTO> elements = efsEditBD.saveEfsElements(changedElements);
            Pair<Collection<EfsElementDTO>, VehicleConfigDTO> elementsToVehicleConfig = new Pair<>(elements,
                    getVehicleConfig());

            return new Result(elementsToVehicleConfig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected record Result(Pair<Collection<EfsElementDTO>, VehicleConfigDTO> pair) { }
}
