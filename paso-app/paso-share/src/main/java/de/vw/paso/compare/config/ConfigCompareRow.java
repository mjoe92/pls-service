package de.vw.paso.compare.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.vw.paso.compare.ComparableRow;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberFamilyDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class ConfigCompareRow implements ComparableRow<ConfigCompareRow> {

    private PrNumberFamilyDTO prNumberFamily;
    private PrNumberDTO prNumber;
    private boolean isGroupSubItem;

    public ConfigCompareRow() {
        this(new PrNumberFamilyDTO());
    }

    public ConfigCompareRow(PrNumberFamilyDTO family) {
        prNumberFamily = family;
    }

    public ConfigCompareRow(PrNumberDTO pn, boolean groupSubItem) {
        prNumber = pn;
        isGroupSubItem = groupSubItem;
    }

    @Override
    public void addChildRow(ConfigCompareRow row) {
        //void
    }

    @Override
    public Map<ApCompareGroup, Double> getWeights(Long vehicleConfigId) {
        return new HashMap<>();
    }

    @Override
    public boolean isSum() {
        return false;
    }

    @Override
    public String getSet() {
        return null;
    }

    @Override
    public List<EfsElementDTO> getElements(Long vehicleConfigId, ApCompareGroup ap) {
        return List.of();
    }

    public PrNumberFamilyDTO getPrNumberFamily() {
        return prNumberFamily;
    }

    public PrNumberDTO getPrNumber() {
        return prNumber;
    }

    public boolean isGroupSubItem() {
        return isGroupSubItem;
    }
}
