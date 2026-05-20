package de.vw.paso.logic.tiwhimport;

import java.util.List;

import de.vw.paso.exception.DataNotFoundException;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.repository.tiwhimport.TiWhImportRepository;
import de.vw.paso.status.ImportStatus;
import de.vw.paso.tiwh.domain.TiWhImport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class TiWhImportManager {

  private final TiWhImportRepository tiWhImportRepository;
  private final UserManager userManager;

  public List<TiWhImport> loadDatenstaende(final String productKey) {
    return tiWhImportRepository.findAllByProductKey(productKey);
  }

  @Transactional
  public TiWhImport importPartList(final String productKey) {
    final TiWhImport tiWhImport = new TiWhImport();

    tiWhImport.setProductKey(productKey);
    tiWhImport.setImportStatus(ImportStatus.REQUESTED);
    tiWhImport.setChange(userManager.getCurrentUserId());

    return tiWhImportRepository.save(tiWhImport);
  }

  public ImportStatus loadImportStatus(final Long tiWhImportId) {
    return tiWhImportRepository.findById(tiWhImportId)
      .orElseThrow(() -> new DataNotFoundException("Could not load tiwhImport")).getImportStatus();
  }

}
