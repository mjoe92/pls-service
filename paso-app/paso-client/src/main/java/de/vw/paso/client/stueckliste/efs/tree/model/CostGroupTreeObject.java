package de.vw.paso.client.stueckliste.efs.tree.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class CostGroupTreeObject extends AggregatedEfsTreeObject<CostGroupDTO> {

    public CostGroupTreeObject() {
        this(new CostGroupDTO());
    }

    public CostGroupTreeObject(final CostGroupDTO costGroup) {
        this(costGroup, new ArrayList<>());
    }

    public CostGroupTreeObject(final CostGroupDTO costGroup, final List<EfsElementDTO> efsElements) {
        super(costGroup, efsElements);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getId() {
        return getAggregationObject().getCostGroupName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getParentId() {
        return (getAggregationObject().getParent() != null) ? getAggregationObject().getParent().getCostGroupName() :
                null;
    }

    public boolean isKnown() {
        return Objects.nonNull(getAggregationObject()) && Objects.nonNull(getAggregationObject().getDescription());
    }

}
