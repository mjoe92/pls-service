package de.vw.paso.client.stueckliste.event;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PartGroupTreeRefreshEvent {

  private final Long vehiclePartListId;
  private final EfsElementDTO efsElement;
  private final boolean treeRefresh;

}
