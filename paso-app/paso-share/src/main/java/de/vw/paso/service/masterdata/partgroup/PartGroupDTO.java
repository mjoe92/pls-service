package de.vw.paso.service.masterdata.partgroup;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PartGroupDTO {

  private Long id;
  private Integer category;
  private Integer mgr;
  private Integer mgrEnd;
  private Integer ugr;
  private String description;


  public boolean isCategory() {
    return mgr == null;
  }
  public boolean isMgr() {
    return mgr != null && ugr == null;
  }

  public boolean isUgr() {
    return ugr != null;
  }

}
