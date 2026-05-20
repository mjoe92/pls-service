package de.vw.paso.service.tableconfig;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TableConfigDTO {

  private Long id;
  private String userId;
  private String name;
  private List<String> selectedColumns = new ArrayList<>();
  private List<String> selectedColumnIds = new ArrayList<>();
  private boolean isPublic;
  private boolean isDefault;

  public TableConfigDTO(TableConfigDTO tableConfigDTO, String userId, boolean isPublic) {
    this.userId = userId;
    this.name = tableConfigDTO.getName();
    this.selectedColumnIds = tableConfigDTO.getSelectedColumnIds();
    this.selectedColumns = tableConfigDTO.getSelectedColumns();
    this.isPublic = isPublic;
    this.isDefault = false;
  }

  @Override
  public String toString() {
    return isPublic ? name + " (Public)" : name;
  }
}
