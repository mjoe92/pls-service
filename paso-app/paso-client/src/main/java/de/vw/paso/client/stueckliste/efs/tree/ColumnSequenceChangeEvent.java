package de.vw.paso.client.stueckliste.efs.tree;

import java.util.List;

import de.vw.paso.service.vehicle.VehicleConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ColumnSequenceChangeEvent {

    private VehicleConfigDTO vehicleConfig;
    private List<String> columns;
    private Class<?> senderClass;

}
