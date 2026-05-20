package de.vw.paso.client.stueckliste.efs.tree;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.TableColumnBase;

import de.vw.paso.service.vehicle.VehicleConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VisibleColumnsChangedEvent {

    private VehicleConfigDTO vehicleConfig;
    private List<String> columns;

    public static List<String> getColumnNames(List<? extends TableColumnBase<?, ?>> columns) {
        return columns.stream().map(TableColumnBase::getText).collect(Collectors.toList());
    }
}
