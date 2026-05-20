package de.vw.paso.repository.activitylog;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AdminActivityLogPk(String userId, LocalDateTime logDate) implements Serializable { }
