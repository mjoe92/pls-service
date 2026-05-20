package de.vw.paso.service.masterdata.product;

import de.vw.paso.core.domain.AbstractModifiableDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductDTO extends AbstractModifiableDTO<String> {
  private String productKey;
  private String productType;
  private Long setVersionId;
  private SetVersionDTO setVersionDTO;
  private boolean isEditable = false;

  public ProductDTO(String productKey, String productType, Long setVersionId, SetVersionDTO setVersionDTO) {
    this.productKey = productKey;
    this.productType = productType;
    this.setVersionId = setVersionId;
    this.setVersionDTO = setVersionDTO;
    this.isEditable = false;
  }

    public ProductDTO(String productKey, Long setVersion, SetVersionDTO setVersionDTO) {
      this.productKey = productKey;
      this.setVersionId = setVersion;
      this.setVersionDTO = setVersionDTO;
      this.isEditable = false;
    }

    public String getId() {
    return productKey;
  }

  public void setId(String productKey) {
    this.productKey = productKey;
  }
}
