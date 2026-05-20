package de.vw.paso.repository.modelimport;

import java.util.List;

import de.vw.paso.model.ModelImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModelImportRepository extends JpaRepository<ModelImport, Long> {

  @Query("SELECT m FROM ModelImport AS m WHERE :salesKey LIKE CONCAT('%', m.salesKey, '%')")
  List<ModelImport> findAllBySalesKey(@Param("salesKey") String salesKey);

}
