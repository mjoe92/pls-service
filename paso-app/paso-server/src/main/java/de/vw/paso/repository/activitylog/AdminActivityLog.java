package de.vw.paso.repository.activitylog;

import java.time.LocalDateTime;

import de.vw.paso.core.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_activity_log")
@IdClass(AdminActivityLogPk.class)
public class AdminActivityLog extends AbstractEntity<AdminActivityLogPk> {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Id
    @Column(name = "log_date")
    private LocalDateTime logDate;

    @Lob
    @Column(name = "log_text", columnDefinition = "longtext")
    private String logText;

    //todo: remove?
    @Lob
    @Column(name = "log_data", columnDefinition = "blob")
    private byte[] logData;

    @Override
    public AdminActivityLogPk getId() {
        return new AdminActivityLogPk(userId, logDate);
    }

    @Override
    public void setId(AdminActivityLogPk adminActivityLogPk) {
        userId = adminActivityLogPk.userId();
        logDate = adminActivityLogPk.logDate();
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getLogDate() {
        return logDate;
    }

    public String getLogText() {
        return logText;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLogDate(LocalDateTime logDate) {
        this.logDate = logDate;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }
}
