package de.vw.paso.client.control.cell;

import java.util.Collection;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.PrNumberUtil;

public class PrNumberRuleTableCell extends AbstractTableCell<EfsElementDTO, String> {

    private final VehicleConfigDTO vehicleConfig;

    public PrNumberRuleTableCell(VehicleConfigDTO vehicleConfig) {
        this.vehicleConfig = vehicleConfig;
    }

    @Override
    protected void updateItem(String prRule, boolean empty) {
        super.updateItem(prRule, empty);

        if (empty || prRule == null) {
            setGraphic(null);
            setTooltip(null);
        } else {
            Collection<String> prNumbersOfElement = CellUtils.sortPrNumbersByFamily(PrNumberUtil.split(prRule));

            setGraphic(CellUtils.getPrNumberRule(prNumbersOfElement, vehicleConfig));
            setTooltip(CellUtils.setTooltipContent(prNumbersOfElement));
        }
    }
}
