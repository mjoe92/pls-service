package de.vw.paso.client.stueckliste.efs.tree.model;

import java.util.ArrayList;
import java.util.List;

import de.vw.paso.client.valueobject.PartGroupVMO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class PartGroupTreeObject extends AggregatedEfsTreeObject<PartGroupVMO> {

    public PartGroupTreeObject() {
        this(new PartGroupVMO());
    }

    public PartGroupTreeObject(PartGroupVMO partGroup) {
        this(partGroup, new ArrayList<>());
    }

    public PartGroupTreeObject(PartGroupVMO aggregationObject, List<EfsElementDTO> efsElements) {
        super(aggregationObject, efsElements);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer getId() {
        if (getAggregationObject().isCategory()) {
            return getAggregationObject().getCategory();
        } else if (getAggregationObject().isMgr()) {
            return getAggregationObject().getMgr();
        }

        return getAggregationObject().getUgr();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer getParentId() {
        if (getAggregationObject().isCategory()) {
            return null;
        } else if (getAggregationObject().isMgr()) {
            return getAggregationObject().getCategory();
        }

        return getAggregationObject().getMgr();
    }

}
