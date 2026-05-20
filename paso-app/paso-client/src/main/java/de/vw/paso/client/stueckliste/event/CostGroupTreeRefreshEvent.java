package de.vw.paso.client.stueckliste.event;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Todo ZsN - Aggregated view - Delete
@Getter
@AllArgsConstructor
public class CostGroupTreeRefreshEvent {

  private final Long vehiclePartListId;
  private final EfsElementDTO efsElement;
  private final boolean treeRefresh;

}
