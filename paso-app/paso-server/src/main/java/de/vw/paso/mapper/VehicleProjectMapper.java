package de.vw.paso.mapper;

import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.masterdata.domain.VehicleProject;
import de.vw.paso.service.masterdata.product.ProductDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;

public final class VehicleProjectMapper {

    public static VehicleProject toEntity(VehicleProjectDTO dto) {
        VehicleProject entity = new VehicleProject();

        entity.setId(dto.getId());
        entity.setProjectName(dto.getProjectName());
        entity.setDescription(dto.getDescription());
        entity.setProductKey(dto.getProductKey());
        entity.setSalesKey(dto.getSalesKey());
        entity.setFirstModelYear(dto.getFirstModelYear());
        entity.setPlatform(dto.getPlatform());
        entity.setBrandCode(dto.getBrandCode());
        entity.setArchive(dto.isArchive());
        entity.setUserCreate(dto.getUserCreate());
        entity.setTimestampCreate(dto.getTimestampCreate());
        entity.setUserChange(dto.getUserChange());
        entity.setTimestampChange(dto.getTimestampChange());
        entity.setEntityChange(dto.isEntityChange());

        Product product = ProductMapper.toEntity(dto.getProductDTO());
        entity.setProduct(product);

        return entity;
    }

    public static VehicleProjectDTO toDto(VehicleProject entity) {
        Product product = entity.getProduct();
        ProductDTO productDto = product == null ? null : ProductMapper.toDto(product);

        VehicleProjectDTO vehicleProjectDTO = new VehicleProjectDTO(entity.getId(), entity.getProjectName(),
                entity.getDescription(), entity.getProductKey(), productDto, entity.getSalesKey(),
                entity.getFirstModelYear(), entity.getPlatform(), entity.getBrandCode(), entity.isArchive());

        vehicleProjectDTO.setUserCreate(entity.getUserCreate());
        vehicleProjectDTO.setTimestampCreate(entity.getTimestampCreate());
        vehicleProjectDTO.setUserChange(entity.getUserChange());
        vehicleProjectDTO.setTimestampChange(entity.getTimestampChange());
        vehicleProjectDTO.setEntityChange(entity.isEntityChange());

        return vehicleProjectDTO;
    }
}
