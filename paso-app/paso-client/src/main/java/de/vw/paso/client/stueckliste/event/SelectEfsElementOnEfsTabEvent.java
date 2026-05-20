package de.vw.paso.client.stueckliste.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SelectEfsElementOnEfsTabEvent {

  private final boolean clearFilter;
  private final Long vehicleConfigId;
  private final Long efsElementId;

}
