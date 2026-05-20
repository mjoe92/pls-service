package de.vw.paso.service.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.vw.paso.core.domain.AbstractModifiableDTO;
import de.vw.paso.partlist.domain.IPartListChildDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class VehiclePartListDTO extends AbstractModifiableDTO<Long> implements IPartListChildDTO {
  private Long id;
  private VehicleConfigDTO vehicleConfig;
  private Long revision = 0L;
  private Double weight = 0.0D;
  private String productKeyVehicle;
  private String productKeyMotor;
  private String productKeyGearbox;

  @Override
  @JsonIgnore
  public Long getVehiclePartListId() {
    return id;
  }

  @Override
  @JsonIgnore
  public EfsElementDTO asParent() {
    return null;
  }

  public void setProductKeyVehicle(String productKeyVehicle) {
    this.productKeyVehicle = productKeyVehicle;
  }

  public void setProductKeyMotor(String productKeyMotor) {
    this.productKeyMotor = productKeyMotor;
  }

  public void setProductKeyGearbox(String productKeyGearbox) {
    this.productKeyGearbox = productKeyGearbox;
  }

  public String getProductKeyVehicle() {
    return productKeyVehicle;
  }

  public String getProductKeyMotor() {
    return productKeyMotor;
  }

  public String getProductKeyGearbox() {
    return productKeyGearbox;
  }
}
