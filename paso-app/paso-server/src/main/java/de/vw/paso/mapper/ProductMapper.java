package de.vw.paso.mapper;

import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.service.masterdata.product.ProductDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;

public final class ProductMapper {

    public static Product toEntity(ProductDTO dto) {
        Product entity = new Product();

        entity.setId(dto.getProductKey());
        entity.setSetVersionId(dto.getSetVersionId());
        entity.setSetVersion(SetVersionMapper.toEntity(dto.getSetVersionDTO()));

        return entity;
    }

    public static ProductDTO toDto(Product entity) {
        SetVersionDTO setVersion = SetVersionMapper.toDto(entity.getSetVersion());
        return new ProductDTO(entity.getProductKey(), entity.getProductType(), entity.getSetVersionId(), setVersion);
    }
}
