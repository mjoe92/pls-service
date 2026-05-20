package de.vw.paso.pls.job;

import de.vw.paso.pls.service.TiWhImportService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

  private final TiWhImportService tiWhImportService;

  public ApplicationStartup(TiWhImportService tiWhImportService) {
    this.tiWhImportService = tiWhImportService;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    tiWhImportService.clearRequestQueue();
    tiWhImportService.deleteTiWhFiles();
  }

}
