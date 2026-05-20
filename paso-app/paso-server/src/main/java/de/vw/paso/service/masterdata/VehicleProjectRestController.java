package de.vw.paso.service.masterdata;

import java.util.List;

import de.vw.paso.logic.masterdata.VehicleProjectManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.SetVersionMapper;
import de.vw.paso.mapper.VehicleProjectMapper;
import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.masterdata.domain.VehicleProject;
import de.vw.paso.service.masterdata.product.ProductDTO;
import de.vw.paso.service.masterdata.vehicleproject.UpdateVehicleProjectArchiveStateDTO;
import de.vw.paso.service.masterdata.vehicleproject.UpdatedVehicleProjectSetVersionDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectListDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectRestService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = VehicleProjectRestService.URL)
public class VehicleProjectRestController implements VehicleProjectRestService {

    private final VehicleProjectManager vehicleProjectManager;
    private final UserManager userManager;

    public VehicleProjectRestController(VehicleProjectManager vehicleProjectManager, UserManager userManager) {
        this.vehicleProjectManager = vehicleProjectManager;
        this.userManager = userManager;
    }

    @Override
    @GetMapping
    @Transactional
    public VehicleProjectListDTO loadVehicleProjects() {
        List<VehicleProjectDTO> vehicleProjectDTOList = vehicleProjectManager.loadVehicleProjects().stream()
                .map(this::convertToDTO).toList();
        return new VehicleProjectListDTO(vehicleProjectDTOList);
    }

    @Override
    @PutMapping
    public void updateVehicleProjectArchiveState(@RequestBody UpdateVehicleProjectArchiveStateDTO updateDTO) {
        userManager.requireAdminUser();

        vehicleProjectManager.updateVehicleProjectArchiveState(updateDTO.vehicleProjectIds(), updateDTO.isArchived());
    }

    @Override
    @PostMapping(UPDATE_SET_VERSION)
    public VehicleProjectDTO updateVehicleProjectSetVersion(
            @RequestBody UpdatedVehicleProjectSetVersionDTO vehicleProjectSetVersionDTO) {
        userManager.requireAdminUser();

        return VehicleProjectMapper.toDto(vehicleProjectManager.updateDefaultSetVersion(vehicleProjectSetVersionDTO));
    }

    private VehicleProjectDTO convertToDTO(VehicleProject vehicleProject) {
        return new VehicleProjectDTO(vehicleProject.getId(), vehicleProject.getProjectName(),
                vehicleProject.getDescription(), vehicleProject.getProductKey(),
                convertToProductDTO(vehicleProject.getProduct()), vehicleProject.getSalesKey(),
                vehicleProject.getFirstModelYear(), vehicleProject.getPlatform(), vehicleProject.getBrandCode(),
                vehicleProject.isArchive());
    }

    private ProductDTO convertToProductDTO(Product product) {
        return new ProductDTO(product.getProductKey(), product.getProductType(), product.getSetVersionId(),
                SetVersionMapper.toDto(product.getSetVersion()));
    }
}
