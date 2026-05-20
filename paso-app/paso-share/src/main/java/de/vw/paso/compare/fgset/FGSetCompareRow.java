package de.vw.paso.compare.fgset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.vw.paso.compare.ComparableRow;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.EfsWeightUtil;
import de.vw.paso.utility.MathUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FGSetCompareRow implements ComparableRow<FGSetCompareRow> {

    private String setKeyStr;
    private Map<Long, DataSet> dataSets = new HashMap<>();
    private List<FGSetCompareRow> children = new ArrayList<>();
    private Map<Long, Map<ApCompareGroup, Double>> table = new HashMap<>();
    private boolean isSum = false;

    public FGSetCompareRow(String set) {
        this.setKeyStr = set;
    }

    void addDataSet(EfsElementDTO element, Long vehicleConfigId) {
        DataSet dataSet = dataSets.get(vehicleConfigId);
        if (dataSet == null) {
            dataSet = new DataSet();
            dataSets.put(vehicleConfigId, dataSet);
        }
        dataSet.addElement(element);
    }

    @Override
    public void addChildRow(FGSetCompareRow row) {
        children.add(row);
    }

    @Override
    public Map<ApCompareGroup, Double> getWeights(Long vehicleConfigId) {
        if (table.containsKey(vehicleConfigId)) {
            return table.get(vehicleConfigId);
        } else {
            Map<ApCompareGroup, Double> result = new HashMap<>();
            DataSet dataSet = dataSets.get(vehicleConfigId);
            if (dataSet != null) {
                result = dataSet.getWeights();
                if (result == null) {
                    result = new HashMap<>();
                }
            }
            for (FGSetCompareRow fgSetCompareRow : getChildren()) {
                Map<ApCompareGroup, Double> childWeights = fgSetCompareRow.getWeights(vehicleConfigId);
                Double platformOfElement = childWeights.get(ApCompareGroup.PLATFORM);
                Double systemOfElement = childWeights.get(ApCompareGroup.SYSTEM);
                Double hutOfElement = childWeights.get(ApCompareGroup.HUT);
                Double sum = MathUtil.nullSafeAddition(platformOfElement, systemOfElement, hutOfElement);

                result.put(ApCompareGroup.PLATFORM,
                        MathUtil.nullSafeAddition(result.get(ApCompareGroup.PLATFORM), platformOfElement));
                result.put(ApCompareGroup.SYSTEM,
                        MathUtil.nullSafeAddition(result.get(ApCompareGroup.SYSTEM), systemOfElement));
                result.put(ApCompareGroup.HUT, MathUtil.nullSafeAddition(result.get(ApCompareGroup.HUT), hutOfElement));
                result.put(ApCompareGroup.SUM, MathUtil.nullSafeAddition(result.get(ApCompareGroup.SUM), sum));
            }
            table.put(vehicleConfigId, result);
            return result;
        }
    }

    @Override
    public List<EfsElementDTO> getElements(Long vehicleConfigId, ApCompareGroup ap) {
        List<EfsElementDTO> result = new ArrayList<>();
        DataSet dataSet = dataSets.get(vehicleConfigId);
        if (dataSet != null) {
            if (ApCompareGroup.SUM.equals(ap)) {
                result.addAll(dataSet.getElements());
            } else {
                result.addAll(dataSet.getElements().stream().filter(e -> ap.containsAp(e.getAp()))
                        .collect(Collectors.toList()));
            }
        }
        getChildren().forEach(e -> result.addAll(e.getElements(vehicleConfigId, ap)));
        return result;
    }

    @Override
    public String getSet() {
        return setKeyStr;
    }

    public static class DataSet {

        private List<EfsElementDTO> elements = new ArrayList<>();

        public void addElement(EfsElementDTO element) {
            elements.add(element);
        }

        public Map<ApCompareGroup, Double> getWeights() {
            return EfsWeightUtil.calculate(elements);
        }

        public List<EfsElementDTO> getElements() {
            return elements;
        }
    }
}
