package de.vw.paso.service.tiwhimport;

import de.vw.paso.core.domain.AbstractModifiableDTO;
import de.vw.paso.status.ImportStatus;
import de.vw.paso.tiwh.IDatenstand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TiWhImportDTO extends AbstractModifiableDTO<Long> implements IDatenstand {

  private Long id;
  private String productKey;
  private ImportStatus importStatus;
}
