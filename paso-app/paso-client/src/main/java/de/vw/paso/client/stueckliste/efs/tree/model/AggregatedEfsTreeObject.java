package de.vw.paso.client.stueckliste.efs.tree.model;

import java.util.List;
import java.util.Map;

import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.EfsWeightUtil;

public abstract class AggregatedEfsTreeObject<T> {

    private final T aggregationObject;
    private final List<EfsElementDTO> efsElements;

    private Double platform;
    private Double system;
    private Double hut;
    private Double weightAll;

    protected AggregatedEfsTreeObject(T aggregationObject, List<EfsElementDTO> efsElements) {
        this.aggregationObject = aggregationObject;
        this.efsElements = efsElements;
    }

    public abstract <T_ID> T_ID getId();

    public abstract <T_ID> T_ID getParentId();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AggregatedEfsTreeObject<?> aggregatedEfsTreeObject && getId() != null ? getId().equals(
            aggregatedEfsTreeObject.getId()) : super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getId() == null ? super.hashCode() : getId().hashCode();
    }

    public void calculateWeights() {
        Map<ApCompareGroup, Double> calculations = EfsWeightUtil.calculate(getEfsElements());

        this.platform = calculations.get(ApCompareGroup.PLATFORM);
        this.system = calculations.get(ApCompareGroup.SYSTEM);
        this.hut = calculations.get(ApCompareGroup.HUT);
        this.weightAll = calculations.get(ApCompareGroup.SUM);
    }

    public T getAggregationObject() {
        return aggregationObject;
    }

    public List<EfsElementDTO> getEfsElements() {
        return efsElements;
    }

    public Double getPlatform() {
        return platform;
    }

    public Double getSystem() {
        return system;
    }

    public Double getHut() {
        return hut;
    }

    public Double getWeightAll() {
        return weightAll;
    }
}
