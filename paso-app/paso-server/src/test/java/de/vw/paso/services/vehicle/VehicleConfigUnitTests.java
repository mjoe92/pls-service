package de.vw.paso.services.vehicle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.vw.paso.logic.user.UserManager;
import de.vw.paso.logic.user.UserPropertyManager;
import de.vw.paso.logic.vehicle.VehicleConfigManager;
import de.vw.paso.masterdata.domain.VehicleProject;
import de.vw.paso.pls.Status;
import de.vw.paso.repository.masterdata.ProductRepository;
import de.vw.paso.repository.masterdata.SalesRegionRepository;
import de.vw.paso.repository.masterdata.VehicleProjectRepository;
import de.vw.paso.repository.modelimport.ModelImportRepository;
import de.vw.paso.repository.modelimport.ModelRepository;
import de.vw.paso.repository.partlist.CostGroupRepository;
import de.vw.paso.repository.partlist.VehiclePartListRepository;
import de.vw.paso.repository.tiwhimport.TiWhImportRepository;
import de.vw.paso.repository.user.UserGroupRepository;
import de.vw.paso.repository.vehicle.ResourceRepository;
import de.vw.paso.repository.vehicle.VehicleConfigCategoryStatusRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.user.domain.Resource;
import de.vw.paso.user.domain.UserGroup;
import de.vw.paso.util.UnauthorizedException;
import de.vw.paso.vehicle.VehicleConfigCategory;
import de.vw.paso.vehicle.domain.VehicleConfig;
import de.vw.paso.vehicle.domain.VehicleConfigCategoryStatus;
import de.vw.paso.vehicle.domain.VehicleConfigCategoryStatusPK;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

//TODO: maybe a few more tcs would be nice here
public class VehicleConfigUnitTests {

    @InjectMocks
    private VehicleConfigManager vehicleConfigManager;

    @Mock
    private CostGroupRepository costGroupRepository;
    @Mock
    private ModelRepository modelRepository;
    @Mock
    private ModelImportRepository modelImportRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private SalesRegionRepository salesRegionRepository;
    @Mock
    private TiWhImportRepository tiWhImportRepository;
    @Mock
    private VehicleConfigRepository vehicleConfigRepository;
    @Mock
    private VehicleConfigCategoryStatusRepository vehicleConfigCategoryStatusRepository;
    @Mock
    private VehiclePartListRepository vehiclePartListRepository;
    @Mock
    private VehicleProjectRepository vehicleProjectRepository;
    @Mock
    private UserManager userManager;
    @Mock
    private UserPropertyManager userPropertyManager;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserGroupRepository userGroupRepository;

    private AutoCloseable autoCloseable;
    private VehicleConfig vehicleConfig;
    private List<UserGroup> userGroups;
    private Resource resource;

    @BeforeEach
    public void onSetUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        vehicleConfig = new VehicleConfig();

        resource = new Resource();
    }

    @AfterEach
    public void closeMocks() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void editVehicleConfigWithoutAccess() {
        given(userManager.getCurrentUserId()).willReturn("User");

        given(userManager.getVehicleConfigIdsWithAccess()).willReturn(Map.of(1L, false));

        vehicleConfig.setId(1L);

        var exception = assertThrows(UnauthorizedException.class,
                () -> vehicleConfigManager.saveVehicleConfig(vehicleConfig, false));

        assertEquals("Current user doesn't have the right to edit vehicle config (id: 1)", exception.getMessage());
    }

    @Test
    public void editOrCreateVehicleConfigWithAccess() {
        var userGroup = new UserGroup();
        userGroup.setId(1L);
        var project = new VehicleProject();
        project.setProjectName("project1");
        var persisntenceVehicleConfig = new VehicleConfig();
        persisntenceVehicleConfig.setOwnerGroup(userGroup);
        persisntenceVehicleConfig.setVehicleProject(project);
        given(userManager.getCurrentUserId()).willReturn("User");

        given(userManager.getVehicleConfigIdsWithAccess()).willReturn(Map.of(1L, true));

        given(vehicleConfigRepository.save(vehicleConfig)).willReturn(persisntenceVehicleConfig);

        resource.setId(1L);

        VehicleConfigCategoryStatusPK pk = new VehicleConfigCategoryStatusPK();
        pk.setVehicleConfigCategory(VehicleConfigCategory.KONFIGURATION);

        VehicleConfigCategoryStatus status = new VehicleConfigCategoryStatus();
        status.setId(pk);

        vehicleConfig.setName("config");
        vehicleConfig.setResource(resource);
        vehicleConfig.setVehicleConfigCategoryStatus(List.of(status));
        vehicleConfig.setSetVersionId(1L);
        vehicleConfig.setOwnerGroup(userGroup);
        vehicleConfig.setVehicleProject(project);

        assertDoesNotThrow(() -> vehicleConfigManager.saveVehicleConfig(vehicleConfig, false));
    }

    @Test
    public void deleteVehicleConfigWithoutAccess() {
        given(userManager.getCurrentUserId()).willReturn("User");

        given(userManager.getVehicleConfigIdsWithAccess()).willReturn(Map.of(1L, false));

        vehicleConfig.setId(1L);

        var exception = assertThrows(UnauthorizedException.class, () -> vehicleConfigManager.deleteVehicleConfig(1L));

        assertEquals("Current user doesn't have the right to delete vehicle config (id: 1)", exception.getMessage());
    }

    @Test
    public void deleteVehicleConfigWithAccess() {
        given(userManager.getCurrentUserId()).willReturn("User");

        given(userManager.getVehicleConfigIdsWithAccess()).willReturn(Map.of(1L, true));

        vehicleConfig.setStatus(Status.COMPLETE);
        given(vehicleConfigRepository.findById(1L)).willReturn(Optional.of(vehicleConfig));

        assertDoesNotThrow(() -> vehicleConfigManager.deleteVehicleConfig(1L));
    }
}
