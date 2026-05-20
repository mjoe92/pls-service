package de.vw.paso.pls;

import java.time.Duration;
import java.time.LocalDateTime;

import de.vw.paso.pls.repository.PlsLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LogService {

  @Value("${log.delete-after:30}")
  private int deleteAfterDays;

  private final PlsLogRepository plsLogRepository;

  public LogService(PlsLogRepository plsLogRepository) {
    this.plsLogRepository = plsLogRepository;
  }

  public void cleanupLog() {
    synchronized (this) {
      LocalDateTime date = LocalDateTime.now().minus(Duration.ofDays(deleteAfterDays));
      plsLogRepository.deleteOldLogs(date.toLocalDate());
    }
  }
}
