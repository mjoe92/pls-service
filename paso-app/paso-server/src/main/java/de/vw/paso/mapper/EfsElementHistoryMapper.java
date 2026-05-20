package de.vw.paso.mapper;

import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementHistory;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.partlist.efselementhistory.EfsElementHistoryDTO;

public final class EfsElementHistoryMapper {

    public static EfsElementHistory toEntity(EfsElement efsElement, String userId) {
        EfsElementHistory entity = PartListFactory.createEfsElementHistory();

        entity.setEfsElement(efsElement);
        entity.setParent(efsElement.getParent());
        entity.setVehiclePartListId(efsElement.getVehiclePartListId());
        entity.setRevision(efsElement.getRevision());
        entity.setDeleted(efsElement.getDeleted());
        entity.setNodeId(efsElement.getNodeId());
        entity.setNodeLabel(efsElement.getNodeLabel());
        entity.setGap(efsElement.getGap());
        entity.setTisSort(efsElement.getTisSort());
        entity.setNodeType(efsElement.getNodeType());
        entity.setNodeLevel(efsElement.getNodeLevel());
        entity.setBomNumber(efsElement.getBomNumber());
        entity.setProduct(efsElement.getProduct());
        entity.setPartType(efsElement.getPartType());
        entity.setConstructionsGroup(efsElement.getConstructionsGroup());
        entity.setProductStructure(efsElement.getProductStructure());
        entity.setAp(efsElement.getAp());
        entity.setQuantity(efsElement.getQuantity());
        entity.setQuantityUnit(efsElement.getQuantityUnit());
        entity.setQuantityUnitExtended(efsElement.getQuantityUnitExtended());
        entity.setWeightControlFlag(efsElement.getWeightControlFlag());
        entity.setWeight(efsElement.getWeight());
        entity.setTiWhImportId(efsElement.getTiWhImportId());
        entity.setSetKey(efsElement.getSetKey());
        entity.setCostGroup(efsElement.getCostGroup());
        entity.setPrNumberRule(efsElement.getPrNumberRule());
        entity.setBeginDateKey(efsElement.getBeginDateKey());
        entity.setEndDateKey(efsElement.getEndDateKey());
        entity.setBeginDate(efsElement.getBeginDate());
        entity.setEndDate(efsElement.getEndDate());
        entity.setAggregate(efsElement.getAggregate());
        entity.setEfsElementMara(efsElement.getEfsElementMara());
        entity.setTimestampChange(efsElement.getTimestampChange());
        entity.setTimestampCreate(efsElement.getTimestampCreate());
        entity.setUserCreate(efsElement.getUserCreate());
        entity.setWahlweiseFall(efsElement.getWahlweiseFall());
        entity.setWahlweiseNr(efsElement.getWahlweiseNr());
        entity.setBaukasten(efsElement.getBaukasten());
        entity.setBaukastenNodeId(efsElement.getBaukastenNodeId());
        entity.setBaukastenStatus(efsElement.getBaukastenStatus());
        entity.setProcessStatus(efsElement.getProcessStatus());
        entity.setDmuRelevant(efsElement.getDmuRelevant());
        entity.setMaterialType(efsElement.getMaterialType());
        entity.setEarliestPvs(efsElement.getEarliestPvs());
        entity.setEarliestNs(efsElement.getEarliestNs());
        entity.setEarliestSop(efsElement.getEarliestSop());
        entity.setPActivationDate(efsElement.getPActivationDate());
        entity.setKonstructureDate(efsElement.getKonstructureDate());
        entity.setAvonStatus(efsElement.getAvonStatus());
        entity.setCogX(efsElement.getCogX());
        entity.setCogY(efsElement.getCogY());
        entity.setCogZ(efsElement.getCogZ());
        entity.setChange(userId);

        String changeUserId = efsElement.getUserChange();
        if (changeUserId == null) {
            changeUserId = userId;
        }
        entity.setUserChange(changeUserId);

        return entity;
    }

