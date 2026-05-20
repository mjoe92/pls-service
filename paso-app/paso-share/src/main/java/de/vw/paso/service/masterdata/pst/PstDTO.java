package de.vw.paso.service.masterdata.pst;

import java.util.Objects;
import java.util.StringJoiner;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PstDTO {

  private Long id;
  private String name;
  private String descEng;
  private String descDe;
  private Long parentId;

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof PstDTO pstDTO)) {
      return false;
    }
    return Objects.equals(id, pstDTO.id) && Objects.equals(name, pstDTO.name) && Objects.equals(descEng, pstDTO.descEng)
      && Objects.equals(descDe, pstDTO.descDe) && Objects.equals(parentId, pstDTO.parentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, descEng, descDe, parentId);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", PstDTO.class.getSimpleName() + "[", "]").add("id=" + id).add("name='" + name + "'")
      .add("descEng='" + descEng + "'").add("descDe='" + descDe + "'").add("parentId=" + parentId).toString();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescEng() {
    return descEng;
  }

  public void setDescEng(String descEng) {
    this.descEng = descEng;
  }

  public String getDescDe() {
    return descDe;
  }

  public void setDescDe(String descDe) {
    this.descDe = descDe;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }
}
