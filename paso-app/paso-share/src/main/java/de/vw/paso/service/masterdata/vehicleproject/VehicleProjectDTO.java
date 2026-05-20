package de.vw.paso.service.masterdata.vehicleproject;

import de.vw.paso.core.domain.AbstractModifiableDTO;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.masterdata.product.ProductDTO;

public class VehicleProjectDTO extends AbstractModifiableDTO<Long> {

  private Long id;
  private String projectName;
  private String description;
  private String productKey;
  private ProductDTO productDTO;
  private String salesKey;
  private Integer firstModelYear;
  private String platform;
  private Brand brandCode;
  private boolean archive;

  public VehicleProjectDTO() {
    // noop
  }

  public VehicleProjectDTO(Long id, String projectName, String description, String productKey, ProductDTO productDTO,
    String salesKey, Integer firstModelYear, String platform, Brand brandCode, boolean archive) {
    this.id = id;
    this.projectName = projectName;
    this.description = description;
    this.productKey = productKey;
    this.productDTO = productDTO;
    this.salesKey = salesKey;
    this.firstModelYear = firstModelYear;
    this.platform = platform;
    this.brandCode = brandCode;
    this.archive = archive;
  }

  public Brand getBrandCode() {
    return brandCode;
  }

  public String getDescription() {
    return description;
  }

  public Integer getFirstModelYear() {
    return firstModelYear;
  }

  @Override
  public Long getId() {
    return id;
  }

  public String getPlatform() {
    return platform;
  }

  public ProductDTO getProductDTO() {
    return productDTO;
  }

  public String getProductKey() {
    return productKey;
  }

  public String getProjectName() {
    return projectName;
  }

  public String getSalesKey() {
    return salesKey;
  }

  public boolean isArchive() {
    return archive;
  }

  public void setArchive(boolean archive) {
    this.archive = archive;
  }

  public void setBrandCode(Brand brandCode) {
    this.brandCode = brandCode;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFirstModelYear(Integer firstModelYear) {
    this.firstModelYear = firstModelYear;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public void setProductDTO(ProductDTO productDTO) {
    this.productDTO = productDTO;
  }

  public void setProductKey(String productKey) {
    this.productKey = productKey;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public void setSalesKey(String salesKey) {
    this.salesKey = salesKey;
  }
}
