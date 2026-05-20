package de.vw.paso.pls.job;

import de.vw.paso.pls.LogService;
import de.vw.paso.pls.service.MbtImportService;
import de.vw.paso.pls.service.ProductDataService;
import de.vw.paso.pls.service.TiWhImportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
class CleanupScheduler {

  private final ProductDataService productDataService;
  private final TiWhImportService tiWhImportService;
  private final LogService logService;
  private final MbtImportService mbtImportService;

  @Scheduled(cron = "${cron.cleanup.file-lock}")
  void runFileLockCleanup() {
    productDataService.deleteProductDataWithExpiredLocks();
  }

  @Scheduled(cron = "${cron.cleanup.ti-wh-files}")
  void runTiWhFilesCleanup() {
    tiWhImportService.deleteTiWhFiles();
  }

  @Scheduled(cron = "${cron.cleanup.mbt-files}")
  void runMbtImportService() {
    mbtImportService.cleanUpRemoteDirectory();
  }

  @Scheduled(cron = "${cron.cleanup.logs}")
  void runLogCleanup() {
    logService.cleanupLog();
  }
}
