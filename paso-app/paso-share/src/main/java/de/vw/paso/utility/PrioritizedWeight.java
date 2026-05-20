package de.vw.paso.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrioritizedWeight {

  private WeightOrigin weightOrigin;
  private Double weight;
}