    public static EfsElementHistoryDTO toDto(EfsElementHistory entity) {
        EfsElementHistoryDTO dto = new EfsElementHistoryDTO();
        dto.setId(entity.getId());
        dto.setVehiclePartListId(entity.getVehiclePartListId());
        dto.setRevision(entity.getRevision());
        dto.setDeleted(entity.getDeleted());
        dto.setNodeId(entity.getNodeId());
        dto.setNodeLabel(entity.getNodeLabel());
        dto.setNodeLevel(entity.getNodeLevel());
        dto.setBomNumber(entity.getBomNumber());
        dto.setProduct(entity.getProduct());
        dto.setGap(entity.getGap());
        dto.setTisSort(entity.getTisSort());
        dto.setNodeType(entity.getNodeType());
        dto.setNodeValueParent(entity.getNodeValueParent());
        dto.setNodeValue(entity.getNodeValue());
        dto.setQuantity(entity.getQuantity());
        dto.setQuantityUnit(entity.getQuantityUnit());
        dto.setQuantityUnitExtended(entity.getQuantityUnitExtended());
        dto.setWeightControlFlag(entity.getWeightControlFlag());
        dto.setConstructionsGroup(entity.getConstructionsGroup());
        dto.setProductStructure(entity.getProductStructure());
        dto.setPositionVariant(entity.getPositionVariant());
        dto.setDeletionFlag(entity.getDeletionFlag());
        dto.setCostGroup(entity.getCostGroup());
        dto.setTiWhImportId(entity.getTiWhImportId());
        dto.setAp(entity.getAp());
        dto.setPrNumberRule(entity.getPrNumberRule());
        dto.setBeginDateKey(entity.getBeginDateKey());
        dto.setEndDateKey(entity.getEndDateKey());
        dto.setBeginDate(entity.getBeginDate());
        dto.setEndDate(entity.getEndDate());
        dto.setPartType(entity.getPartType());
        dto.setAggregate(entity.getAggregate());
        dto.setSetKey(entity.getSetKey());
        dto.setWahlweiseFall(entity.getWahlweiseFall());
        dto.setWahlweiseNr(entity.getWahlweiseNr());
        dto.setBaukasten(entity.getBaukasten());
        dto.setBaukastenStatus(entity.getBaukastenStatus());
        dto.setBaukastenNodeId(entity.getBaukastenNodeId());
        dto.setWorkPackageNumber(entity.getWorkPackageNumber());
        dto.setProcessStatus(entity.getProcessStatus());
        dto.setDmuRelevant(entity.getDmuRelevant());
        dto.setMaterialType(entity.getMaterialType());
        dto.setEarliestPvs(entity.getEarliestPvs());
        dto.setEarliestNs(entity.getEarliestNs());
        dto.setEarliestSop(entity.getEarliestSop());
        dto.setPActivationDate(entity.getPActivationDate());
        dto.setKonstructureDate(entity.getKonstructureDate());
        dto.setAvonStatus(entity.getAvonStatus());
        dto.setCogX(entity.getCogX());
        dto.setCogY(entity.getCogY());
        dto.setCogZ(entity.getCogZ());
        dto.setUserCreate(entity.getUserCreate());
        dto.setUserChange(entity.getUserChange());
        dto.setTimestampCreate(entity.getTimestampCreate());
        dto.setTimestampChange(entity.getTimestampChange());
        dto.setEntityChange(entity.isEntityChange());

        EfsElement parent = entity.getParent();
        EfsElementDTO parentEfsElementDTO = parent == null ? null : EfsElementMapper.toDto(parent);
        dto.setParent(parentEfsElementDTO);

        EfsElementDTO efsElementDTO = EfsElementMapper.toDto(entity.getEfsElement());
        dto.setEfsElement(efsElementDTO);

        EfsElementMaraDTO efsElementMaraDTO = EfsElementMaraMapper.toDto(entity.getEfsElementMara());
        dto.setEfsElementMara(efsElementMaraDTO);

        return dto;
    }

    public static void map(EfsElement element, EfsElementHistory history) {
        element.setRevision(history.getRevision());
        element.setDeleted(history.getDeleted());
        element.setNodeId(history.getNodeId());
        element.setNodeLabel(history.getNodeLabel());
        element.setGap(history.getGap());
        element.setTisSort(history.getTisSort());
        element.setNodeType(history.getNodeType());
        element.setNodeLevel(history.getNodeLevel());
        element.setBomNumber(history.getBomNumber());
        element.setProduct(history.getProduct());
        element.setPartType(history.getPartType());
        element.setConstructionsGroup(history.getConstructionsGroup());
        element.setProductStructure(history.getProductStructure());
        element.setAp(history.getAp());
        element.setQuantity(history.getQuantity());
        element.setQuantityUnit(history.getQuantityUnit());
        element.setQuantityUnitExtended(history.getQuantityUnitExtended());
        element.setWeightControlFlag(history.getWeightControlFlag());
        element.setWeight(history.getWeight());
        element.setTiWhImportId(history.getTiWhImportId());
        element.setSetKey(history.getSetKey());
        element.setCostGroup(history.getCostGroup());
        element.setPrNumberRule(history.getPrNumberRule());
        element.setBeginDateKey(history.getBeginDateKey());
        element.setEndDateKey(history.getEndDateKey());
        element.setBeginDate(history.getBeginDate());
        element.setEndDate(history.getEndDate());
        element.setAggregate(history.getAggregate());
        element.setEfsElementMara(history.getEfsElementMara());
        element.setTimestampChange(history.getTimestampChange());
        element.setTimestampCreate(history.getTimestampCreate());
        element.setUserChange(history.getUserChange());
        element.setUserCreate(history.getUserCreate());
        element.setWahlweiseFall(history.getWahlweiseFall());
        element.setWahlweiseNr(history.getWahlweiseNr());
        element.setBaukasten(history.getBaukasten());
        element.setBaukastenNodeId(history.getBaukastenNodeId());
        element.setBaukastenStatus(history.getBaukastenStatus());
        element.setProcessStatus(history.getProcessStatus());
        element.setDmuRelevant(history.getDmuRelevant());
        element.setMaterialType(history.getMaterialType());
        element.setEarliestPvs(history.getEarliestPvs());
        element.setEarliestNs(history.getEarliestNs());
        element.setEarliestSop(history.getEarliestSop());
        element.setPActivationDate(history.getPActivationDate());
        element.setKonstructureDate(history.getKonstructureDate());
        element.setAvonStatus(history.getAvonStatus());
        element.setCogX(history.getCogX());
        element.setCogY(history.getCogY());
        element.setCogZ(history.getCogZ());
    }
}
