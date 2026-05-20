package de.vw.paso.client.control.cell;

import java.util.Collection;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.PrNumberUtil;

public class PrNumberRuleTreeTableCell extends AbstractTreeTableCell<EfsElementDTO, String> {

    public PrNumberRuleTreeTableCell() {
        super();
    }

    @Override
    public void updateItem(String prRule, boolean empty) {
        super.updateItem(prRule, empty);

        if (empty || prRule == null) {
            setText(null);
            setTooltip(null);
        } else {
            Collection<String> prNumbersOfElement = CellUtils.sortPrNumbersByFamily(PrNumberUtil.split(prRule));

            setText(PrNumberUtil.joinNames(prNumbersOfElement));
            setTooltip(CellUtils.setTooltipContent(prNumbersOfElement));
        }
    }
}
