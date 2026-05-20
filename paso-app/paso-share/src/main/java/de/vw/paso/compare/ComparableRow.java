package de.vw.paso.compare;

import java.util.List;
import java.util.Map;

import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public interface ComparableRow<T> {

    void addChildRow(T row);

    Map<ApCompareGroup, Double> getWeights(Long vehicleConfigId);

    boolean isSum();

    String getSet();

    List<EfsElementDTO> getElements(Long vehicleConfigId, ApCompareGroup ap);

}
