package de.vw.paso.partlist.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.utility.EfsElementUtil;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEfsElementMara extends AbstractModifiableEntity<Long> {

    private static final long serialVersionUID = 1L;

    private static final String COLUMN_REVISION = "REVISION";
    private static final String COLUMN_PART_NUMBER = "PART_NUMBER";
    private static final String COLUMN_PART_NUMBER_VORNUMMER = "PART_NUMBER_VORNUMMER";
    private static final String COLUMN_PART_NUMBER_MITTELGRUPPE = "PART_NUMBER_MITTELGRUPPE";
    private static final String COLUMN_PART_NUMBER_END_NUMBER = "PART_NUMBER_END_NUMBER";
    private static final String COLUMN_PART_NUMBER_INDEX = "PART_NUMBER_INDEX";
    private static final String COLUMN_DESCRIPTION1_DE = "DESCRIPTION1_DE";
    private static final String COLUMN_DESCRIPTION1_EN = "DESCRIPTION1_EN";
    private static final String COLUMN_DESCRIPTION2_DE = "DESCRIPTION2_DE";
    private static final String COLUMN_DESCRIPTION2_EN = "DESCRIPTION2_EN";
    private static final String COLUMN_WEIGHT_CALCULATED_TE = "WEIGHT_CALCULATED_TE";
    private static final String COLUMN_WEIGHT_CALCULATED_TE_DATE = "WEIGHT_CALCULATED_TE_DATE";
    private static final String COLUMN_WEIGHT_ESTIMATED_TE = "WEIGHT_ESTIMATED_TE";
    private static final String COLUMN_WEIGHT_ESTIMATED_TE_DATE = "WEIGHT_ESTIMATED_TE_DATE";
    private static final String COLUMN_WEIGHT_WEIGHTED_TE = "WEIGHT_WEIGHTED_TE";
    private static final String COLUMN_WEIGHT_WEIGHTED_TE_DATE = "WEIGHT_WEIGHTED_TE_DATE";
    private static final String COLUMN_WEIGHT_WEIGHTED_PROD = "WEIGHT_WEIGHTED_PROD";
    private static final String COLUMN_WEIGHT_WEIGHTED_PROD_DATE = "WEIGHT_WEIGHTED_PROD_DATE";
    private static final String COLUMN_ASSEMBLY_INDICATOR = "ASSEMBLY_INDICATOR";
    private static final String COLUMN_DRAWING_STATUS = "DRAWING_STATUS";
    private static final String COLUMN_DRAWING_DATE = "DRAWING_DATE";
    private static final String COLUMN_CONSTRUCTIONS_STATE = "CONSTRUCTIONS_STATE";
    private static final String COLUMN_QUALITY = "QUALITY";
    private static final String COLUMN_MATERIAL_THICKNESS = "MATERIAL_THICKNESS";
    private static final String COLUMN_SEE_DRAWING = "SEE_DRAWING";
    private static final String COLUMN_RESPONSIBLE_CONSTR_1 = "RESPONSIBLE_CONSTR_1";
    private static final String COLUMN_RESPONSIBLE_CONSTR_2 = "RESPONSIBLE_CONSTR_2";
    private static final String COLUMN_BUILD_SAMPLE_APPROVAL = "BUILD_SAMPLE_APPROVAL";
    private static final String COLUMN_TECHNICALLY_OKAY = "TECHNICALLY_OKAY";
    private static final String COLUMN_RELEASE_DATE_SOLL = "RELEASE_DATE_SOLL";
    private static final String COLUMN_DESIGNER_NAME = "DESIGNER_NAME";
    private static final String COLUMN_DESIGNER_COST_GROUP = "DESIGNER_COST_GROUP";
    private static final String COLUMN_DESIGNER_PHONE_NUMBER = "DESIGNER_PHONE_NUMBER";
    private static final String COLUMN_K_STAND_RELEASE_DATE = "K_STAND_RELEASE_DATE";
    private static final String COLUMN_TIO_FREI_RELEASE_DATE = "TIO_FREI_RELEASE_DATE";
    private static final String COLUMN_BUILD_SAMPLE_APPROVAL_TARGET_DATE = "BUILD_SAMPLE_APPROVAL_TARGET_DATE";
    private static final String COLUMN_MFP_STATUS = "MFP_STATUS";
    private static final String COLUMN_MFP_THICKNESS = "MFP_THICKNESS";
    private static final String COLUMN_KSE_KZ = "KSE_KZ";
    private static final String COLUMN_WEIGHT_ACCEPTED_FROM_EPIS = "WEIGHT_ACCEPTED_FROM_EPIS";

    public abstract Long getVehiclePartListId();

    public abstract void setVehiclePartListId(Long vehiclePartList);

    @Column(name = COLUMN_REVISION, nullable = false)
    private Long revision = 0L;

    @Column(name = COLUMN_PART_NUMBER, length = 12, nullable = false)
    private String partNumber;

    @JsonIgnore
    @Transient
    private String formattedPartNumber;

    @Column(name = COLUMN_PART_NUMBER_VORNUMMER, length = 3)
    private String partNumberVornummer;

    @Column(name = COLUMN_PART_NUMBER_MITTELGRUPPE, length = 3)
    private String partNumberMittelgruppe;

    @Column(name = COLUMN_PART_NUMBER_END_NUMBER, length = 3)
    private String partNumberEndNumber;

    @Column(name = COLUMN_PART_NUMBER_INDEX, length = 2)
    private String partNumberIndex;

    @Column(name = COLUMN_DESCRIPTION1_DE, length = 60, nullable = false)
    private String description1De;

    @Column(name = COLUMN_DESCRIPTION1_EN, length = 60)
    private String description1En;

    @Column(name = COLUMN_DESCRIPTION2_DE, length = 60)
    private String description2De;

    @Column(name = COLUMN_DESCRIPTION2_EN, length = 60)
    private String description2En;

    @Column(name = COLUMN_WEIGHT_CALCULATED_TE, columnDefinition = "decimal(10, 3)", nullable = false)
    private Double weightCalculatedTe = 0.0D;

    @Column(name = COLUMN_WEIGHT_CALCULATED_TE_DATE)
    @Temporal(TemporalType.DATE)
    private Date weightCalculatedTeDate;

    @Column(name = COLUMN_WEIGHT_ESTIMATED_TE, columnDefinition = "decimal(10, 3)", nullable = false)
    private Double weightEstimatedTe = 0.0D;

    @Column(name = COLUMN_WEIGHT_ESTIMATED_TE_DATE)
    @Temporal(TemporalType.DATE)
    private Date weightEstimatedTeDate;

    @Column(name = COLUMN_WEIGHT_WEIGHTED_TE, columnDefinition = "decimal(10, 3)", nullable = false)
    private Double weightWeightedTe = 0.0D;

    @Column(name = COLUMN_WEIGHT_WEIGHTED_TE_DATE)
    @Temporal(TemporalType.DATE)
    private Date weightWeightedTeDate;

    @Column(name = COLUMN_WEIGHT_WEIGHTED_PROD, columnDefinition = "decimal(10, 3)", nullable = false)
    private Double weightWeightedProd = 0.0D;

    @Column(name = COLUMN_WEIGHT_WEIGHTED_PROD_DATE)
    @Temporal(TemporalType.DATE)
    private Date weightWeightedProdDate;

    @Column(name = COLUMN_ASSEMBLY_INDICATOR, columnDefinition = "char(1)")
    private String assemblyIndicator;

    @Column(name = COLUMN_DRAWING_STATUS, columnDefinition = "char(2)")
    private String drawingStatus;

    @Column(name = COLUMN_DRAWING_DATE)
    @Temporal(TemporalType.DATE)
    private Date drawingDate;

    @Column(name = COLUMN_CONSTRUCTIONS_STATE, length = 6)
    private String constructionsState;

    @Column(name = COLUMN_QUALITY, length = 40)
    private String quality;

    @Column(name = COLUMN_MATERIAL_THICKNESS, columnDefinition = "decimal(7, 3)")
    private Double materialThickness;

    @Column(name = COLUMN_SEE_DRAWING, length = 12)
    private String seeDrawing;

    @Column(name = COLUMN_RESPONSIBLE_CONSTR_1, length = 1)
    private String responsibleConstr1;

    @Column(name = COLUMN_RESPONSIBLE_CONSTR_2, length = 1)
    private String responsibleConstr2;

    @Column(name = COLUMN_BUILD_SAMPLE_APPROVAL, length = 1)
    private String buildSampleApproval;

    @Column(name = COLUMN_TECHNICALLY_OKAY, length = 12)
    private String technicallyOkay;

    @Column(name = COLUMN_RELEASE_DATE_SOLL)
    @Temporal(TemporalType.DATE)
    private Date releaseDateSoll;

    @Column(name = COLUMN_DESIGNER_NAME, length = 20)
    private String designerName;

    @Column(name = COLUMN_DESIGNER_COST_GROUP, length = 5)
    private String designerCostGroup;

    @Column(name = COLUMN_DESIGNER_PHONE_NUMBER, length = 15)
    private String designerPhoneNumber;

    @Column(name = COLUMN_K_STAND_RELEASE_DATE)
    @Temporal(TemporalType.DATE)
    private Date kStandReleaseDate;

    @Column(name = COLUMN_TIO_FREI_RELEASE_DATE)
    @Temporal(TemporalType.DATE)
    private Date tioFreiReleaseDate;

    @Column(name = COLUMN_BUILD_SAMPLE_APPROVAL_TARGET_DATE)
    @Temporal(TemporalType.DATE)
    private Date buildSampleApprovalTargetDate;

    @Column(name = COLUMN_MFP_STATUS, length = 4)
    private String mfpStatus;

    @Column(name = COLUMN_MFP_THICKNESS, columnDefinition = "decimal(7, 3)")
    private Double mfpThickness;

    @Column(name = COLUMN_KSE_KZ, length = 1)
    private String kseKz;

    @Column(name = COLUMN_WEIGHT_ACCEPTED_FROM_EPIS, length = 1)
    private String weightAcceptedFromEPIS;

    @JsonIgnore
    @Transient
    private Double prioritizedWeight = 0d;

    @JsonIgnore
    public String getFormatedPartNumber() {
        if (formattedPartNumber == null) {
            formattedPartNumber = EfsElementUtil.convertPartNumberString(getPartNumber());
        }
        return formattedPartNumber;
    }
}
