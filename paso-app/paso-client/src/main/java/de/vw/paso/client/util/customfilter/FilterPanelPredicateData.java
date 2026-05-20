package de.vw.paso.client.util.customfilter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FilterPanelPredicateData {

  private String rule;
  private String upperField;
  private String lowerField;
}
