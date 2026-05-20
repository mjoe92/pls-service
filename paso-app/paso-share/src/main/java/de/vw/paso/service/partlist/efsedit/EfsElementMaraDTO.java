package de.vw.paso.service.partlist.efsedit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementMaraDTO;
import de.vw.paso.utility.EfsElementUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EfsElementMaraDTO extends AbstractEfsElementMaraDTO {

    private Long id;
    private Long vehiclePartListId;

    @JsonIgnore
    @Override
    public String getFormattedPartNumber() {
        if (super.getFormattedPartNumber() == null) {
            setFormattedPartNumber(EfsElementUtil.convertPartNumberString(getPartNumber()));
        }
        return super.getFormattedPartNumber();
    }

    @Override
    public Long getVehiclePartListId() {
        return vehiclePartListId;
    }

    @Override
    public void setVehiclePartListId(Long vehiclePartList) {
        this.vehiclePartListId = vehiclePartList;
    }
}
