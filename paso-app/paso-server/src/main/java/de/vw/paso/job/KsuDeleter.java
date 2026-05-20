package de.vw.paso.job;

import de.vw.paso.logic.message.MessageManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.logic.user.UserPropertyManager;
import de.vw.paso.logic.vehicle.VehicleConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KsuDeleter {

    private static final Logger LOG = LoggerFactory.getLogger(KsuDeleter.class);

    private final UserManager userManager;
    private final UserPropertyManager userPropertyManager;
    private final MessageManager messageManager;
    private final VehicleConfigManager vehicleConfigManager;

    public KsuDeleter(UserManager userManager, UserPropertyManager userPropertyManager, MessageManager messageManager,
            VehicleConfigManager vehicleConfigManager) {
        this.userManager = userManager;
        this.userPropertyManager = userPropertyManager;
        this.messageManager = messageManager;
        this.vehicleConfigManager = vehicleConfigManager;
    }

    @Scheduled(cron = "${cron.cleanup}")
    void run() {
        safeRun(vehicleConfigManager::deleteExpiredVehicleConfigs, "KSU-Klasse 0.2: Expired vehicle configurations");
        safeRun(vehicleConfigManager::deleteUnfinishedVehicleConfigs,
                "KSU-Klasse 0.0: Unfinished vehicle configurations");
        safeRun(vehicleConfigManager::deleteExpiredDeletedVehicleConfigurations,
                "KSU-Klasse 0.2: Expired deleted vehicle configurations");
        safeRun(userPropertyManager::deleteExpiredRecentlyUsedVehicleConfigurations,
                "KSU-Klasse 3.2: Expired recently used vehicle configurations");
        safeRun(userManager::deleteInactiveNonAdminUser, "KSU-Klasse 3.2: Inactive user cleanup");
        safeRun(userManager::usersWithOutRolesCleanup, "KSU-Klasse 0.0: Users without roles cleanup");
        safeRun(userManager::inactiveTestersLastLoginCleanup, "KSU-Klasse 0.0: Inactive testers last login cleanup");
        safeRun(messageManager::deleteUnfinishedMessages, "KSU-Klasse 0.0: Message cleanup");
    }

    private void safeRun(Runnable method, String taskName) {
        try {
            LOG.info("Starting scheduled task: {}", taskName);
            method.run();
            LOG.info("Scheduled task ended: {}", taskName);
        } catch (Exception e) {
            LOG.error("Error while running scheduled task", e);
        }
    }
}