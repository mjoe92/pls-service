package de.vw.paso.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import de.vw.paso.logic.pls.PlsEfsElement;
import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementImport;
import de.vw.paso.partlist.domain.EfsElementMara;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.util.QuantityUnit;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

public final class EfsElementMapper {

    public static EfsElement toEntity(EfsElementDTO dto) {
        EfsElement entity = new EfsElement();
        entity.setId(dto.getId());
        entity.setVehiclePartListId(dto.getVehiclePartListId());
        entity.setParentId(dto.getParentId());
        entity.setChildren(new ArrayList<>());
        entity.setDuplicateId(dto.getDuplicateId());
        entity.setRevision(dto.getRevision());
        entity.setDeleted(dto.getDeleted());
        entity.setNodeId(dto.getNodeId());
        entity.setNodeLabel(dto.getNodeLabel());
        entity.setNodeLevel(dto.getNodeLevel());
        entity.setBomNumber(dto.getBomNumber());
        entity.setProduct(dto.getProduct());
        entity.setGap(dto.getGap());
        entity.setTisSort(dto.getTisSort());
        entity.setNodeType(dto.getNodeType());
        entity.setNodeValueParent(dto.getNodeValueParent());
        entity.setNodeValue(dto.getNodeValue());
        entity.setQuantity(dto.getQuantity());
        entity.setQuantityUnit(dto.getQuantityUnit());
        entity.setQuantityUnitExtended(dto.getQuantityUnitExtended());
        entity.setWeightControlFlag(dto.getWeightControlFlag());
        entity.setConstructionsGroup(dto.getConstructionsGroup());
        entity.setProductStructure(dto.getProductStructure());
        entity.setPositionVariant(dto.getPositionVariant());
        entity.setDeletionFlag(dto.getDeletionFlag());
        entity.setCostGroup(dto.getCostGroup());
        entity.setTiWhImportId(dto.getTiWhImportId());
        entity.setAp(dto.getAp());
        entity.setPrNumberRule(dto.getPrNumberRule());
        entity.setBeginDateKey(dto.getBeginDateKey());
        entity.setEndDateKey(dto.getEndDateKey());
        entity.setBeginDate(dto.getBeginDate());
        entity.setEndDate(dto.getEndDate());
        entity.setPartType(dto.getPartType());
        entity.setAggregate(dto.getAggregate());
        entity.setSetKey(dto.getSetKey());
        entity.setWahlweiseFall(dto.getWahlweiseFall());
        entity.setWahlweiseNr(dto.getWahlweiseNr());
        entity.setBaukasten(dto.getBaukasten());
        entity.setBaukastenStatus(dto.getBaukastenStatus());
        entity.setBaukastenNodeId(dto.getBaukastenNodeId());
        entity.setWorkPackageNumber(dto.getWorkPackageNumber());
        entity.setProcessStatus(dto.getProcessStatus());
        entity.setDmuRelevant(dto.getDmuRelevant());
        entity.setMaterialType(dto.getMaterialType());
        entity.setEarliestPvs(dto.getEarliestPvs());
        entity.setEarliestNs(dto.getEarliestNs());
        entity.setEarliestSop(dto.getEarliestSop());
        entity.setPActivationDate(dto.getPActivationDate());
        entity.setKonstructureDate(dto.getKonstructureDate());
        entity.setAvonStatus(dto.getAvonStatus());
        entity.setCogX(dto.getCogX());
        entity.setCogY(dto.getCogY());
        entity.setCogZ(dto.getCogZ());
        entity.setUserCreate(dto.getUserCreate());
        entity.setUserChange(dto.getUserChange());
        entity.setTimestampCreate(dto.getTimestampCreate());
        entity.setTimestampChange(dto.getTimestampChange());
        entity.setEntityChange(dto.isEntityChange());

        EfsElementMara efsElementMara = EfsElementMaraMapper.toEntity(dto.getEfsElementMara());
        entity.setEfsElementMara(efsElementMara);

        return entity;
    }

    public static EfsElementDTO toDto(EfsElement entity) {
        EfsElementDTO dto = new EfsElementDTO();
        dto.setId(entity.getId());
        dto.setParentId(entity.getParentId());
        dto.setChildren(new ArrayList<>());
        dto.setVehiclePartListId(entity.getVehiclePartListId());
        dto.setDuplicateId(entity.getDuplicateId());
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

        EfsElementMaraDTO mara = EfsElementMaraMapper.toDto(entity.getEfsElementMara());
        dto.setEfsElementMara(mara);

        return dto;
    }

