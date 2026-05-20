package de.vw.paso.service.partlist.efselementhistory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.utility.EfsElementUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EfsElementMaraHistoryDTO extends AbstractEfsElementMaraDTO {

    private Long id;
    private Long vehiclePartListId;
    private EfsElementMaraDTO efsElementMaraDTO;

    @JsonIgnore
    @Override
    public String getFormattedPartNumber() {
        if (super.getFormattedPartNumber() == null) {
            setFormattedPartNumber(EfsElementUtil.convertPartNumberString(getPartNumber()));
        }
        return super.getFormattedPartNumber();
    }

    @Override
    public void setVehiclePartListId(Long vehiclePartListId) {
        this.vehiclePartListId = vehiclePartListId;
    }

    @Override
    public Long getVehiclePartListId() {
        return vehiclePartListId;
    }
}
