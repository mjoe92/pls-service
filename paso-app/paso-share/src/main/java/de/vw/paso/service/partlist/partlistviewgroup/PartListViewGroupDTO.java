package de.vw.paso.service.partlist.partlistviewgroup;

import de.vw.paso.partlist.domain.PartListViewMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PartListViewGroupDTO {

  private Long id;
  private String name;
  private String ruleDescription;
  private String costGroup;
  private String partGroups;
  private PartListViewMode partListViewMode;
}
