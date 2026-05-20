package de.vw.paso.pls.job;

import de.vw.paso.pls.datarequest.DataRequestException;
import de.vw.paso.pls.service.TiWhImportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Checks for new TI Warehouse data regularly.
 */
@Component
class TiWhResultPollScheduler {

  private final TiWhImportService tiWhImportService;

  TiWhResultPollScheduler(TiWhImportService tiWhImportService) {
    this.tiWhImportService = tiWhImportService;
  }

  @Scheduled(cron = "${fixed-rate.fs-watcher.poll}")
  void pollForArrivingRawData() throws DataRequestException {
    tiWhImportService.checkForNewData();
  }

}
