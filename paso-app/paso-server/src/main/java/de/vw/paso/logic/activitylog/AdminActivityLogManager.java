package de.vw.paso.logic.activitylog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import de.vw.paso.repository.activitylog.AdminActivityLog;
import de.vw.paso.repository.activitylog.AdminActivityLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminActivityLogManager {

    private AdminActivityLogRepository adminActivityLogRepository;

    public AdminActivityLog logPermissionChange(String adminUserId, Set<String> affectedUserId,
            List<String> assignedRoles, List<String> removedRoles) {
        AdminActivityLog log = new AdminActivityLog();
        log.setUserId(adminUserId);
        log.setLogDate(LocalDateTime.now());
        log.setLogText(
                String.format("Changed roles for %s: Removed Roles: %s Added Roles: %s", affectedUserId, removedRoles,
                        assignedRoles));
        return adminActivityLogRepository.save(log);
    }

    public AdminActivityLog logUserActive(String adminUserId, List<String> affectedUserIds, boolean active) {
        AdminActivityLog log = new AdminActivityLog();
        log.setUserId(adminUserId);
        log.setLogDate(LocalDateTime.now());
        log.setLogText(String.format("Changed active state for %s: Set active to %s.", affectedUserIds, active));
        return adminActivityLogRepository.save(log);
    }

    public AdminActivityLog logUserSettingDeletion(String adminUserId, List<String> affectedUserIds) {
        AdminActivityLog log = new AdminActivityLog();
        log.setUserId(adminUserId);
        log.setLogDate(LocalDateTime.now());
        log.setLogText(String.format("Deleted settings for %s", affectedUserIds));
        return adminActivityLogRepository.save(log);
    }

    public AdminActivityLog logSystemMessageCreated(String adminUserId, String text) {
        AdminActivityLog log = new AdminActivityLog();
        log.setUserId(adminUserId);
        log.setLogDate(LocalDateTime.now());
        log.setLogText(String.format("Created new system message: %s", text));
        return adminActivityLogRepository.save(log);
    }

    public AdminActivityLog logUserGroupChange(String adminUserId, String logMessage) {
        AdminActivityLog log = new AdminActivityLog();
        log.setUserId(adminUserId);
        log.setLogDate(LocalDateTime.now());
        log.setLogText(logMessage);

        return adminActivityLogRepository.save(log);
    }

    public AdminActivityLog logNewUserGroupCreation(String adminUserId, long userGroupId, String userGroupName,
            String brand, boolean writeAccess) {
        AdminActivityLog log = new AdminActivityLog();
        log.setUserId(adminUserId);
        log.setLogDate(LocalDateTime.now());
        log.setLogText(String.format("Created User Group with id: %s, name: %s for Brand: %s with write access: %s",
                userGroupId, userGroupName, brand, writeAccess));
        return adminActivityLogRepository.save(log);
    }

    public AdminActivityLog logUserDataDeletion(String adminUserId, String logMessage) {
        AdminActivityLog log = new AdminActivityLog();
        log.setUserId(adminUserId);
        log.setLogDate(LocalDateTime.now());
        log.setLogText(logMessage);

        return adminActivityLogRepository.save(log);
    }

}
