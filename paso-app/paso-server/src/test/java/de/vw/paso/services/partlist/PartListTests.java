package de.vw.paso.services.partlist;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static testutil.TestUtils.saveUserGroup;

import de.vw.paso.consumer.partlist.CreateVehiclePartListConsumer;
import de.vw.paso.consumer.vehicle.SaveVehicleConfigConsumer;
import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.repository.partlist.VehiclePartListRepository;
import de.vw.paso.repository.user.UserGroupRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PartListTests extends AbstractServiceTests {

    @Autowired
    private SaveVehicleConfigConsumer saveVehicleConfigConsumer;
    @Autowired
    private CreateVehiclePartListConsumer createVehiclePartListConsumer;
    @Autowired
    private VehiclePartListRepository vehiclePartListRepository;
    @Autowired
    private UserManager userManager;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private VehicleConfigRepository vehicleConfigRepository;

    @Test
    public void creatingVehiclePartlistOnlyWithTiWhImportsFzg() {
        VehicleConfigDTO vehicleConfig = createFzgConfigWithNameAndVehicleProject();
        vehicleConfig.setTiWhImportVehicle(getTiWhImport("YYY"));

        saveUserGroup(userManager, userGroupRepository, "Test-User-ID", vehicleConfigRepository, 1L);

        saveVehicleConfigConsumer.saveVehicleConfig(vehicleConfig, null);
        vehicleConfig = saveVehicleConfigConsumer.getResult();

        createVehiclePartListConsumer.createVehiclePartList(vehicleConfig.getId());

        VehiclePartListDTO persistedVehiclePartList = createVehiclePartListConsumer.getResult().getVehiclePartList();
        assertNotNull(vehiclePartListRepository.getReferenceById(persistedVehiclePartList.getId()));
        assertNotNull(persistedVehiclePartList.getProductKeyVehicle());
        assertNull(persistedVehiclePartList.getProductKeyMotor());
        assertNull(persistedVehiclePartList.getProductKeyGearbox());
    }

}
