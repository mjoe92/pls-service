package de.vw.paso.service.partlist.efselementhistory;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EfsElementAndMaraAndHistoryDTO {
  private EfsElementDTO efsElementDTO;
  private EfsElementHistoryDTO efsElementHistoryDTO;
  private EfsElementMaraDTO efsElementMaraDTO;
  private EfsElementMaraHistoryDTO efsElementMaraHistoryDTO;
}
