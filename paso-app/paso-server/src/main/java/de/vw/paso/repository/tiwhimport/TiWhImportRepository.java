package de.vw.paso.repository.tiwhimport;

import java.util.List;

import de.vw.paso.tiwh.domain.TiWhImport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TiWhImportRepository extends JpaRepository<TiWhImport, Long> {

  List<TiWhImport> findAllByProductKey(String productKey);

}
