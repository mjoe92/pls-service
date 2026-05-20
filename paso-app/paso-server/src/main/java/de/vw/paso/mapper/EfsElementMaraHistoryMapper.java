package de.vw.paso.mapper;

import de.vw.paso.partlist.domain.EfsElementMara;
import de.vw.paso.partlist.domain.EfsElementMaraHistory;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.partlist.efselementhistory.EfsElementMaraHistoryDTO;

public final class EfsElementMaraHistoryMapper {

    public static EfsElementMaraHistoryDTO toDto(EfsElementMaraHistory entity) {
        EfsElementMaraHistoryDTO dto = new EfsElementMaraHistoryDTO();
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

        EfsElementMaraDTO mara = EfsElementMaraMapper.toDto(entity.getEfsElementMara());
        dto.setEfsElementMaraDTO(mara);

        return dto;
    }

    public static EfsElementMaraHistory toEntity(EfsElementMara mara, String userId) {
        EfsElementMaraHistory entity = new EfsElementMaraHistory();
        entity.setEfsElementMara(mara);
        entity.setVehiclePartListId(mara.getVehiclePartListId());
        entity.setDescription1De(mara.getDescription1De());
        entity.setDescription1En(mara.getDescription1En());
        entity.setDescription2De(mara.getDescription2De());
        entity.setWeightCalculatedTe(mara.getWeightCalculatedTe());
        entity.setWeightCalculatedTeDate(mara.getWeightCalculatedTeDate());
        entity.setWeightEstimatedTe(mara.getWeightEstimatedTe());
        entity.setWeightEstimatedTeDate(mara.getWeightEstimatedTeDate());
        entity.setWeightWeightedTe(mara.getWeightWeightedTe());
        entity.setWeightWeightedTeDate(mara.getWeightWeightedTeDate());
        entity.setWeightWeightedProd(mara.getWeightWeightedProd());
        entity.setWeightWeightedProdDate(mara.getWeightWeightedProdDate());
        entity.setAssemblyIndicator(mara.getAssemblyIndicator());
        entity.setRevision(mara.getRevision());
        entity.setPartNumber(mara.getPartNumber());
        entity.setPartNumberVornummer(mara.getPartNumberVornummer());
        entity.setPartNumberMittelgruppe(mara.getPartNumberMittelgruppe());
        entity.setPartNumberEndNumber(mara.getPartNumberEndNumber());
        entity.setPartNumberIndex(mara.getPartNumberIndex());
        entity.setDrawingDate(mara.getDrawingDate());
        entity.setDrawingStatus(mara.getDrawingStatus());
        entity.setConstructionsState(mara.getConstructionsState());
        entity.setQuality(mara.getQuality());
        entity.setMaterialThickness(mara.getMaterialThickness());
        entity.setSeeDrawing(mara.getSeeDrawing());
        entity.setResponsibleConstr1(mara.getResponsibleConstr1());
        entity.setResponsibleConstr2(mara.getResponsibleConstr2());
        entity.setBuildSampleApproval(mara.getBuildSampleApproval());
        entity.setBuildSampleApprovalTargetDate(mara.getBuildSampleApprovalTargetDate());
        entity.setTechnicallyOkay(mara.getTechnicallyOkay());
        entity.setReleaseDateSoll(mara.getReleaseDateSoll());
        entity.setDesignerName(mara.getDesignerName());
        entity.setDesignerCostGroup(mara.getDesignerCostGroup());
        entity.setDesignerPhoneNumber(mara.getDesignerPhoneNumber());
        entity.setKStandReleaseDate(mara.getKStandReleaseDate());
        entity.setTioFreiReleaseDate(mara.getTioFreiReleaseDate());
        entity.setMfpStatus(mara.getMfpStatus());
        entity.setMfpThickness(mara.getMfpThickness());
        entity.setKseKz(mara.getKseKz());
        entity.setWeightAcceptedFromEPIS(mara.getWeightAcceptedFromEPIS());
        entity.setTimestampChange(mara.getTimestampChange());
        entity.setTimestampCreate(mara.getTimestampCreate());
        entity.setUserChange(mara.getUserChange());
        entity.setUserCreate(mara.getUserCreate());
        entity.setChange(userId);

        return entity;
    }

    public static void map(EfsElementMara mara, EfsElementMaraHistory history) {
        mara.setVehiclePartListId(history.getVehiclePartListId());
        mara.setDescription1De(history.getDescription1De());
        mara.setDescription1En(history.getDescription1En());
        mara.setDescription2De(history.getDescription2De());
        mara.setWeightCalculatedTe(history.getWeightCalculatedTe());
        mara.setWeightCalculatedTeDate(history.getWeightCalculatedTeDate());
        mara.setWeightEstimatedTe(history.getWeightEstimatedTe());
        mara.setWeightEstimatedTeDate(history.getWeightEstimatedTeDate());
        mara.setWeightWeightedTe(history.getWeightWeightedTe());
        mara.setWeightWeightedTeDate(history.getWeightWeightedTeDate());
        mara.setWeightWeightedProd(history.getWeightWeightedProd());
        mara.setWeightWeightedProdDate(history.getWeightWeightedProdDate());
        mara.setAssemblyIndicator(history.getAssemblyIndicator());
        mara.setRevision(history.getRevision());
        mara.setPartNumber(history.getPartNumber());
        mara.setPartNumberVornummer(history.getPartNumberVornummer());
        mara.setPartNumberMittelgruppe(history.getPartNumberMittelgruppe());
        mara.setPartNumberEndNumber(history.getPartNumberEndNumber());
        mara.setPartNumberIndex(history.getPartNumberIndex());
        mara.setDrawingDate(history.getDrawingDate());
        mara.setDrawingStatus(history.getDrawingStatus());
        mara.setConstructionsState(history.getConstructionsState());
        mara.setQuality(history.getQuality());
        mara.setMaterialThickness(history.getMaterialThickness());
        mara.setSeeDrawing(history.getSeeDrawing());
        mara.setResponsibleConstr1(history.getResponsibleConstr1());
        mara.setResponsibleConstr2(history.getResponsibleConstr2());
        mara.setBuildSampleApproval(history.getBuildSampleApproval());
        mara.setBuildSampleApprovalTargetDate(history.getBuildSampleApprovalTargetDate());
        mara.setTechnicallyOkay(history.getTechnicallyOkay());
        mara.setReleaseDateSoll(history.getReleaseDateSoll());
        mara.setDesignerName(history.getDesignerName());
        mara.setDesignerCostGroup(history.getDesignerCostGroup());
        mara.setDesignerPhoneNumber(history.getDesignerPhoneNumber());
        mara.setKStandReleaseDate(history.getKStandReleaseDate());
        mara.setTioFreiReleaseDate(history.getTioFreiReleaseDate());
        mara.setMfpStatus(history.getMfpStatus());
        mara.setMfpThickness(history.getMfpThickness());
        mara.setKseKz(history.getKseKz());
        mara.setWeightAcceptedFromEPIS(history.getWeightAcceptedFromEPIS());
        mara.setTimestampChange(history.getTimestampChange());
        mara.setTimestampCreate(history.getTimestampCreate());
        mara.setUserChange(history.getUserChange());
        mara.setUserCreate(history.getUserCreate());
    }
}