    public static EfsElementImport toEntity(EfsElementImport parentImportElement, PlsEfsElement parentEfsElement,
            VehiclePartList vehiclePartList, Map<String, EfsElementMara> partNumberToMaraMap) {
        EfsElementMara mara;
        if (StringUtils.isEmpty(parentEfsElement.getPartNumber())) {
            mara = EfsElementMaraMapper.toEntity(parentEfsElement, vehiclePartList);
        } else {
            mara = partNumberToMaraMap.get(parentEfsElement.getPartNumber());
            if (mara == null) {
                mara = EfsElementMaraMapper.toEntity(parentEfsElement, vehiclePartList);
                partNumberToMaraMap.put(parentEfsElement.getPartNumber(), mara);
            }
        }

        EfsElementImport element = new EfsElementImport();

        element.setTisSort(parentEfsElement.getGlobalSort());
        //    element.setTisSort((parentPlsElement.getPartSort() != null) ? parentPlsElement.getPartSort().longValue() : null);
        element.setGap(parentEfsElement.isGap() ? 1 : 0);
        element.setNodeLabel(parentEfsElement.getNodeLabel());
        element.setNodeValue(parentEfsElement.getNodeValue());
        element.setNodeValueParent(parentEfsElement.getNodeValueParent());
        element.setNodeLevel(parentEfsElement.getNodeLevel());
        element.setNodeId(parentEfsElement.getOriginNodeId());
        element.setNodeType(parentEfsElement.getNodeType());
        element.setVehiclePartListId(vehiclePartList.getId());
        element.setParent(parentImportElement);
        element.setEfsElementMara(mara);
        element.setPositionVariant(parentEfsElement.getPositionVariant());
        element.setDeletionFlag(parentEfsElement.getDeletionFlag());
        element.setQuantity(parentEfsElement.getQuantity());
        element.setQuantityUnit(parentEfsElement.getQuantityUnit() == null ? QuantityUnit.UNKNOWN.getShortName() :
                parentEfsElement.getQuantityUnit().getShortName());
        element.setWeightControlFlag(parentEfsElement.getWeightControlFlag());
        element.setPrNumberRule(parentEfsElement.getPrNumberRule());
        element.setAggregate(parentEfsElement.getAggregate());
        element.setBeginDate(parentEfsElement.getBeginDate());
        element.setEndDate(parentEfsElement.getEndDate());
        element.setBeginDateKey(parentEfsElement.getBeginDateKey());
        element.setEndDateKey(parentEfsElement.getEndDateKey());
        element.setConstructionsGroup(parentEfsElement.getConstructionsGroup());
        element.setCostGroup(parentEfsElement.getCostGroup());
        element.setProductStructure(parentEfsElement.getProductStructure());
        element.setSetKey(parentEfsElement.getSetKey());
        element.setDuplicateId(parentEfsElement.getDuplicateId());
        element.setWahlweiseFall(parentEfsElement.getWahlweiseFall());
        element.setWahlweiseNr(parentEfsElement.getWahlweiseNr());
        element.setBaukastenFlag(parentEfsElement.isEbk() ? 1 : 0);
        element.setBomNumber(parentEfsElement.getBomNumber());
        element.setProduct(parentEfsElement.getProduct());
        element.setPartType(parentEfsElement.getPartType());
        element.setBaukastenStatus(parentEfsElement.getBaukastenStatus());
        element.setBaukastenNodeId(parentEfsElement.getBaukastenNodeId());
        element.setProcessStatus(parentEfsElement.getProcessStatus());
        element.setDmuRelevant(parentEfsElement.getDMURelevant());
        element.setMaterialType(parentEfsElement.getMaterialType());
        element.setEarliestPvs(parentEfsElement.getEarliestPVS());
        element.setEarliestNs(parentEfsElement.getEarliestNS());
        element.setEarliestSop(parentEfsElement.getEarliestSOP());
        element.setPActivationDate(parentEfsElement.getpActivationDate());
        element.setKonstructureDate(parentEfsElement.getKonstructureDate());
        element.setAvonStatus(parentEfsElement.getAvonStatus());

        String ap = parentEfsElement.getWorkPackageNumber();
        if (ap == null) {
            ap = StringConstant.EMPTY;
        }
        element.setAp(ap);

        Collection<EfsElementImport> children = parentEfsElement.getChildren().stream()
                .map(child -> toEntity(element, child, vehiclePartList, partNumberToMaraMap)).toList();
        element.setChildren(children);

        if (element.isMotor()) {
            vehiclePartList.setProductKeyMotor(element.getAggregate());
        }

        if (element.isGetriebe()) {
            vehiclePartList.setProductKeyGearbox(element.getAggregate());
        }

        return element;
    }
}
