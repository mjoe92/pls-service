package de.vw.paso.service.masterdata.vehicleproject;

import java.util.Objects;
import java.util.StringJoiner;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UpdatedVehicleProjectSetVersionDTO {

  private String setVersion;
  private Long vehicleProjectId;

  public String getSetVersion() {
    return setVersion;
  }

  public void setSetVersion(String setVersion) {
    this.setVersion = setVersion;
  }

  public Long getVehicleProjectId() {
    return vehicleProjectId;
  }

  public void setVehicleProjectId(Long vehicleProjectId) {
    this.vehicleProjectId = vehicleProjectId;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UpdatedVehicleProjectSetVersionDTO that)) {
      return false;
    }
    return Objects.equals(setVersion, that.setVersion) && Objects.equals(vehicleProjectId, that.vehicleProjectId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(setVersion, vehicleProjectId);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", UpdatedVehicleProjectSetVersionDTO.class.getSimpleName() + "[", "]").add(
      "setVersion='" + setVersion + "'").add("vehicleProjectId=" + vehicleProjectId).toString();
  }
}
