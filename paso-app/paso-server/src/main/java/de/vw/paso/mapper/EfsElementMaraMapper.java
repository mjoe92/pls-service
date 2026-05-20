package de.vw.paso.mapper;

import de.vw.paso.logic.pls.PlsEfsElement;
import de.vw.paso.partlist.domain.EfsElementMara;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.partlist.domain.SpecialPartNumberType;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import org.apache.commons.lang3.StringUtils;

public final class EfsElementMaraMapper {

    public static EfsElementMara toEntity(EfsElementMaraDTO dto) {
        EfsElementMara entity = new EfsElementMara();
        entity.setId(dto.getId());
        entity.setVehiclePartListId(dto.getVehiclePartListId());
        entity.setRevision(dto.getRevision());
        entity.setPartNumber(dto.getPartNumber());
        entity.setPartNumberVornummer(dto.getPartNumberVornummer());
        entity.setPartNumberMittelgruppe(dto.getPartNumberMittelgruppe());
        entity.setPartNumberEndNumber(dto.getPartNumberEndNumber());
        entity.setPartNumberIndex(dto.getPartNumberIndex());
        entity.setDescription1De(dto.getDescription1De());
        entity.setDescription1En(dto.getDescription1En());
        entity.setDescription2De(dto.getDescription2De());
        entity.setDescription2En(dto.getDescription2En());
        entity.setWeightCalculatedTe(dto.getWeightCalculatedTe());
        entity.setWeightCalculatedTeDate(dto.getWeightCalculatedTeDate());
        entity.setWeightEstimatedTe(dto.getWeightEstimatedTe());
        entity.setWeightEstimatedTeDate(dto.getWeightEstimatedTeDate());
        entity.setWeightWeightedTe(dto.getWeightWeightedTe());
        entity.setWeightWeightedTeDate(dto.getWeightWeightedTeDate());
        entity.setWeightWeightedProd(dto.getWeightWeightedProd());
        entity.setWeightWeightedProdDate(dto.getWeightWeightedProdDate());
        entity.setAssemblyIndicator(dto.getAssemblyIndicator());
        entity.setDrawingStatus(dto.getDrawingStatus());
        entity.setDrawingDate(dto.getDrawingDate());
        entity.setConstructionsState(dto.getConstructionsState());
        entity.setQuality(dto.getQuality());
        entity.setMaterialThickness(dto.getMaterialThickness());
        entity.setSeeDrawing(dto.getSeeDrawing());
        entity.setResponsibleConstr1(dto.getResponsibleConstr1());
        entity.setResponsibleConstr2(dto.getResponsibleConstr2());
        entity.setBuildSampleApproval(dto.getBuildSampleApproval());
        entity.setTechnicallyOkay(dto.getTechnicallyOkay());
        entity.setReleaseDateSoll(dto.getReleaseDateSoll());
        entity.setDesignerName(dto.getDesignerName());
        entity.setDesignerCostGroup(dto.getDesignerCostGroup());
        entity.setDesignerPhoneNumber(dto.getDesignerPhoneNumber());
        entity.setKStandReleaseDate(dto.getKStandReleaseDate());
        entity.setTioFreiReleaseDate(dto.getTioFreiReleaseDate());
        entity.setBuildSampleApprovalTargetDate(dto.getBuildSampleApprovalTargetDate());
        entity.setMfpStatus(dto.getMfpStatus());
        entity.setMfpThickness(dto.getMfpThickness());
        entity.setKseKz(dto.getKseKz());
        entity.setWeightAcceptedFromEPIS(dto.getWeightAcceptedFromEPIS());
        entity.setUserCreate(dto.getUserCreate());
        entity.setUserChange(dto.getUserChange());
        entity.setTimestampCreate(dto.getTimestampCreate());
        entity.setTimestampChange(dto.getTimestampChange());
        entity.setEntityChange(dto.isEntityChange());

        return entity;
    }

    public static EfsElementMaraDTO toDto(EfsElementMara entity) {
        EfsElementMaraDTO dto = new EfsElementMaraDTO();
        dto.setId(entity.getId());
        dto.setVehiclePartListId(entity.getVehiclePartListId());
        dto.setRevision(entity.getRevision());
        dto.setPartNumber(entity.getPartNumber());
        dto.setPartNumberVornummer(entity.getPartNumberVornummer());
        dto.setPartNumberMittelgruppe(entity.getPartNumberMittelgruppe());
        dto.setPartNumberEndNumber(entity.getPartNumberEndNumber());
        dto.setPartNumberIndex(entity.getPartNumberIndex());
        dto.setDescription1De(entity.getDescription1De());
        dto.setDescription1En(entity.getDescription1En());
        dto.setDescription2De(entity.getDescription2De());
        dto.setDescription2En(entity.getDescription2En());
        dto.setWeightCalculatedTe(entity.getWeightCalculatedTe());
        dto.setWeightCalculatedTeDate(entity.getWeightCalculatedTeDate());
        dto.setWeightEstimatedTe(entity.getWeightEstimatedTe());
        dto.setWeightEstimatedTeDate(entity.getWeightEstimatedTeDate());
        dto.setWeightWeightedTe(entity.getWeightWeightedTe());
        dto.setWeightWeightedTeDate(entity.getWeightWeightedTeDate());
        dto.setWeightWeightedProd(entity.getWeightWeightedProd());
        dto.setWeightWeightedProdDate(entity.getWeightWeightedProdDate());
        dto.setAssemblyIndicator(entity.getAssemblyIndicator());
        dto.setDrawingStatus(entity.getDrawingStatus());
        dto.setDrawingDate(entity.getDrawingDate());
        dto.setConstructionsState(entity.getConstructionsState());
        dto.setQuality(entity.getQuality());
        dto.setMaterialThickness(entity.getMaterialThickness());
        dto.setSeeDrawing(entity.getSeeDrawing());
        dto.setResponsibleConstr1(entity.getResponsibleConstr1());
        dto.setResponsibleConstr2(entity.getResponsibleConstr2());
        dto.setBuildSampleApproval(entity.getBuildSampleApproval());
        dto.setTechnicallyOkay(entity.getTechnicallyOkay());
        dto.setReleaseDateSoll(entity.getReleaseDateSoll());
        dto.setDesignerName(entity.getDesignerName());
        dto.setDesignerCostGroup(entity.getDesignerCostGroup());
        dto.setDesignerPhoneNumber(entity.getDesignerPhoneNumber());
        dto.setKStandReleaseDate(entity.getKStandReleaseDate());
        dto.setTioFreiReleaseDate(entity.getTioFreiReleaseDate());
        dto.setBuildSampleApprovalTargetDate(entity.getBuildSampleApprovalTargetDate());
        dto.setMfpStatus(entity.getMfpStatus());
        dto.setMfpThickness(entity.getMfpThickness());
        dto.setKseKz(entity.getKseKz());
        dto.setWeightAcceptedFromEPIS(entity.getWeightAcceptedFromEPIS());
        dto.setUserCreate(entity.getUserCreate());
        dto.setUserChange(entity.getUserChange());
        dto.setTimestampCreate(entity.getTimestampCreate());
        dto.setTimestampChange(entity.getTimestampChange());
        dto.setEntityChange(entity.isEntityChange());

        return dto;
    }

