package de.vw.paso.client.stueckliste.efs.views.inspector.solver.smartfix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.control.dialog.DialogUtil;
import de.vw.paso.client.smartfix.SmartFixEditDialog;
import de.vw.paso.client.stueckliste.efs.views.inspector.event.InspectorEditOfEfsElementSolutionEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.stueckliste.fzgkonfig.VehicleConfigChangedEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.delegate.stueckliste.EfsEditLoadAdapter;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.partlist.domain.smartfix.SmartFixUtil;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.smartfix.SmartFixDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;

public class SmartFixSolver extends AbstractSolver {

    private final Collection<SmartFixDTO> initSmartFixes;

    private Collection<SmartFixDTO> smartFixes;
    private Collection<EfsElementDTO> selectedEfsElements;

    public SmartFixSolver(Collection<SmartFixDTO> initSmartFixes) {
        this.initSmartFixes = initSmartFixes;
    }

    @Override
    public String getTitleKey() {
        return "smart.fix";
    }

    @Override
    public boolean solve() {
        return smartFixes.isEmpty() ? showAddDialog() : showEditDialog();
    }

    @Override
    public boolean disable() {
        return !UserProperties.getUser().isAdmin();
    }

    @Override
    public void setEntries(Collection<TreeItem<InspectorTreeItemObject>> selectedEntryItems, boolean showIgnored) {
        selectedEfsElements = new ArrayList<>(selectedEntryItems.size());

        Map<InspectorEntryType, Collection<InspectorTreeItemObject>> itemMap = new EnumMap<>(InspectorEntryType.class);
        for (TreeItem<InspectorTreeItemObject> element : selectedEntryItems) {
            InspectorTreeItemObject inspectorItem = element.getValue();
            if (!showIgnored && inspectorItem.isIgnored()) {
                continue;
            }

            itemMap.computeIfAbsent(inspectorItem.getType(), type -> new ArrayList<>()).add(inspectorItem);

            if (inspectorItem.isEntryNode()) {
                selectedEfsElements.add(inspectorItem.getEntry().getElement());
            }
        }

        //todo: should be precached instead reload from server everytime
        smartFixes = loadRelevantSmartFixes(itemMap);

        super.setEntries(selectedEntryItems, showIgnored);
    }

    private boolean showAddDialog() {
        SmartFixDTO smartFix = new SmartFixDTO();
        smartFix.setActive(true);
        smartFix.setOldValue(getEntries().iterator().next().getValue().getEntry().getProblemGroup());

        SmartFixEditDialog dialog = new SmartFixEditDialog(smartFix);
        Optional<SmartFixDTO> smartFixes = dialog.showAndWait();
        if (smartFixes.isPresent()) {
            ServiceController<Result> task = new ServiceController<>();
            task.setOnFailed(event -> handleException(event.getSource().getException()));
            task.setExecutionTime(5000);
            task.start(() -> getSmartFixes(List.of(smartFixes.get())));
            task.setOnSucceeded(event -> onSucceeded(task.getValue()));

            showNoSmartFixDialog(task.getValue());
        }

        return !dialog.isCancelled();
    }

    private boolean showEditDialog() {
        SmartFixSolutionDialog smartFixDialog = new SmartFixSolutionDialog(getEntries(), smartFixes);
        Optional<Collection<SmartFixDTO>> smartFixes = smartFixDialog.showAndWait();
        if (smartFixes.isPresent()) {
            ServiceController<Result> task = new ServiceController<>();
            task.setOnFailed(event -> handleException(event.getSource().getException()));
            task.setExecutionTime(5000);
            task.start(() -> getSmartFixes(smartFixes.get()));
            task.setOnSucceeded(event -> onSucceeded(task.getValue()));
        }

        return !smartFixDialog.isCancelled();
    }

    private void showNoSmartFixDialog(Result result) {
        if (result == null) {
            DialogUtil.showWarnDialog(I18N.getString("warning"), I18N.getString("smart.fix.warning.result"),
                I18N.getString("smart.fix.warning.description"));
        }
    }

    private Result getSmartFixes(Collection<SmartFixDTO> fixesToApply) throws Exception {
        Collection<EfsElementDTO> changedElements = SmartFixUtil.apply(selectedEfsElements, fixesToApply);
        if (changedElements.isEmpty()) {
            return null;
        }

        Collection<EfsElementDTO> updatedElements = new EfsEditLoadAdapter().saveEfsElements(changedElements);

        return new Result(updatedElements, getVehicleConfig(), changedElements);
    }

    private void onSucceeded(Result value) {
        if (value == null) {
            return;
        }

        Collection<EfsElementDTO> savedElements = value.changedElements;
        EfsElementResolver.registerElements(savedElements);

        VehicleConfigDTO vehicleConfig = value.vehicleConfig();
        EventBus.getInstance().post(new VehicleConfigChangedEvent(vehicleConfig));
        EventBus.getInstance().post(new InspectorEditOfEfsElementSolutionEvent(savedElements, vehicleConfig.getId()));
    }

    private Collection<SmartFixDTO> loadRelevantSmartFixes(
        Map<InspectorEntryType, Collection<InspectorTreeItemObject>> itemMap) {
        if (!UserProperties.getUser().isAdmin()) {
            return List.of();
        }

        Collection<String> oldValues = new HashSet<>();
        if (itemMap.containsKey(InspectorEntryType.MISSING_SET_KEY) || itemMap.containsKey(
            InspectorEntryType.UNKNOWN_SET_KEY)) {

            Collection<String> setKeys = getValues(itemMap, item -> item.getEntry().getElement().getSetKey());
            oldValues.addAll(setKeys);
        }

        if (itemMap.containsKey(InspectorEntryType.MISSING_COST_GROUP) || itemMap.containsKey(
            InspectorEntryType.UNKNOWN_COST_GROUP)) {

            Collection<String> costGroups = getValues(itemMap, item -> item.getEntry().getElement().getCostGroup());
            oldValues.addAll(costGroups);
        }

        return initSmartFixes.stream().filter(smartFix -> oldValues.contains(smartFix.getOldValue())).toList();
    }

    private Collection<String> getValues(Map<InspectorEntryType, Collection<InspectorTreeItemObject>> itemMap,
        Function<InspectorTreeItemObject, String> getter) {
        Collection<String> result = new HashSet<>();
        for (Collection<InspectorTreeItemObject> inspectorObjects : itemMap.values()) {
            for (InspectorTreeItemObject inspectorObject : inspectorObjects) {
                if (inspectorObject.isEntryNode()) {
                    result.add(getter.apply(inspectorObject));
                }
            }
        }

        return result;
    }

    private record Result(Collection<EfsElementDTO> savedElements, VehicleConfigDTO vehicleConfig,
                          Collection<EfsElementDTO> changedElements) { }
}
