package de.vw.paso.job;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.vw.paso.logic.message.MessageManager;
import de.vw.paso.logic.pls.PlsServiceManager;
import de.vw.paso.message.domain.UserMessage;
import de.vw.paso.pls.PartListStatus;
import de.vw.paso.pls.Status;
import de.vw.paso.repository.vehicle.PendingProductDataIdDTO;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.service.pls.StatusResponse;
import de.vw.paso.vehicle.domain.VehicleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Periodically updates the vehicle configuration based off the requested part list from PLS.
 * Saves the status of the resulting operation, e.g., if it worked or an exception occurred.
 */
@Component
public class VehicleConfigPartListUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleConfigPartListUpdater.class);

    private final PlsServiceManager plsServiceManager;
    private final VehicleConfigRepository vehicleConfigRepository;
    private final MessageManager messageManager;

    private boolean plsAvailable;

    public VehicleConfigPartListUpdater(PlsServiceManager plsServiceManager,
            VehicleConfigRepository vehicleConfigRepository, MessageManager messageManager) {
        this.plsServiceManager = plsServiceManager;
        this.vehicleConfigRepository = vehicleConfigRepository;
        this.messageManager = messageManager;
        plsAvailable = true;
    }

    @Scheduled(cron = "${cron.pls.update-configurations}")
    @Transactional
    public void updatePendingVehicleConfigurations() {
        if (!plsAvailable) {
            LOG.info("PLS was not available last time");

            if (plsServiceManager.checkPlsAvailable()) {
                LOG.info("PLS is available again. Starting normal update");
                plsAvailable = true;
            }
        }

        if (plsAvailable) {
            try {
                processPending();
            } catch (Exception e) {
                plsAvailable = false;
                LOG.error("Could not get status for requested configurations. PLS not available", e);
            }
        } else {
            LOG.info("PLS still not available. Skip checking requested data");
        }
    }

    private void processPending() {
        Collection<PendingProductDataIdDTO> pendingData = vehicleConfigRepository.getPendingProductDataId();
        if (pendingData.isEmpty()) {
            LOG.info("No requested configs found.");
            return;
        }

        LOG.info("Found {} configuration(s)", pendingData.size());
        Set<String> productDataIds = new HashSet<>();
        Map<String, String> productDataIdToProductKey = new HashMap<>();
        for (PendingProductDataIdDTO element : pendingData) {
            String productDataId = element.productDataId();
            productDataIds.add(productDataId);
            productDataIdToProductKey.put(productDataId, element.productKey());
        }
        Collection<StatusResponse> statusForConfigurations = plsServiceManager.getStatusForProductData(productDataIds);
        Map<String, Integer> positions = plsServiceManager.getTiWhQueuePositions();

        for (StatusResponse statusResponse : statusForConfigurations) {
            String productDataId = statusResponse.productDataId();
            PartListStatus newStatus = statusResponse.status();
            Integer position = positions.get(productDataIdToProductKey.get(productDataId));
            LOG.info("Update status for product data id: {} , pos: {}, status: {}", productDataId, position, newStatus);

            Collection<VehicleConfig> vehicleConfigs = vehicleConfigRepository.getVehicleConfigByProductDataID(
                    productDataId);
            if (PartListStatus.READY == newStatus) {
                LOG.info("Part list data is ready for product data id: {}", productDataId);

                for (VehicleConfig vehicleConfig : vehicleConfigs) {
                    LOG.info("Will schedule part list creation for vehicle config id: {}", vehicleConfig.getId());
                    createPartList(vehicleConfig);
                }
            } else {
                for (VehicleConfig vehicleConfig : vehicleConfigs) {
                    plsServiceManager.saveStatusAndPosition(vehicleConfig, Status.ofPartList(newStatus), position);
                }
            }
        }
    }

    private void createPartList(VehicleConfig vehicleConfig) {
        plsServiceManager.saveStatusAndPosition(vehicleConfig, Status.READY, 0);

        UserMessage um = UserMessage.createPartListInCreationMessage(vehicleConfig.getUserCreate(),
                vehicleConfig.getId());
        messageManager.save(um);

        try {
            LOG.info("Start part list creation for: {}", vehicleConfig.getId());
            plsServiceManager.createPartList(vehicleConfig);
            LOG.info("Part list creation done for: {} and user: {}", vehicleConfig.getId(),
                    vehicleConfig.getUserCreate());

            um = UserMessage.createPartListCreatedMessage(vehicleConfig.getUserCreate(), vehicleConfig.getId());
            messageManager.save(um);
        } catch (Exception e) {
            LOG.error("Could not create part list for vehicle config id: {}", vehicleConfig.getId(), e);
            plsServiceManager.saveStatusAndPosition(vehicleConfig, Status.ERROR, null);

            um = UserMessage.createPartListErrorMessage(vehicleConfig.getUserCreate(), vehicleConfig.getId());
            messageManager.save(um);
        }
    }
}