    public static EfsElementMara toEntity(PlsEfsElement plsElement, VehiclePartList vehiclePartList) {
        EfsElementMaraDTO mara = createNewMara(plsElement);

        mara.setPartNumberVornummer(plsElement.getPartNumberVornummer());
        mara.setPartNumberMittelgruppe(plsElement.getPartNumberMittelGruppe());
        mara.setPartNumberEndNumber(plsElement.getPartNumberEndNumber());
        mara.setPartNumberIndex(plsElement.getPartNumberIndex());
        mara.setWeightWeightedTe(plsElement.getWeightWeightedTe());
        mara.setWeightWeightedTeDate(plsElement.getWeightWeightedTeDate());
        mara.setWeightEstimatedTe(plsElement.getWeightEstimatedTe());
        mara.setWeightEstimatedTeDate(plsElement.getWeightEstimatedTeDate());
        mara.setWeightCalculatedTe(plsElement.getWeightCalculatedTe());
        mara.setWeightCalculatedTeDate(plsElement.getWeightCalculatedTeDate());
        mara.setWeightWeightedProd(plsElement.getWeightWeightedProd());
        mara.setWeightWeightedProdDate(plsElement.getWeightWeightedProdDate());
        mara.setAssemblyIndicator(plsElement.getAssemblyIndicator());
        mara.setDrawingDate(plsElement.getDrawingDate());
        mara.setDrawingStatus(plsElement.getDrawingStatus());
        mara.setConstructionsState(plsElement.getConstructionsState());
        mara.setQuality(plsElement.getQuality());
        mara.setMaterialThickness(plsElement.getMaterialThickness());
        mara.setSeeDrawing(plsElement.getSeeDrawing());
        mara.setResponsibleConstr1(plsElement.getResponsibleConstr1());
        mara.setResponsibleConstr2(plsElement.getResponsibleConstr2());
        mara.setBuildSampleApproval(plsElement.getBuildSampleApproval());
        mara.setBuildSampleApprovalTargetDate(plsElement.getBuildSampleApprovalTargetDate());
        mara.setTechnicallyOkay(plsElement.getTechnicallyOkay());
        mara.setReleaseDateSoll(plsElement.getReleaseDateSoll());
        mara.setDesignerName(plsElement.getDesignerName());
        mara.setDesignerCostGroup(plsElement.getDesignerCostGroup());
        mara.setDesignerPhoneNumber(plsElement.getDesignerPhoneNumber());
        mara.setKStandReleaseDate(plsElement.getkStandReleaseDate());
        mara.setTioFreiReleaseDate(plsElement.getTioFreiReleaseDate());
        mara.setMfpStatus(plsElement.getMFPStatus());
        mara.setMfpThickness(plsElement.getMFPThickness());
        mara.setKseKz(plsElement.getKseKz());
        mara.setWeightAcceptedFromEPIS(plsElement.getWeightAcceptedFromEPIS());
        mara.setDescription2De(plsElement.getDescription2De());
        mara.setDescription1En(plsElement.getDescription1En());
        mara.setDescription2En(plsElement.getDescription2En());

        EfsElementMara efsElementMara = EfsElementMaraMapper.toEntity(mara);
        efsElementMara.setVehiclePartListId(vehiclePartList.getId());
        return efsElementMara;
    }

    private static EfsElementMaraDTO createNewMara(PlsEfsElement plsElement) {
        if (plsElement.isGap()) {
            return PartListFactory.createEfsElementMara(plsElement.getNodeLabel(),
                    SpecialPartNumberType.GAP.getLabel());
        } else if (StringUtils.isEmpty(plsElement.getPartNumber())) {
            return PartListFactory.createEfsElementMara(plsElement.getNodeLabel(),
                    SpecialPartNumberType.NO_MARA.getLabel());
        }

        return PartListFactory.createEfsElementMara(plsElement.getDescription1De(), plsElement.getPartNumber());
    }
}
