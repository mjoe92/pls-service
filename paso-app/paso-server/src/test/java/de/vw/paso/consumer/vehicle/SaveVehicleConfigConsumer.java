package de.vw.paso.consumer.vehicle;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.mapper.UserGroupMapper;
import de.vw.paso.repository.user.UserGroupRepository;
import de.vw.paso.service.vehicle.ISaveVehicleConfigConsumer;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.service.vehicle.VehicleConfigRestService;
import de.vw.paso.user.domain.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SaveVehicleConfigConsumer extends AbstractTestConsumer<VehicleConfigDTO>
        implements ISaveVehicleConfigConsumer {

    @Autowired
    private VehicleConfigRestService vehicleConfigService;
    @Autowired
    private UserGroupRepository userGroupRepository;

    @Override
    public void saveVehicleConfig(VehicleConfigDTO vehicleConfig, Runnable onSaveSuccess) {
        if (vehicleConfig.getOwnerGroup() == null) {
            UserGroup userGroup = userGroupRepository.findAll().getFirst();
            vehicleConfig.setOwnerGroup(UserGroupMapper.toDto(userGroup, null, null, null));
        }

        run(() -> vehicleConfigService.saveFzgKonfig(vehicleConfig));
    }
}
