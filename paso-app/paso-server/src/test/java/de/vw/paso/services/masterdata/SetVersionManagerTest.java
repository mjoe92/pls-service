package de.vw.paso.services.masterdata;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.vw.paso.logic.masterdata.SetVersionManager;
import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.masterdata.domain.VehicleProject;
import de.vw.paso.partlist.domain.SetVersion;
import de.vw.paso.repository.masterdata.ProductRepository;
import de.vw.paso.repository.partlist.SetKeyRepository;
import de.vw.paso.repository.partlist.SetVersionRepository;
import de.vw.paso.repository.user.UserRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.right.Role;
import de.vw.paso.user.domain.User;
import de.vw.paso.util.SetVersionReferenceException;
import de.vw.paso.util.UnauthorizedException;
import de.vw.paso.utility.StringConstant;
import de.vw.paso.vehicle.domain.VehicleConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SetVersionManagerTest {

    private SetVersionManager setVersionManager;

    @Mock
    private SetVersionRepository setVersionRepository;
    @Mock
    private SetKeyRepository setKeyRepository;
    @Mock
    private VehicleConfigRepository vehicleConfigRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    private Authentication authentication;
    private AutoCloseable autoCloseable;
    private User user;
    private Role role;
    private SetVersion setVersion;

    @BeforeEach
    public void onSetUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        user = new User();
        setVersionManager = new SetVersionManager(setVersionRepository, setKeyRepository, vehicleConfigRepository,
                productRepository, userRepository);
        given(userRepository.findById("USER")).willReturn(Optional.of(user));

        authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        role = new Role();
        role.setName("ADMIN");
        role.setId(1L);
        setVersion = new SetVersion();
        setVersion.setId(2L);
        setVersion.setName(StringConstant.EMPTY);

        SetVersion defaultSetVersion = new SetVersion();
        defaultSetVersion.setId(1L);
        defaultSetVersion.setName("DEFAULT");

        when(setVersionRepository.findByName("DEFAULT")).thenReturn(Optional.of(defaultSetVersion));

        when(setVersionRepository.findById(1L)).thenReturn(Optional.of(defaultSetVersion));
    }

    @AfterEach
    public void closeMocks() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void deleteWithoutAdminRights() {
        when(authentication.getName()).thenReturn("USER");

        assertThrows(UnauthorizedException.class, () -> setVersionManager.deleteSetVersion(1L));
    }

    @Test
    public void deleteWithOtherProductReferencing() {
        when(authentication.getName()).thenReturn("USER");

        user.setRoles(Set.of(role));

        Collection<String> productIds = List.of("PRODUCT1", "PRODUCT2", "PRODUCT3", "PRODUCT4", "PRODUCT5");

        given(setVersionRepository.findById(2L)).willReturn(Optional.of(setVersion));

        given(productRepository.findBySetVersionId(2L)).willReturn(productIds.stream().map(this::createUser).toList());

        given(setKeyRepository.findByIdVersion(2L)).willReturn(List.of());

        given(vehicleConfigRepository.findBySetVersionId(1L)).willReturn(List.of());

        SetVersionReferenceException a = assertThrows(SetVersionReferenceException.class,
                () -> setVersionManager.deleteSetVersion(2L));

        assertEquals("This set version can not be deleted, because it references other items, namely: \nproduct names: "
                + String.join(StringConstant.COMMA_SPACE, productIds), a.getMessage());
    }

    private Product createUser(String id) {
        Product product = new Product();
        product.setId(id);
        product.setProductType(StringConstant.EMPTY);
        product.setSetVersionId(1L);
        product.setChange("USER");

        return product;
    }

    @Test
    public void deleteWithOtherVehicleConfigReferencing() {
        when(authentication.getName()).thenReturn("USER");

        user.setRoles(Set.of(role));

        VehicleProject vehicleProject = new VehicleProject();
        vehicleProject.setProjectName("VehicleProject");
        List<String> vehicleConfigNames = List.of("CONFIG1", "CONFIG2", "CONFIG3", "CONFIG4", "CONFIG5");
        List<VehicleConfig> vehicleConfigs = vehicleConfigNames.stream().map(id -> getVehicleConfig(id, vehicleProject))
                .toList();

        given(setVersionRepository.findById(2L)).willReturn(Optional.of(setVersion));

        given(vehicleConfigRepository.findBySetVersionId(2L)).willReturn(vehicleConfigs);

        given(setKeyRepository.findByIdVersion(2L)).willReturn(List.of());

        given(productRepository.findBySetVersionId(2L)).willReturn(List.of());

        SetVersionReferenceException setVersionReferenceException = assertThrows(SetVersionReferenceException.class,
                () -> setVersionManager.deleteSetVersion(2L));

        assertEquals(
                "This set version can not be deleted, because it references other items, namely: \nvehicle configs: "
                        + String.join(StringConstant.COMMA_SPACE, vehicleConfigs.stream()
                        .map(config -> StringConstant.LEFT_PARENTHESIS + config.toString()
                                + StringConstant.RIGHT_PARENTHESIS).toList()),
                setVersionReferenceException.getMessage());
    }

    private VehicleConfig getVehicleConfig(String id, VehicleProject vehicleProject) {
        VehicleConfig vehicleConfig = new VehicleConfig();
        vehicleConfig.setName(id);
        vehicleConfig.setVehicleProject(vehicleProject);

        return vehicleConfig;
    }

    @Test
    public void deleteWithOtherBothVehicleConfigAndProductReferencing() {
        when(authentication.getName()).thenReturn("USER");

        user.setRoles(Set.of(role));
        VehicleProject vehicleProject = new VehicleProject();
        vehicleProject.setProjectName("VehicleProject");
        Collection<String> productIds = List.of("PRODUCT1", "PRODUCT2", "PRODUCT3", "PRODUCT4", "PRODUCT5");
        Collection<String> vehicleConfigNames = List.of("CONFIG1", "CONFIG2", "CONFIG3", "CONFIG4", "CONFIG5");
        List<VehicleConfig> vehicleConfigs = vehicleConfigNames.stream().map(id -> getVehicleConfig(id, vehicleProject))
                .toList();

        given(setVersionRepository.findById(2L)).willReturn(Optional.of(setVersion));

        given(vehicleConfigRepository.findBySetVersionId(2L)).willReturn(vehicleConfigs);

        given(productRepository.findBySetVersionId(2L)).willReturn(productIds.stream().map(this::createUser).toList());

        given(setKeyRepository.findByIdVersion(2L)).willReturn(List.of());

        SetVersionReferenceException a = assertThrows(SetVersionReferenceException.class,
                () -> setVersionManager.deleteSetVersion(2L));

        assertEquals("This set version can not be deleted, because it references other items, namely: \nproduct names: "
                + String.join(StringConstant.COMMA_SPACE, productIds) + "\nvehicle configs: " + String.join(
                StringConstant.COMMA_SPACE, vehicleConfigs.stream()
                        .map(config -> StringConstant.LEFT_PARENTHESIS + config.toString()
                                + StringConstant.RIGHT_PARENTHESIS).toList()), a.getMessage());
    }

    @Test
    public void deleteWithNoReference() {
        when(authentication.getName()).thenReturn("USER");

        user.setRoles(Set.of(role));

        given(setVersionRepository.findById(2L)).willReturn(Optional.of(setVersion));
        given(setKeyRepository.findByIdVersion(2L)).willReturn(List.of());
        given(productRepository.findBySetVersionId(2L)).willReturn(List.of());
        given(vehicleConfigRepository.findBySetVersionId(2L)).willReturn(List.of());

        assertDoesNotThrow(() -> setVersionManager.deleteSetVersion(2L));
    }

    @Test
    public void deleteWithSoftDeletedVehicleConfig() {
        when(authentication.getName()).thenReturn("USER");

        user.setRoles(Set.of(role));

        VehicleProject vehicleProject = new VehicleProject();
        vehicleProject.setProjectName("VehicleProject");
        List<String> vehicleConfigNames = List.of("CONFIG1", "CONFIG2", "CONFIG3", "CONFIG4", "CONFIG5");
        List<VehicleConfig> vehicleConfigs = vehicleConfigNames.stream().map(id -> {
            var config = getVehicleConfig(id, vehicleProject);
            config.setDeletionDate(new Date());
            return config;
        }).toList();

        given(setVersionRepository.findById(2L)).willReturn(Optional.of(setVersion));
        given(vehicleConfigRepository.findBySetVersionId(2L)).willReturn(vehicleConfigs);
        given(setKeyRepository.findByIdVersion(2L)).willReturn(List.of());
        given(productRepository.findBySetVersionId(2L)).willReturn(List.of());

        assertDoesNotThrow(() -> setVersionManager.deleteSetVersion(2L));
    }
}
