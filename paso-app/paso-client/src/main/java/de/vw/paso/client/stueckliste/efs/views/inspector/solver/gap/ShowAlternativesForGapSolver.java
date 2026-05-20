package de.vw.paso.client.stueckliste.efs.views.inspector.solver.gap;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.delegate.stueckliste.efsriss.EfsRissRestClientHolder;
import de.vw.paso.partlist.domain.SpecialPartNumberType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsriss.AlternativePartsForGapListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class ShowAlternativesForGapSolver extends AbstractSolver {

    @Override
    public String getTitleKey() {
        return "alternatives.risse";
    }

    @Override
    public boolean solve() {
        ServiceController<Map<EfsElementDTO, String>> serviceController = new ServiceController<>();
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        serviceController.setExecutionTime(750);
        serviceController.start(this::loadAlternativePartsForGap);
        serviceController.setOnSucceeded(event -> openDialog(serviceController.getValue()));

        return true;
    }

    private Map<EfsElementDTO, String> loadAlternativePartsForGap() {
        String nodeId = getEntries().iterator().next().getValue().getEntry().getElement().getNodeId();
        AlternativePartsForGapListDTO alternativePartsForGap = EfsRissRestClientHolder.getInstance()
                .getAlternativePartsForGap(nodeId, getVehicleConfig().getId());

        return AlternativePartsForGapListDTO.createEfsElementMap(alternativePartsForGap);
    }

    @Override
    public boolean disable() {
        return getEntries().size() != 1;
    }

    private void openDialog(Map<EfsElementDTO, String> efsElements) {
        Map<EfsElementDTO, String> elements = efsElements.entrySet().stream()
                .filter(entry -> !entry.getKey().getPartNumber().equals(SpecialPartNumberType.NO_MARA.getLabel()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        ShowAlternativesForGapDialog solutionDialog = new ShowAlternativesForGapDialog(getEntries(), elements,
                vehicleConfig);

        openDialog(solutionDialog, result -> result.ifPresent(this::processEfsEdit));
    }

    private void processEfsEdit(Collection<EfsElementDTO> changingElements) {
        try {
            InspectorUtil.processEfsEdit(changingElements, getVehicleConfig());
        } catch (Exception e) {
            handleException(e);
        }
    }
}
