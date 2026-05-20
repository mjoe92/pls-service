package de.vw.paso.client.stueckliste.compare.partlist;

import java.util.HashMap;
import java.util.Map;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PartListCompareRow {

    private EfsElementDTO baseElement;
    private Map<Long, EfsElementDTO> elementMap = new HashMap<>();
    private Map<EfsProperty<?>, PartListCompareStatus> propertyStatusMap = new HashMap<>();

    @Getter
    @Setter
    private PartListCompareStatus rowStatus;

    public PartListCompareRow(EfsElementDTO element, PartListCompareStatus status) {
        addElement(element);
        this.rowStatus = status;
    }

    public void addElement(EfsElementDTO element) {
        this.elementMap.put(element.getVehiclePartListId(), element);
        if (baseElement == null) {
            baseElement = element;
        }
    }

    public EfsElementDTO getBaseElement() {
        return baseElement;
    }

    public EfsElementDTO getElement(Long partListId) {
        return elementMap.get(partListId);
    }

    public int getSize() {
        return elementMap.size();
    }

    public void setStatusForProperty(EfsProperty<?> prop, PartListCompareStatus status) {
        propertyStatusMap.put(prop, status);
    }

    public PartListCompareStatus getPropertyStatus(EfsProperty<?> property) {
        return propertyStatusMap.getOrDefault(property, PartListCompareStatus.UNCHANGED);
    }
}
