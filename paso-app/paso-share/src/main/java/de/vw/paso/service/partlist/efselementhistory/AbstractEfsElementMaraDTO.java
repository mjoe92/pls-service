package de.vw.paso.service.partlist.efselementhistory;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.vw.paso.core.domain.AbstractModifiableDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class AbstractEfsElementMaraDTO extends AbstractModifiableDTO<Long> {

    private Long revision = 0L;
    private String partNumber;
    @JsonIgnore
    private String formattedPartNumber;
    private String partNumberVornummer;
    private String partNumberMittelgruppe;
    private String partNumberEndNumber;
    private String partNumberIndex;
    private String description1De;
    private String description1En;
    private String description2De;
    private String description2En;
    private Double weightCalculatedTe = 0.0D;
    private Date weightCalculatedTeDate;
    private Double weightEstimatedTe = 0.0D;
    private Date weightEstimatedTeDate;
    private Double weightWeightedTe = 0.0D;
    private Date weightWeightedTeDate;
    private Double weightWeightedProd = 0.0D;
    private Date weightWeightedProdDate;
    private String assemblyIndicator;
    private String drawingStatus;
    private Date drawingDate;
    private String constructionsState;
    private String quality;
    private Double materialThickness;
    private String seeDrawing;
    private String responsibleConstr1;
    private String responsibleConstr2;
    private String buildSampleApproval;
    private String technicallyOkay;
    private Date releaseDateSoll;
    private String designerName;
    private String designerCostGroup;
    private String designerPhoneNumber;
    private Date kStandReleaseDate;
    private Date tioFreiReleaseDate;
    private Date buildSampleApprovalTargetDate;
    private String mfpStatus;
    private Double mfpThickness;
    private String kseKz;
    private String weightAcceptedFromEPIS;
    @JsonIgnore
    private Double prioritizedWeight = 0d;

    public abstract Long getVehiclePartListId();

    public abstract void setVehiclePartListId(Long vehiclePartList);

    public String getFormattedPartNumber() {
        return formattedPartNumber;
    }
}
