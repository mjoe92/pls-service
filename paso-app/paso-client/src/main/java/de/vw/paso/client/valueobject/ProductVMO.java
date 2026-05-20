package de.vw.paso.client.valueobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import de.vw.paso.service.masterdata.product.ProductDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductVMO {

    private ObjectProperty<ProductDTO> productProperty = new SimpleObjectProperty<>(this, "product");

    public String getProductKey() {
        return Objects.nonNull(productProperty.getValue()) ? productProperty.getValue().getProductKey() : null;
    }

    public SimpleStringProperty getProductKeyProperty() {
        return Objects.nonNull(productProperty.getValue()) ?
                new SimpleStringProperty(productProperty.getValue().getProductKey()) : null;
    }

    public void setSetVersionId(String productKey) {
        if (Objects.nonNull(productProperty.getValue())) {
            productProperty.getValue().setProductKey(productKey);
        }
    }

    public Long getSetVersion() {
        return Objects.nonNull(productProperty.getValue()) ? productProperty.getValue().getSetVersionId() : null;
    }

    public String getProductType() {
        return Objects.nonNull(productProperty.getValue()) ? productProperty.getValue().getProductType() : null;
    }

    public ObjectProperty<Long> getSetVersionProperty() {
        return Objects.nonNull(productProperty.getValue()) ?
                new SimpleObjectProperty<>(productProperty.getValue().getSetVersionId()) : null;
    }

    public void setSetVersionId(Long setVersionId) {
        if (Objects.nonNull(productProperty.getValue())) {
            productProperty.getValue().setSetVersionId(setVersionId);
        }
    }

    public SetVersionDTO getSetVersionDTO() {
        return Objects.nonNull(productProperty.getValue()) ? productProperty.getValue().getSetVersionDTO() : null;
    }

    public SimpleStringProperty getSetVersionName() {
        return Objects.nonNull(productProperty.getValue()) ?
                new SimpleStringProperty(productProperty.getValue().getSetVersionDTO().getName()) : null;
    }

    public void setSetVersionDTO(SetVersionDTO setVersion) {
        if (Objects.nonNull(productProperty.getValue())) {
            productProperty.getValue().setSetVersionDTO(setVersion);
        }
    }

    public boolean isEditable() {
        return Objects.nonNull(productProperty.getValue()) && productProperty.getValue().isEditable();
    }

    public static ProductVMO toVMO(ProductDTO productDTO) {
        ProductVMO vmo = new ProductVMO();
        vmo.productProperty.set(productDTO);
        return vmo;
    }

    public static List<ProductVMO> toProductVMOList(Collection<ProductDTO> productDTOList) {
        return productDTOList.stream().map(ProductVMO::toVMO).collect(Collectors.toCollection(ArrayList::new));
    }

    public static ProductDTO toDTO(ProductVMO productVMO) {
        return new ProductDTO(productVMO.getProductKey(), productVMO.getProductType(), productVMO.getSetVersion(),
                productVMO.getSetVersionDTO());
    }
}
