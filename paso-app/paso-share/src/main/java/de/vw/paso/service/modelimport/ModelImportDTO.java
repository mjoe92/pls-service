package de.vw.paso.service.modelimport;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import de.vw.paso.service.masterdata.salesregion.SalesRegionDTO;
import de.vw.paso.status.ImportStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ModelImportDTO implements Serializable {
  private Long id;
  private String salesKey;
  private Integer modelYear;
  private ImportStatus importStatus;
  private SalesRegionDTO salesRegion;
  private Set<ModelDTO> models = new HashSet<>();
  private Timestamp timestampChange;
}
