package de.vw.paso.client.control.cell;

import de.vw.paso.client.stueckliste.util.CogUtil;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CogCoordinates {

  private Double x;
  private Double y;
  private Double z;

  public CogCoordinates(IEfsElementForDTO element) {
    if (element != null) {
      this.x = element.getCogX();
      this.y = element.getCogY();
      this.z = element.getCogZ();
    }
  }

  public CogCoordinates(Double x, Double y, Double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Double getCogX() {
    return x;
  }

  public Double getCogY() {
    return y;
  }

  public Double getCogZ() {
    return z;
  }

  public boolean isEmpty() {
    return x == null && y == null && z == null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    CogCoordinates that = (CogCoordinates) o;

    return new EqualsBuilder()
      .append(x, that.x)
      .append(y, that.y)
      .append(z, that.z)
      .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
      .append(x)
      .append(y)
      .append(z)
      .toHashCode();
  }

  @Override
  public String toString() {
    return CogUtil.toString(this);
  }
}
