package de.vw.paso.logic.masterdata;

import java.util.Collection;

import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.masterdata.domain.VehicleProject;
import de.vw.paso.partlist.domain.SetVersion;
import de.vw.paso.repository.masterdata.ProductRepository;
import de.vw.paso.repository.masterdata.VehicleProjectRepository;
import de.vw.paso.repository.partlist.SetVersionRepository;
import de.vw.paso.repository.user.UserRepository;
import de.vw.paso.service.masterdata.vehicleproject.UpdatedVehicleProjectSetVersionDTO;
import de.vw.paso.user.domain.User;
import de.vw.paso.util.EntityDoesNotExistException;
import de.vw.paso.util.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleProjectManager {

    private final VehicleProjectRepository vehicleProjectRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SetVersionRepository setVersionRepository;

    public VehicleProjectManager(VehicleProjectRepository vehicleProjectRepository, UserRepository userRepository,
            ProductRepository productRepository, SetVersionRepository setVersionRepository) {
        this.vehicleProjectRepository = vehicleProjectRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.setVersionRepository = setVersionRepository;
    }

    public Collection<VehicleProject> loadVehicleProjects() {
        return vehicleProjectRepository.findAll().stream()
                .filter(vehicleProject -> !ProductManager.PRODUCTS_TO_EXCLUDE.contains(
                        vehicleProject.getProduct().getProductType())).toList();
    }

    @Transactional
    public void updateVehicleProjectArchiveState(Collection<Long> vehicleProjectIds, boolean isArchived) {
        vehicleProjectRepository.updateArchiveStates(vehicleProjectIds, isArchived);
    }

    @Transactional
    public VehicleProject updateDefaultSetVersion(UpdatedVehicleProjectSetVersionDTO vehicleProjectSetVersionDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        User user = userRepository.findById(userId).orElseThrow(UnauthorizedException::new);

        if (!user.isAdmin()) {
            throw new UnauthorizedException("Only admins can update set versions");
        }

        Long projectId = vehicleProjectSetVersionDTO.getVehicleProjectId();

        VehicleProject project = vehicleProjectRepository.findById(projectId)
                .orElseThrow(EntityDoesNotExistException::new);

        String productKey = project.getProductKey();
        Product product = productRepository.findOneByProductKey(productKey);

        String setVersionName = vehicleProjectSetVersionDTO.getSetVersion();
        SetVersion setVersion = setVersionRepository.findByName(setVersionName)
                .orElseThrow(EntityDoesNotExistException::new);

        product.setSetVersionId(setVersion.getId());

        productRepository.save(product);

        return project;
    }
}
